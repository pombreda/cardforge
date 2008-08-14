public class Computer_Draw extends Input
{
    public void showMessage() 
    {
	AllZone.GameAction.drawCard(Constant.Player.Computer);	
	AllZone.Phase.nextPhase();
    }    
}
