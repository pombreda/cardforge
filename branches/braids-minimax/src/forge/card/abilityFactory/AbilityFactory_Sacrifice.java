package forge.card.abilityFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import forge.AllZone;
import forge.AllZoneUtil;
import forge.Card;
import forge.CardList;
import forge.ComputerUtil;
import forge.Constant;
import forge.MyRandom;
import forge.Player;
import forge.card.cardFactory.CardFactoryUtil;
import forge.card.spellability.Ability_Activated;
import forge.card.spellability.Ability_Sub;
import forge.card.spellability.Cost;
import forge.card.spellability.Spell;
import forge.card.spellability.SpellAbility;
import forge.card.spellability.Target;

public class AbilityFactory_Sacrifice {
	//**************************************************************
	// *************************** Sacrifice ***********************
	//**************************************************************
	
	public static SpellAbility createAbilitySacrifice(final AbilityFactory AF){
		final SpellAbility abSacrifice = new Ability_Activated(AF.getHostCard(), AF.getAbCost(), AF.getAbTgt()){
			private static final long serialVersionUID = -1933592438783630254L;
			
			final AbilityFactory af = AF;
			
			public boolean canPlayAI()
			{
				return sacrificeCanPlayAI(af, this);
			}
			
			@Override
			public void resolve() {
				sacrificeResolve(af, this);
			}
			
			public String getStackDescription(){
				return sacrificeDescription(af, this);
			}

			@Override
			public boolean doTrigger(boolean mandatory) {
				return sacrificeTriggerAI(af, this, mandatory);
			}
		};
		return abSacrifice;
	}
	
	public static SpellAbility createSpellSacrifice(final AbilityFactory AF){
		final SpellAbility spSacrifice = new Spell(AF.getHostCard(), AF.getAbCost(), AF.getAbTgt()){
			private static final long serialVersionUID = -5141246507533353605L;
			
			final AbilityFactory af = AF;
			
			public boolean canPlayAI()
			{
				return sacrificeCanPlayAI(af, this);
			}
			
			@Override
			public void resolve() {
				sacrificeResolve(af, this);
			}
			
			public String getStackDescription(){
				return sacrificeDescription(af, this);
			}
		};
		return spSacrifice;
	}
	
	public static SpellAbility createDrawbackSacrifice(final AbilityFactory AF){
		final SpellAbility dbSacrifice = new Ability_Sub(AF.getHostCard(), AF.getAbTgt()){
			private static final long serialVersionUID = -5141246507533353605L;
			
			final AbilityFactory af = AF;
			
			@Override
			public void resolve() {
				sacrificeResolve(af, this);
			}

			@Override
			public boolean chkAI_Drawback() {
				return sacrificePlayDrawbackAI(af, this);
			}
			
			public String getStackDescription(){
				return sacrificeDescription(af, this);
			}

			@Override
			public boolean doTrigger(boolean mandatory) {
				return sacrificeTriggerAI(af, this, mandatory);
			}
		};
		return dbSacrifice;
	}
	
	public static String sacrificeDescription(final AbilityFactory af, SpellAbility sa) {
		HashMap<String,String> params = af.getMapParams();
		StringBuilder sb = new StringBuilder();
		
		if (sa instanceof Ability_Sub)
			sb.append(" ");
		else
			sb.append(sa.getSourceCard().getName()).append(" - ");
		
		String conditionDesc = params.get("ConditionDescription");
		if (conditionDesc != null)
			sb.append(conditionDesc).append(" ");
		
		Target tgt = af.getAbTgt();
		ArrayList<Player> tgts;
		if (tgt != null)
			tgts = tgt.getTargetPlayers();
		else
			tgts = AbilityFactory.getDefinedPlayers(sa.getSourceCard(), params.get("Defined"), sa);
		
		String valid = params.get("SacValid");
		if (valid == null)
			valid = "Self";
		
		String num = params.get("Amount");
		num = (num == null) ? "1" : num;
		int amount = AbilityFactory.calculateAmount(sa.getSourceCard(), num, sa);
		
		if (valid.equals("Self"))
			sb.append("Sacrifice ").append(sa.getSourceCard().toString());
		else if(valid.equals("Card.AttachedBy")) {
			Card toSac = sa.getSourceCard().getEnchantingCard();
			sb.append(toSac.getController()).append(" sacrifices ").append(toSac).append(".");
		}
		else{
			for(Player p : tgts)
				sb.append(p.getName()).append(" ");
			
			String msg = params.get("SacMessage");
			if (msg == null)
				msg = valid;
			
			sb.append("Sacrifices ").append(amount).append(" ").append(msg).append(".");
		}
		
		Ability_Sub abSub = sa.getSubAbility();
        if (abSub != null)
        	sb.append(abSub.getStackDescription());
		
		return sb.toString();
	}
	
