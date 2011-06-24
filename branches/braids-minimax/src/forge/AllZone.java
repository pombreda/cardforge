package forge;


import java.util.Iterator;
import java.util.Map;

import forge.ai.minimax.Unstatic;
import forge.card.cardFactory.CardFactory;
import forge.card.mana.ManaPool;
import forge.card.trigger.TriggerHandler;
import forge.gui.input.InputControl;
import forge.properties.NewConstants;


public class AllZone implements NewConstants {
    //only for testing, should read decks from local directory
//  public static final IO IO = new IO("all-decks");
	
	public static Player getHumanPlayer() {
		return Unstatic.getGlobalGameState().getHumanPlayer();
	}
	
    public static Player getComputerPlayer() {
         return Unstatic.getGlobalGameState().getComputerPlayer(); 
    }

    public static forge.quest.data.QuestData getQuestData() {
         return Unstatic.getGlobalGameState().getQuestData(); 
    }
    
    public static void setQuestData(forge.quest.data.QuestData questData) {
    	Unstatic.getGlobalGameState().setQuestData(questData);
    }
    
    public static Quest_Assignment getQuestAssignment() {
         return Unstatic.getGlobalGameState().getQuestAssignment(); 
    }
	
    public static void setQuestAssignment(Quest_Assignment assignment) {
		Unstatic.getGlobalGameState().setQuestAssignment(assignment);
	}
    
    public static NameChanger getNameChanger() {
         return Unstatic.getGlobalGameState().getNameChanger(); 
    }
    
    public static EndOfTurn getEndOfTurn() {
         return Unstatic.getGlobalGameState().getEndOfTurn(); 
    }
    
    public static forge.EndOfCombat getEndOfCombat() {
         return Unstatic.getGlobalGameState().getEndOfCombat(); 
    }
    
    public static Phase getPhase() {
         return Unstatic.getGlobalGameState().getPhase(); 
    }
    
    public static CardFactory getCardFactory() {
         return Unstatic.getCardFactory(); 
    }
    
    public static MagicStack getStack() {
         return Unstatic.getGlobalGameState().getStack(); 
    }
    
    public static InputControl getInputControl() {
         return Unstatic.getGlobalGameState().getInputControl(); 
    }
    
    public static GameAction getGameAction() {
         return Unstatic.getGlobalGameState().getGameAction(); 
    }
    
    public static StaticEffects getStaticEffects() {
         return Unstatic.getGlobalGameState().getStaticEffects(); 
    }
    
    public static GameInfo getGameInfo() {
         return Unstatic.getGlobalGameState().getGameInfo(); 
    }
    
    public static TriggerHandler getTriggerHandler() {
         return Unstatic.getGlobalGameState().getTriggerHandler(); 
    }
    
    public static ComputerAI_Input getComputer() {
         return Unstatic.getGlobalGameState().getComputer(); 
    }
	
    public static void setComputer(ComputerAI_Input input) {
		Unstatic.getGlobalGameState().setComputer(input);		
	}

    public static Combat getCombat() {
         return Unstatic.getGlobalGameState().getCombat(); 
    }
	
    public static void setCombat(Combat attackers) {
		Unstatic.getGlobalGameState().setCombat(attackers);
	}

    //Human_Play, Computer_Play is different because Card.comesIntoPlay() is called when a card is added by PlayerZone.add(Card)
    public static PlayerZone getHumanBattlefield() {
         return Unstatic.getGlobalGameState().getHumanBattlefield(); 
    }
    
    public static PlayerZone getHumanHand() {
         return Unstatic.getGlobalGameState().getHumanHand(); 
    }
    
    public static PlayerZone getHumanGraveyard() {
         return Unstatic.getGlobalGameState().getHumanGraveyard(); 
    }
    
    public static PlayerZone getHumanLibrary() {
         return Unstatic.getGlobalGameState().getHumanLibrary(); 
    }
    
    public static PlayerZone getHumanExile() {
         return Unstatic.getGlobalGameState().getHumanExile(); 
    }
    
    public static PlayerZone getHumanCommand() {
         return Unstatic.getGlobalGameState().getHumanCommand(); 
    }
    
    public static PlayerZone getComputerBattlefield() {
         return Unstatic.getGlobalGameState().getComputerBattlefield(); 
    }
    
    public static PlayerZone getComputerHand() {
         return Unstatic.getGlobalGameState().getComputerHand(); 
    }
    
    public static PlayerZone getComputerGraveyard() {
         return Unstatic.getGlobalGameState().getComputerGraveyard(); 
    }
    
    public static PlayerZone getComputerLibrary() {
         return Unstatic.getGlobalGameState().getComputerLibrary(); 
    }
    
    public static PlayerZone getComputerExile() {
         return Unstatic.getGlobalGameState().getComputerExile(); 
    }
    
    public static PlayerZone getComputerCommand() {
         return Unstatic.getGlobalGameState().getComputerCommand(); 
    }
    
    public static PlayerZone getStackZone() {
         return Unstatic.getGlobalGameState().getStackZone(); 
    }
    
    public static ManaPool getManaPool() {
         return Unstatic.getGlobalGameState().getManaPool(); 
    }
    
    public static ManaPool getComputerManaPool() {
         return Unstatic.getGlobalGameState().getComputerManaPool(); 
    }
    
    public static Display getDisplay() {
         return Unstatic.getGlobalGameState().getDisplay(); 
    }
	
    public static void setDisplay(Display display) {
		Unstatic.getGlobalGameState().setDisplay(display);
	}

    private static Map<String,PlayerZone> getMap() {
         return Unstatic.getGlobalGameState().getNamesToZones(); 
    }

    public static PlayerZone getZone(Card c)
    {
		Iterator<PlayerZone> it = getMap().values().iterator();
		PlayerZone p;
		while(it.hasNext())
		{
		    p = (PlayerZone)it.next();
	
		    if(AllZoneUtil.isCardInZone(p, c))
		    	return p;
		}
		return null;
    }
    
    public static PlayerZone getZone(String zone, Player player)
    {
    	if(zone.equals("Stack")) player = null;
		Object o = getMap().get(zone + player);
		if(o == null)
		    throw new RuntimeException("AllZone : getZone() invalid parameters " +zone +" " +player);
	
		return (PlayerZone)o;
    }

    public static void resetZoneMoveTracking()
    {
        ((DefaultPlayerZone)getHumanCommand()).resetCardsAddedThisTurn();
        ((DefaultPlayerZone)getHumanLibrary()).resetCardsAddedThisTurn();
        ((DefaultPlayerZone)getHumanHand()).resetCardsAddedThisTurn();
        ((DefaultPlayerZone)getHumanBattlefield()).resetCardsAddedThisTurn();
        ((DefaultPlayerZone)getHumanGraveyard()).resetCardsAddedThisTurn();
        ((DefaultPlayerZone)getComputerCommand()).resetCardsAddedThisTurn();
        ((DefaultPlayerZone)getComputerLibrary()).resetCardsAddedThisTurn();
        ((DefaultPlayerZone)getComputerHand()).resetCardsAddedThisTurn();
        ((DefaultPlayerZone)getComputerBattlefield()).resetCardsAddedThisTurn();
        ((DefaultPlayerZone)getComputerGraveyard()).resetCardsAddedThisTurn();
    }
}//AllZone