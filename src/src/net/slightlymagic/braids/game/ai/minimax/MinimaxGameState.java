package net.slightlymagic.braids.game.ai.minimax;

import java.util.Collection;

/**
 * Represents the entire state of a game between moves.
 */
public interface MinimaxGameState {
	/**
	 * Create a copy of this MinimaxGameState from the perspective of the given
	 * player.  
	 * 
	 * If the game has hidden (or "imperfect") information, the clone
	 * must not contain anything the player does not already know for certain.
	 * For trading card games, we recommend replacing all hidden cards with
	 * cards that cannot be played; this preserves the stack sizes, and is 
	 * helpful for strategies that involve reducing the size of an opponent's
	 * deck.
	 * 
	 * @param p  the person whose perspective to use
	 * @return a new MinimaxGameState instance, without hidden information
	 */
	public MinimaxGameState cloneFor(MinimaxPlayer p);

	/**
	 * @see MinimaxMove
	 * 
	 * @return a collection of MinimaxMove instances for the player who has
	 *         priority; this may be trimmed down to eliminate idiocy, but keep
	 *         in mind that in bizarre situations, a move that is normally
	 *         idiotic may save the day.
	 */
	public Collection<MinimaxMove> getPossibleMoves();
	
	/**
	 * @return the player who has priority; i.e., the player who enacts all
	 * the moves in getPossibleMoves.
	 * @see #getPossibleMoves
	 */
	public MinimaxPlayer whoHasPriority();

	/**
	 * Optional; returns a message-digest for this game-state.  Sufficiently
	 * large digests may be used to distinguish one game-state from another
	 * (within a certain margin of probability) without storing the entire
	 * game-state in memory.
	 * 
	 * It is OK for some game-states to implement this and some to return null
	 * instead. It is only used to enhance performance when computing the
	 * game-tree.
	 *  
	 * @return null if not implemented, or a byte array that obeys the same
	 *         rules as Object.hashCode
	 *  
	 * @see Object#hashCode
	 */
	public byte[] getDigest();
}
