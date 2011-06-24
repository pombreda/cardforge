
package forge.gui.input;

import java.util.Collection;

import net.slightlymagic.braids.game.ai.minimax.MinimaxMove;
import net.slightlymagic.braids.util.NotImplementedError;
import forge.AllZone;
import forge.AllZoneUtil;
import forge.ButtonUtil;
import forge.Card;
import forge.CardList;
import forge.CombatUtil;
import forge.Command;
import forge.Constant;
import forge.GameActionUtil;
import forge.PlayerZone;


public class Input_Attack extends Input {
    private static final long serialVersionUID = 7849903731842214245L;
    
    @Override
    public void showMessage() {
    	// TODO: still seems to have some issues with multiple planeswalkers
    	
        ButtonUtil.enableOnlyOK();

        Object o = AllZone.getCombat().nextDefender();        
        if (o == null){
        	return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Declare Attackers: Select Creatures to Attack ");
        sb.append(o.toString());
        
        AllZone.getDisplay().showMessage(sb.toString());
        
        if (AllZone.getCombat().getRemainingDefenders() == 0) {
        	// Nothing left to attack, has to attack this defender
            CardList possibleAttackers = AllZoneUtil.getPlayerCardsInPlay(AllZone.getHumanPlayer());
            possibleAttackers = possibleAttackers.getType("Creature");
            for (int i = 0; i < possibleAttackers.size(); i++) {
                Card c = possibleAttackers.get(i);
                if (c.hasKeyword("CARDNAME attacks each turn if able.") 
                		&& CombatUtil.canAttack(c,AllZone.getCombat()) 
                		&& !c.isAttacking()) {
                    AllZone.getCombat().addAttacker(c);
                    //if(!c.hasKeyword("Vigilance")) 
                    //	c.tap();
                }
            }
        }
    }
    
    @Override
    public void selectButtonOK() {
    	if (AllZone.getCombat().getAttackers().length > 0)
    		AllZone.getPhase().setCombat(true);
    
    	if (AllZone.getCombat().getRemainingDefenders() != 0)
    		AllZone.getPhase().repeatPhase();
    	
    	AllZone.getPhase().setNeedToNextPhase(true);
    	AllZone.getInputControl().resetInput();
    }
    
    @Override
    public void selectCard(Card card, PlayerZone zone) {
    	if (card.isAttacking() || card.getController().isComputer())
    		return;
    	
        if(zone.is(Constant.Zone.Battlefield, AllZone.getHumanPlayer()) && CombatUtil.canAttack(card,AllZone.getCombat())) {
            
        	// TODO add the propaganda code here and remove it in Phase.nextPhase()
        	// if (!CombatUtil.checkPropagandaEffects(card))
        	// 		return;
        	         
            AllZone.getCombat().addAttacker(card);
            AllZone.getHumanBattlefield().updateObservers();	// just to make sure the attack symbol is marked
            
            //for Castle Raptors, since it gets a bonus if untapped
            for(String effect:AllZone.getStaticEffects().getStateBasedMap().keySet()) {
                Command com = GameActionUtil.commands.get(effect);
                com.execute();
            }
            GameActionUtil.executeCardStateEffects();
            
            CombatUtil.showCombat();
        }
    }//selectCard()
    
    public void unselectCard(Card card, PlayerZone zone) {

    }

	@Override
	public Collection<MinimaxMove> getMoves() {
		throw new NotImplementedError();
	}
}
