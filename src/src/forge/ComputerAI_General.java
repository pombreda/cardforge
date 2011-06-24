package forge;
import static forge.error.ErrorViewer.showError;

import java.util.ArrayList;

import com.esotericsoftware.minlog.Log;

import forge.card.cardFactory.CardFactoryUtil;
import forge.card.spellability.SpellAbility;
import forge.card.spellability.Spell_Permanent;


public class ComputerAI_General implements Computer {

    public ComputerAI_General() {

    }
    
    public void main1() {
    	if (!ComputerUtil.chooseLandsToPlay()){
    		if (AllZone.getPhase().getPriorityPlayer().isComputer())
    			stackResponse();
    		return;
    	}
        playCards(Constant.Phase.Main1);
    }//main1()
    
    public void main2() {
    	if (!ComputerUtil.chooseLandsToPlay()){	// in case we can play more lands now, or drew cards since first main phase
    		if (AllZone.getPhase().getPriorityPlayer().isComputer())
    			stackResponse();
    		return;
    	}
        playCards(Constant.Phase.Main2);
   }
    
    private void playCards(final String phase) {
        SpellAbility[] sp = phase.equals(Constant.Phase.Main1)? getMain1():getMain2();
        
        boolean nextPhase = ComputerUtil.playCards(sp);
        
        if(nextPhase) {
        	AllZone.getPhase().passPriority();
        }
    }//playCards()

    private SpellAbility[] getMain1() {
    	//Card list of all cards to consider
    	CardList hand = AllZoneUtil.getPlayerHand(AllZone.getComputerPlayer());
    	
    	if (AllZone.getComputerManaPool().isEmpty())
	    	hand = hand.filter(new CardListFilter() {
	    		public boolean addCard(Card c) {

	    			if (c.getSVar("PlayMain1").equals("TRUE"))
	    				return true;
	    			
	    			if (c.isSorcery()) //timing should be handled by the AF's
	    				return true;
	
	    			if (c.isCreature() 
	    					&& (c.hasKeyword("Haste")) || c.hasKeyword("Exalted")) return true;
	
	    			CardList buffed = AllZoneUtil.getPlayerCardsInPlay(AllZone.getComputerPlayer()); //get all cards the computer controls with BuffedBy
	    			for (int j = 0; j < buffed.size(); j++) {
	    				Card buffedcard = buffed.get(j);
	    				if (buffedcard.getSVar("BuffedBy").length() > 0) {
	    					String buffedby = buffedcard.getSVar("BuffedBy");
	    					String bffdby[] = buffedby.split(",");
	    					if (c.isValidCard(bffdby,c.getController(),c)) return true;
	    				}       
	    			}//BuffedBy
	
	    			CardList antibuffed = AllZoneUtil.getPlayerCardsInPlay(AllZone.getHumanPlayer()); //get all cards the human controls with AntiBuffedBy
	    			for(int k = 0; k < antibuffed.size(); k++) {
	    				Card buffedcard = antibuffed.get(k);
	    				if (buffedcard.getSVar("AntiBuffedBy").length() > 0) {
	    					String buffedby = buffedcard.getSVar("AntiBuffedBy");
	    					String bffdby[] = buffedby.split(",");
	    					if (c.isValidCard(bffdby,c.getController(),c)) return true;
	    				}       
	    			}//AntiBuffedBy
	
	    			if(c.isLand()) return false;
	
	    			CardList vengevines = AllZoneUtil.getPlayerGraveyard(AllZone.getComputerPlayer(), "Vengevine");
	    			if (vengevines.size() > 0) {
	    				CardList creatures = AllZoneUtil.getPlayerHand(AllZone.getComputerPlayer());
	    				CardList creatures2 = new CardList();
	    				for (int i = 0; i < creatures.size(); i++) {
	    					if (creatures.get(i).isCreature() 
	    							&& CardUtil.getConvertedManaCost(creatures.get(i).getManaCost()) <= 3) {
	    						creatures2.add(creatures.get(i));
	    					}
	    				}
	    				if (creatures2.size() + Phase.getComputerCreatureSpellCount() > 1 
	    						&& c.isCreature() 
	    						&& CardUtil.getConvertedManaCost(c.getManaCost()) <= 3) return true;	
	    			} // AI Improvement for Vengevine
	    			// Beached As End
	    			return false;
	    		}
	    	});
    	CardList all = AllZoneUtil.getPlayerCardsInPlay(AllZone.getComputerPlayer());
    	all.addAll(hand);

    	CardList humanPlayable = AllZoneUtil.getPlayerCardsInPlay(AllZone.getHumanPlayer());
    	humanPlayable = humanPlayable.filter(new CardListFilter()
    	{
    		public boolean addCard(Card c)
    		{
    			return (c.canAnyPlayerActivate());
    		}
    	});

    	all.addAll(humanPlayable);

    	return getPlayable(all);
    }//getMain1()
    

