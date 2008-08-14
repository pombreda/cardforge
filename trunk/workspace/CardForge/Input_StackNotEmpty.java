public class Input_StackNotEmpty extends Input implements java.io.Serializable
{
  public void showMessage()
  {
    updateGUI();

    ButtonUtil.enableOnlyOK();
    String phase = AllZone.Phase.getPhase();
    String player = AllZone.Phase.getActivePlayer();
    AllZone.Display.showMessage("Spells or Abilities on are on the Stack\nPhase: " +phase +", Player: " +player);
  }
  public void selectButtonOK()
  {
    updateGUI();

    SpellAbility sa = AllZone.Stack.pop();
    Card c = sa.getSourceCard();


    //special consideration for "Beacon of Unrest" and other "Beacon" cards
    if((c.isInstant() || c.isSorcery())     &&
       (! c.getName().startsWith("Beacon")) &&
       (! c.getName().startsWith("Pulse")))
    {
      AllZone.GameAction.moveToGraveyard(c);
    }
    //resolve() needs to be call AFTER stuff is moved to the graveyard
    //because cards like Elvish Fury are returned to hand after resolving
    sa.resolve();
    AllZone.GameAction.checkStateEffects();


    //update all zones, something things arent' updated for some reason
    AllZone.Human_Hand.updateObservers();
    AllZone.Human_Play.updateObservers();
    AllZone.Computer_Play.updateObservers();

    if(AllZone.InputControl.getInput() == this)
      AllZone.InputControl.resetInput();
  }
  public void selectCard(Card card, PlayerZone zone)
  {
    InputUtil.playInstantAbility(card, zone);
  }//selectCard()
  private void updateGUI()
  {
    AllZone.Computer_Play.updateObservers();
    AllZone.Human_Play.updateObservers();
    AllZone.Human_Hand.updateObservers();
  }
}