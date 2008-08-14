public class Input_CombatDamage extends Input
{
  public Input_CombatDamage()
  {
    AllZone.Combat.verifyCreaturesInPlay();
    AllZone.pwCombat.verifyCreaturesInPlay();

    CombatUtil.showCombat();
  }

  public void showMessage()
  {
    ButtonUtil.enableOnlyOK();
    AllZone.Display.showMessage("Combat Damage is on the stack - Play Instants and Abilities");
  }
  public void selectButtonOK()
  {
    damageCreatureAndPlayer();

    AllZone.GameAction.checkStateEffects();

    AllZone.Combat.reset();
    AllZone.Display.showCombat("");
    AllZone.Phase.nextPhase();
  }
  public void selectCard(Card card, PlayerZone zone)
  {
    InputUtil.playInstantAbility(card, zone);
  }//selectCard()
  private void playerDamage(PlayerLife p)
  {
    int n = p.getAssignedDamage();
    p.setAssignedDamage(0);
    p.subtractLife(n);
  }

  //moves assigned damage to damage for all creatures
  //deals damage to player if needed
  private void damageCreatureAndPlayer()
  {
    PlayerLife life = AllZone.GameAction.getPlayerLife(AllZone.Combat.getDefendingPlayer());
    life.subtractLife(AllZone.Combat.getDefendingDamage());

    life = AllZone.GameAction.getPlayerLife(AllZone.Combat.getAttackingPlayer());
    life.subtractLife(AllZone.Combat.getAttackingDamage());
    life.subtractLife(AllZone.pwCombat.getAttackingDamage());


    //get all attackers and blockers
    CardList check = new CardList();
    check.addAll(AllZone.Human_Play.getCards());
    check.addAll(AllZone.Computer_Play.getCards());

    CardList all = check.getType("Creature");

    if(AllZone.pwCombat.getPlaneswalker() != null)
      all.add(AllZone.pwCombat.getPlaneswalker());

    Card c;
    for(int i = 0; i < all.size(); i++)
    {
      c = all.get(i);
      //because this sets off Jackal Pup, and Filthly Cur damage ability
      //and the stack says "Jack Pup causes 0 damage to the Computer"
      if(c.getAssignedDamage() != 0)
      {
        c.addDamage(c.getAssignedDamage());
        c.setAssignedDamage(0);
      }
    }
  }//moveDamage()
}