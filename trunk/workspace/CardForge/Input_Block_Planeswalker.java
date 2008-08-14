import java.util.*;

public class Input_Block_Planeswalker extends Input
{
  private Card currentAttacker = null;
  private ArrayList allBlocking = new ArrayList();

  public void showMessage()
  {
    //for Castle Raptors, since it gets a bonus if untapped
    GameActionUtil.executeCardStateEffects();


    //could add "Reset Blockers" button
    ButtonUtil.enableOnlyOK();

    if(currentAttacker == null)
      AllZone.Display.showMessage("Planeswalker Combat\r\nTo Block, click on your Opponents attacker first , then your blocker(s)");
    else
      AllZone.Display.showMessage("Select a creature to block " +currentAttacker.getName() +" (" +currentAttacker.getUniqueNumber()  +") ");

    CombatUtil.showCombat();
  }
  public void selectButtonOK()
  {
    ButtonUtil.reset();
    AllZone.Phase.nextPhase();
    this.stop();
  }
  public void selectCard(Card card, PlayerZone zone)
  {
    //is attacking?
    if(CardUtil.toList(AllZone.pwCombat.getAttackers()).contains(card))
    {
      currentAttacker = card;
    }
    else if(zone.is(Constant.Zone.Play, Constant.Player.Human) &&
            card.isCreature() &&
            card.isUntapped() &&
            CombatUtil.canBlock(currentAttacker, card))
    {
      if(currentAttacker != null && (! allBlocking.contains(card)))
      {
        allBlocking.add(card);
        AllZone.pwCombat.addBlocker(currentAttacker, card);
      }
    }
    showMessage();
  }//selectCard()
}
