
package forge.gui.input;

import java.util.Collection;

import net.slightlymagic.braids.game.ai.minimax.MinimaxMove;
import forge.AllZone;
import forge.Card;
import forge.Player;
import forge.PlayerZone;


public abstract class Input implements java.io.Serializable {
    private static final long serialVersionUID = -6539552513871194080L;
    
    private boolean			isFree		= false;

    private boolean isSilent = false;
    
    //showMessage() is always the first method called
    public void showMessage() {
    	if (!isSilent()) {
    		AllZone.getDisplay().showMessage("Blank Input");
    	}
    }
    
    public void selectCard(Card c, PlayerZone zone) {}
    
    public void selectPlayer(final Player player) { }
    
    public void selectButtonOK() {}
    
    public void selectButtonCancel() {}
    
    //helper methods, since they are used alot
    //to be used by anything in CardFactory like SetTargetInput
    //NOT TO BE USED by Input_Main or any of the "regular" Inputs objects that are not set using AllZone.getInputControl().setInput(Input)
    final public void stop() {
        //clears a "temp" Input like Input_PayManaCost if there is one
        AllZone.getInputControl().resetInput();
        
        if(AllZone.getPhase().isNeedToNextPhase()) {
            // mulligan needs this to move onto next phase
            AllZone.getPhase().setNeedToNextPhase(false);
            AllZone.getPhase().nextPhase();
        }
    }
    
    //exits the "current" Input and sets the next Input
    final public void stopSetNext(Input in) {
        stop();
        AllZone.getInputControl().setInput(in);
    }
    
    @Override
    public String toString() {
        return "blank";
    }//returns the Input name like "EmptyStack"
    
    public void setFree(boolean isFree) {
        this.isFree = isFree;
    }
    
    public boolean isFree() {
        return isFree;
    }

    
    /**
     * Used by Minimax AI to prevent theoretical game-state changes from 
     * affecting the actual display.  If true, then subclasses must not make
     * changes to the user-interface.
     * 
     * @param isSilent if true, prevents this instance from making any 
     * (further) changes to the user-interface.
     */
	public void setSilent(boolean isSilent) {
		this.isSilent = isSilent;
	}

    /**
     * Used by Minimax AI to prevent theoretical game-state changes from 
     * affecting the actual display.
     * 
     * @see #setSilent(boolean)
     */
	public boolean isSilent() {
		return isSilent;
	}

	/**
	 * Required by Minimax AI to get a list of possible moves.
	 * @return a collection of MinimaxMove instances (implementors)
	 * @see forge.ai.minimax.AbstractInputMove 
	 */
	public abstract Collection<MinimaxMove> getMoves();
}
