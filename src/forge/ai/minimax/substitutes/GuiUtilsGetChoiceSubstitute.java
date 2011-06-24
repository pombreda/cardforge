package forge.ai.minimax.substitutes;

import java.util.ArrayList;
import java.util.Collection;

import net.slightlymagic.braids.game.ai.minimax.MinimaxMove;
import net.slightlymagic.braids.util.lambda.Lambda1;
import net.slightlymagic.braids.util.lambda.Null;
import forge.AllZone;
import forge.ai.minimax.GenericMove;
import forge.gui.input.Input;

public class GuiUtilsGetChoiceSubstitute extends Input {

	private static final long serialVersionUID = 4451622183433390764L;

	private Input parentInput;
	private Collection<String> choices;
	private Lambda1<Null,String> effectProc;

	public GuiUtilsGetChoiceSubstitute(ArrayList<String> choices, 
			Input parentInput, 
			Lambda1<Null,String> effectProc) 
	{
		this.parentInput = parentInput;
		this.choices = choices;
		this.effectProc = effectProc;
	}
	
	@Override
	public Collection<MinimaxMove> getMoves() {
		
		int numChoices = choices.size();
		
		ArrayList<MinimaxMove> result = new ArrayList<MinimaxMove>(numChoices);
		
		for (String choice : choices) {

			Lambda1<Null,String> moveProc = new Lambda1<Null,String>() {
				public Null apply(String arg) {
					effectProc.apply(arg);
					
					// Revert back to the parent-input controller.
					AllZone.getInputControl().setInput(parentInput);
					return null;
				}
			};
			
			result.add(new GenericMove(moveProc, new Object[] {choice}));
		}
		
		return result;
	}
}