    private SpellAbility[] getMain2() {
    	//Card list of all cards to consider
    	CardList all = AllZoneUtil.getPlayerHand(AllZone.getComputerPlayer());
    	//Don't play permanents with Flash before humans declare attackers step
    	all = all.filter(new CardListFilter() {
    		public boolean addCard(Card c) {
    			if (c.isPermanent() 
    					&& c.hasKeyword("Flash") 
    					&& (AllZone.getPhase().isPlayerTurn(AllZone.getComputerPlayer())
    							|| AllZone.getPhase().isBefore(Constant.Phase.Combat_Declare_Attackers_InstantAbility)))
    				return false;
    			return true;
    		}
    	});
    	all.addAll(AllZoneUtil.getPlayerCardsInPlay(AllZone.getComputerPlayer()));
    	all.addAll(CardFactoryUtil.getGraveyardActivationCards(AllZone.getComputerPlayer()));

    	// Prevent the computer from summoning Ball Lightning type creatures during main phase 2
    	all = all.getNotKeyword("At the beginning of the end step, sacrifice CARDNAME.");

    	all = all.filter(new CardListFilter() {
    		public boolean addCard(Card c) {
    			if(c.isLand()) return false;
    			return true;
    		}
    	});

    	CardList humanPlayable = AllZoneUtil.getPlayerCardsInPlay(AllZone.getHumanPlayer());
    	humanPlayable = humanPlayable.filter(new CardListFilter()
    	{
    		public boolean addCard(Card c)
    		{
    			return (c.canAnyPlayerActivate());
    		}
    	});
    	all.addAll(humanPlayable);

    	return getPlayable(all);
    }//getMain2()
    
    private CardList getAvailableSpellAbilities(){
    	CardList all = AllZoneUtil.getPlayerHand(AllZone.getComputerPlayer());
    	//Don't play permanents with Flash before humans declare attackers step
    	all = all.filter(new CardListFilter() {
    		public boolean addCard(Card c) {
    			if (c.isPermanent() 
    					&& c.hasKeyword("Flash") 
    					&& (AllZone.getPhase().isPlayerTurn(AllZone.getComputerPlayer())
    							|| AllZone.getPhase().isBefore(Constant.Phase.Combat_Declare_Attackers_InstantAbility)))
    				return false;
    			return true;
    		}
    	});
    	all.addAll(AllZoneUtil.getPlayerCardsInPlay(AllZone.getComputerPlayer()));
    	all.addAll(CardFactoryUtil.getGraveyardActivationCards(AllZone.getComputerPlayer()));


    	CardList humanPlayable = AllZoneUtil.getPlayerCardsInPlay(AllZone.getHumanPlayer());
    	humanPlayable = humanPlayable.filter(new CardListFilter()
    	{
    		public boolean addCard(Card c)
    		{
    			return (c.canAnyPlayerActivate());
    		}
    	});
    	all.addAll(humanPlayable);
    	return all;
    }
    
    private SpellAbility[] getOtherPhases(){
        return getPlayable(getAvailableSpellAbilities());
    }
    
    private ArrayList<SpellAbility> getPossibleCounters(){
        return getPlayableCounters(getAvailableSpellAbilities());
    }
    
    private ArrayList<SpellAbility> getPossibleETBCounters(){
        return getETBCounters(getAvailableSpellAbilities());
    }
    
    /**
     * Returns the spellAbilities from the card list that the computer is able to play
     */
    private SpellAbility[] getPlayable(CardList l) {
        ArrayList<SpellAbility> spellAbility = new ArrayList<SpellAbility>();
        for(Card c:l)
            for(SpellAbility sa:c.getSpellAbility())
            	// if SA is from AF_Counter don't add to getPlayable
                //This try/catch should fix the "computer is thinking" bug
                try {
                	sa.setActivatingPlayer(AllZone.getComputerPlayer());
                    if(ComputerUtil.canBePlayedAndPayedByAI(sa)){
                    	spellAbility.add(sa);
                    }
                } catch(Exception ex) {
                    showError(ex, "There is an error in the card code for %s:%n", c.getName(), ex.getMessage());
                }
        return spellAbility.toArray(new SpellAbility[spellAbility.size()]);
    }
    
