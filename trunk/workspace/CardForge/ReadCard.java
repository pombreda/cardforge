import java.io.*;
import java.util.*;

public class ReadCard implements Runnable
{
    private BufferedReader in;
    private ArrayList allCards = new ArrayList();

    public static void main(String args[]) throws Exception
    {
	try
	{
	    ReadCard read = new ReadCard(Constant.IO.cardFile);

	    javax.swing.SwingUtilities.invokeAndWait(read);
	    //    read.run();

	    Card c[] = new Card[read.allCards.size()];
	    read.allCards.toArray(c);
	    for(int i = 0; i < c.length; i++)
	    {
		System.out.println(c[i].getName());
		System.out.println(c[i].getManaCost());
		System.out.println(c[i].getType());
		System.out.println(c[i].getSpellText());
		System.out.println(c[i].getKeyword());
		System.out.println(c[i].getAttack() +"/" +c[i].getDefense() +"\n");
	    }
	}catch(Exception ex)
	{
	    System.out.println("Error reading file " +ex);
	}
    }
    public ArrayList getCards()
    {
	return new ArrayList(allCards);
    }

    public ReadCard(String filename)
    {
      File file = new File(filename);
      if(! file.exists())
        throw new RuntimeException("ReadCard : constructor error -- file not found -- filename is " +file.getAbsolutePath());

	//makes the checked exception, into an unchecked runtime exception
	try
	{
	    in = new BufferedReader(new FileReader(filename));
	}
	catch(Exception ex)
	{throw new RuntimeException("ReadCard : constructor error -- file not found -- filename is " +filename); }
    }//ReadCard()

    public void run()
    {
	Card c;
	String s = readLine();
	ArrayList cardNames = new ArrayList();

	while(! s.equals("End"))
	{
	    c = new Card();
	    if(s.equals(""))
		throw new RuntimeException("ReadCard : run() reading error, cardname is blank");
	    c.setName(s);

//for debugging
//System.out.println(c.getName());


	    s = readLine();
	    if(! s.equals("no cost"))
		c.setManaCost(s);

	    s = readLine();
	    addTypes(c, s);

	    s = readLine();
	    if(! s.equals("no text"))
		c.setText(s);

	    s = readLine();
	    if(c.isCreature())
	    {

		int n = s.indexOf("/");
		int att = Integer.parseInt(s.substring(0, n));
		int def = Integer.parseInt(s.substring(n + 1));
		c.setAttack(att);
		c.setDefense(def);
		s = readLine();
	    }

	    while(! s.equals(""))
	    {
		c.addKeyword(s);
		s = readLine();
	    }
	    s = readLine();

	    if(cardNames.contains(c.getName()))
	    {
		System.out.println("ReadCard:run() error - duplicate card name: " +c.getName());
		throw new RuntimeException("ReadCard:run() error - duplicate card name: " +c.getName());
	    }

	    cardNames.add(c.getName());
	    allCards.add(c);
	}
    }//run()

    private void addTypes(Card c, String types)
    {
	StringTokenizer tok = new StringTokenizer(types);
	while(tok.hasMoreTokens())
	    c.addType(tok.nextToken());
    }
    private String readLine()
    {
	//makes the checked exception, into an unchecked runtime exception
	try
	{
	    String s = in.readLine();
	    if(s != null)
		s = s.trim();
 	    return s;
	}catch(Exception ex)
	{throw new RuntimeException("ReadCard : readLine(Card) error");}
    }//readLine(Card)
}