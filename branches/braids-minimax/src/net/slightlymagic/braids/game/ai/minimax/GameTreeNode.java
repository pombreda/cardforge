package net.slightlymagic.braids.game.ai.minimax;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GameTreeNode {

	private MinimaxGameState gameState;
	private Map<MinimaxMove,GameTreeNode> movesToChildNodes;
	private GameTreeNode parent;
	private MinimaxTeam effectingTeam;
	
	private Long getHeuristicValueFor_memoizedValue;
	private MinimaxTeam getHeuristicValueFor_memoizedTeam;
	private MinimaxGameStateEvaluator getHeuristicValueFor_memoizedEvaluator;
	
	public static long getHeuristicValueFor_numHits = 0;  // XXX
	
	
	public GameTreeNode(MinimaxGameState state, GameTreeNode myParent) {
		setGameState(state);
		parent = myParent;
		
		if (myParent == null) {
			setEffectingTeam(null);
		}
		else {
			setEffectingTeam(myParent.getGameState().whoHasPriority().getTeam());
		}
	}
	
	protected void setEffectingTeam(MinimaxTeam team) {
		effectingTeam = team;
	}

	/**
	 * The effecting team of a node is the team of the player who had priority
	 * to make the move that created that node. In other words, the team of the
	 * player who made the move that caused the node to exist is that node's
	 * effecting team.
	 * 
	 * @return the effecting team for this node, or null if this is the root 
	 * node
	 */
	public MinimaxTeam getEffectingTeam() {
		return effectingTeam;
	}

	public MinimaxGameState getGameState() {
		return gameState;
	}

	public void setGameState(MinimaxGameState state) {
		gameState = state;
	}


	public int getNumberOfChildren() {
		int result = 0;
		if (movesToChildNodes != null) {
			result = movesToChildNodes.size();
		}
		return result;
	}

	
	/**
	 * @return null if this is the root node; otherwise, returns the parent
	 * node of this node.
	 */
	public GameTreeNode getParent() {
		return parent;
	}
	

	/**
	 * The root node has depth 0.
	 * @return the depth of this node.
	 */
	public int getDepth() {
		int depth = 0;
		
		GameTreeNode parent = getParent();
		while (parent != null) {
			parent = parent.getParent();
			depth++;
		}
		
		return depth;
	}
	
	
	public Collection<GameTreeNode> getChildNodes() {
		if (movesToChildNodes != null) {
			return movesToChildNodes.values();
		}
		else {
			return new ArrayList<GameTreeNode>(0);
		}
	}
	
	
	/**
	 * A move is an edge (or line) in the tree that leads to a child node.
	 */
	public Set<MinimaxMove> getMoves() {
		return movesToChildNodes.keySet();
	}
	
	
	/**
	 * Computes as many immediate children as RAM allows.
	 * 
	 * @return false iff there is definitely no more RAM to compute more nodes
	 */
	public boolean computeAllImmediateChildren(MinimaxController ctrl, 
			MinimaxPlayer whoDeterminesPerspective) {
		return populateMovesToChildNodes(ctrl, 0, null, null, whoDeterminesPerspective);
	}
	
	/**
	 * Compute and populate the immediate children of this node; if
	 * this computation exceeds the given constraints, then we do not add any
	 * children.
	 * 
	 * If both maxClockTime_ms and maxNodesRemaining are null, we only stop if
	 * we run out of RAM.
	 * 
	 * Note that providing both maxClockTime_ms and maxNodesRemaining means that
	 * if either value is exceeded, we stop (and possibly commit infanticide).
	 * 
	 * @param startTime  the Date.getTime value at which we started the 
	 * computations; we use this to determine if we have met or exceeded 
	 * maxClockTime_ms.
	 *  
	 * @param maxClockTime_ms  if null, we ignore startTime and do not consider
	 * clock-time to be a constraint on the computations; otherwise, we do use 
	 * it.
	 * 
	 * @param maxNodesRemaining  if null, we do not take the number of nodes
	 * into consideration as a constraint on the computations; otherwise, we
	 * do not exceed computing this many child-nodes. 
	 * 
	 * @return false iff there is definitely no more time (or RAM) to compute 
	 * more nodes
	 */
	public boolean populateMovesToChildNodes(MinimaxController ctrl, long startTime, 
			Long maxClockTime_ms, Long maxNodesRemaining, 
			MinimaxPlayer whoDeterminesPerspective) 
	{
		// Begin memory-intensive operation.
		try {
			movesToChildNodes = new HashMap<MinimaxMove,GameTreeNode>();
		
			boolean weHaveMoreTime = true;
			
			Collection<MinimaxMove> possibleMoves = gameState.getPossibleMoves();
			int possibleMovesSize = possibleMoves.size();
			
			for (MinimaxMove move : possibleMoves) {
				
				MinimaxGameState clone;
				clone = gameState.cloneFor(whoDeterminesPerspective);
	
				ctrl.invoke(move, clone);
	
				GameTreeNode node = new GameTreeNode(clone, this);
				movesToChildNodes.put(move, node);
				
				if (maxNodesRemaining != null) {
					maxNodesRemaining--;
				}
	
				long nowTime_ms = new Date().getTime();
	
				if ((maxClockTime_ms != null && nowTime_ms - startTime >= maxClockTime_ms) ||
					(maxNodesRemaining != null && maxNodesRemaining <= 0))
				{
					weHaveMoreTime = false;
				}
	
				if (!weHaveMoreTime) {
					break;
				}
			}

			if (possibleMovesSize > getNumberOfChildren()) {
				// Infanticide!
				System.err.println(":Killing off " +  (possibleMovesSize - getNumberOfChildren()) + " child nodes");
				movesToChildNodes = null;
				weHaveMoreTime = false;
			}
			else if (getNumberOfChildren() > 0) {
				// We have computed all of this node's children.
				// To save RAM, delete the gameState at this node iff we are not
				// the root node. This is safe, because Minimax is only
				// concerned with evaluating the game-states at the terminal
				// (leaf) nodes.
				
				if (parent != null) {
					gameState = null;
				}
			}
			
			return weHaveMoreTime;
		
		} catch (OutOfMemoryError exn) {
			// An incomplete computation of a node's children is 
			// not tolerable.
			
			movesToChildNodes = null;
			return false;  // we may have more time, but we are out of RAM.
		}
	}
	
	
	public long getHeuristicValueFor(MinimaxTeam team, 
			MinimaxGameStateEvaluator gse) 
	{
		if (getHeuristicValueFor_memoizedValue == null || 
			getHeuristicValueFor_memoizedTeam != team || 
			getHeuristicValueFor_memoizedEvaluator != gse)
		{ 
			getHeuristicValueFor_memoizedTeam = team;
			getHeuristicValueFor_memoizedEvaluator = gse;
			getHeuristicValueFor_memoizedValue = gse.evaluatePositionFor(team, gameState);
		}
		/*XXX disable this section when not debugging */
		else { 
			getHeuristicValueFor_numHits++;
		}
		/*XXX disable above section when not debugging */
		return getHeuristicValueFor_memoizedValue;
	}
	
	public boolean isTerminal() {
		return (getNumberOfChildren() == 0);
	}

	public GameTreeNode getChild(MinimaxMove move) {
		return movesToChildNodes.get(move);
	}

	public static String toSimpleString(Object obj) {
		if (obj == null) { 
			return "null";
		}

		StringBuffer buf = new StringBuffer();
		buf.append(obj.getClass().getSimpleName());
		buf.append('@');
		buf.append(Integer.toHexString(obj.hashCode()));
		return buf.toString();
	}
	
	
	@Override
	public String toString() {
		
		StringBuffer buf = new StringBuffer();
		buf.append("GameTreeNode{gameState:");
		buf.append(gameState.toString());
		
		buf.append(",parent=");
		buf.append(toSimpleString(parent));
		

		buf.append(",children=");
		
		if (isTerminal()) {
			buf.append("{}");
		}
		else {
			buf.append('{');
			for (MinimaxMove move : getMoves()) {
				buf.append(move);
				buf.append(':');
				buf.append(toSimpleString(getChild(move)));
				buf.append(',');
			}
			buf.append('}');
		}
		
		buf.append('}');
		
		return buf.toString();
	}

	public void shortcutChildrenFrom(GameTreeNode twin) {
		this.movesToChildNodes = twin.movesToChildNodes;

		if (parent != null) {
			gameState = null;
		}
	}
	
}