    private ArrayList<SpellAbility> getPlayableCounters(CardList l) {
        ArrayList<SpellAbility> spellAbility = new ArrayList<SpellAbility>();
        for(Card c:l){
            for(SpellAbility sa:c.getSpellAbility()){
            	// Check if this AF is a Counterpsell
            	if (sa.getAbilityFactory() != null && sa.getAbilityFactory().getAPI().equals("Counter"))
            		spellAbility.add(sa);
            }
        }
        
        return spellAbility;
    }
    
    private ArrayList<SpellAbility> getETBCounters(CardList l) {
        ArrayList<SpellAbility> spellAbility = new ArrayList<SpellAbility>();
        for(Card c:l){
            for(SpellAbility sa:c.getSpellAbility()){
            	// Or if this Permanent has an ETB ability with Counter
            	if (sa instanceof Spell_Permanent){
            		if (Spell_Permanent.checkETBEffects(c, sa, "Counter"))
            			spellAbility.add(sa);
            	}
            }
        }
        
        return spellAbility;
    }
    
	public void begin_combat() {
		stackResponse();
	}
    
    public void declare_attackers() {
    	// 12/2/10(sol) the decision making here has moved to getAttackers()
    	
    	AllZone.setCombat(ComputerUtil.getAttackers());

        Card[] att = AllZone.getCombat().getAttackers();
        if (att.length > 0)
            AllZone.getPhase().setCombat(true);
        
        for(int i = 0; i < att.length; i++) {
        	// tapping of attackers happens after Propaganda is paid for
            //if (!att[i].hasKeyword("Vigilance")) att[i].tap();
            Log.debug("Computer just assigned " + att[i].getName() + " as an attacker.");
        }
        
        AllZone.getComputerBattlefield().updateObservers();
        CombatUtil.showCombat();
        
        AllZone.getPhase().setNeedToNextPhase(true);
    }
    
    public void declare_attackers_after()
    {
    	stackResponse();
    }
    
    public void declare_blockers() {
        CardList blockers = AllZoneUtil.getCreaturesInPlay(AllZone.getComputerPlayer());

        AllZone.setCombat(ComputerUtil_Block2.getBlockers(AllZone.getCombat(), blockers));
        
        CombatUtil.showCombat();
        
        AllZone.getPhase().setNeedToNextPhase(true);
    }
    
    public void declare_blockers_after() {
    	stackResponse();
    }

    public void end_of_combat(){
    	stackResponse();
    }
    
    //end of Human's turn
    public void end_of_turn() {
    	stackResponse();
    }
    
    public void stack_not_empty() {
    	stackResponse();
    }
    
    public void stackResponse(){
    	// if top of stack is empty 
    	SpellAbility[] sas = null;
    	if (AllZone.getStack().size() == 0){
    		sas = getOtherPhases();
    		
    		boolean pass = (sas.length == 0) || AllZone.getPhase().is(Constant.Phase.Upkeep, AllZone.getComputerPlayer()) ||
    			AllZone.getPhase().is(Constant.Phase.Draw, AllZone.getComputerPlayer()) || 
    			AllZone.getPhase().is(Constant.Phase.End_Of_Turn, AllZone.getComputerPlayer());
    		if (!pass){		// Each AF should check the phase individually
    	        pass = ComputerUtil.playCards(sas);
    		}
    			
    		if (pass)
    			AllZone.getPhase().passPriority();
    		return;
    	}
    	
    	// if top of stack is owned by me
    	if (AllZone.getStack().peekInstance().getActivatingPlayer().isComputer()){
    		// probably should let my stuff resolve to force Human to respond to it
    		AllZone.getPhase().passPriority();
    		return;
    	}
    	
    	// top of stack is owned by human,
    	ArrayList<SpellAbility> possibleCounters = getPossibleCounters();
    	
    	if (possibleCounters.size() > 0 && ComputerUtil.playCounterSpell(possibleCounters)){
    		// Responding CounterSpell is on the Stack trying to Counter the Spell
    		// If playCounterSpell returns true, a Spell is hitting the Stack
    		return;
    	}
    	
    	possibleCounters.clear();
    	possibleCounters = getPossibleETBCounters();
    	if (possibleCounters.size() > 0 && !ComputerUtil.playCards(possibleCounters)){
    		// Responding Permanent w/ ETB Counter is on the Stack
    		// AllZone.getPhase().passPriority();
    		return;
    	}
    	
    	sas = getOtherPhases();
    	if (sas.length > 0){
			// Spell not Countered 
    		if (!ComputerUtil.playCards(sas))
    			return;
    	}
    	// if this hasn't been covered above, just PassPriority()
    	AllZone.getPhase().passPriority();
    }
}
