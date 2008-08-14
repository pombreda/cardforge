public class Input_Block_Instant extends Input
{
  public void showMessage()
  {
    AllZone.Combat.verifyCreaturesInPlay();
    CombatUtil.showCombat();

    ButtonUtil.enableOnlyOK();
    AllZone.Display.showMessage("Declare Blockers: Play Instants and Abilities");
  }
  public void selectButtonOK()
  {
    AllZone.Combat.setAssignedDamage();
    AllZone.pwCombat.setAssignedDamage();

    AllZone.Phase.nextPhase();
  }
  public void selectCard(Card card, PlayerZone zone)
  {
    InputUtil.playInstantAbility(card, zone);
  }//selectCard()
}