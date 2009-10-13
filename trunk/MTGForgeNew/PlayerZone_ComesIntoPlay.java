public class PlayerZone_ComesIntoPlay extends DefaultPlayerZone
{
	private static final long serialVersionUID = 5750837078903423978L;
	
	private boolean trigger = true;
	private boolean leavesTrigger = true;

	public PlayerZone_ComesIntoPlay(String zone, String player)
	{
		super(zone, player);
	}

	public void add(Object o)
	{
		if (o == null)
			throw new RuntimeException(
					"PlayerZone_ComesInto Play : add() object is null");

		super.add(o);

		Card c = (Card) o;
		if (trigger)
		{
			c.setSickness(true);// summoning sickness
			c.comesIntoPlay();
			
			PlayerZone play = AllZone.getZone(Constant.Zone.Play, c.getController());
			
			
			if (c.isLand())
			{
				//System.out.println("A land just came into play: " + c.getName());
				
				CardList list = new CardList(play.getCards());
				list = list.filter(new CardListFilter()
				{
					public boolean addCard(Card c) {
						return c.getKeyword().contains("Landfall");
					}
				});
				
				for (int i=0; i<list.size();i++)
				{
					GameActionUtil.executeLandfallEffects(list.get(i));
				}
				
			}//isLand()
			
			//hack to make tokens trigger ally effects:
			CardList clist = new CardList(play.getCards());
			clist = clist.filter(new CardListFilter()
			{
				public boolean addCard(Card c) {
					return c.getName().equals("Conspiracy") && c.getChosenType().equals("Ally");
				}
			});
			
			if (c.getType().contains("Ally") || clist.size() > 0)
			{
				CardList list = new CardList(play.getCards());
				list = list.filter(new CardListFilter()
				{
					public boolean addCard(Card c) {
						return c.getType().contains("Ally") || c.getKeyword().contains("Changeling");
					}				
				});
				
				for (Card var : list)
				{
					GameActionUtil.executeAllyEffects(var);
				}
			}
			
			
		}
		if (AllZone.StateBasedEffects.getCardToEffectsList().containsKey(c.getName()))
		{
			String[] effects = AllZone.StateBasedEffects.getCardToEffectsList().get(c.getName());
			for (String effect : effects) {
				AllZone.StateBasedEffects.addStateBasedEffect(effect);
			}	
		}
		
		
		/*
		for (String effect : AllZone.StateBasedEffects.getStateBasedMap().keySet() ) {
			Command com = GameActionUtil.commands.get(effect);
			com.execute();
		}
		*/

		//System.out.println("Size: " + AllZone.StateBasedEffects.getStateBasedMap().size());
	}
	
	public void remove(Object o)
	{
		
		super.remove(o);
		
		Card c = (Card) o;		
		if (leavesTrigger)
			c.leavesPlay();
		
		if (AllZone.StateBasedEffects.getCardToEffectsList().containsKey(c.getName()))
		{
			String[] effects = AllZone.StateBasedEffects.getCardToEffectsList().get(c.getName());
			String tempEffect = "";
			for (String effect : effects) {
				tempEffect = effect; 
				AllZone.StateBasedEffects.removeStateBasedEffect(effect);
				Command comm = GameActionUtil.commands.get(tempEffect); //this is to make sure cards reset correctly
				comm.execute();
			}
			
		}
		for (String effect : AllZone.StateBasedEffects.getStateBasedMap().keySet() ) {
			Command com = GameActionUtil.commands.get(effect);
			com.execute();
		}
		
		
	}

	public void setTrigger(boolean b)
	{
		trigger = b;
	}
	
	public void setLeavesTrigger(boolean b)
	{
		leavesTrigger = b;
	}
	
	public void setTriggers(boolean b)
	{
		trigger = b;
		leavesTrigger = b;
	}
}
