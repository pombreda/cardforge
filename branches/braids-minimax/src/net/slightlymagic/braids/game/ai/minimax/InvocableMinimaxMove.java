package net.slightlymagic.braids.game.ai.minimax;

import net.slightlymagic.braids.util.lambda.Null;
import net.slightlymagic.braids.util.lambda.Thunk;

/**
 * One can invoke the move by calling the apply() method.  This method assumes
 * that the game-state has already been cloned and is available.
 *  
 * @see Thunk#apply()
 */
public interface InvocableMinimaxMove extends MinimaxMove, Thunk<Null> {
	// The apply method comes from ThunkVoid.
}
