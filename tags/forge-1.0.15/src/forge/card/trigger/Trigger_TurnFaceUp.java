package forge.card.trigger;

import forge.Card;
import forge.card.spellability.SpellAbility;

import java.util.HashMap;

/**
 * <p>Trigger_TurnFaceUp class.</p>
 *
 * @author Forge
 * @version $Id: $
 */
public class Trigger_TurnFaceUp extends Trigger {

    /**
     * <p>Constructor for Trigger_TurnFaceUp.</p>
     *
     * @param params a {@link java.util.HashMap} object.
     * @param host a {@link forge.Card} object.
     */
    public Trigger_TurnFaceUp(HashMap<String, String> params, Card host) {
        super(params, host);
    }

    /** {@inheritDoc} */
    @Override
    public boolean performTest(HashMap<String, Object> runParams) {
        if (mapParams.containsKey("ValidCard")) {
            if (!matchesValid(runParams.get("Card"), mapParams.get("ValidCard").split(","), hostCard)) {
                return false;
            }
        }

        return true;
    }

    /** {@inheritDoc} */
    @Override
    public Trigger getCopy() {
        Trigger copy = new Trigger_TurnFaceUp(mapParams, hostCard);
        if (overridingAbility != null) {
            copy.setOverridingAbility(overridingAbility);
        }
        copy.setName(name);
        copy.setID(ID);

        return copy;
    }

    /** {@inheritDoc} */
    @Override
    public void setTriggeringObjects(SpellAbility sa) {
        sa.setTriggeringObject("Card", runParams.get("Card"));
    }
}