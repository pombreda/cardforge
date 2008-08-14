public class ComputerAI_Testing implements Computer 
{
    
    //must shuffle this
    public Card[] getLibrary() {return new Card[] {};}

    public void stack_not_empty() 
    {
	System.out.println("Computer: not empty");
	//same as Input.stop() method
	//ends the method
	//different than methods because this isn't a phase like Main1 or Declare Attackers
	AllZone.InputControl.resetInput();
	AllZone.InputControl.updateObservers();    
    }
    
    public void main1() {AllZone.Phase.nextPhase();}
    public void declare_attackers(){AllZone.Phase.nextPhase();}

    public void declare_blockers(){AllZone.Phase.nextPhase();}

    //can play Instants and Abilities
    public void declare_blockers_after(){AllZone.Phase.nextPhase();}

    public void main2(){AllZone.Phase.nextPhase();}

    //end of Human's turn
    public void end_of_turn(){AllZone.Phase.nextPhase();}
}