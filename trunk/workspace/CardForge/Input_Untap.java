public class Input_Untap extends Input
{
  public void showMessage()
  {
    GameActionUtil.executeUpkeepEffects();

    PlayerZone p =  AllZone.getZone(Constant.Zone.Play, AllZone.Phase.getActivePlayer());
    Card[] c = p.getCards();

    for(int i = 0; i < c.length; i++)
      c[i].setSickness(false);

    if(isMarbleTitanInPlay())
      marbleUntap();
    else
      regularUntap();

    //otherwise land seems to stay tapped when it is really untapped
    AllZone.Human_Play.updateObservers();
    AllZone.Phase.nextPhase();
  }
  private void marbleUntap()
  {
    PlayerZone p =  AllZone.getZone(Constant.Zone.Play, AllZone.Phase.getActivePlayer());
    Card[] c = p.getCards();

    for(int i = 0; i < c.length; i++)
      if(c[i].getAttack() < 3)
        c[i].untap();
  }
  private boolean isMarbleTitanInPlay()
  {
    CardList all = new CardList();
    all.addAll(AllZone.Human_Play.getCards());
    all.addAll(AllZone.Computer_Play.getCards());

    all = all.getName("Marble Titan");
    return all.size() > 0;
  }
  private void regularUntap()
  {
    PlayerZone p =  AllZone.getZone(Constant.Zone.Play, AllZone.Phase.getActivePlayer());
    Card[] c = p.getCards();

    for(int i = 0; i < c.length; i++)
      c[i].untap();
  }//regularUntap()
}