	public static boolean sacrificeCanPlayAI(final AbilityFactory af, SpellAbility sa){
		
		HashMap<String,String> params = af.getMapParams();
		boolean chance = sacrificeTgtAI(af, sa);

		// Some additional checks based on what is being sacrificed, and who is sacrificing
		Target tgt = af.getAbTgt();
		if (tgt != null){
			String valid = params.get("SacValid");
			String num = params.get("Amount");
			num = (num == null) ? "1" : num;
			int amount = AbilityFactory.calculateAmount(sa.getSourceCard(), num, sa);		
			
			CardList list = AllZoneUtil.getPlayerCardsInPlay(AllZone.getHumanPlayer());
			list = list.getValidCards(valid.split(","), sa.getActivatingPlayer(), sa.getSourceCard());
			
			if (list.size() == 0)
				return false;
			
			Card source = sa.getSourceCard();
			if (num.equals("X") && source.getSVar(num).equals("Count$xPaid")){
				// Set PayX here to maximum value.
				int xPay = Math.min(ComputerUtil.determineLeftoverMana(sa), amount);
				source.setSVar("PayX", Integer.toString(xPay));
			}
			
			int half = amount / 2 + amount % 2;	// Half of amount rounded up
			
			// If the Human has at least half rounded up of the amount to be sacrificed, cast the spell
			if (list.size() < half)
				return false;
		}
		
		Ability_Sub subAb = sa.getSubAbility();
		if (subAb != null)
			chance &= subAb.chkAI_Drawback();
		
		return chance;
	}
	
	public static boolean sacrificePlayDrawbackAI(final AbilityFactory af, SpellAbility sa){
		// AI should only activate this during Human's turn
		boolean chance = sacrificeTgtAI(af, sa);

		// TODO: restrict the subAbility a bit
			
		Ability_Sub subAb = sa.getSubAbility();
		if (subAb != null)
			chance &= subAb.chkAI_Drawback();
		
		return chance;
	}
	
	public static boolean sacrificeTriggerAI(final AbilityFactory af, SpellAbility sa, boolean mandatory){
		if (!ComputerUtil.canPayCost(sa))	// If there is a cost payment
			return false;
		
		// AI should only activate this during Human's turn
		boolean chance = sacrificeTgtAI(af, sa);

		// Improve AI for triggers. If source is a creature with:
		// When ETB, sacrifice a creature. Check to see if the AI has something to sacrifice
		
		// Eventually, we can call the trigger of ETB abilities with not mandatory as part of the checks to cast something
		
		
		Ability_Sub subAb = sa.getSubAbility();
		if (subAb != null)
			chance &= subAb.chkAI_Drawback();
		
		return chance || mandatory;
	}
	
