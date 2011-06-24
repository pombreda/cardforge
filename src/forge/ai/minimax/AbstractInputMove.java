package forge.ai.minimax;

import net.slightlymagic.braids.game.ai.minimax.InvocableMinimaxMove;
import static net.slightlymagic.braids.util.UtilFunctions.*;
import forge.gui.input.Input;

public abstract class AbstractInputMove 
	implements InvocableMinimaxMove
{
	private Input source;

	protected AbstractInputMove(Input source) {
		this.setSource(source);
	}

	public void setSource(Input source) {
		checkNotNull(null, source);
		this.source = source;
	}

	public Input getSource() {
		return source;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		AbstractInputMove that = checkNullOrNotInstance(this, obj);
		
		if (that == null || !this.source.equals(that.source))  return false;
		else  return true;
	}
}
