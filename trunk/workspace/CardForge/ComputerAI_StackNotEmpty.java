public class ComputerAI_StackNotEmpty extends Input
{
    public void showMessage() 
    {
	AllZone.Display.showMessage("Phase: " +AllZone.Phase.getPhase() +"\nComputer is thinking");    
	AllZone.Computer.stackNotEmpty();
    }
}
