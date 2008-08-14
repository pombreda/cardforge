import java.util.*;

public class Input_PayManaCostUtil
{
  //all mana abilities start with this and typical look like "tap: add G"
  //mana abilities are Strings and are retreaved by calling card.getKeyword()
  private static final String manaString = "tap: add ";

  //taps any card that has mana ability, not just land
  public static ManaCost tapCard(Card card, ManaCost manaCost)
  {
    ArrayList mana = getManaAbilities(card);

    if(mana.isEmpty() || (card.isCreature() && card.hasSickness()))
      return manaCost;

    String color = mana.get(0).toString();
    if(1 < mana.size())
    {
      color = (String) AllZone.Display.getChoice("Choose mana", mana.toArray());
    }
    boolean coloredMana = true;
    if(color.equals(Constant.Color.Colorless))
      coloredMana = false;

    if(isManaNeeded(color, manaCost))
    {
      card.tap();
      manaCost.subtractMana(color);
      AllZone.Human_Play.updateObservers();//DO NOT REMOVE THIS, otherwise the cards don't always tap

      //pain lands
      ArrayList pain = new ArrayList();
      pain.add("Battlefield Forge");
      pain.add("Caves of Koilos");
      pain.add("Llanowar Wastes");
      pain.add("Shivan Reef");
      pain.add("Yavimaya Coast");
      pain.add("Adarkar Wastes");
      pain.add("Brushland");
      pain.add("Karplusan Forest");
      pain.add("Underground River");
      pain.add("Sulfurous Springs");
      if(pain.contains(card.getName()) && coloredMana)
        AllZone.GameAction.getPlayerLife(card.getController()).subtractLife(1);
    }

    return manaCost;
  }
  public static ArrayList getManaAbilities(Card card)
  {
    ArrayList check = card.getKeyword();
    ArrayList mana  = new ArrayList();

    char letter;

    for(int i = 0; i < check.size(); i++)
    {
      if(check.get(i).toString().startsWith(manaString))
      {
        //gets G from "tap: add G"
        letter = check.get(i).toString().charAt(manaString.length());
        mana.add(getColor(letter +""));
      }
    }

    return mana;
  }
  public static boolean isManaNeeded(String color, ManaCost manaCost)
   {
    //do not use the orginal, get new object
    manaCost = new ManaCost(manaCost.toString());
    manaCost.subtractMana(color);

    String s = manaCost.toString().trim();
    if(s != "" && s.charAt(0) == '-')//is this mana needed?
      return false;
    else
      return true;
  }
  //color is like "G", returns "Green"
  public static String getColor(String color)
  {
    Map m = new HashMap();
    m.put("G", Constant.Color.Green);
    m.put("R", Constant.Color.Red);
    m.put("U", Constant.Color.Blue);
    m.put("B", Constant.Color.Black);
    m.put("W", Constant.Color.White);

    Object o = m.get(color);

    if(o == null)
      o = Constant.Color.Colorless;


    return o.toString();
  }
}
