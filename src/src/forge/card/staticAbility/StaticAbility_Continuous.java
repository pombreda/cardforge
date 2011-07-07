package forge.card.staticAbility;

import java.util.HashMap;

import forge.AllZone;
import forge.AllZoneUtil;
import forge.Card;
import forge.CardList;
import forge.Player;
import forge.StaticEffect;
import forge.card.abilityFactory.AbilityFactory;
import forge.card.cardFactory.CardFactoryUtil;
import forge.card.spellability.SpellAbility;

public class StaticAbility_Continuous {
	
	public static void applyContinuousAbility(StaticAbility stAb) {
		HashMap<String, String> params = stAb.getMapParams();
		Card hostCard = stAb.getHostCard();
		Player controller = hostCard.getController();
		
    	//AllZone.getGameAction().destroy(hostCard);
		
		StaticEffect se = new StaticEffect();
		
		CardList affectedCards = AllZoneUtil.getCardsInPlay();
		
		if (params.containsKey("AffectedZone"))
			affectedCards = AllZoneUtil.getCardsInZone(params.get("AffectedZone"));
		
		if (params.containsKey("Affected"))
			affectedCards = affectedCards.getValidCards(params.get("Affected").split(","), controller, hostCard);
		
		se.setAffectedCards(affectedCards);
		se.setParams(params);
		AllZone.getStaticEffects().addStaticEffect(se);
		
		int powerBonus = 0;
		int toughnessBonus = 0;
		String addKeywords[] = null;
		String addAbilities[] = null;
		String addSVars[] = null;
		
		if (params.containsKey("AddPower")) {
			if (params.get("AddPower").equals("X")) {
				powerBonus = CardFactoryUtil.xCount(hostCard, hostCard.getSVar("X").split("\\$")[1]);
				se.setXValue(powerBonus);
			}
			else if (params.get("AddPower").equals("Y")) {
				powerBonus = CardFactoryUtil.xCount(hostCard, hostCard.getSVar("Y").split("\\$")[1]);
				se.setYValue(powerBonus);
			}
			else powerBonus = Integer.valueOf(params.get("AddPower"));
		}
		
		if (params.containsKey("AddToughness")) {
			if (params.get("AddToughness").equals("X")) {
				toughnessBonus = CardFactoryUtil.xCount(hostCard, hostCard.getSVar("X").split("\\$")[1]);
				se.setXValue(toughnessBonus);
			}
			else if (params.get("AddToughness").equals("Y")) {
				toughnessBonus = CardFactoryUtil.xCount(hostCard, hostCard.getSVar("Y").split("\\$")[1]);
				se.setYValue(toughnessBonus);
			}
			else toughnessBonus = Integer.valueOf(params.get("AddToughness"));
		}
		
		if (params.containsKey("AddKeyword"))
			addKeywords = params.get("AddKeyword").split(" & ");
		
		if (params.containsKey("AddAbility")) {
			String sVars[] = params.get("AddAbility").split(" & ");
			for(int i = 0 ; i < sVars.length ; i++)
				sVars[i] = hostCard.getSVar(sVars[i]);
			addAbilities = sVars;
		}
		
		if (params.containsKey("AddSVar"))
			addSVars = params.get("AddSVar").split(" & ");
			
		for (int i = 0; i < affectedCards.size(); i++) {
            Card affectedCard = affectedCards.get(i);
            
            //add P/T bonus
            affectedCard.addSemiPermanentAttackBoost(powerBonus);
            affectedCard.addSemiPermanentDefenseBoost(toughnessBonus);
            
            //add keywords
            if (addKeywords != null)
            	for (String keyword : addKeywords)
            		affectedCard.addExtrinsicKeyword(keyword);
            
            //add abilities
            if (addAbilities != null)
            	for (String abilty : addAbilities)
                    if (abilty.startsWith("AB")) { // grant the ability
                        AbilityFactory AF = new AbilityFactory();
                        SpellAbility sa = AF.getAbility(abilty, affectedCard);
                        sa.setType("Temporary");
                        affectedCard.addSpellAbility(sa);
                    }
            
            //add SVars
            if (addSVars != null)
            	for (String sVar : addSVars)
            		affectedCard.setSVar(sVar, hostCard.getSVar(sVar));
		}
	}

}
