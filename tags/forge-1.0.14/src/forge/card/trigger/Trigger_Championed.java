package forge.card.trigger;

import java.util.HashMap;

import forge.Card;
import forge.card.spellability.SpellAbility;

public class Trigger_Championed extends Trigger {

	public Trigger_Championed(HashMap<String, String> params, Card host) {
		super(params, host);
	}

	@Override
	public boolean performTest(HashMap<String, Object> runParams) {
		Card championed = (Card)runParams.get("Championed");

		if(mapParams.containsKey("ValidCard")) {
			if(!championed.isValidCard(mapParams.get("ValidCard").split(","), hostCard.getController(), hostCard)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public Trigger getCopy() {
		Trigger copy = new Trigger_Championed(mapParams, hostCard);
		if(overridingAbility != null) {
			copy.setOverridingAbility(overridingAbility);
		}
		copy.setName(name);
        copy.setID(ID);

		return copy;
	}

	@Override
	public void setTriggeringObjects(SpellAbility sa) {
		sa.setTriggeringObject("Championed", runParams.get("Championed"));
        sa.setTriggeringObject("Card", runParams.get("Card"));
	}
}
