package forge.card.abilityFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import forge.AllZone;
import forge.AllZoneUtil;
import forge.Card;
import forge.CardList;
import forge.Command;
import forge.ComputerUtil;
import forge.Counters;
import forge.MyRandom;
import forge.Player;

import forge.card.cardFactory.CardFactoryUtil;
import forge.card.spellability.Ability_Sub;
import forge.card.spellability.Cost;
import forge.card.spellability.Spell;
import forge.card.spellability.SpellAbility;
import forge.card.spellability.Spell_Permanent;
import forge.card.spellability.Target;

public class AbilityFactory_Attach {
	public static SpellAbility createSpellAttach(final AbilityFactory AF){
		// There are two types of Spell Attachments: Auras and Instants/Sorceries
		// Auras generally target what that card will attach to
		// I/S generally target the Attacher and the Attachee
		SpellAbility spAttach = null;
		if (AF.getHostCard().isAura()){
			// The 4th parameter is to resolve an issue with SetDescription in default Spell_Permanent constructor
			spAttach = new Spell_Permanent(AF.getHostCard(), AF.getAbCost(), AF.getAbTgt(), false){
				private static final long serialVersionUID = 6631124959690157874L;
				
				final AbilityFactory af = AF;
			
				@Override
				public String getStackDescription(){
				// when getStackDesc is called, just build exactly what is happening
					return attachStackDescription(af, this);
				}
				
				public boolean canPlayAI(){
					return attachCanPlayAI(af, this);
				}
				
				@Override
				public void resolve() {
					// The Spell_Permanent (Auras) version of this AF needs to move the card into play before Attaching
					Card c = AllZone.getGameAction().moveToPlay(getSourceCard());
					this.setSourceCard(c);
					attachResolve(af, this);
				}
			};
		}
		else{
			// This is here to be complete, however there's only a few cards that use it
			// And the Targeting system can't really handle them at this time (11/7/1)
			spAttach = new Spell(AF.getHostCard(), AF.getAbCost(), AF.getAbTgt()){
				private static final long serialVersionUID = 6631124959690157874L;
				
				final AbilityFactory af = AF;
			
				@Override
				public String getStackDescription(){
				// when getStackDesc is called, just build exactly what is happening
					return attachStackDescription(af, this);
				}
				
				public boolean canPlayAI(){
					return attachCanPlayAI(af, this);
				}
				
				@Override
				public void resolve() {
					attachResolve(af, this);
				}
			};
		}
		return spAttach;
	}

	// Attach Ability
	public static SpellAbility createAbilityAttach(final AbilityFactory AF){
		// placeholder for Equip and other similar cards
		return null;
	}
	
	// Attach Drawback
	public static SpellAbility createDrawbackAttach(final AbilityFactory AF){
		// placeholder for DBs that might attach
		return null;
	}
	
	public static String attachStackDescription(AbilityFactory af, SpellAbility sa){
		StringBuilder sb = new StringBuilder();

		if (!(sa instanceof Ability_Sub))
			sb.append(sa.getSourceCard().getName()).append(" - ");
		else
			sb.append(" ");
		
		String conditionDesc = af.getMapParams().get("ConditionDescription");
		if (conditionDesc != null)
			sb.append(conditionDesc).append(" ");

		sb.append(" Attach to ");
		
		ArrayList<Object> targets;

		// Should never allow more than one Attachment per card
		Target tgt = af.getAbTgt();
		if (tgt != null)
			targets = tgt.getTargets();
		else
			targets = AbilityFactory.getDefinedObjects(sa.getSourceCard(), af.getMapParams().get("Defined"), sa);
		
		for(Object o : targets)
			sb.append(o).append(" ");

		Ability_Sub abSub = sa.getSubAbility();
		if (abSub != null)
			sb.append(abSub.getStackDescription());

		return sb.toString();
	}
	
	public static boolean attachPreference(AbilityFactory af, SpellAbility sa, HashMap<String,String> params, Target tgt, boolean mandatory){
		Object o;
		if (tgt.canTgtPlayer())
			o = attachToPlayerAIPreferences(af, sa, mandatory);

		else
			o = attachToCardAIPreferences(af, sa, params, mandatory);

		if (o == null)
			return false;
		
		tgt.addTarget(o);
		return true;
	}
	
