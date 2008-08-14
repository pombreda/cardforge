import java.util.*;

public class Input_Main extends Input
{
    //Input_Draw changes this
    public static boolean canPlayLand;

    public void showMessage()
    {
	ButtonUtil.enableOnlyOK();

	if(AllZone.Phase.getPhase().equals(Constant.Phase.Main1))
	    AllZone.Display.showMessage("Main 1 Phase: Play any card");
	else
	    AllZone.Display.showMessage("Main 2 Phase: Play any card");
    }
    public void selectButtonOK()
    {
	AllZone.Phase.nextPhase();
    }
    public void selectCard(Card card, PlayerZone zone)
    {
	//these if statements cannot be combined
	if(card.isLand() && zone.is(Constant.Zone.Hand, Constant.Player.Human))
	{
	    if(canPlayLand)
	    {
		InputUtil.playAnyCard(card, zone);
		canPlayLand = false;
                AllZone.GameAction.checkStateEffects();
	    }
	}
	else
	{
//	    SpellAbility sa = card.getSpellAbility()[0];
//	    sa.setRandomTargetAI();
//	    AllZone.Stack.add(sa);
	    InputUtil.playAnyCard(card, zone);
	}
    }//selectCard()
}