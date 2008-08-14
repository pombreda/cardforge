

public class Spell_Evoke extends Spell_Permanent
{
  private boolean evoke;

  public Spell_Evoke(Card sourceCard, String manaCost)
  {
    super(sourceCard);
    this.setManaCost(manaCost);
    this.setDescription("Evoke " +manaCost +" - Sacrifice this creature when it comes into play.");
  }
  public void resolve()
  {
    super.resolve();

    final Card card = getSourceCard();

    final SpellAbility ability = new Ability(card, "0")
    {
      public void resolve()
      {
        AllZone.GameAction.sacrifice(card);
      }
    };

    ability.setStackDescription("Evoke - sacrifice " +card);
    AllZone.Stack.add(ability);
  }//resolve()
}