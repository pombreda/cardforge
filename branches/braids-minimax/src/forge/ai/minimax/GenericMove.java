package forge.ai.minimax;

import net.slightlymagic.braids.game.ai.minimax.MinimaxMove;
import net.slightlymagic.braids.util.UtilFunctions;
import net.slightlymagic.braids.util.lambda.FrozenCall;
import net.slightlymagic.braids.util.lambda.Lambda;
import net.slightlymagic.braids.util.lambda.Null;

public class GenericMove extends FrozenCall<Null> implements MinimaxMove {
	
	public GenericMove(Lambda<Null> proc, Object[] args) {
		super(proc,args);
	}


	@Override
	public boolean equals(Object obj) {
		GenericMove that = UtilFunctions.checkNullOrNotInstance(this, obj);	
		if (that == null)  return false;
		else  return super.equals(that);
	}
}
