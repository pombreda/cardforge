package forge.card.trigger;

import forge.Card;
import forge.card.spellability.SpellAbility;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 6/23/11
 * Time: 10:59 AM
 * To change this template use File | Settings | File Templates.
 */
public class Trigger_BecomesTarget extends Trigger {

    public Trigger_BecomesTarget(HashMap<String, String> params, Card host) {
        super(params, host);
    }


    @Override
    public boolean performTest(HashMap<String,Object> runParams)
    {
        if(mapParams.containsKey("SourceType"))
        {
            SpellAbility sa = (SpellAbility)runParams.get("SourceSA");
            if(mapParams.get("SourceType").equalsIgnoreCase("spell"))
            {
                if(!sa.isSpell())
                {
                    return false;
                }
            }
            else if(mapParams.get("SourceType").equalsIgnoreCase("ability"))
            {
                if(!sa.isAbility())
                {
                    return false;
                }
            }
        }
        if(mapParams.containsKey("ValidSource"))
        {
            if(!matchesValid(runParams.get("Source"),mapParams.get("ValidSource").split(","),hostCard))
            {
                return false;
            }
        }
        if(mapParams.containsKey("ValidTarget"))
        {
            if(!matchesValid(runParams.get("Target"),mapParams.get("ValidTarget").split(","),hostCard))
            {
                return false;
            }
        }

        return true;
    }

    @Override
    public Trigger getCopy()
    {
        Trigger copy = new Trigger_BecomesTarget(mapParams,hostCard);
		if(overridingAbility != null)
		{
			copy.setOverridingAbility(overridingAbility);
		}
		copy.setName(name);
        copy.setID(ID);

		return copy;
    }

    @Override
    public void setTriggeringObjects(SpellAbility sa)
    {
        sa.setTriggeringObject("Source",runParams.get("Source"));
        sa.setTriggeringObject("Target",runParams.get("Target"));
    }
}
