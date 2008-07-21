abstract public class Ability extends SpellAbility
{
    public Ability(Card sourceCard, String manaCost)
    {
	super(SpellAbility.Ability, sourceCard);
	setManaCost(manaCost);
    }
    public Ability(Card sourceCard, String manaCost, String stackDescription)
    {
      this(sourceCard, manaCost);
      setStackDescription(stackDescription);
    }
    public boolean canPlay()
    {
//      if(getSourceCard().isCreature() && (!getSourceCard().hasSickness()))
        return AllZone.GameAction.isCardInPlay(getSourceCard());

//      return false;
    }
}
