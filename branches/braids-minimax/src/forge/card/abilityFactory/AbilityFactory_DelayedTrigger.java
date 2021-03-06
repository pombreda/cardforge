package forge.card.abilityFactory;

import forge.AllZone;
import forge.card.spellability.Ability_Sub;
import forge.card.spellability.SpellAbility;
import forge.card.trigger.Trigger;
import forge.card.trigger.TriggerHandler;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 5/18/11
 * Time: 8:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class AbilityFactory_DelayedTrigger {
    private static AbilityFactory tempCreator = new AbilityFactory();

    public static Ability_Sub getDrawback(final AbilityFactory AF)
    {
        final Ability_Sub drawback = new Ability_Sub(AF.getHostCard(),AF.getAbTgt()) {
			private static final long serialVersionUID = 6192972525033429820L;

			@Override
            public boolean chkAI_Drawback() {
                return doChkAI_Drawback(AF,this);  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public boolean doTrigger(boolean mandatory) {
                return doTriggerAI(AF,this);  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void resolve() {
                doResolve(AF,this);
            }
        };

        return drawback;
    }

    private static boolean doChkAI_Drawback(final AbilityFactory AF, final SpellAbility SA)
    {
    	HashMap<String,String> params = AF.getMapParams();
        String svarName = params.get("Execute");
        SpellAbility trigsa = tempCreator.getAbility(AF.getHostCard().getSVar(svarName), AF.getHostCard());

        if(trigsa instanceof Ability_Sub)
        {
            return ((Ability_Sub)trigsa).chkAI_Drawback();
        }
        else
        {
            return trigsa.canPlayAI();
        }
    }

    private static boolean doTriggerAI(final AbilityFactory AF,final SpellAbility SA)
    {
    	HashMap<String,String> params = AF.getMapParams();
        String svarName = params.get("Execute");
        SpellAbility trigsa = tempCreator.getAbility(AF.getHostCard().getSVar(svarName),AF.getHostCard());

        if(!params.containsKey("OptionalDecider"))
        {
                return trigsa.doTrigger(true);
        }
        else
        {
            return trigsa.doTrigger(!params.get("OptionalDecider").equals("You"));
        }
    }

    private static void doResolve(AbilityFactory AF,SpellAbility SA)
    {
        HashMap<String,String> mapParams = AF.getMapParams();

        if(mapParams.containsKey("Cost"))
            mapParams.remove("Cost");

        if(mapParams.containsKey("SpellDescription"))
        {
            mapParams.put("TriggerDescription",mapParams.get("SpellDescription"));
            mapParams.remove("SpellDescription");
        }

        Trigger delTrig = TriggerHandler.parseTrigger(mapParams,AF.getHostCard());

        AllZone.getTriggerHandler().registerDelayedTrigger(delTrig);
    }
}
