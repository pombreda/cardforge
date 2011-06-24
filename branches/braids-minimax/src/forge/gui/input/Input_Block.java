
package forge.gui.input;


import java.util.ArrayList;
import java.util.Collection;

import net.slightlymagic.braids.game.ai.minimax.MinimaxMove;
import net.slightlymagic.braids.util.NotImplementedError;

import forge.AllZone;
import forge.ButtonUtil;
import forge.Card;
import forge.CardUtil;
import forge.CombatUtil;
import forge.Command;
import forge.Constant;
import forge.GameActionUtil;
import forge.PlayerZone;


public class Input_Block extends Input {
    private static final long serialVersionUID = 6120743598368928128L;
    
    private Card              currentAttacker  = null;
    private ArrayList<Card>   allBlocking      = new ArrayList<Card>();
    
    public void removeFromAllBlocking(Card c){
    	allBlocking.remove(c);
    }
    
    @Override
    public void showMessage() {
        //for Castle Raptors, since it gets a bonus if untapped
        for(String effect:AllZone.getStaticEffects().getStateBasedMap().keySet()) {
            Command com = GameActionUtil.commands.get(effect);
            com.execute();
        }
        GameActionUtil.executeCardStateEffects();
        
        //could add "Reset Blockers" button
        ButtonUtil.enableOnlyOK();
        
        
        if(currentAttacker == null) {
        	/*
        	//Lure
        	CardList attackers = new CardList(AllZone.getCombat().getAttackers());
        	for(Card attacker:attackers) {
        		if(attacker.hasKeyword("All creatures able to block CARDNAME do so.")) {
        			CardList bls = AllZoneUtil.getCreaturesInPlay(AllZone.getHumanPlayer());
        			for(Card bl:bls) {
        				if(CombatUtil.canBlock(attacker, bl, AllZone.getCombat())) {
        					allBlocking.add(bl);
        					AllZone.getCombat().addBlocker(attacker, bl);
        				}
        			}
        		}
        	}*/
        	
        	AllZone.getDisplay().showMessage("To Block, click on your Opponents attacker first, then your blocker(s)");
        }
        else {
        	String attackerName = currentAttacker.isFaceDown() ? "Morph" : currentAttacker.getName();
        	AllZone.getDisplay().showMessage("Select a creature to block " + attackerName + " ("
        			+ currentAttacker.getUniqueNumber() + ") ");
        }
        
        CombatUtil.showCombat();
    }
    
    @Override
    public void selectButtonOK() {
    	if(CombatUtil.finishedMandatotyBlocks(AllZone.getCombat())) {
	        // Done blocking
	        ButtonUtil.reset();
	
	        AllZone.getPhase().setNeedToNextPhase(true);
    	}
    }
    
    @Override
    public void selectCard(Card card, PlayerZone zone) {
        //is attacking?
        if(CardUtil.toList(AllZone.getCombat().getAttackers()).contains(card)) {
            currentAttacker = card;
        } else if(zone.is(Constant.Zone.Battlefield, AllZone.getHumanPlayer()) && card.isCreature() 
        		&& CombatUtil.canBlock(currentAttacker, card, AllZone.getCombat())) {
            if(currentAttacker != null && (!allBlocking.contains(card))) {
                allBlocking.add(card);
                AllZone.getCombat().addBlocker(currentAttacker, card);
            }
        }
        showMessage();
    }//selectCard()

	@Override
	public Collection<MinimaxMove> getMoves() {
		throw new NotImplementedError();
	}
}
