package net.slightlymagic.braids.game.tictactoe;

import net.slightlymagic.braids.game.ai.minimax.MinimaxController;
import net.slightlymagic.braids.game.ai.minimax.MinimaxGameState;
import net.slightlymagic.braids.game.ai.minimax.MinimaxMove;

public class TTTController  implements MinimaxController {
	public void invoke(MinimaxMove move1, MinimaxGameState gs1) {
		TTTMove move = (TTTMove) move1;
		TTTGameState gs = (TTTGameState) gs1;
		int col = move.getColumn();
		int row = move.getRow();
		boolean x = move.isX();
		
		gs.set(col, row, x);
		gs.givePriorityToOtherTeam();
	}
}
