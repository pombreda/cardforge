package forge.card.trigger;

import forge.Card;
import forge.card.spellability.SpellAbility;

import java.util.HashMap;

public class Trigger_LandPlayed extends Trigger {

    public Trigger_LandPlayed(String n, HashMap<String, String> params, Card host) {
        super(n, params, host);
    }

    public Trigger_LandPlayed(HashMap<String, String> params, Card host) {
        super(params, host);
    }

    @Override
    public Trigger getCopy() {
        Trigger copy = new Trigger_LandPlayed(name, mapParams, hostCard);
        copy.setID(ID);

        if (this.overridingAbility != null) {
            copy.setOverridingAbility(overridingAbility);
        }

        return copy;
    }

    @Override
    public void setTriggeringObjects(SpellAbility sa) {
        sa.setTriggeringObject("Card", runParams.get("Card"));
    }

    @Override
    public boolean performTest(HashMap<String, Object> runParams) {
        if (mapParams.containsKey("ValidCard")) {
            if (!matchesValid(runParams.get("Card"), mapParams.get("ValidCard").split(","), hostCard)) {
                return false;
            }
        }
        return true;
    }

}
