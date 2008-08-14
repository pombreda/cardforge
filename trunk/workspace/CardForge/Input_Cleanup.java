public class Input_Cleanup extends Input
{
    public void showMessage() 
    {
	ButtonUtil.disableAll();
	int n = AllZone.Human_Hand.getCards().length;

	//MUST showMessage() before stop() or it will overwrite the next Input's message
	AllZone.Display.showMessage("Cleanup Phase: You can only have a maximum of 7 cards, you currently have " +n +" cards in your hand - select a card to discard");

	//goes to the next phase
	if(n <= 7)
	{   
	    CombatUtil.removeAllDamage();
	    AllZone.Phase.nextPhase();
	}	
    }
    public void selectCard(Card card, PlayerZone zone) 
    {
	if(zone.is(Constant.Zone.Hand, Constant.Player.Human))
	{
	    AllZone.GameAction.discard(card);
	    showMessage();
	}
    }//selectCard()
}
