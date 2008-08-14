public class Spell_Permanent extends Spell
{
  public Spell_Permanent(Card sourceCard)
  {
    super(sourceCard);

    setManaCost(sourceCard.getManaCost());

    if(sourceCard.isCreature())
      setStackDescription(sourceCard.getName() +" - Creature " +sourceCard.getAttack() +" / " +sourceCard.getDefense());
    else
      setStackDescription(sourceCard.getName());

    setDescription(getStackDescription());
  }//Spell_Permanent()

  public boolean canPlayAI()
  {
    //check on legendary crap
    if(getSourceCard().getType().contains("Legendary"))
    {
      CardList list = new CardList(AllZone.Computer_Play.getCards());
      return ! list.containsName(getSourceCard().getName());
    }
    return true;
  }//canPlayAI()

  public void resolve()
  {
    Card c = getSourceCard();
    PlayerZone play = AllZone.getZone(Constant.Zone.Play, c.getController());
    play.add(c);
  }
}