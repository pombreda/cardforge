public class CombatUtil
{
  public static boolean canBlock(Card attacker, Card blocker)
  {
    if(attacker == null || blocker == null)
      return false;

    PlayerZone blkPZ = AllZone.getZone(Constant.Zone.Play, blocker.getController());
    CardList blkCL = new CardList(blkPZ.getCards());

    if(attacker.getKeyword().contains("Plainswalk"))
    {
        blkCL = blkCL.getType("Plains");
        if(!blkCL.isEmpty())
            return false;
        return true;
    }

    if(attacker.getKeyword().contains("Islandwalk"))
    {
        blkCL = blkCL.getType("Island");
        if(!blkCL.isEmpty())
            return false;
        return true;
    }

    if(attacker.getKeyword().contains("Swampwalk"))
    {
       blkCL = blkCL.getType("Swamp");
        if(!blkCL.isEmpty())
            return false;
        return true;
    }

    if(attacker.getKeyword().contains("Mountainwalk"))
    {
        blkCL = blkCL.getType("Mountain");
        if(!blkCL.isEmpty())
            return false;
        return true;
    }

    if(attacker.getKeyword().contains("Forestwalk"))
    {
        blkCL = blkCL.getType("Forest");
        if(!blkCL.isEmpty())
            return false;
        return true;
    }


    if(blocker.getName().equals("Cloud Sprite") && !attacker.getKeyword().contains("Flying"))
      return false;;

    if(attacker.getKeyword().contains("Unblockable"))
      return false;

    if(blocker.getKeyword().contains("This creature cannot block"))
      return false;

    if(attacker.getKeyword().contains("Flying"))
      return blocker.getKeyword().contains("Flying") ||
      blocker.getKeyword().contains("This creature can block as though it had flying.");

    if(attacker.getKeyword().contains("Fear"))
      return blocker.getType().contains("Artifact")     ||
      CardUtil.getColors(blocker).contains(Constant.Color.Black) ||
      CardUtil.getColors(blocker).contains(Constant.Color.Colorless);


    if(attacker.getName().equals("Skirk Shaman"))
      return blocker.getType().contains("Artifact")     ||
      CardUtil.getColors(blocker).contains(Constant.Color.Red);


    return true;
  }//canBlock()
  public static boolean canAttack(Card c)
  {
    if(c.isTapped() || c.hasSickness() || c.getKeyword().contains("Defender"))
      return false;

    //if Card has Haste, Card.hasSickness() will return false
    return true;
  }
  public static boolean canDestroyAttacker(Card attacker, Card defender)
  {
    return defender.getAttack() >= (attacker.getDefense() - attacker.getDamage());
  }

  public static void removeAllDamage()
  {
    Card[] c = AllZone.Human_Play.getCards();
    for(int i = 0; i < c.length; i++)
      c[i].setDamage(0);

    c = AllZone.Computer_Play.getCards();
    for(int i = 0; i < c.length; i++)
      c[i].setDamage(0);
  }
  public static void showCombat()
  {
    //clear
    AllZone.Display.showCombat("");

    Card attack[] = AllZone.Combat.getAttackers();
    Card defend[] = null;
    String display = "";

    //loop through attackers
    for(int i = 0; i < attack.length; i++)
    {
      display += attack[i].getName() +" (" +attack[i].getUniqueNumber() +") "  +attack[i].getAttack() +"/" +attack[i].getDefense()   +" is attacking \n";

      defend = AllZone.Combat.getBlockers(attack[i]).toArray();

      //loop through blockers
      for(int inner = 0; inner < defend.length; inner++)
      {
        display += "     " +defend[inner].getName()  +" (" +defend[inner].getUniqueNumber()  +") "  +defend[inner].getAttack()  +"/" +defend[inner].getDefense()    +" is blocking \n";
      }
    }//while - loop through attackers
    String s = display + getPlaneswalkerBlockers();
    AllZone.Display.showCombat(s.trim());

  }//showBlockers()

  private static String getPlaneswalkerBlockers()
  {
    Card attack[] = AllZone.pwCombat.getAttackers();
    Card defend[] = null;
    String display = "";

    if(attack.length != 0)
      display = "Planeswalker Combat\r\n";

    //loop through attackers
    for(int i = 0; i < attack.length; i++)
    {
      display += attack[i].getName() +" (" +attack[i].getUniqueNumber() +") "  +attack[i].getAttack() +"/" +attack[i].getDefense()   +" is attacking \n";

      defend = AllZone.pwCombat.getBlockers(attack[i]).toArray();

      //loop through blockers
      for(int inner = 0; inner < defend.length; inner++)
      {
        display += "     " +defend[inner].getName()  +" (" +defend[inner].getUniqueNumber()  +") "  +defend[inner].getAttack()  +"/" +defend[inner].getDefense()    +" is blocking \n";
      }
    }//while - loop through attackers

    return display;
  }//getPlaneswalkerBlockers()
}