package forge.ai.minimax;

import java.util.ArrayList;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArrayList;

import net.slightlymagic.braids.game.ai.minimax.MinimaxMove;
import forge.Card;
import forge.Constant;

public class Constants {
	public static final Card DUMMY_CARD = new Card();
	static {
		DUMMY_CARD.setName("Dummy Card");

		ArrayList<String> types = new ArrayList<String>(1);
		types.add("Sorcery");  // Limits when it could be cast
		DUMMY_CARD.setType(types);

		DUMMY_CARD.setManaCost(Integer.MAX_VALUE + " W U B R G");
	}
	
	public static final MinimaxMove PASS_MOVE = new PassMove();
	public static final TreeSet<String> PHASES_WHERE_PLAYERS_CAN_USE_ABILITIES =
	new TreeSet<String>(new CopyOnWriteArrayList<String>(new String[] {
			Constant.Phase.Upkeep, 
			Constant.Phase.Draw, 
			Constant.Phase.Main1, 
			Constant.Phase.Combat_Begin, 
			Constant.Phase.Combat_Declare_Attackers_InstantAbility, 
			Constant.Phase.Combat_Declare_Blockers_InstantAbility, 
			//Constant.Phase.Combat_End, 
			Constant.Phase.Main2, 
			Constant.Phase.End_Of_Turn, 
	}));
	
}