	public static boolean sacrificeTgtAI(AbilityFactory af, SpellAbility sa){
		
		HashMap<String,String> params = af.getMapParams();
		Card card = sa.getSourceCard();
		Target tgt = af.getAbTgt();
		
		if (tgt != null){
			tgt.resetTargets();
			if (AllZone.getHumanPlayer().canTarget(sa.getSourceCard()))
				tgt.addTarget(AllZone.getHumanPlayer());
			else
				return false;
		}
		else{
			String defined = params.get("Defined");
			if (defined == null){
				// Self Sacrifice. 
			}
			else if (defined.equals("Each")){
			// If Sacrifice hits both players:
			// Only cast it if Human has the full amount of valid
			// Only cast it if AI doesn't have the full amount of Valid
			// TODO: Cast if the type is favorable: my "worst" valid is worse than his "worst" valid
				String valid = params.get("SacValid");
				String num = params.containsKey("Amount") ? params.get("Amount") : "1";
				int amount = AbilityFactory.calculateAmount(card, num, sa);
				
				Card source = sa.getSourceCard();
				if (num.equals("X") && source.getSVar(num).equals("Count$xPaid")){
					// Set PayX here to maximum value.
					amount = Math.min(ComputerUtil.determineLeftoverMana(sa), amount);
				}
				
				CardList humanList = AllZoneUtil.getPlayerCardsInPlay(AllZone.getHumanPlayer());
				humanList = humanList.getValidCards(valid.split(","), sa.getActivatingPlayer(), sa.getSourceCard());
				CardList computerList = AllZoneUtil.getPlayerCardsInPlay(AllZone.getComputerPlayer());
				computerList = computerList.getValidCards(valid.split(","), sa.getActivatingPlayer(), sa.getSourceCard());
				
				//Since all of the cards have remAIDeck:True, I enabled 1 for 1 (or X for X) trades for special decks
				if(humanList.size() < amount /*|| computerList.size() >= amount */) return false;
			}
		}
		
		return true;
	}
	
	public static void sacrificeResolve(final AbilityFactory af, final SpellAbility sa){
		HashMap<String,String> params = af.getMapParams();
		Card card = sa.getSourceCard();
		
		// Expand Sacrifice keyword here depending on what we need out of it.
		String num = params.containsKey("Amount") ? params.get("Amount") : "1";
		int amount = AbilityFactory.calculateAmount(card, num, sa);
		
		Target tgt = af.getAbTgt();
		ArrayList<Player> tgts;
		if (tgt != null)
			tgts = tgt.getTargetPlayers();
		else
			tgts = AbilityFactory.getDefinedPlayers(sa.getSourceCard(), params.get("Defined"), sa);
		
		String valid = params.get("SacValid");
		if (valid == null)
			valid = "Self";
		
		String msg = params.get("SacMessage");
		if (msg == null)
			msg = valid;
		
		msg = "Sacrifice a " + msg;

		if (valid.equals("Self")){
			if (AllZone.getZone(sa.getSourceCard()).is(Constant.Zone.Battlefield))
				AllZone.getGameAction().sacrifice(sa.getSourceCard());
		}
		//TODO - maybe this can be done smarter...
		else if(valid.equals("Card.AttachedBy")) {
			Card toSac = sa.getSourceCard().getEnchantingCard();
			if (AllZone.getZone(sa.getSourceCard()).is(Constant.Zone.Battlefield) && AllZoneUtil.isCardInPlay(toSac)) {
				AllZone.getGameAction().sacrifice(toSac);
			}
		}
		else if( valid.equals("TriggeredCard")) {
			Card equipee = (Card)sa.getTriggeringObject("Card");
			if(tgts.contains(card.getController()) && AllZoneUtil.isCardInPlay(equipee)) {
				AllZone.getGameAction().sacrifice(equipee);
			}
		}
		else{
			for(Player p : tgts){
	
				if (p.isComputer())
					sacrificeAI(p, amount, valid, sa);
				else
					sacrificeHuman(p, amount, valid, sa, msg);
			}
		}
	}
	
	
	private static void sacrificeAI(Player p, int amount, String valid, SpellAbility sa){
		CardList list = AllZoneUtil.getPlayerCardsInPlay(p);
		list = list.getValidCards(valid.split(","), sa.getActivatingPlayer(), sa.getSourceCard());
		
		ComputerUtil.sacrificePermanents(amount, list);
	}
	
