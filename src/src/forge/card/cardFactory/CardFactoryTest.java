package forge.card.cardFactory;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import forge.AllZone;
import forge.Card;
import forge.CardList;
import forge.Player;
import forge.properties.ForgeProps;
import forge.properties.NewConstants;

public class CardFactoryTest {

	private CardFactory factory;

	@Before
	public void setUp() throws Exception {
		factory = new CardFactory(ForgeProps.getFile(NewConstants.CARDSFOLDER));
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void test_getCard2_goodCards() {
		Player owner = AllZone.getHumanPlayer();

		helper_test_getCard2("Fireball", owner);
		helper_test_getCard2("Bog Imp", owner);
		helper_test_getCard2("Braids, Cabal Minion", owner);
		helper_test_getCard2("Braids, Conjurer Adept", owner);
		helper_test_getCard2("Ashen-Skin Zubera", owner);
	}

	protected void helper_test_getCard2(String cardName, Player owner) {
		Card actual = factory.getCard2(cardName, owner);
		assertEquals("card name", cardName, actual.getName());
		assertEquals("card owner", owner, actual.getOwner());
	}

	@Test
	public final void test_loadCard_identical() throws CardNotFoundException {
		String cardName = "Braids, Cabal Minion";
		Card expected = factory.loadCard(cardName);
		Card actual = factory.loadCard(cardName);
		
		assertSame(expected, actual);
	}

	/**
	 * This test takes a long time, so you might want to comment out the 
	 * @Test marker.
	 */
	//@Test
	public final void test_loadCard_bogusCard() {
		CardNotFoundException actualExn = null;
		
		String cardName = "\u00c6fjdsifhud, hfuidsfhudhuifdahsu-hdasifdahsufii";
		try {
			factory.loadCard(cardName);
		}
		catch (CardNotFoundException exn) {
			actualExn = exn;
		}
		
		assertNotNull("exception thrown", actualExn);
	}

	/**
	 * This test takes a long time, so you might want to comment out the 
	 * @Test marker.
	 */
	@Test
	public final void test_getAllCards() {
		CardList expected = factory.getCards();
		CardList actual = factory.getAllCards();
		
		int actualSize = actual.size();
		assertTrue("cardsfolder contains more than 1,000 cards", actualSize > 1000);
		
		assertEquals("length of card lists", expected.size(), actualSize);
		assertEquals("first element's name", expected.get(0).getName(), actual.get(0).getName());
		assertEquals("998th element's name", expected.get(998).getName(), actual.get(998).getName());
		assertEquals("last element's name", expected.get(actualSize-1).getName(), actual.get(actualSize-1).getName());
	}

}
