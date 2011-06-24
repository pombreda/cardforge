package forge.ai.minimax;

import forge.Card;
import forge.Combat;

public class DeclareAttackersMove extends AbstractCombatDeclarationMove {
	public DeclareAttackersMove(Combat combat) {
		super(combat);
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		
		Card[] attackers = getCombat().getAttackers();

		for (Card attackCard : attackers) 
		{
			Object defender = getCombat().getDefenderByAttacker(attackCard);
			buf.append("attacks ");
			buf.append(defender.toString());
			buf.append(" with ");
			buf.append(attackCard);
			buf.append(", ");
		}
		
		int bufLength = buf.length();
		if (bufLength > 2) {
			buf.delete(bufLength-2, bufLength);
			buf.append('.');
		}
		else {
			buf.append("does not attack");
		}
		
		return buf.toString();
	}
}