	private static void sacrificeHuman(Player p, int amount, String valid, SpellAbility sa, String message){
		CardList list = AllZoneUtil.getPlayerCardsInPlay(p);
		list = list.getValidCards(valid.split(","), sa.getActivatingPlayer(), sa.getSourceCard());
		
		// TODO: Wait for Input to finish before moving on with the rest of Resolution
		AllZone.getInputControl().setInput(CardFactoryUtil.input_sacrificePermanentsFromList(amount, list, message), true);
	}
	
	
	//**************************************************************
	// *************************** SacrificeAll ***********************
	//**************************************************************
	
	public static SpellAbility createAbilitySacrificeAll(final AbilityFactory AF){
		final SpellAbility abSacrifice = new Ability_Activated(AF.getHostCard(), AF.getAbCost(), AF.getAbTgt()){
			private static final long serialVersionUID = -1933592438783630254L;
			
			final AbilityFactory af = AF;
			
			public boolean canPlayAI()
			{
				return sacrificeAllCanPlayAI(af, this);
			}
			
			@Override
			public void resolve() {
				sacrificeAllResolve(af, this);
			}
			
			public String getStackDescription(){
				return sacrificeAllStackDescription(af, this);
			}

			@Override
			public boolean doTrigger(boolean mandatory) {
				return sacrificeAllCanPlayAI(af, this);
			}
		};
		return abSacrifice;
	}
	
	public static SpellAbility createSpellSacrificeAll(final AbilityFactory AF){
		final SpellAbility spSacrifice = new Spell(AF.getHostCard(), AF.getAbCost(), AF.getAbTgt()){
			private static final long serialVersionUID = -5141246507533353605L;
			
			final AbilityFactory af = AF;
			
			public boolean canPlayAI()
			{
				return sacrificeAllCanPlayAI(af, this);
			}
			
			@Override
			public void resolve() {
				sacrificeAllResolve(af, this);
			}
			
			public String getStackDescription(){
				return sacrificeAllStackDescription(af, this);
			}
		};
		return spSacrifice;
	}
	
	public static SpellAbility createDrawbackSacrificeAll(final AbilityFactory AF){
		final SpellAbility dbSacrifice = new Ability_Sub(AF.getHostCard(), AF.getAbTgt()){
			private static final long serialVersionUID = -5141246507533353605L;
			
			final AbilityFactory af = AF;
			
			@Override
			public void resolve() {
				sacrificeAllResolve(af, this);
			}

			@Override
			public boolean chkAI_Drawback() {
				return true;
			}
			
			public String getStackDescription(){
				return sacrificeAllStackDescription(af, this);
			}

			@Override
			public boolean doTrigger(boolean mandatory) {
				return sacrificeAllCanPlayAI(af, this);
			}
		};
		return dbSacrifice;
	}
	
	public static String sacrificeAllStackDescription(final AbilityFactory af, SpellAbility sa){
		// when getStackDesc is called, just build exactly what is happening

		StringBuilder sb = new StringBuilder();
		String name = af.getHostCard().getName();
		HashMap<String,String> params = af.getMapParams();

		String conditionDesc = params.get("ConditionDescription");
		if (conditionDesc != null)
			sb.append(conditionDesc).append(" ");

		ArrayList<Card> tgtCards;

		Target tgt = af.getAbTgt();
		if (tgt != null)
			tgtCards = tgt.getTargetCards();
		else{
			tgtCards = new ArrayList<Card>(); 
			tgtCards.add(sa.getSourceCard());
		}

		sb.append(name).append(" - Sacrifice permanents");

		Ability_Sub abSub = sa.getSubAbility();
		if (abSub != null) {
			sb.append(abSub.getStackDescription());
		}

		return sb.toString();
	}
	