	public static Card attachToCardAIPreferences(AbilityFactory af, final SpellAbility sa, HashMap<String,String> params, boolean mandatory){
		Target tgt = sa.getTarget();
		Card attachSource = sa.getSourceCard();
		// TODO AttachSource is currently set for the Source of the Spell, but at some point can support attaching a different card
		
		CardList list = AllZoneUtil.getCardsInZone(tgt.getZone());
		list = list.getValidCards(tgt.getValidTgts(), sa.getActivatingPlayer(), sa.getSourceCard());
		
		if (list.size() == 0)
			return null;
		
		Card c = null;
		String logic = params.get("AILogic");
		
		if ("GainControl".equals(logic))
			c = attachAIControlPreference(sa, list, mandatory, attachSource);
		
		// TODO: Pump and ChangeType definitely don't have enough AI yet, Curse might but better wait on converting
		
		else if ("Curse".equals(logic))
			c = attachAICursePreference(sa, list, mandatory, attachSource);
		else if ("Pump".equals(logic))
			c = attachAIPumpPreference(sa, list, mandatory, attachSource);
		else if ("ChangeType".equals(logic))	// Evil Presence, Spreading Seas
			c = attachAIChangeTypePreference(sa, list, mandatory, attachSource);
		// Does KeepTapped need it's own list? Might be able to lump with Curse
		
		if (c == null && mandatory){
			list.shuffle();
			c = list.get(0);
		}
		
		return c;
	}
	
	public static Card attachAIControlPreference(final SpellAbility sa, CardList list, boolean mandatory, Card attachSource){
		// AI For choosing a Card to Gain Control of. 

		// Filter list by Controller (only steal cards I don't already control)
		list = list.getController(AllZone.getHumanPlayer());
		
		if (list.size() == 0)
			return null;
		
		if (sa.getTarget().canTgtPermanent()){
			// If can target all Permanents, and Life isn't in eminent danger, grab Planeswalker first, then Creature
			// if Life < 5 grab Creature first, then Planeswalker. Lands, Enchantments and Artifacts are probably "not good enough"
			
		}
		
		Card c = CardFactoryUtil.AI_getBest(list);
        
        if (mandatory)
        	return c;
        
        // TODO: If Not Mandatory, make sure the card is "good enough"
        if (c.isCreature()){
	        int eval = CardFactoryUtil.evaluateCreature(c);
	        if (eval < 160 && (eval < 130 || AllZone.getComputerPlayer().getLife() > 5))
	        	return null;
        }

		return c;
	}
	
	public static Card attachAIPumpPreference(final SpellAbility sa, CardList list, boolean mandatory, Card attachSource){
		// AI For choosing a Card to Pump 
		
		// Filter list by Controller (only steal cards I don't already control)
		list = list.getController(AllZone.getComputerPlayer());
		
		if (list.size() == 0)
			return null;
		
		Card c = null;
		
		CardList magnetList = null;
		String stCheck = null;
		
		if (attachSource.isAura()){
			stCheck = "stPumpEnchanted";
			magnetList = list.getEnchantMagnets();
		}
		else if (attachSource.isEquipment()){
			stCheck = "stPumpEquipped";
			magnetList = list.getEquipMagnets();
		}
		
		// TODO: 
		// determine Power Bonus
		// determine Toughness Bonus
		// determine Granted Keywords
		
		if (magnetList != null && magnetList.size() > 0){
			// If negative toughness won't kill it, attach to a Magnet
		}
		
		// If Aura grants only Keywords, don't Stack unstackable keywords
		
		// Otherwise choose an appropriately "good enough" creature
		// Try not to over Aura-tize cards
		// 
		
		if (c == null)
			return null;
		
        if (mandatory)
        	return c;
        
        // If Not Mandatory, make sure the card is "good enough" (TODO Expand)
        if (c.isCreature()){
	        int eval = CardFactoryUtil.evaluateCreature(c);
	        if (eval < 160 && (eval < 130 || AllZone.getComputerPlayer().getLife() > 5))
	        	return null;
        }

		return c;
	}
	
