
package forge.gui.input;

import java.util.ArrayList;
import java.util.Collection;

import net.slightlymagic.braids.game.ai.minimax.MinimaxMove;
import forge.AllZone;
import forge.AllZoneUtil;
import forge.ButtonUtil;
import forge.Card;
import forge.CardList;
import forge.CombatUtil;
import forge.Constant;
import forge.Player;
import forge.PlayerZone;
import forge.ai.minimax.GenericInputMove;
import forge.ai.minimax.Unstatic;

public class Input_Cleanup extends Input {
    private static final long serialVersionUID = -4164275418971547948L;

    /**
     * This doesn't just show messages; it advances the phase once the player
     * has discarded sufficient cards.
     */
    @Override
    public void showMessage() {
    	if (AllZone.getPhase().getPlayerTurn().isComputer()){
    		AI_CleanupDiscard();
    		return;
    	}

        Player prioritrix = Unstatic.getGlobalGameState().whoHasPriorityAsPlayer();
		int n = AllZoneUtil.getPlayerHand(prioritrix).size();

		if (!isSilent()) {
    		ButtonUtil.disableAll();

    		//MUST showMessage() before stop() or it will overwrite the next Input's message
	        StringBuffer sb = new StringBuffer();
	        sb.append("Cleanup Phase: You can only have a maximum of ").append(prioritrix.getMaxHandSize());
	        sb.append(" cards, you currently have ").append(n).append(" cards in your hand - select a card to discard");

        	AllZone.getDisplay().showMessage(sb.toString());
        }
        
        //goes to the next phase
        if(n <= prioritrix.getMaxHandSize() || prioritrix.getMaxHandSize() == -1) {
            CombatUtil.removeAllDamage();
            
            AllZone.getPhase().setNeedToNextPhase(true);
            AllZone.getPhase().nextPhase();	// TODO: keep an eye on this code, see if we can get rid of it.
        }
    }
    
    @Override
    public void selectCard(Card card, PlayerZone zone) {
        Player prioritrix = Unstatic.getGlobalGameState().whoHasPriorityAsPlayer();
    	
        if(zone.is(Constant.Zone.Hand, prioritrix)) {
            card.getController().discard(card, null);
            if (AllZone.getStack().size() == 0)
            	showMessage();
        }
    }//selectCard()
    
    
    public void AI_CleanupDiscard(){
    	int size = AllZoneUtil.getPlayerHand(AllZone.getComputerPlayer()).size();
    	
    	if (AllZone.getComputerPlayer().getMaxHandSize() != -1){
    		int numDiscards = size - AllZone.getComputerPlayer().getMaxHandSize(); 
    		AllZone.getComputerPlayer().discard(numDiscards, null, false);
    	}
        CombatUtil.removeAllDamage();
        
        AllZone.getPhase().setNeedToNextPhase(true);
    }

	@Override
	public Collection<MinimaxMove> getMoves() {
		
        Player prioritrix = Unstatic.getGlobalGameState().whoHasPriorityAsPlayer();
		CardList cardsInHand = AllZoneUtil.getPlayerHand(prioritrix);
		ArrayList<MinimaxMove> result = new ArrayList<MinimaxMove>(cardsInHand.size());
		
		for (Card card : cardsInHand) {
			result.add(new GenericInputMove(this, card));
		}
		
		return result;
	}
}
