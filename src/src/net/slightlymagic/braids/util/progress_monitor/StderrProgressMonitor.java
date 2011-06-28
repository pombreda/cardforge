package net.slightlymagic.braids.util.progress_monitor;

public class StderrProgressMonitor extends BaseProgressMonitor {

    public StderrProgressMonitor(int numPhases, long totalUnitsFirstPhase) {
    	this(numPhases, totalUnitsFirstPhase, 2.0f, null);
    }
    
    public StderrProgressMonitor(int numPhases, long totalUnitsFirstPhase, 
            float minUpdateIntervalSec) 
	{
		this(numPhases, totalUnitsFirstPhase, minUpdateIntervalSec, null);
	}
    
    public StderrProgressMonitor(int numPhases, long totalUnitsFirstPhase, 
                 float minUpdateIntervalSec, float[] phaseWeights) 
    {
        super(numPhases, totalUnitsFirstPhase, minUpdateIntervalSec, phaseWeights);
    }
    
    @Override
    public void incrementUnitsCompletedThisPhase(long numUnits) {
        super.incrementUnitsCompletedThisPhase(numUnits);

        if (shouldUpdateUI()) {

        	if ((getNumPhases() > 1)) {
                printUpdate(
                 "Phase " + getCurrentPhase() + ": " + 
                 getUnitsCompletedSoFarThisPhase() + " units processed. " + 
                 "Overall: " + getTotalPercentCompleteAsString() + "% complete, " +
                 "ETA in " + getRelativeETAAsString() + "."
                 );
            }
            else {
                printUpdate(
                 "Overall: " + 
                 getUnitsCompletedSoFarThisPhase() + " units processed " +
                 "(" + getTotalPercentCompleteAsString() + "%); " +
                 "ETA in " + getRelativeETAAsString() + "."
                 );
            }
        }
     }

    public void printUpdate(String message) {
    	
    	while (message.length() < 79) {
    		message += ' ';
    	}
    	
        System.err.print("\r");
        System.err.print(message);

        if (message.length() > 79) {
        	System.err.print("\n");
        }

        justUpdatedUI();

    }
}