	public static Card attachAICursePreference(final SpellAbility sa, CardList list, boolean mandatory, Card attachSource){
		// AI For choosing a Card to Curse of. 
		
		// Filter list by Controller (only steal cards I don't already control)
		list = list.getController(AllZone.getHumanPlayer());
		
		if (list.size() == 0)
			return null;
		
		// TODO Probably should be more specific then just getBest here
		
		Card c = CardFactoryUtil.AI_getBest(list);
		
        if (mandatory)
        	return c;
        
        // TODO: If Not Mandatory, make sure the card is "good enough"
        if (c.isCreature()){
	        int eval = CardFactoryUtil.evaluateCreature(c);
	        if (eval < 160 && (eval < 130 || AllZone.getComputerPlayer().getLife() > 5))
	        	return null;
        }

		return c;
	}
	
	public static Card attachAIChangeTypePreference(final SpellAbility sa, CardList list, boolean mandatory, Card attachSource){
		// AI For Cards like Evil Presence or Spreading Seas
		Card c = null;
		
		// Filter list by Controller (only steal cards I don't already control)
		list = list.getController(AllZone.getHumanPlayer());
		
		if (list.size() == 0)
			return null;
				
		// TODO: Port over some of the existing code, but rewrite most of it.
		// Filter out Basic Lands that have the same type as the changing type
		// Ultimately, these spells need to be used to reduce mana base of a color. So it might be better to choose a Basic over a Nonbasic
        
        if (mandatory)
        	return c;

		return c;
	}
	
	// Todo: Does RemainTapped need its own SubAttach AF?
	
	public static Player attachToPlayerAIPreferences(AbilityFactory af, final SpellAbility sa, boolean mandatory){
		Target tgt = sa.getTarget();
		Player p;
		if (tgt.canOnlyTgtOpponent()){
			// If can Only Target Opponent, do so.
			p = AllZone.getHumanPlayer();
			if (p.canTarget(sa.getSourceCard()))
				return p;
			else
				return null;
		}
		
		if (af.isCurse())
			p = AllZone.getHumanPlayer();
		else
			p = AllZone.getComputerPlayer();

		if (p.canTarget(sa.getSourceCard()))
			return p;
		
		if (!mandatory)
			return null;
		
		p = p.getOpponent();
		if (p.canTarget(sa.getSourceCard()))
			return p;
		
		return null;
	}
	
	public static boolean attachCanPlayAI(final AbilityFactory af, final SpellAbility sa){
		Random r = MyRandom.random;
		HashMap<String,String> params = af.getMapParams();
		Cost abCost = sa.getPayCosts();
		final Card source = sa.getSourceCard();

		if (abCost != null){
			// No Aura spells have Additional Costs 
			
			// AI currently disabled for these costs
			if (abCost.getSacCost()){

			}
			if (abCost.getLifeCost())	 return false;
			if (abCost.getDiscardCost()) return false;

			if (abCost.getSubCounter()){
				//non +1/+1 counters should be used
				if (abCost.getCounterType().equals(Counters.P1P1)){
					// A card has a 25% chance per counter to be able to pass through here
					// 4+ counters will always pass. 0 counters will never
				int currentNum = source.getCounters(abCost.getCounterType());
				double percent = .25 * (currentNum / abCost.getCounterNum());
				if (percent <= r.nextFloat())
					return false;
				}
			}
		}

		// prevent run-away activations - first time will always return true
		boolean chance = r.nextFloat() <= Math.pow(.6667, source.getAbilityUsed());
		
		// Attach spells always have a target
		Target tgt = sa.getTarget();
		if (tgt != null){
			tgt.resetTargets();
			if (!attachPreference(af, sa, params, tgt, false))
				return false;
		}
		
		if (abCost.getMana().contains("X") && source.getSVar("X").equals("Count$xPaid")){
			// Set PayX here to maximum value. (Endless Scream and Venarian Gold)
			int xPay = ComputerUtil.determineLeftoverMana(sa);
			
			if (xPay == 0)
				return false;
			
			source.setSVar("PayX", Integer.toString(xPay));
		}
		
		chance &= r.nextFloat() <= .6667;
		return chance;
	}
	
