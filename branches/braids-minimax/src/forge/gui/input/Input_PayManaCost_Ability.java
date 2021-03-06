
package forge.gui.input;

import java.util.Collection;

import net.slightlymagic.braids.game.ai.minimax.MinimaxMove;
import forge.AllZone;
import forge.ButtonUtil;
import forge.Card;
import forge.Command;
import forge.PlayerZone;
import forge.card.mana.ManaCost;
import forge.card.spellability.SpellAbility;

//if cost is paid, Command.execute() is called
public class Input_PayManaCost_Ability extends Input {
    private static final long serialVersionUID = 3836655722696348713L;
    
    private String            originalManaCost;
    private String            message          = "";
    private ManaCost          manaCost;
    private SpellAbility 	  fakeAbility;

    private Command           paidCommand;
    private Command           unpaidCommand;
    
    //only used for X costs:
    private boolean 		  showOnlyOKButton = false;

    public Input_PayManaCost_Ability(String manaCost, Command paid) {
        this(manaCost, paid, Command.Blank);
    }

    public Input_PayManaCost_Ability(String manaCost_2, Command paidCommand_2, Command unpaidCommand_2) {
    	this("", manaCost_2, paidCommand_2, unpaidCommand_2);
    }
    
    public Input_PayManaCost_Ability(String m, String manaCost_2, Command paidCommand_2, Command unpaidCommand_2) {
    	this(m, manaCost_2, paidCommand_2, unpaidCommand_2, false);
    }
    
    public Input_PayManaCost_Ability(String m, String manaCost_2, Command paidCommand_2, Command unpaidCommand_2, boolean showOKButton) {
       	fakeAbility = new SpellAbility(SpellAbility.Ability, null) {
			@Override
			public void resolve() {}
			
			@Override
			public boolean canPlay() { return false; }
		};
    	originalManaCost = manaCost_2;
        message = m;
        
        manaCost = new ManaCost(originalManaCost);
        paidCommand = paidCommand_2;
        unpaidCommand = unpaidCommand_2;
        showOnlyOKButton = showOKButton;
    }
    
    
    public void resetManaCost() {
        manaCost = new ManaCost(originalManaCost);
    }
    
    @Override
    public void selectCard(Card card, PlayerZone zone) {
        //only tap card if the mana is needed
        manaCost = Input_PayManaCostUtil.activateManaAbility(fakeAbility, card, manaCost);
        showMessage();
        
        if(manaCost.isPaid()) {
            resetManaCost();
            AllZone.getManaPool().clearPay(fakeAbility, false);
            
            paidCommand.execute();
            
            AllZone.getInputControl().resetInput();
        }
    }
    
    @Override
    public void selectButtonCancel() {
        resetManaCost();
        AllZone.getManaPool().unpaid(fakeAbility, true);
        unpaidCommand.execute();
        AllZone.getInputControl().resetInput();
    }
    
    @Override
    public void selectButtonOK() {
    	if (showOnlyOKButton)
    	{
    		unpaidCommand.execute();
    		AllZone.getInputControl().resetInput();
    	}
    }
    
    @Override
    public void showMessage() {
    	ButtonUtil.enableOnlyCancel();
        if (showOnlyOKButton)
        	ButtonUtil.enableOnlyOK();
        AllZone.getDisplay().showMessage(message + "Pay Mana Cost: \r\n" + manaCost.toString());
    }
    

    @Override
	public Collection<MinimaxMove> getMoves() {
		throw new IllegalStateException("This method must never be called.");
	}
}
