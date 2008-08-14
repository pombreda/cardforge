import java.io.*;
import java.util.*;

public class NameChanger
{
  private Map mutatedMap = new HashMap();
  private Map originalMap = new HashMap();

  private boolean changeCardName;

  public NameChanger()
  {
    readFile();
    setShouldChangeCardName(false);
  }

  //should change card name?
  public boolean shouldChangeCardName()
  {
    return changeCardName;
  }
  public void setShouldChangeCardName(boolean b)
  {
    changeCardName = b;
  }

  //returns an array of copies
  public Card[] changeCard(Card c[])
  {
    for(int i = 0; i < c.length; i++)
      changeCard(c[i]);

    return c;
  }

  //changes card name, getText(), and all SpellAbility getStackDescription() and toString()
  public Card changeCard(Card c)
  {
    //change name
    String newName = changeName(c.getName());
    c.setName(newName);

    //change text
    String s;
    s = c.getSpellText();
    c.setText(changeString(c, s));

    //change all SpellAbilities
    SpellAbility[] spell = c.getSpellAbility();
    for(int i = 0; i < spell.length; i++)
    {
      s = spell[i].getStackDescription();
      spell[i].setStackDescription(changeString(c, s));

      s = spell[i].toString();
      spell[i].setDescription(changeString(c, s));
    }

    return c;
  }//getMutatedCard()

  public String changeString(Card c, String in)
  {
    String name = getOriginalName(c.getName());
    in = in.replaceAll(name, changeName(name));

    return in;
  }

  //always returns mutated (alias) for the card name
  //if argument is a mutated name, it returns the same mutated name
  public String changeName(String originalName)
  {
    Object o = mutatedMap.get(originalName);

    if(o == null)
      return originalName;

    return o.toString();
  }//getMutatedName()

  //always returns the original cardname
  //if argument is a original name, it returns the same original name
  public String getOriginalName(String mutatedName)
  {
    Object o = originalMap.get(mutatedName);

    if(o == null)
      return mutatedName;

    return o.toString();
  }//getOriginalName()

  private void readFile()
  {
    try{
      BufferedReader in = new BufferedReader(new FileReader("name-mutator.txt"));

      String line = in.readLine();

      //stop reading if end of file or blank line is read
      while(line != null && (line.trim().length() != 0))
      {
        processLine(line.trim());

        line = in.readLine();
      }//while
    }//try
    catch(Exception ex)
    {
      throw new RuntimeException("NameMutator : readFile() error, " +ex);
    }
  }//readFile()

  //line is formated "original card name : alias card name"
  private void processLine(String line)
  {
    StringTokenizer tok = new StringTokenizer(line, ":");

    if(tok.countTokens() != 2)
      throw new RuntimeException("NameMutator : processLine() error, invalid line in file name-mutator.txt - " +line);

    String original = tok.nextToken().trim();
    String mutated = tok.nextToken().trim();

    mutatedMap.put(original, mutated);
    originalMap.put(mutated, original);
  }

  private void printMap(Map map)
  {
    Iterator it = map.keySet().iterator();
    String key;
    for(int i = 0; i < 5; i++)
    {
      key = it.next().toString();
      System.out.println(key +" : " +map.get(key));
    }
  }

  public static void main(String[] args)
  {
  }//main()
}