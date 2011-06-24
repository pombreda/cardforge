package net.slightlymagic.braids.game.ai.minimax;

/**
 * This contains AI-specific code to evaluate a game-state for a specific player
 * or team of players.
 */
public interface MinimaxGameStateEvaluator {
	
	/**
	 * Produce a numeric value indicating the relative strength of the team's
	 * position; higher numbers mean the team's position is better.
	 * 
	 * @param team  the team for whom we are rooting; pom-poms are optional. 
	 * @param gs  the game state to examine
	 * 
	 * @return Long.MAX_VALUE if the game-state already represents a win for
	 * the team, Long.MIN_VALUE if the state already represents a loss, 0 if
	 * the game is in perfect balance between the team and its enemies, or
	 * another number indicating the team's relative strength.
	 */
	public long evaluatePositionFor(MinimaxTeam team, MinimaxGameState gs);
}


