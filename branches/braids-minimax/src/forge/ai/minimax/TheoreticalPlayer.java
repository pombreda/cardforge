package forge.ai.minimax;

import net.slightlymagic.braids.game.ai.minimax.MinimaxPlayer;
import net.slightlymagic.braids.game.ai.minimax.MinimaxTeam;
import forge.AIPlayer;
import forge.AllZone;
import forge.Player;
import forge.PlayerZone;

/**
 * @deprecated
 */
public class TheoreticalPlayer extends AIPlayer
	implements MinimaxPlayer, MinimaxTeam
{
	
	private static final long serialVersionUID = -8428847600551195328L;

	private Player actualPlayer;

	protected TheoreticalPlayer(Player actualPlayer) {
		super(actualPlayer.getName(), actualPlayer.getLife(), 
				actualPlayer.getPoisonCounters());

		this.actualPlayer = actualPlayer;
	}

	public static TheoreticalPlayer wrap(Player player) {
		if (player instanceof TheoreticalPlayer) {
			return (TheoreticalPlayer) player;
		}
		else {
			return new TheoreticalPlayer(player);
		}
	}
	
	
	@Override
	public boolean isPlayer(Player p1) {
		return actualPlayer.isPlayer(p1);
	}

	/**
	 * Teams have not yet been implemented, so each player IS his or her own
	 * team.
	 */
	public MinimaxTeam getTeam() {
		return this;
	}

	public PlayerZone getHand() {
		if (isPlayer(AllZone.getHumanPlayer())) {
			return AllZone.getHumanHand();
		}
		else {
			return AllZone.getComputerHand();
		}
	}

	public PlayerZone getLibrary() {
		if (isPlayer(AllZone.getHumanPlayer())) {
			return AllZone.getHumanLibrary();
		}
		else {
			return AllZone.getComputerLibrary();
		}
	}

}
