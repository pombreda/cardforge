package forge.card.trigger;

import forge.Card;
import forge.card.spellability.SpellAbility;

import java.util.HashMap;

/**
 * <p>Trigger_ChangesZone class.</p>
 *
 * @author Forge
 * @version $Id: $
 */
public class Trigger_ChangesZone extends Trigger {

    /**
     * <p>Constructor for Trigger_ChangesZone.</p>
     *
     * @param params a {@link java.util.HashMap} object.
     * @param host a {@link forge.Card} object.
     */
    public Trigger_ChangesZone(HashMap<String, String> params, Card host) {
        super(params, host);
    }

    /** {@inheritDoc} */
    @Override
    public boolean performTest(HashMap<String, Object> runParams) {
        if (mapParams.containsKey("Origin")) {
            if (!mapParams.get("Origin").equals("Any")) {
                if (mapParams.get("Origin") == null) {
                    return false;
                }
                if (!mapParams.get("Origin").equals((String) runParams.get("Origin"))) {
                    return false;
                }
            }
        }

        if (mapParams.containsKey("Destination")) {
            if (!mapParams.get("Destination").equals("Any")) {
                if (!mapParams.get("Destination").equals((String) runParams.get("Destination"))) {
                    return false;
                }
            }
        }

        if (mapParams.containsKey("ValidCard")) {
            Card moved = (Card) runParams.get("Card");
            if (!moved.isValidCard(mapParams.get("ValidCard").split(","), hostCard.getController(), hostCard)) {
                return false;
            }
        }

        return true;
    }

    /** {@inheritDoc} */
    @Override
    public Trigger getCopy() {
        Trigger copy = new Trigger_ChangesZone(mapParams, hostCard);
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
