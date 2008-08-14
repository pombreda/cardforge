public class Input_EOT extends Input
{
  public void showMessage()
  {
    updateGUI();

    ButtonUtil.enableOnlyOK();
    String phase = AllZone.Phase.getPhase();
    String player = AllZone.Phase.getActivePlayer();
    AllZone.Display.showMessage("Computer's End of Turn - Play Instants and Abilities");
  }
  public void selectButtonOK()
  {
    updateGUI();
    AllZone.Phase.nextPhase();
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