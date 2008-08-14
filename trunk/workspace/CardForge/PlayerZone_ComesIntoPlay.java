public class PlayerZone_ComesIntoPlay extends DefaultPlayerZone
{
    private boolean trigger = true;
    
    public PlayerZone_ComesIntoPlay(String zone, String player)
    {
	super(zone, player);
    }
    public void add(Object o)
    {
	if(o == null)
	    throw new RuntimeException("PlayerZone_ComesInto Play : add() object is null");
	
	super.add(o);
	
	Card c = (Card)o;
	if(trigger)
	{
	    c.setSickness(true);//summoning sickness
	    c.comesIntoPlay();
	}
    }
    public void setTrigger(boolean b)
    {trigger = b;}
}
