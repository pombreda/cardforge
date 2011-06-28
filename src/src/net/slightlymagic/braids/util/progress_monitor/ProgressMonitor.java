package net.slightlymagic.braids.util.progress_monitor;

public interface ProgressMonitor {

	public abstract int getNumPhases();

	public abstract float getMinUpdateIntervalSec();

	public abstract int getCurrentPhase();

	public abstract long getUnitsCompletedSoFarThisPhase();

	public abstract long getTotalUnitsThisPhase();

	public abstract long getLastUIUpdateTime();

	public abstract long getPhaseOneStartTime();

	public abstract long getCurrentPhaseStartTime();

	public abstract void setMinUpdateIntervalSec(float value);

	public abstract void setTotalUnitsThisPhase(long value);

	/**
	 * Resulting string does not contain a percent sign.
	 */
	public abstract String getPercentCompleteOfThisPhaseAsString();

	/**
	 * Resulting string does not contain a percent sign.
	 */
	public abstract String getTotalPercentCompleteAsString();

	public abstract String getRelativeETAAsString();

	/**
	 * May return "unknown"
	 */
	public abstract String getRelativeETAAsString(boolean thisPhaseOnly);

	public abstract String getAbsoluteETAAsLocalTimeString();

	/**
	 * May return "unknown"
	 */
	public abstract String getAbsoluteETAAsLocalTimeString(boolean thisPhaseOnly);

	/**
	 * Note this will NOT advance the phase.  
	 * To do that, use markCurrentPhaseAsComplete().
	 */
	public abstract void incrementUnitsCompletedThisPhase(long numUnits);

	/**
	 * Returns a boolean, whether or not to display the updated information.
	 * This throttles the update so it doesn't refresh so fast that it is
	 * unreadable.  Implementers should call this method from their own
	 * incrementUnitsCompletedThisPhase method.
	 * 
	 * If we have just reached 100% for (the current phase, we return true, 
	 * even if it would otherwise be too soon to update the UI.
	 */
	public abstract boolean shouldUpdateUI();

	/**
	 * This is the only way to advance the phase number.
	 * It automatically "starts the clock" for the next phase.
	 * 
	 * @param totalUnitsNextPhase  if unknown, use zero (0), and be sure to call
	 * setTotalUnitsThisPhase() soon after.
	 */
	public abstract void markCurrentPhaseAsComplete(long totalUnitsNextPhase);

	public abstract void sendMessage(String message);

	public abstract void setCurrentPhaseAsExponential(int value);

	public abstract int getCurrentPhaseExponent();

}
