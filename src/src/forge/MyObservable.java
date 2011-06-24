package forge;
import java.util.Observable;

public class MyObservable extends Observable
{
	public final void updateObservers()
	{	
		this.setChanged();
		this.notifyObservers();
		
		if(AllZone.getPhase() != null && AllZone.getPhase().isNeedToNextPhase()){
		    	if(AllZone.getPhase().isNeedToNextPhaseInit()){
		    		// this is used.
		    		AllZone.getPhase().setNeedToNextPhase(false);
		    		AllZone.getPhase().nextPhase();
		    }
		}
	}
}

