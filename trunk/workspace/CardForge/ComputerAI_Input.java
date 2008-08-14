public class ComputerAI_Input extends Input
{
    private final Computer computer;
    
    public ComputerAI_Input(Computer i_computer)
    {
	computer = i_computer;
    }    
    //wrapper method that ComputerAI_StackNotEmpty class calls
    //ad-hoc way for ComptuerAI_StackNotEmpty to get to the Computer class
    public void stackNotEmpty()
    {
	computer.stack_not_empty();
    }
    public void showMessage()
    {
	ButtonUtil.disableAll();
	AllZone.Display.showMessage("Phase: " +AllZone.Phase.getPhase() +"\nComputer is thinking");
	
	think();
    }//getMessage();
    private void think()
    {
	final String phase = AllZone.Phase.getPhase();

	if(phase.equals(Constant.Phase.Main1))
	{
	    computer.main1();
	}
	else if(phase.equals(Constant.Phase.Combat_Declare_Attackers))
	{
	    computer.declare_attackers();
	}
	else if(phase.equals(Constant.Phase.Combat_Declare_Blockers_InstantAbility))
	{
	    computer.declare_blockers_after();
	}
	else if(phase.equals(Constant.Phase.Main2))
	{
	    computer.main2();
	}
	else if(phase.equals(Constant.Phase.At_End_Of_Turn))
	{
	    AllZone.EndOfTurn.executeAt();
	    AllZone.Phase.nextPhase();

	    AllZone.InputControl.updateObservers();
	}
	else if(phase.equals(Constant.Phase.Until_End_Of_Turn))
	{
	    AllZone.EndOfTurn.executeUntil();
	    AllZone.Phase.nextPhase();

	    AllZone.InputControl.updateObservers();
	}
	
	//takes place during the human's turn, like blocking etc...
	else if(phase.equals(Constant.Phase.Combat_Declare_Blockers))
	{
	    computer.declare_blockers();
	}
	else if(phase.equals(Constant.Phase.End_Of_Turn))
	{
	    computer.end_of_turn();
	}
    }//think
}