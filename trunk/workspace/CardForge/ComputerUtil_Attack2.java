import java.util.*;

public class ComputerUtil_Attack2
{
  //possible attackers and blockers
  private CardList attackers;
  private CardList blockers;
  private int blockerLife;

  public ComputerUtil_Attack2(Card[] possibleAttackers, Card[] possibleBlockers, int blockerLife)
  {
    this(new CardList(possibleAttackers), new CardList(possibleBlockers), blockerLife);
  }

  public ComputerUtil_Attack2(CardList possibleAttackers, CardList possibleBlockers, int blockerLife)
  {
    attackers = getUntappedCreatures(possibleAttackers, true);
    blockers  = getUntappedCreatures(possibleBlockers , false);
    this.blockerLife = blockerLife;

    final ArrayList valuable = new ArrayList();
    valuable.add("Kamahl, Pit Fighter");
    valuable.add("Elvish Piper");

    attackers = attackers.filter(new CardListFilter()
    {
      public boolean addCard(Card c)
      {
        return 0 < c.getAttack() && ! valuable.contains(c.getName());
      }
    });
  }//constructor
  public CardList getUntappedCreatures(CardList in, final boolean checkCanAttack)
  {
    CardList list = new CardList(in.toArray());
    list = list.filter(new CardListFilter()
    {
      public boolean addCard(Card c)
      {
        boolean b = c.isCreature() && c.isUntapped();

        if(checkCanAttack)
          return b && CombatUtil.canAttack(c);

        return b;
      }
    });
    return list;
  }//getUntappedCreatures()

  public Combat getAttackers()
  {
    Combat combat = new Combat();
    if(doAssault() /*|| (blockers.size() == 1 && 1 < attackers.size())*/)
    {
      for(int i = 0; i < attackers.size(); i++)
        combat.addAttacker(attackers.get(i));
    }
    else
    {
      Card bigDef;
      Card bigAtt;
      int nNotAttacking = 0;

      //so the biggest creature will usually attack
      //I think this works, not sure, may have to change it
      //sortNonFlyingFirst has to be done first, because it reverses everything
      CardListUtil.sortNonFlyingFirst(attackers);
      CardListUtil.sortAttackLowFirst(attackers);
/*
      //if under 7 life, always don't attack with smallest creature
      if(AllZone.Computer_Life.getLife() < 7 && 2 < attackers.size())
      {
        nNotAttacking++;
        attackers.remove(0);
      }
*/
      for(int i = 0; i < attackers.size(); i++)
      {
        //try to make the computer a little unpredictable
        //do not attack 9% of the time
        //always attack if 3 creatures are not already attacking
        if(MyRandom.percentTrue(9) && nNotAttacking < 3 && !blockers.isEmpty())
        {
          nNotAttacking++;
          continue;
        }

        bigAtt = getBiggestAttack(attackers.get(i));
        bigDef = getBiggestDefense(attackers.get(i));

        //if attacker can destroy biggest blocker or
        //biggest blocker cannot destroy attacker
        if(CombatUtil.canDestroyAttacker(bigDef, attackers.get(i)) /*||
           (! CombatUtil.canDestroyAttacker(attackers.get(i), bigAtt))*/)
          combat.addAttacker(attackers.get(i));
        else
          nNotAttacking++;
      }
    }//getAttackers()

    return combat;
  }//getAttackers()

  //returns 0/1 Card if no blockers found
  public Card getBiggestAttack(Card attack)
  {
    CardListUtil.sortAttack(blockers);
    for(int i = 0; i < blockers.size(); i++)
      if(CombatUtil.canBlock(attack, blockers.get(i)))
        return blockers.get(i);

    return AllZone.CardFactory.getCard("Birds of Paradise", "");
  }



  //returns 1/1 Card if no blockers found
  public Card getBiggestDefense(Card attack)
  {
    CardListUtil.sortDefense(blockers);
    for(int i = 0; i < blockers.size(); i++)
      if(CombatUtil.canBlock(attack, blockers.get(i)))
        return blockers.get(i);

    return AllZone.CardFactory.getCard("Elvish Piper", "");
  }

  public boolean doAssault()
  {
    CardListUtil.sortAttack(attackers);

    int totalAttack = 0;
    //presumes the Human will block
    for(int i = 0; i < (attackers.size() - blockers.size()); i++)
      totalAttack += attackers.get(i).getAttack();

    return totalAttack >= (blockerLife - 5)? true : false;
  }//doAssault()

  public void assignDamage(CardList list, int damage)
  {
    CardListUtil.sortAttack(list);
    int kill;
    for(int i = 0; i < list.size(); i++)
    {
      kill = list.get(i).getKillDamage();
      if(kill <= damage)
      {
        damage -= kill;
        list.get(i).setAssignedDamage(kill);
      }
    }
  }//assignDamage()
}