package net.slightlymagic.braids.game.tictactoe;

import net.slightlymagic.braids.game.ai.minimax.MinimaxTeam;

public class TTTTeam implements MinimaxTeam {
	private boolean isX;
	public TTTTeam(boolean isX) {
		this.isX = isX;
	}
	public boolean isX() {
		return this.isX;
	}
	
}
