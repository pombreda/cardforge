import java.util.*;

public class Input_Draw extends Input
{
    public void showMessage() 
    {
	if(AllZone.Phase.getActivePlayer().equals(Constant.Player.Computer))
	{
	    AllZone.GameAction.drawCard(Constant.Player.Computer);
	    AllZone.Phase.nextPhase();
	    return;
	}
	    
	boolean drawCard = true;
	if(0 < getDredge().size())
	{
	    String choices[] = {"Yes", "No"};
	    Object o = AllZone.Display.getChoice("Do you want to dredge?", choices);
	    if(o.equals("Yes"))
	    {
		drawCard = false;
		Card c = (Card) AllZone.Display.getChoice("Select card to dredge", getDredge().toArray());
		
		//might have to make this more sophisticated
		//dredge library, put card in hand
		AllZone.Human_Hand.add(c);
		AllZone.Human_Graveyard.remove(c);
		
		for(int i = 0; i < getDredgeNumber(c); i++)
		{
		    Card c2 = AllZone.Human_Library.get(0);
		    AllZone.Human_Library.remove(0);
		    AllZone.Human_Graveyard.add(c2);
		}
	    }
	}//if(0 < getDredge().size())
	
	if(drawCard)
	    AllZone.GameAction.drawCard(Constant.Player.Human);
	
	if(AllZone.Phase.getPhase().equals(Constant.Phase.Draw))
	{
	    Input_Main.canPlayLand = true;
	    AllZone.Phase.nextPhase();
	}
	else
	    stop();
    }
    public ArrayList getDredge()
    {
	ArrayList dredge = new ArrayList();
	Card c[] = AllZone.Human_Graveyard.getCards();

	for(int outer = 0; outer < c.length; outer++)
	{
	    ArrayList a = c[outer].getKeyword();
	    for(int i = 0; i < a.size(); i++)
		if(a.get(i).toString().startsWith("Dredge"))
		{
		    if(AllZone.Human_Library.size() >= getDredgeNumber(c[outer]))
			dredge.add(c[outer]);
		}
	}
	return dredge;
    }//hasDredge()
    public int getDredgeNumber(Card c)
    {
	ArrayList a = c.getKeyword();
	for(int i = 0; i < a.size(); i++)
	    if(a.get(i).toString().startsWith("Dredge"))
	    {
		String s = a.get(i).toString();
		return Integer.parseInt("" +s.charAt(s.length() -1));
	    }
	
	throw new RuntimeException("Input_Draw : getDredgeNumber() card doesn't have dredge - " +c.getName());
    }//getDredgeNumber()
}
