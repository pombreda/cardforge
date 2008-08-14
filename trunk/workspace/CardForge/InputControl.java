import java.util.*;

public class InputControl extends MyObservable implements java.io.Serializable
{
    private Input input;
    static int n = 0;

    public void setInput(Input in)
    {
	input = in;
	updateObservers();
    }
    public void resetInput()
    {
	input = null;
	updateObservers();
    }
    public Input getInput()
    {
	final String phase = AllZone.Phase.getPhase();
	final String player = AllZone.Phase.getActivePlayer();
//System.out.println(n++ +" " +phase +" " +player);

	if(input != null)
	    return input;

	else if(AllZone.Stack.size() > 0)
	{
	    input = new Input_StackNotEmpty();
	    return input;
	}
	else if(player.equals(Constant.Player.Human))
	{
	    if(phase.equals(Constant.Phase.Untap))
	    {
		AllZone.Combat.reset();
		AllZone.Combat.setAttackingPlayer(Constant.Player.Human);
		AllZone.Combat.setDefendingPlayer(Constant.Player.Computer);

                AllZone.pwCombat.reset();
                AllZone.pwCombat.setAttackingPlayer(Constant.Player.Human);
                AllZone.pwCombat.setDefendingPlayer(Constant.Player.Computer);

		return new Input_Untap();
	    }
	    //else if(phase.equals(Constant.Phase.Upkeep))
		//return new Input_Instant(Input_Instant.YES_NEXT_PHASE, "Upkeep Phase: Play Instants and Abilities");

	    else if(phase.equals(Constant.Phase.Draw))
		return new Input_Draw();

	    else if(phase.equals(Constant.Phase.Main1) || phase.equals(Constant.Phase.Main2))
		return new Input_Main();

	    else if(phase.equals(Constant.Phase.Combat_Declare_Attackers))
	    {
		return new Input_Attack();
	    }
	    //this is called twice per turn, only when its human's turn
	    //and then the 2nd time when its the computer's turn
	    else if(phase.equals(Constant.Phase.Combat_Declare_Blockers_InstantAbility))
	    {
		if(! skipPhase())
		    return new Input_Block_Instant();
		else
		{
		    AllZone.Phase.nextPhase();
		    return getInput();
		}
	    }
	    else if(phase.equals(Constant.Phase.Combat_Damage))
	    {
		if(! skipPhase())
		    return new Input_CombatDamage();
		else
		{
		    AllZone.Phase.nextPhase();
		    return getInput();
		}
	    }
	    else if(phase.equals(Constant.Phase.At_End_Of_Turn))
	    {
		AllZone.EndOfTurn.executeAt();
		AllZone.Phase.nextPhase();
		return getInput();
	    }
            else if(phase.equals(Constant.Phase.End_Of_Turn))
            {
              if(AllZone.Display.stopEOT())
                return new Input_EOT();
              else
              {
                AllZone.Phase.nextPhase();
                return getInput();
              }
	    }
	    else if(phase.equals(Constant.Phase.Until_End_Of_Turn))
	    {
		AllZone.EndOfTurn.executeUntil();
		AllZone.Phase.nextPhase();
		return getInput();
	    }
	    else if(phase.equals(Constant.Phase.Cleanup))
		return new Input_Cleanup();

	    //takes place during the computer's turn, like blocking etc...
	    else if(phase.equals(Constant.Phase.Combat_Declare_Blockers))
	    {
		if(! skipPhase())
		    return new Input_Block();
		else
		{
		    AllZone.Phase.nextPhase();
		    return getInput();
		}
	    }
//	    else if(phase.equals(Constant.Phase.End_Of_Turn))
//		return new Input_Instant(Input_Instant.YES_NEXT_PHASE, "End of Computer's Turn: Play Instants and Abilities");

	}//Human
	else
	{  //computer
	    if(phase.equals(Constant.Phase.Untap))
	    {
		AllZone.Combat.reset();
		AllZone.Combat.setAttackingPlayer(Constant.Player.Computer);
		AllZone.Combat.setDefendingPlayer(Constant.Player.Human);

                AllZone.pwCombat.reset();
                AllZone.pwCombat.setAttackingPlayer(Constant.Player.Computer);
                AllZone.pwCombat.setDefendingPlayer(Constant.Player.Human);

		return new Input_Untap();
	    }
	    else if(phase.equals(Constant.Phase.Draw))
		return new Computer_Draw();

	    else if(phase.equals(Constant.Phase.Cleanup))
            {
              return new Computer_Cleanup();
            }
	    else
		return AllZone.Computer;
	}//Computer

	return new Input()
	{
	    public void showMessage()
            {
              AllZone.Display.showMessage("InputControl : Error nothing found");
              throw new RuntimeException("InputControl : getInput() error, should not be here, phase " +phase +", player " +player);
            }
	};
    }//getInput()
    private boolean skipPhase()
    {
      CardList check = new CardList();
      check.addAll(AllZone.Combat.getAttackers());
      check.addAll(AllZone.pwCombat.getAttackers());

      return check.size() == 0;
    }
}//InputControl