	public static boolean sacrificeAllCanPlayAI(final AbilityFactory af, final SpellAbility sa){
		// AI needs to be expanded, since this function can be pretty complex based on what the expected targets could be
		Random r = MyRandom.random;
		Cost abCost = sa.getPayCosts();
		final Card source = sa.getSourceCard();
		final HashMap<String,String> params = af.getMapParams();
		String Valid = "";

		if(params.containsKey("ValidCards")) 
			Valid = params.get("ValidCards");

		if (Valid.contains("X") && source.getSVar("X").equals("Count$xPaid")){
			// Set PayX here to maximum value.
			int xPay = ComputerUtil.determineLeftoverMana(sa);
			source.setSVar("PayX", Integer.toString(xPay));
			Valid = Valid.replace("X", Integer.toString(xPay));
		}

		CardList humanlist = AllZoneUtil.getPlayerCardsInPlay(AllZone.getHumanPlayer());
		CardList computerlist = AllZoneUtil.getPlayerCardsInPlay(AllZone.getComputerPlayer());

		humanlist = humanlist.getValidCards(Valid.split(","), source.getController(), source);
		computerlist = computerlist.getValidCards(Valid.split(","), source.getController(), source);

		if (abCost != null){
			// AI currently disabled for some costs
			if (abCost.getSacCost()){ 
				//OK
			}
			if (abCost.getLifeCost()){
				if (AllZone.getComputerPlayer().getLife() - abCost.getLifeAmount() < 4)
					return false;
			}
			if (abCost.getDiscardCost()) ;//OK

			if (abCost.getSubCounter()){
				// OK
			}
		}

		if (!ComputerUtil.canPayCost(sa))
			return false;

		// prevent run-away activations - first time will always return true
		boolean chance = r.nextFloat() <= Math.pow(.6667, source.getAbilityUsed());

		// if only creatures are affected evaluate both lists and pass only if human creatures are more valuable
		if (humanlist.getNotType("Creature").size() == 0 && computerlist.getNotType("Creature").size() == 0) {
			if(CardFactoryUtil.evaluateCreatureList(computerlist) + 200 >= CardFactoryUtil.evaluateCreatureList(humanlist))
				return false;
		}//only lands involved
		else if (humanlist.getNotType("Land").size() == 0 && computerlist.getNotType("Land").size() == 0) {
			if(CardFactoryUtil.evaluatePermanentList(computerlist) + 1 >= CardFactoryUtil.evaluatePermanentList(humanlist))
				return false;
		} // otherwise evaluate both lists by CMC and pass only if human permanents are more valuable
		else if(CardFactoryUtil.evaluatePermanentList(computerlist) + 3 >= CardFactoryUtil.evaluatePermanentList(humanlist))
			return false;

		Ability_Sub subAb = sa.getSubAbility();
		if (subAb != null)
			chance &= subAb.chkAI_Drawback();

		return ((r.nextFloat() < .9667) && chance);
	}
	
	public static void sacrificeAllResolve(final AbilityFactory af, final SpellAbility sa){
		HashMap<String,String> params = af.getMapParams();

		Card card = sa.getSourceCard();
		
		String Valid = "";
		
		if(params.containsKey("ValidCards")) 
			Valid = params.get("ValidCards");
		
		// Ugh. If calculateAmount needs to be called with DestroyAll it _needs_ to use the X variable
		// We really need a better solution to this
		if (Valid.contains("X"))	
			Valid = Valid.replace("X", Integer.toString(AbilityFactory.calculateAmount(card, "X", sa)));
		
		CardList list = AllZoneUtil.getCardsInPlay();
		
		boolean remSacrificed = params.containsKey("RememberSacrificed");
		if (remSacrificed)
			card.clearRemembered();
		
		list = list.getValidCards(Valid.split(","), card.getController(), card);
		
 		for(int i = 0; i < list.size(); i++) 
 			if(AllZone.getGameAction().sacrifice(list.get(i)) && remSacrificed)
				card.addRemembered(list.get(i));
     }
	
}