	public static boolean attachDoTriggerAI(AbilityFactory af, SpellAbility sa, boolean mandatory){
		if (!ComputerUtil.canPayCost(sa))	// If there is a cost payment it's usually not mandatory
			return false;

		// Check if there are any valid targets
		
		// Now are Valid Targets better than my targets?

		// check SubAbilities DoTrigger?
		Ability_Sub abSub = sa.getSubAbility();
		if (abSub != null)
			return abSub.doTrigger(mandatory);

		return true;
	}
	
	public static void attachResolve(final AbilityFactory af, final SpellAbility sa){
		HashMap<String,String> params = af.getMapParams();
		Card card = sa.getSourceCard();
		
		ArrayList<Object> targets;

		Target tgt = af.getAbTgt();
		if (tgt != null){
			targets = tgt.getTargets();
			// TODO Remove invalid targets (although more likely this will just fizzle earlier)
		}
		else
			targets = AbilityFactory.getDefinedObjects(sa.getSourceCard(), params.get("Defined"), sa);
		
		// If Cast Targets will be checked on the Stack
		for(Object o : targets){
			handleAttachment(card, o, af);
		}
	}
	
	public static void handleAttachment(Card card, Object o, AbilityFactory af){

		if (o instanceof Card){
			Card c = (Card)o;
			if (card.isAura()){
				// Most Auras can enchant permanents, a few can Enchant cards in graveyards
				// Spellweaver Volute, Dance of the Dead, Animate Dead
				// Although honestly, I'm not sure if the three of those could handle being scripted
				boolean gainControl = "GainControl".equals(af.getMapParams().get("AILogic")); 
				handleAura(card, c, gainControl);
			}
			else if (card.isEquipment())
				card.equipCard(c);
			//else if (card.isFortification())
			//	card.fortifyCard(c);
		}
		else if (o instanceof Player){
			// Currently, a few cards can enchant players
			// Psychic Possession, Paradox Haze, Wheel of Sun and Moon
			// Player p = (Player)o;
			//if (card.isAura())
			//	card.enchantPlayer(p);
		}
	}
	
	public static void handleAura(final Card card, final Card tgt, boolean gainControl){
		if (card.isEnchanting()){
			// If this Card is already Enchanting something
			// Need to unenchant it, then clear out the commands
			Card oldEnchanted = card.getEnchantingCard();
			card.removeEnchanting(oldEnchanted);
			card.clearEnchantCommand();
			card.clearUnEnchantCommand();
			card.clearTriggers();	// not sure if cleartriggers is needed?
		}
		
		if (gainControl){
			// Handle GainControl Auras
			final Player[] pl = new Player[1];
			pl[0] = tgt.getController();
			
	        Command onEnchant = new Command() {
				private static final long serialVersionUID = -2519887209491512000L;
	
				public void execute() {
	                if(card.isEnchanting()) {
	                    Card crd = card.getEnchanting().get(0);
	                    pl[0] = crd.getController();
	                    
	                    AllZone.getGameAction().changeController(new CardList(crd), pl[0], card.getController());
	                }
	            }//execute()
	        };//Command
	        
	        Command onUnEnchant = new Command() {
				private static final long serialVersionUID = 3426441132121179288L;
	
				public void execute() {
	                if(card.isEnchanting()) {
	                    Card crd = card.getEnchanting().get(0);
	                    if(AllZoneUtil.isCardInPlay(crd)) {
	                        AllZone.getGameAction().changeController(new CardList(crd), crd.getController(), pl[0]);
	                    }
	                }
	                
	            }//execute()
	        };//Command
	        
	        Command onLeavesPlay = new Command() {
				private static final long serialVersionUID = -639204333673364477L;
	
				public void execute() {
	                if(card.isEnchanting()) {
	                    Card crd = card.getEnchanting().get(0);
	                    card.unEnchantCard(crd);
	                }
	            }
	        };//Command
			
	        // Add Enchant Commands
	        card.addEnchantCommand(onEnchant);
	        card.addUnEnchantCommand(onUnEnchant);
	        card.addLeavesPlayCommand(onLeavesPlay);
		}
		card.enchantCard(tgt);
	}
}
