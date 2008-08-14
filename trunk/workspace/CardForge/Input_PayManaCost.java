import java.util.*;

//pays the cost of a card played from the player's hand
//the card is removed from the players hand if the cost is paid
//CANNOT be used for ABILITIES
public class Input_PayManaCost extends Input
{
  private final String originalManaCost;

  private final Card originalCard;
  private ManaCost manaCost;

  private final ArrayList tappedLand = new ArrayList();
  private final SpellAbility spell;

  public Input_PayManaCost(SpellAbility sa)
  {
    originalManaCost = sa.getManaCost();
    originalCard = sa.getSourceCard();

    spell = sa;
    manaCost = new ManaCost(sa.getManaCost());
  }
  private void resetManaCost()
  {
      manaCost = new ManaCost(originalManaCost);
  }
  public void selectCard(Card card, PlayerZone zone)
  {
    if(zone.is(Constant.Zone.Play, Constant.Player.Human) &&
       card.isUntapped() )
    {
      tappedLand.add(card);

      //tap card if the mana is needed
      manaCost = Input_PayManaCostUtil.tapCard(card, manaCost);
      showMessage();

      if(manaCost.isPaid())
        done();
    }//if
  }
  private void done()
  {
    resetManaCost();

    //if tap ability, tap card
    if(spell instanceof Ability_Tap)
      originalCard.tap();

    //this seems to remove a card if it is in the player's hand
    //and trys to remove abilities, but no error messsage is generated
    AllZone.Human_Hand.remove(originalCard);

    AllZone.Stack.add(spell);
    stopSetNext(new ComputerAI_StackNotEmpty());
  }
  public void selectButtonCancel()
  {
    resetManaCost();

    Card c;
    for(int i = 0; i < tappedLand.size(); i++)
    {
      c = (Card)tappedLand.get(i);
      c.untap();
    }
    tappedLand.clear();
    AllZone.Human_Play.updateObservers();//DO NOT REMOVE THIS, otherwise the cards don't always tap

    stop();
  }
  public void showMessage()
  {
    ButtonUtil.enableOnlyCancel();
    AllZone.Display.showMessage("Pay Mana Cost: " +manaCost.toString());
  }
}