package net.slightlymagic.braids.game.ai.minimax;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.TreeMap;

import net.slightlymagic.braids.util.ComparableByteArray;

public class SimpleMiniMax {
	private int minSearchDepth;  // cannot be null
	private Long maxClockTime_ms;  // may be null
	private Integer maxSearchDepth;  // may be null
	private Long maxNodesToEvaluate;  // may be null
	
	public static Random random = new Random();

	public SimpleMiniMax(Long maxClockTime_ms) {
		setMinSearchDepth(1);
		setMaxSearchDepth(null);
		setMaxNodesToEvaluate(null);
		setMaxClockTime_ms(maxClockTime_ms);
	}
	
	/**
	 * Set the approximate maximum time to evaluate consequences of possible 
	 * actions in suggestBestMove.
	 * 
	 * The suggestBestMove method will actually take longer than sec,
	 * because it delays calling the MinimaxGameState.evaluatePositionFor method
	 * until sec has already elapsed.
	 * 
	 * @param millisec
	 * 
	 * @see #suggestBestMove(MinimaxGameState)
	 */
	public void setMaxClockTime_ms(Long millisec)
	{
		if (millisec != null && millisec <= 0) {

			throw new IllegalArgumentException(
					"sec must be positive; received " + millisec);
			
		}
		maxClockTime_ms = millisec;
	}

	/**
	 * The minimum search depth overrides the other three maxima.  In other
	 * words, suggestBestMove will only stop after it has covered the minimum
	 * depth.
	 * 
	 * @param depth how deep into the game tree to evaluate possibilities
	 */
	public void setMinSearchDepth(Integer depth) {
		if (depth == null) {
			// We interpret null as "no minimum".
			depth = 0;
		}
		else if (depth < 0) {
			throw new IllegalArgumentException(
					"depth must be nonnegative; received " + depth);
		}

		minSearchDepth = depth;
	}
	
	public void setMaxSearchDepth(Integer depth) {
		maxSearchDepth = depth;
	}
	
	public void setMaxNodesToEvaluate(Long numNodes) {
		maxNodesToEvaluate = numNodes;
	}
	
	
	public MinimaxMove suggestBestMoveForPlayer(MinimaxController ctrl, 
			MinimaxGameState initialState, 
			MinimaxPlayer player,
			MinimaxGameStateEvaluator gse)
		throws WrongPlayerException
	{
		long startTime_ms = new Date().getTime();

		if (initialState.whoHasPriority() != player) {
			throw new WrongPlayerException(
				player.toString() +	" must have priority");
		}
		
		GameTreeNode rootNode = new GameTreeNode(initialState, null);

		populateGameTreeBreadthFirst(ctrl, rootNode, 
				minSearchDepth, maxSearchDepth, 
				startTime_ms, maxClockTime_ms, 
				maxNodesToEvaluate, player);

		List<MinimaxMove> winningMoves = new ArrayList<MinimaxMove>();
			
		Long bestPositionValue = null; 

		for (MinimaxMove move : rootNode.getMoves()) {
			GameTreeNode child = rootNode.getChild(move);
			MinimaxTeam team = player.getTeam();
			
			int maxDepth = Integer.MAX_VALUE;
			if (maxSearchDepth != null) {
				maxDepth = maxSearchDepth;
			}

			long positionValue = minimaxFor(team, gse, child, maxDepth);
			
			if (bestPositionValue == null || positionValue > bestPositionValue) 
			{
				bestPositionValue = positionValue;
				winningMoves.clear();
				winningMoves.add(move);
			}
			else 
			{
				addToWinnersIfTiedForPosition(move, winningMoves, 
						positionValue, bestPositionValue);
			}
		}

		
		int winningMovesSize = winningMoves.size();
		MinimaxMove result;
		
		// Choose from among the tied members at random.
		if (winningMovesSize == 1) {
			result = winningMoves.get(0);
		}
		else if (winningMovesSize > 1) {
			System.err.println(":There are multiple best moves: " + winningMoves.toString());
			int winnerIx = random.nextInt(winningMovesSize);
			result = winningMoves.get(winnerIx);
		}
		else {
			throw new AssertionError(
					"This should never happen; there are no choices!");
		}
		
		return result;
	}
	
	
	public static long minimaxFor(MinimaxTeam team, MinimaxGameStateEvaluator gse,
			GameTreeNode node, int depth) 
	{
		Long result = null;
		
		if (depth <= 0 || node.isTerminal()) {
			result = node.getHeuristicValueFor(team, gse);
			
			if (result == Long.MAX_VALUE) {
				// Decrease it by the number of turns needed to get to that
				// value.  This favors earlier wins.
				
				result -= node.getDepth();
			}
			else if (result == Long.MIN_VALUE) {
				// Similarly, this causes the opponent to favor earlier losses.
				result += node.getDepth();
			}

			return result;
		}

		for (GameTreeNode child : node.getChildNodes()) {
			long childScore = minimaxFor(team, gse, child, depth-1);

			boolean opponentsMadeChildNode = (child.getEffectingTeam() != team);

			if (result != null) {
				// This is not the first child.
				
				if (opponentsMadeChildNode) {
					result = Math.min(result, childScore);
				}
				else {
					result = Math.max(result, childScore);
				}
			}
			else {
				// Always use the first child's score, then compare it against
				// its siblings.
				result = childScore;
			}
		}

		return result;

	}

