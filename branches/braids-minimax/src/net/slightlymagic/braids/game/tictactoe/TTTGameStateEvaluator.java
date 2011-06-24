package net.slightlymagic.braids.game.tictactoe;

import net.slightlymagic.braids.game.ai.minimax.MinimaxGameState;
import net.slightlymagic.braids.game.ai.minimax.MinimaxGameStateEvaluator;
import net.slightlymagic.braids.game.ai.minimax.MinimaxTeam;

public class TTTGameStateEvaluator implements MinimaxGameStateEvaluator {

	//TODO @Override
	public long evaluatePositionFor(MinimaxTeam team2, MinimaxGameState gs2) {
		TTTTeam team = (TTTTeam) team2;
		TTTGameState gs = (TTTGameState) gs2;

		Boolean xWon = gs.hasXWon();
		
		if (xWon != null) {
			if (xWon == team.isX()) {
				return Long.MAX_VALUE;
			}
			else {
				return Long.MIN_VALUE;
			}
		}
		
		if (gs.getValueAt(0,0) != null &&
			gs.getValueAt(0,1) != null &&
			gs.getValueAt(0,2) != null &&
			gs.getValueAt(1,0) != null &&
			gs.getValueAt(1,1) != null &&
			gs.getValueAt(1,2) != null &&
			gs.getValueAt(2,0) != null &&
			gs.getValueAt(2,1) != null &&
			gs.getValueAt(2,2) != null)
		{
			// Tie game.
			return 0;
		}
		
		long result = 0;

		
		// I've weighted each square by the number of possible wins that go 
		// through that square.
		
		
		// center
		result += evaluateSquare(1, 1, 4, gs, team);
		
		// corners
		result += evaluateSquare(0, 0, 3, gs, team);
		result += evaluateSquare(0, 2, 3, gs, team);
		result += evaluateSquare(2, 0, 3, gs, team);
		result += evaluateSquare(2, 2, 3, gs, team);
		
		// sides
		result += evaluateSquare(0, 1, 2, gs, team);
		result += evaluateSquare(1, 0, 2, gs, team);
		result += evaluateSquare(1, 2, 2, gs, team);
		result += evaluateSquare(2, 1, 2, gs, team);
		
		return result;
	}

	public long evaluateSquare(int col, int row, int score, TTTGameState gs,
			TTTTeam team) 
	{
		long result = 0;
		
		if (gs.getValueAt(col, row) != null) {
			if (team.isX() == gs.getValueAt(col, row)) { 
				// MinimaxTeam has the square.
				result = score;
			}
			else {
				// Opponents have the square.
				result = -score;
			}
		}
		
		return result;
	}

}
