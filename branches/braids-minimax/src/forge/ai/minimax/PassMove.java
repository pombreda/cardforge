package forge.ai.minimax;

import net.slightlymagic.braids.game.ai.minimax.InvocableMinimaxMove;
import net.slightlymagic.braids.util.lambda.Null;

/**
 * Use Constants.PASS_MOVE instead.
 * Package-only access for this class is intentional.
 * 
 * @see Constants#PASS_MOVE
 */
class PassMove implements InvocableMinimaxMove {
	public String toString() {
		return "passes priority";
	}

	public Null apply() {
		Unstatic.getGlobalGameState().getPhase().passPriority();
		return null;
	}
}
