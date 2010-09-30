package forge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import forge.error.ErrorViewer;

public class Generate2ColorDeck
{
	private String color1 = "";
	private String color2 = "";
	private Random r = null;
	private Map<String,String> ClrMap = null;
	private ArrayList<String> notColors = null;
	
	public Generate2ColorDeck(String Clr1, String Clr2)
	{				
		r = new Random();
		
		ClrMap = new HashMap<String,String>();
		ClrMap.put("White", "W");
		ClrMap.put("Blue", "U");
		ClrMap.put("Black", "B");
		ClrMap.put("Red", "R");
		ClrMap.put("Green", "G");
		
		notColors = new ArrayList<String>();
		notColors.add("White");
		notColors.add("Blue");
		notColors.add("Black");
		notColors.add("Red");
		notColors.add("Green");
		
		if (Clr1.equals("AI"))
		{
			// choose first color
			color1 = notColors.get(r.nextInt(5));
			
			// choose second color
			String c2 = notColors.get(r.nextInt(5));
			while (c2.equals(color1))
				c2 = notColors.get(r.nextInt(5));
			color2 = c2;
		}
		else
		{
			color1 = Clr1;
			color2 = Clr2;
		}
		
		notColors.remove(color1);
		notColors.remove(color2);
	}

	public CardList get2ColorDeck(int Size)
	{
		String tmpDeck = "";
		CardList tDeck = new CardList();
		
		Map<String, Integer> CardCounts = new HashMap<String, Integer>();
		
		int LandsPercentage = 42;
		int CreatPercentage = 34;
		int SpellPercentage = 24;
		
		// start with all cards
		CardList AllCards = AllZone.CardFactory.getAllCards();

		// remove cards that generated decks don't like
		AllCards = AllCards.filter(new CardListFilter(){
			public boolean addCard(Card c){
				return (!c.getSVar("RemAIDeck").equals("True"));
			}
		});
		
		// reduce to cards that match the colors
		CardList CL1 = AllCards.getColor(ClrMap.get(color1));
		CardList CL2 = AllCards.getColor(ClrMap.get(color2));
		
		// remove multicolor cards that don't match the colors
		CardListFilter clrF = new CardListFilter(){
			public boolean addCard(Card c){
				for (int i=0; i<notColors.size(); i++)
				{
					if (c.getManaCost().contains(ClrMap.get(notColors.get(i))))
						return false;
				}
				return true;
			}
		};
		CL1 = CL1.filter(clrF);
		CL2 = CL2.filter(clrF);
		
		// build subsets based on type
		CardList Cr1 = CL1.getType("Creature");
		CardList Cr2 = CL2.getType("Creature");
		
		String ISE[] = {"Instant", "Sorcery", "Enchantment", "Planeswalker"};
		CardList Sp1 = CL1.getValidCards(ISE);
		CardList Sp2 = CL2.getValidCards(ISE);

		// final card pools
		CardList Cr12 = new CardList();
		CardList Sp12 = new CardList();
				
		// used for mana curve in the card pool
		final int MinCMC[] = {1}, MaxCMC[] = {2};
		CardListFilter cmcF = new CardListFilter(){
			public boolean addCard(Card c){
				int cCMC = c.getCMC();
				return (cCMC >= MinCMC[0]) && (cCMC <= MaxCMC[0]);					
			}
		};
		
		// select cards to build card pools using a mana curve
		for (int i=4; i>0; i--)
		{
			CardList Cr1CMC = Cr1.filter(cmcF);
			CardList Cr2CMC = Cr2.filter(cmcF);
			CardList Sp1CMC = Sp1.filter(cmcF);
			CardList Sp2CMC = Sp2.filter(cmcF);
			
			for (int j=0; j<i; j++)
			{
				Card c = Cr1CMC.get(r.nextInt(Cr1CMC.size()));
				Cr12.add(c);
				CardCounts.put(c.getName(), 0);
				
				c = Cr2CMC.get(r.nextInt(Cr2CMC.size()));
				Cr12.add(c);
				CardCounts.put(c.getName(), 0);
				
				c = Sp1CMC.get(r.nextInt(Sp1CMC.size()));
				Sp12.add(c);
				CardCounts.put(c.getName(), 0);
				
				c = Sp2CMC.get(r.nextInt(Sp2CMC.size()));
				Sp12.add(c);
				CardCounts.put(c.getName(), 0);
			}
			
			MinCMC[0] += 2; MaxCMC[0] +=2;
			// resulting mana curve of the card pool
			//16x 1 - 2 
			//12x 3 - 4 
			//8x 5 - 6
			//4x 7 - 8
			//=40x - card pool could support up to a 275 card deck (all 4-ofs plus basic lands)
		}
		
		// shuffle card pools
		Cr12.shuffle();
		Sp12.shuffle();

		// calculate card counts
		float p = (float) ((float)CreatPercentage * .01);
		int CreatCnt = (int)(p * (float)Size);
		tmpDeck += "Creature Count:" + CreatCnt + "\n";
		
		p = (float) ((float)SpellPercentage * .01);
		int SpellCnt = (int)(p * (float)Size);
		tmpDeck += "Spell Count:" + SpellCnt + "\n";
		
		// build deck from the card pools
		for (int i=0; i<CreatCnt; i++)
		{
			Card c = Cr12.get(r.nextInt(Cr12.size()));
			while (CardCounts.get(c.getName()) > 3)
				c = Cr12.get(r.nextInt(Cr12.size()));
			
			tDeck.add(AllZone.CardFactory.getCard(c.getName(), Constant.Player.Computer));
			int n = CardCounts.get(c.getName());
			CardCounts.put(c.getName(), n + 1);
			tmpDeck += c.getName() + " " + c.getManaCost() + "\n";
		}
		
		for (int i=0; i<SpellCnt; i++)
		{
			Card c = Sp12.get(r.nextInt(Sp12.size()));
			while (CardCounts.get(c.getName()) > 3)
				c = Sp12.get(r.nextInt(Sp12.size()));
			
			tDeck.add(AllZone.CardFactory.getCard(c.getName(), Constant.Player.Computer));
			int n = CardCounts.get(c.getName());
			CardCounts.put(c.getName(), n + 1);
			tmpDeck += c.getName() + " " + c.getManaCost() + "\n";
		}
		
		// Add basic lands
		// TODO: dual lands?
		int numBLands = 0;
		if (LandsPercentage > 0)
		{
			p = (float)((float)LandsPercentage * .01);
			numBLands = (int)(p * (float)Size);
		}
		else 	// otherwise, just fill in the rest of the deck with basic lands
			numBLands = Size - tDeck.size();
		
		tmpDeck += "numBLands:" + numBLands + "\n";
		
		if (numBLands > 0)	// attempt to optimize basic land counts according to color representation
		{
			CCnt ClrCnts[] = {new CCnt("Plains", 0),
							  new CCnt("Island", 0),
							  new CCnt("Swamp", 0),
							  new CCnt("Mountain", 0),
							  new CCnt("Forest", 0)};
					
			// count each card color using mana costs
			// TODO: count hybrid mana differently?
			// TODO: count all color letters? ie: 2 W W counts as 2
			for (int i=0;i<tDeck.size(); i++)
			{
				Card c = tDeck.get(i);
				String mc = c.getManaCost();
				
				if (mc.contains("W"))
					ClrCnts[0].Count++;
				
				if (mc.contains("U"))
					ClrCnts[1].Count++;
				
				if (mc.contains("B"))
					ClrCnts[2].Count++;
				
				if (mc.contains("R"))
					ClrCnts[3].Count++;
	
				if (mc.contains("G"))
					ClrCnts[4].Count++;
			}
	
			// total of all ClrCnts
			int totalColor = 0;
			for (int i=0;i<5; i++)
			{
				totalColor += ClrCnts[i].Count;
				tmpDeck += ClrCnts[i].Color + ":" + ClrCnts[i].Count + "\n";
			}
			
			tmpDeck += "totalColor:" + totalColor + "\n";
			
			for (int i=0; i<5; i++)
			{
				if (ClrCnts[i].Count > 0)
				{	// calculate number of lands for each color
					p = (float)ClrCnts[i].Count / (float)totalColor;
					int nLand = (int)((float)numBLands * p);
					tmpDeck += "numLand-" + ClrCnts[i].Color + ":" + nLand + "\n";
					
					// just to prevent a null exception by the deck size fixing code
					CardCounts.put(ClrCnts[i].Color, nLand);
				
					for (int j=0; j<=nLand; j++)
						tDeck.add(AllZone.CardFactory.getCard(ClrCnts[i].Color, Constant.Player.Computer));
				}
			}
		}
		tmpDeck += "DeckSize:" + tDeck.size() + "\n";
		
		// fix under-sized or over-sized decks, due to integer arithmetic
		if (tDeck.size() < Size)
		{
			int diff = Size - tDeck.size();
			
			for (int i=0; i<diff; i++)
			{
				Card c = tDeck.get(r.nextInt(tDeck.size()));
				
				while (CardCounts.get(c.getName()) >= 4)
					c = tDeck.get(r.nextInt(tDeck.size()));
				
				int n = CardCounts.get(c.getName());
				tDeck.add(AllZone.CardFactory.getCard(c.getName(), Constant.Player.Computer));
				CardCounts.put(c.getName(), n + 1);
				tmpDeck += "Added:" + c.getName() + "\n";
			}
		}
		else if (tDeck.size() > Size)
		{
			int diff = tDeck.size() - Size;
			
			for (int i=0; i<diff; i++)
			{
				Card c = tDeck.get(r.nextInt(tDeck.size()));
				
				while (c.getType().contains("Basic"))	// don't remove basic lands
					c = tDeck.get(r.nextInt(tDeck.size()));
				
				tDeck.remove(c);
				tmpDeck += "Removed:" + c.getName() + "\n";
			}
		}

		tmpDeck += "DeckSize:" + tDeck.size() + "\n";
		//ErrorViewer.showError(tmpDeck);
		
		return tDeck;
	}
	
	class CCnt
	{
		public String Color;
		public int Count;
		
		public CCnt(String clr, int cnt)
		{
			Color = clr;
			Count = cnt;
		}
	}
}
