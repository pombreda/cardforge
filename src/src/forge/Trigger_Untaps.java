package forge;

import java.util.HashMap;

public class Trigger_Untaps extends Trigger {

	public Trigger_Untaps(HashMap<String, String> params, Card host) {
		super(params, host);
	}

	@Override
	public boolean performTest(HashMap<String, Object> runParams) 
	{
		Card untapper = (Card)runParams.get("Card");

		if(mapParams.containsKey("ValidCard"))
		{
			if(!untapper.isValidCard(mapParams.get("ValidCard").split(","), hostCard.getController(), hostCard))
			{
				return false;
			}
		}
		
		return true;
	}

	@Override
	public Trigger getCopy() {
		return new Trigger_Untaps(mapParams,hostCard);
	}
	
	@Override
	public Card getTriggeringCard(HashMap<String,Object> runParams)
	{
		return (Card)runParams.get("Card");
	}
}
