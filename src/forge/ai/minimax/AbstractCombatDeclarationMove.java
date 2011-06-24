package forge.ai.minimax;

import forge.AllZone;
import forge.Combat;
import net.slightlymagic.braids.game.ai.minimax.InvocableMinimaxMove;
import net.slightlymagic.braids.util.lambda.Null;


public abstract class AbstractCombatDeclarationMove 
	implements InvocableMinimaxMove 
{

	private Combat combat;

	protected AbstractCombatDeclarationMove(Combat combat) {
		this.setCombat(combat);
	}

	protected void setCombat(Combat combat) {
		if (combat == null) {
			throw new NullPointerException();
		}

		this.combat = combat;
	}

	public Combat getCombat() {
		return combat;
	}

	public Null apply() {
		AllZone.setCombat(combat);
		return null;
	}
	
}
