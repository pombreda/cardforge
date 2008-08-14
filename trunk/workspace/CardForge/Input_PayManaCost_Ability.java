import java.util.*;

//if cost is paid, Command.execute() is called
public class Input_PayManaCost_Ability extends Input
{
    private String originalManaCost;
    private ManaCost manaCost;
    private ArrayList tappedLand = new ArrayList();

    private Command paidCommand;
    private Command unpaidCommand;
    private boolean isPaid;

    //for Abilities that don't tap
    public Input_PayManaCost_Ability(String manaCost, Command paid)
    {
	this(manaCost, paid, Command.Blank);
    }

    //Command must set InputState, by calling AllZone.Input.selectState()
    //or AllZone.Input.setState() with new InputState
    public Input_PayManaCost_Ability(String manaCost_2, Command paidCommand_2, Command unpaidCommand_2)
    {
	originalManaCost = manaCost_2;

	manaCost      = new ManaCost(originalManaCost);
	paidCommand   = paidCommand_2;
	unpaidCommand = unpaidCommand_2;
    }
    public void resetManaCost()
    {
	manaCost      = new ManaCost(originalManaCost);
    }
    public void selectCard(Card card, PlayerZone zone)
    {
	if(zone.is(Constant.Zone.Play, Constant.Player.Human) &&
	card.isUntapped() )
	{
	    tappedLand.add(card);

	    //only tap card if the mana is needed
	    manaCost = Input_PayManaCostUtil.tapCard(card, manaCost);
	    showMessage();

	    if(manaCost.isPaid())
	    {
		resetManaCost();
		//VERY IMPORTANT, otherwise land can be "magical" untapped
		tappedLand.clear();

                //Command MUST BE AFTER, for Urborg Syphon-Mage - tap, mana, discard abilit
		stopSetNext(new ComputerAI_StackNotEmpty());
                paidCommand.execute();
	    }
	}//if
    }

    public void selectButtonCancel()
    {
	resetManaCost();
	unpaidCommand.execute();

	Card c;
	for(int i = 0; i < tappedLand.size(); i++)
	{
	    c = (Card)tappedLand.get(i);
	    c.untap();
	}
	tappedLand.clear();
	stop();
    }
    public void showMessage()
    {
	ButtonUtil.enableOnlyCancel();
	AllZone.Display.showMessage("Pay Mana Cost: \r\n" +manaCost.toString());
    }
}