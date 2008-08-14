public class Input_Attack extends Input
{
  public void showMessage()
  {
    ButtonUtil.enableOnlyOK();
    AllZone.Display.showMessage("Declare Attackers: Select creatures that you want to attack with");
  }
  public void selectButtonOK()
  {
    Card check = getPlaneswalker();
    if(check == null)
      AllZone.Phase.nextPhase();
    else
    {
      AllZone.pwCombat.setPlaneswalker(check);
      AllZone.InputControl.setInput(new Input_Attack_Planeswalker());
    }
  }
  //return Computer's planeswalker if there is one
  //just returns 1, does not return multiple planeswalkers
  private Card getPlaneswalker()
  {
    CardList c = new CardList(AllZone.Computer_Play.getCards());
    c = c.getType("Planeswalker");

    if(c.isEmpty())
      return null;

    return c.get(0);
  }

  public void selectCard(Card card, PlayerZone zone)
  {
    if(zone.is(Constant.Zone.Play, Constant.Player.Human) &&
       card.isCreature()    &&
       card.isUntapped()    &&
       CombatUtil.canAttack(card)
       )
    {
      if(! card.getKeyword().contains("Vigilance"))
      {
        card.tap();

        //otherwise cards stay untapped, not sure why this is needed but it works
        AllZone.Human_Play.updateObservers();
      }
      AllZone.Combat.addAttacker(card);

      //for Castle Raptors, since it gets a bonus if untapped
      GameActionUtil.executeCardStateEffects();

      CombatUtil.showCombat();
    }
  }//selectCard()
}