package forge.ai.minimax;

import forge.Card;
import forge.CardList;
import forge.Combat;

public class DeclareBlockersMove extends AbstractCombatDeclarationMove {
	public DeclareBlockersMove(Combat combat) {
		super(combat);
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		
		Card[] attackers = getCombat().getAttackers();

		for (Card attackCard : attackers) 
		{
			CardList blockers = getCombat().getBlockers(attackCard);
			
			for (Card blockCard : blockers) {
				buf.append("blocks ");
				buf.append(attackCard);
				buf.append(" with ");
				buf.append(blockCard);
				buf.append(", ");
			}
		}

		int bufLength = buf.length();
		if (bufLength > 2) {
			buf.delete(bufLength-2, bufLength);
			buf.append('.');
		}
		else {
			buf.append("does not block");
		}
		
		return buf.toString();
	}
}
