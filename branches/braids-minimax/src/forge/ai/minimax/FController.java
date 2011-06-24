package forge.ai.minimax;

import net.slightlymagic.braids.game.ai.minimax.InvocableMinimaxMove;
import net.slightlymagic.braids.game.ai.minimax.MinimaxController;
import net.slightlymagic.braids.game.ai.minimax.MinimaxGameState;
import net.slightlymagic.braids.game.ai.minimax.MinimaxMove;
import net.slightlymagic.braids.util.NotImplementedError;

public class FController implements MinimaxController {

	public void invoke(MinimaxMove move, MinimaxGameState gsClone) {
		FGameState realState = Unstatic.getGlobalGameState();
		
		try {
			Unstatic.setGlobalGameState((FGameState) gsClone);

			if (move instanceof InvocableMinimaxMove) {
				InvocableMinimaxMove invocableMove = (InvocableMinimaxMove) move;
				
				invocableMove.apply();
			}
			else {
				throw new NotImplementedError(
					"I only support InvocableMinimaxMove instances at this time.");
			}
		} 
		finally {
			// Regardless if something bombed in the theoretical world, we
			// always want to put the real one back before exiting this method.
			Unstatic.setGlobalGameState(realState);
		}
	}

}
