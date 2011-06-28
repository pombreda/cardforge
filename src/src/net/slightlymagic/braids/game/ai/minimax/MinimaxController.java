package net.slightlymagic.braids.game.ai.minimax;

/**
 * This allows for modifications to the MinimaxGameState to be policed by a separate
 * class.
 */
public interface MinimaxController {
	/**
	 * Change the game-state by invoking a specific move.
	 * @param move  the move to make
	 * @param gs  the game-state to change
	 */
	public void invoke(MinimaxMove move, MinimaxGameState gs);
}