	public static void populateGameTreeBreadthFirst(MinimaxController ctrl,
			GameTreeNode rootNode,
			int minSearchDepth, Integer maxSearchDepth, 
			long startTime_ms, Long maxClockTime_ms, 
			Long maxNodesRemaining, MinimaxPlayer fromWhosePerspective) 
	{
		// The queue is what enables us to perform breadth-first construction
		// of the game-tree.  If we used recursion, it would be depth-first
		// (and far less useful).
		
		Map<ComparableByteArray,GameTreeNode> stateDigestToNode = 
			new TreeMap<ComparableByteArray,GameTreeNode>();

		int numDigestHits = 0;
		
		
		Queue<GameTreeNode> nodeQueue = new LinkedList<GameTreeNode>();
		nodeQueue.add(rootNode);
		boolean weHaveMoreTime = true;
		long totalNodesComputed = 0;  // The root-node doesn't count.
		String reason = null;

		while (weHaveMoreTime && !nodeQueue.isEmpty()) {
			GameTreeNode node = nodeQueue.remove();
			
			byte[] stateDigest = node.getGameState().getDigest();
			ComparableByteArray stateDigestKey = null;
			if (stateDigest != null) {
				
				stateDigestKey = new ComparableByteArray(stateDigest);
				GameTreeNode twin = stateDigestToNode.get(stateDigestKey);
				
				if (twin != null) {
					numDigestHits++;
					
					// This technically turns the tree into a directed, acyclic
					// graph; but it still has a root node and terminal nodes,
					// so they can be traversed in the same way.
					node.shortcutChildrenFrom(twin);
					continue;
				}
			}
			
			int nodeDepth = node.getDepth();
			
			if (maxSearchDepth != null && node.getDepth() >= maxSearchDepth) {
				reason = "Exceeded maximum search-depth";
				break;
			}
			
			else if (nodeDepth < minSearchDepth) {
				//weHaveMoreTime = node.computeAllImmediateChildren(ctrl);
				node.computeAllImmediateChildren(ctrl, fromWhosePerspective);

				long nodesJustEvaluated = node.getNumberOfChildren();
				totalNodesComputed += nodesJustEvaluated;

				if (maxNodesRemaining != null) {
					// We care about the number of nodes.
					maxNodesRemaining -= nodesJustEvaluated;
				}
			}
			else {
				// We have achieved the minimum search-depth.
				
				weHaveMoreTime = 
					node.populateMovesToChildNodes(ctrl, 
							startTime_ms, maxClockTime_ms, 
							maxNodesRemaining, fromWhosePerspective);
				
				long nodesJustEvaluated = node.getNumberOfChildren();
				totalNodesComputed += nodesJustEvaluated;

				if (maxNodesRemaining != null) {
					// We care about the number of nodes.
					maxNodesRemaining -= nodesJustEvaluated;
				}
			}

			if (stateDigest != null && !node.isTerminal()) {
				stateDigestToNode.put(stateDigestKey, node);
			}
			
			try {
				nodeQueue.addAll(node.getChildNodes());

			} catch (OutOfMemoryError exn) {
				// Some may consider this an early abort, because there are
				// still nodes left in the queue; but if a cloned
				// MinimaxGameState is bigger than an entry in the queue -- and
				// it probably is -- then we may as well stop.
				
				// Get some breathing room.
				nodeQueue = null;

				reason = "Ran out of memory";
				break;
			}

		} //endwhile

		
		System.err.println(":# of digest-hits = " + numDigestHits);
		System.err.println(":# of memoized heuristic hits = " + GameTreeNode.getHeuristicValueFor_numHits);
		GameTreeNode.getHeuristicValueFor_numHits = 0; //XXX
		
		if (reason != null) {
			System.err.println(":" + reason + " after computing " + totalNodesComputed + " nodes.");
		}
		else {
			long nowTime_ms = new Date().getTime();

			if (nodeQueue != null && nodeQueue.isEmpty()) {
				System.err.println(":All possibilities determined after computing " + totalNodesComputed + " nodes.");
			}
			else if (maxClockTime_ms != null && nowTime_ms - startTime_ms >= maxClockTime_ms) {
				System.err.println(":Ran out of time after computing " + totalNodesComputed + " nodes.");
			}
			else if (maxNodesRemaining != null && maxNodesRemaining <= 0) {
				System.err.println(":Computed maximum number of nodes (" + totalNodesComputed + ").");
			}
			else {
				System.err.println(":Probably ran out of memory after computing " + totalNodesComputed + " nodes.");
			}
		}
	}

	
	/**
	 * This code used to be inlined, but I grew weary of the constant null
	 * pointer-access warning.
	 * 
	 * @param move  the move to add if it ties
	 * @param winners  the list to which move might be added
	 * @param positionValue  the move's calculated position-value
	 * @param bestPositionValue  the best position-value seen so far
	 */
	public static void addToWinnersIfTiedForPosition(MinimaxMove move,
			List<MinimaxMove> winners, long positionValue, Long bestPositionValue) 
	{
		if (positionValue == bestPositionValue) { 
			// Multiple moves tied.
			winners.add(move);
		}
	}

	
	/**
	 * Invokes System.gc; in debug mode, also tells us how much time it took.
	 * @deprecated
	 */
	public static void gc() {
		long gcStartTime_ms = new Date().getTime(); 
		System.gc();
		long gcFinishTime_ms = new Date().getTime();  
		
		System.err.println(":System.gc took " + (gcFinishTime_ms - gcStartTime_ms) + " milliseconds.");
	}
	
}
