package net.slightlymagic.braids.game.ai.minimax;

import static org.junit.Assert.*;



import net.slightlymagic.braids.game.tictactoe.TTTController;
import net.slightlymagic.braids.game.tictactoe.TTTGameState;
import net.slightlymagic.braids.game.tictactoe.TTTGameStateEvaluator;
import net.slightlymagic.braids.game.tictactoe.TTTMove;
import net.slightlymagic.braids.game.tictactoe.TTTPlayer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class SimpleMiniMaxTest {

	private TTTGameState gs;
	private SimpleMiniMax simpleMiniMax;
	private TTTController ctrl;
	private TTTGameStateEvaluator gse;

	@Before
	public void setUp() throws Exception {
		gs = new TTTGameState();
		gse = new TTTGameStateEvaluator();
		simpleMiniMax = new SimpleMiniMax(null);
		ctrl = new TTTController();
	}

	@After
	public void tearDown() throws Exception {
		gs = null;
		gse = null;
		simpleMiniMax = null;
		ctrl = null;
	}

	
	@Test
	public void test_suggestBestMoveForPlayer_block() {
		
		System.err.println("test_suggestBestMoveForPlayer_block:");
		
		simpleMiniMax.setMaxSearchDepth(6);

		gs.givePriorityToXTeam(true);
		gs.setRow(0, "OX.");
		gs.setRow(1, "O..");
		gs.setRow(2, "...");
		
		TTTPlayer whoHasPriority = (TTTPlayer) gs.whoHasPriority();

		MinimaxMove actual = simpleMiniMax.suggestBestMoveForPlayer(ctrl, gs, whoHasPriority, gse);
		MinimaxMove expected = new TTTMove(0, 2, true);
		
		assertEquals(expected, actual);

	
		gs.givePriorityToXTeam(true);
		gs.setRow(0, "O.X");
		gs.setRow(1, "O..");
		gs.setRow(2, "...");
		actual = simpleMiniMax.suggestBestMoveForPlayer(ctrl, gs, whoHasPriority, gse);
		assertEquals(expected, actual);

		
		gs.givePriorityToXTeam(true);
		gs.setRow(0, "O.X");
		gs.setRow(1, ".O.");
		gs.setRow(2, "...");
		expected = new TTTMove(2, 2, true);
		actual = simpleMiniMax.suggestBestMoveForPlayer(ctrl, gs, whoHasPriority, gse);
		assertEquals(expected, actual);

	}

	
	@Test
	public void test_suggestBestMoveForPlayer_firstMove() {
		
		System.err.println("test_suggestBestMoveForPlayer_firstMove:");

		// Be sure to set this to < 9.  If greater, it realizes that all
		// games result in a tie, and it thinks it can move anywhere.
		simpleMiniMax.setMaxSearchDepth(8);

		TTTPlayer whoHasPriority = (TTTPlayer) gs.whoHasPriority();
		boolean xHasPriority = true;
		if (whoHasPriority != gs.getXPlayer()) {
			xHasPriority = false;
		}

		MinimaxMove actual = simpleMiniMax.suggestBestMoveForPlayer(ctrl, gs, whoHasPriority, gse);
		MinimaxMove expected = new TTTMove(1, 1, xHasPriority);
		
		assertEquals(expected, actual);
	}

	
	@Test
	public void test_suggestBestMoveForPlayer_winDoNotBlock() {

		System.err.println("test_suggestBestMoveForPlayer_winDoNotBlock:");
		
		simpleMiniMax.setMaxSearchDepth(6);

		gs.givePriorityToXTeam(true);
		gs.setRow(0, "OX.");
		gs.setRow(1, "OX.");
		gs.setRow(2, "...");
		
		TTTPlayer whoHasPriority = (TTTPlayer) gs.whoHasPriority();

		MinimaxMove actual = simpleMiniMax.suggestBestMoveForPlayer(ctrl, gs, whoHasPriority, gse);
		MinimaxMove expected = new TTTMove(1, 2, true);
		
		assertEquals(expected, actual);

		gs.givePriorityToXTeam(true);
		gs.setRow(0, "O.X");
		gs.setRow(1, "O.X");
		gs.setRow(2, "...");
		actual = simpleMiniMax.suggestBestMoveForPlayer(ctrl, gs, whoHasPriority, gse);
		expected = new TTTMove(2, 2, true);
		assertEquals(expected, actual);
	
	}

}
