import java.util.*;

class ManaCost
{
  private ManaPool manaPool;
  private boolean hasX;

  //order of the colors doesn't matter
  //cost is like "2 W" or "3 W B R" or "G" or "X R", always put numbers first and use spaces
  public ManaCost(String cost)
  {
    manaPool = new ManaPool();

    StringTokenizer tok = new StringTokenizer(cost);

    while(tok.hasMoreTokens())
    {
      String s = tok.nextToken();

      if(Character.isDigit(s.charAt(0)))
      {
        manaPool.addColor(Constant.Color.Colorless, Integer.parseInt(s));
      }
      else if(s.equals("G"))
        manaPool.addColor(Constant.Color.Green);
      else if(s.equals("B"))
        manaPool.addColor(Constant.Color.Black);
      else if(s.equals("U"))
        manaPool.addColor(Constant.Color.Blue);
      else if(s.equals("R"))
        manaPool.addColor(Constant.Color.Red);
      else if(s.equals("W"))
        manaPool.addColor(Constant.Color.White);
      else if(s.equals("X"))
        hasX = true;
      else
        throw new RuntimeException("ManaCost : constructor() invalid mana cost -" +cost);
    }
  }
  public boolean isPaid()
  {
    String c[] = Constant.Color.Colors;
    for(int i = 0; i < c.length; i++)
      if(0 < manaPool.getColor(c[i]))
        return false;

    if((! hasX) && (0 < manaPool.getColor(Constant.Color.Colorless)))
      return false;

    return true;
  }
  public String toString()
  {
    String s = "";

    int colorless = manaPool.getColor(Constant.Color.Colorless);

    if((! hasX) && (colorless != 0))
      s = colorless +" ";

    String col[] = Constant.Color.Colors;

    for(int outer = 0; outer < col.length; outer++)
    {
      int number = manaPool.getColor(col[outer]);

      for(int inner = 0; inner < number; inner++)
      {
        if(col[outer].equals(Constant.Color.Blue))
          s += "U";
        else if(col[outer].equals(Constant.Color.Black))
          s += "B";
        else if(col[outer].equals(Constant.Color.Green))
          s += "G";
        else if(col[outer].equals(Constant.Color.Red))
          s += "R";
        else if(col[outer].equals(Constant.Color.White))
          s += "W";

        s += " ";
      }
    }
    if(hasX)
      s = s +"X = " +(int)Math.abs(manaPool.getColor(Constant.Color.Colorless)) +" ";

    s = s.toUpperCase();

    return s;
  }
  public void subtractMana(String color)
  {
    int n = manaPool.getColor(color);

    //are all colored mana requirements paid?
    if(n == 0)
      manaPool.subtractColor(Constant.Color.Colorless);
    else
      manaPool.subtractColor(color);
  }
  public void subtractMana(String color, int n)
  {
    for(int i = 0; i < n; i++)
      manaPool.subtractColor(color);
  }
  public int getColor(String color)
  {
    return (int)Math.abs(manaPool.getColor(color));
  }
  public boolean isNeeded(String color)
  {
    int a = getColor(color);
    int b = getColor(Constant.Color.Colorless);

    if(0 < a || 0 < b)
      return true;

    return false;
  }
}//ManaCost
