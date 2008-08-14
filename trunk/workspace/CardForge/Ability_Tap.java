abstract public class Ability_Tap extends SpellAbility implements java.io.Serializable
{
    public Ability_Tap(Card sourceCard)
    {
	this(sourceCard, "0");
    }
    public Ability_Tap(Card sourceCard, String manaCost)
    {
	super(SpellAbility.Ability, sourceCard);
	setManaCost(manaCost);
    }
    public boolean canPlay()
    {
	Card card = getSourceCard();

	if(AllZone.GameAction.isCardInPlay(card) && card.isUntapped())
	{
            if(card.isArtifact() && card.isCreature())
              return !card.hasSickness();

	    if(card.isCreature() && (!card.hasSickness()))
		return true;
	    else if(card.isArtifact() || card.isGlobalEnchantment() || card.isLand())
		return true;
	}
	return false;
    }
}