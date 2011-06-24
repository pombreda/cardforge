package net.slightlymagic.braids.game.tictactoe;

import net.slightlymagic.braids.game.ai.minimax.MinimaxPlayer;
import net.slightlymagic.braids.game.ai.minimax.MinimaxTeam;

public class TTTPlayer implements MinimaxPlayer {
	private boolean isX;
	public TTTPlayer(boolean isX) {
		this.isX = isX;
	}
	
	//TODO @Override
	public MinimaxTeam getTeam() {
		if (isX) {
			return TTTGameState.xTeam;
		}
		else {
			return TTTGameState.oTeam;
		}
	}
}
