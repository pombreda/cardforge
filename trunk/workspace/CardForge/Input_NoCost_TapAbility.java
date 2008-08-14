public class Input_NoCost_TapAbility extends Input
{
  private final Ability_Tap ability;

  public Input_NoCost_TapAbility(Ability_Tap ab)
  {
    ability = ab;
  }
  public void showMessage()
  {
    //prevents this from running multiple times, which it is for some reason
    if(ability.getSourceCard().isUntapped())
    {
      ability.getSourceCard().tap();
      AllZone.Stack.add(ability);
      stopSetNext(new ComputerAI_StackNotEmpty());
    }
  }
}