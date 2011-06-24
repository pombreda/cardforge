package forge.ai.minimax;

import forge.Card;
import net.slightlymagic.braids.game.ai.minimax.InvocableMinimaxMove;
import net.slightlymagic.braids.util.UtilFunctions;
import net.slightlymagic.braids.util.lambda.Null;

public class PlayLandMove implements InvocableMinimaxMove {
	private Card land;

	public PlayLandMove(Card land) {
		this.setLand(land);
	}

	protected void setLand(Card land) {
		if (land == null) {
			throw new NullPointerException();
		}

		this.land = land;
	}

	public Card getLand() {
		return land;
	}

	public Null apply() {
		Unstatic.getGlobalGameState().whoHasPriorityAsPlayer().playLand(land);
		return null;
	}

	@Override
	public String toString() {
		return "plays land \"" + this.land.getName() + "\"";
	}

	@Override
	public boolean equals(Object obj) {
		PlayLandMove that = UtilFunctions.checkNullOrNotInstance(this, obj);
		
		if (that == null || that.land == null)  return false;
		else return (this.land.getName().equals(that.land.getName()));
		// The previous line takes advantage of the fact that all cards in hand
		// having the same name are completely identical for the game's 
		// purposes.  They may have different ID numbers, but that is 
		// inconsequential.
	}
}
