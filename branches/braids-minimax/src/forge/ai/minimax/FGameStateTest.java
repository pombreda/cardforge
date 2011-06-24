package forge.ai.minimax;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.Iterator;

import net.slightlymagic.braids.game.ai.minimax.MinimaxMove;
import net.slightlymagic.braids.game.ai.minimax.MinimaxPlayer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import forge.Card;
import forge.Phase;
import forge.Player;
import forge.PlayerZone;
import forge.card.cardFactory.CardFactory;

public class FGameStateTest {

	private static CardFactory factory = Unstatic.getCardFactory();

	private FGameState gs;
	private FController ctrl;

	
	@Before
	public void setUp() throws Exception {
		gs = new FGameState();
		gs.bootstrap();
		ctrl = new FController();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testFGameState_ctor_noExceptions() {
		@SuppressWarnings("unused")
		FGameState state = new FGameState();
	}

	@Test
	public void test_gameSetup_for_further_tests() {

		// If you forget to do the following, WEIRD STUFF HAPPENS!
		Unstatic.setGlobalGameState(gs);  
		System.out.println("Game-state initialized.");
		
		addCardsByName(gs.getHumanLibrary(), new String[] {
			"Contagion Clasp",  // 4 T: Proliferate.
			"Island", "Island", "Island", "Island", 
			"Island", "Island", "Island", "Island",
			"Island", "Island", "Island", "Island", 
			"Island", "Island", "Island", "Island", 
			"Island", "Island", "Island", "Island", 
			"Island", "Island", "Island", "Island", 
		});
		System.out.println("Cards added to Human's library.");
		
		gs.getHumanPlayer().drawCards(5);
		System.out.println("Human draws 5 cards.");

		// I think I need to actually simulate the drawing and playing of cards.
		// The Islands on the human's battlefield do not register as having
		// mana-abilities.

		Card warrior = factory.getCard("Elvish Warrior", gs.getComputerPlayer());
		gs.getComputerLibrary().add(warrior);
		addCardsByName(gs.getComputerLibrary(), new String[] {
			"Forest", "Forest", "Forest", "Forest",  
			"Forest", "Forest", "Forest", "Forest",  
			"Forest", "Forest", "Forest", "Forest",  
			"Forest", "Forest", "Forest", "Forest",  
		});
		System.out.println("Cards added to Computer's library.");

		gs.getComputerPlayer().drawCards(3);
		System.out.println("Computer draws 3 cards.");

		Phase phase = gs.getPhase();
		phase.handleBeginPhase();
		System.out.println("Phase.handleBeginPhase() completed.");
		
		getNontrivialMoves(phase, 10);
		/*
		phase.nextPhase();  // now upkeep
		printMovesAndTakeIfOnlyOne(phase);
		phase.nextPhase();  // now draw
		printMovesAndTakeIfOnlyOne(phase);
		phase.nextPhase();  // now main1
		printMovesAndTakeIfOnlyOne(phase);
		phase.nextPhase();
		printMovesAndTakeIfOnlyOne(phase);
		phase.nextPhase();
		printMovesAndTakeIfOnlyOne(phase);
		phase.nextPhase();
		printMovesAndTakeIfOnlyOne(phase);
		phase.nextPhase();
		printMovesAndTakeIfOnlyOne(phase);
		phase.nextPhase();
		printMovesAndTakeIfOnlyOne(phase);
		phase.nextPhase();
		printMovesAndTakeIfOnlyOne(phase);
		phase.nextPhase();
		printMovesAndTakeIfOnlyOne(phase);
		phase.nextPhase();
		printMovesAndTakeIfOnlyOne(phase);
		phase.nextPhase();
		printMovesAndTakeIfOnlyOne(phase);
		phase.nextPhase();
		printMovesAndTakeIfOnlyOne(phase);
		phase.nextPhase();
		printMovesAndTakeIfOnlyOne(phase);
		phase.nextPhase();
		printMovesAndTakeIfOnlyOne(phase);
		phase.nextPhase();
		printMovesAndTakeIfOnlyOne(phase);
		*/

		
		// Stuff we can proliferate
		//warrior.addCounter(Counters.M1M1, 1);  // -1/-1 counter
		//gs.getComputerPlayer().addPoisonCounters(1);
		
		
		//moves = gs.getPossibleMoves();
		
		//assertTrue(moves.size() > 0);
	}

	/**
	 * If there is exactly one move besides PASS_MOVE, take it.  If PASS_MOVE
	 * is the only move, take it.  Otherwise return all moves.
	 * 
	 * @param phase  from FGameState.getPhase()
	 * @param  
	 * @return 
	 */
	protected Collection<MinimaxMove> getNontrivialMoves(Phase phase, 
			int maxMoves) 
	{
		Collection<MinimaxMove> moves;
		
		for (int i = 0; i < maxMoves; i++) {
			moves = gs.getPossibleMoves();
			
			int turnNum = phase.getTurn();
			Player playerTurn = phase.getPlayerTurn();
			String phaseName = phase.getPhase();
			MinimaxPlayer prioritrix = gs.whoHasPriority();
			System.out.println("For Turn " + turnNum + " (" + playerTurn + "'s) " + phaseName + " Step, " + prioritrix + "'s possible moves are " + moves.toString() + ".");
	
			if (moves.size() == 1 || 
				(moves.size() <= 2 && moves.contains(Constants.PASS_MOVE))) 
			{
				//System.out.println("Invoking only move.");
				Iterator<MinimaxMove> iter = moves.iterator();
				
				MinimaxMove move = null;
				while (iter.hasNext()) {
					move = iter.next();
					if (move != Constants.PASS_MOVE) {
						break;
					}
				}
				// If PASS_MOVE was the only move available, we take it. 
				System.out.println(gs.whoHasPriority() + " " + move + ".");
				ctrl.invoke(move, gs);
			}
			else {
				return moves;
			}
		}
		
		return null;
	}
	
	
	private void addCardsByName(PlayerZone zone, String[] cardNames) {
		for (int i = 0; i < cardNames.length; i++) {
			zone.add(factory.getCard(cardNames[i], zone.getPlayer()));
		}
	}

	
	@Test
	public void testBootstrap_noExceptions() {
		FGameState state = new FGameState();
		state.bootstrap();
	}

	@Test
	public void testCloneFor_noExceptions() {
		FGameState clone = (FGameState) gs.cloneFor(gs.getComputerPlayer());
		assertNotNull(clone);
	}

	@Test
	public void testNonOustedPlayers_size() {
		Collection<Player> players = gs.getNonOustedPlayers();
		assertEquals("size", 2, players.size());
	}

	@Test
	public void testFillWithDummyCardsIfNotAlreadyDoneSo() {
		PlayerZone zone = gs.getComputerHand();

		for (int i = 0; i < 7; i++) {
			Card card = Unstatic.getCardFactory().getCard("Elvish Warrior", 
				zone.getPlayer());
			
			zone.add(card);
		}
		

		FGameState.fillWithDummyCardsIfNotAlreadyDoneSo(zone);
		
		for (Card card : zone.getCards()) {
			assertEquals("card after first call", Constants.DUMMY_CARD, card);
		}

		FGameState.fillWithDummyCardsIfNotAlreadyDoneSo(zone);

		for (Card card : zone.getCards()) {
			assertEquals("card after second call", Constants.DUMMY_CARD, card);
		}
		
	}

	@Test
	public void testGetPossibleMoves() {
		fail("Not yet implemented");
	}

	@Test
	public void testWhoHasPriority() {
		fail("Not yet implemented");
	}

	@Test
	public void testWhoHasPriorityAsPlayer() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetDigest() {
		fail("Not yet implemented");
	}

}
