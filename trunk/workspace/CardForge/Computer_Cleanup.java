import java.util.Random;

public class Computer_Cleanup extends Input
{
    public void showMessage()
    {
	Random r = new Random();
	Card[] c = AllZone.Computer_Hand.getCards();
	while(7 < c.length)
	{
	    AllZone.GameAction.discard(c[r.nextInt(c.length)]);
	    c = AllZone.Computer_Hand.getCards();
	}
	
	CombatUtil.removeAllDamage();
	AllZone.Phase.nextPhase();
    }
}
