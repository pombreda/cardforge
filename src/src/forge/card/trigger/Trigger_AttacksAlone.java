package forge.card.trigger;

import java.util.HashMap;

import forge.Card;

public class Trigger_AttacksAlone extends Trigger {

	public Trigger_AttacksAlone(HashMap<String, String> params, Card host) {
		super(params, host);
	}

	@Override
	public boolean performTest(HashMap<String, Object> runParams) {
		if(mapParams.containsKey("ValidCard")) {
			if(!matchesValid(runParams.get("Attacker"),mapParams.get("ValidCard").split(","),hostCard)) {
				return false;
			}
		}
		
		return true;
	}

	@Override
	public Trigger getCopy() {
		Trigger copy = new Trigger_AttacksAlone(mapParams,hostCard);
		if(overridingAbility != null) {
			copy.setOverridingAbility(overridingAbility);
		}
		copy.setName(name);
        copy.setID(ID);
		
		return copy;
	}
	
	@Override
	public void setTriggeringObjects(Card c) {
        c.setTriggeringObject("Attacker",runParams.get("Attacker"));
	}
	
}//end Trigger_AttacksAlone class
