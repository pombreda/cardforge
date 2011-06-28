package net.slightlymagic.braids.game.tictactoe;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import net.slightlymagic.braids.game.ai.minimax.MinimaxGameState;
import net.slightlymagic.braids.game.ai.minimax.MinimaxMove;
import net.slightlymagic.braids.game.ai.minimax.MinimaxPlayer;
import net.slightlymagic.braids.game.ai.minimax.SimpleMiniMax;

public class TTTGameState implements MinimaxGameState {
	Boolean board[][];
	boolean isXsTurn;
	private TTTPlayer xPlayer;
	private TTTPlayer oPlayer;
	public static TTTTeam xTeam = new TTTTeam(true);
	public static TTTTeam oTeam = new TTTTeam(false);
	
	
	public TTTGameState() {
		board = new Boolean[3][3];
		isXsTurn = (SimpleMiniMax.random.nextInt(2) == 0);
		xPlayer = new TTTPlayer(true);
		oPlayer = new TTTPlayer(false);
	}
	
	
	public TTTGameState(TTTGameState that) {
		board = new Boolean[3][3];
		for (int col = 0; col < 3; col++) {
			for (int row = 0; row < 3; row++) {
				this.board[col][row] = that.board[col][row];
			}
		}
		this.isXsTurn = that.isXsTurn;
		this.xPlayer = that.xPlayer;
		this.oPlayer = that.oPlayer;
	
	}
	
	
	@Override
	public String toString() {
		StringBuffer result = new StringBuffer(12);
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 3; col++) {
				if (this.board[col][row] == null) {
					result.append('.');
				}
				else if (this.board[col][row]) {
					result.append('X');
				}
				else {
					result.append('O');
				}
			}
			result.append('|');
		}
		
		result.deleteCharAt(result.length() - 1);
		
		return result.toString();
	}
	
	
	//TODO @Override
	public MinimaxGameState cloneFor(MinimaxPlayer p) {
		return new TTTGameState(this);
	}
	
	
	//TODO @Override
	public List<MinimaxMove> getPossibleMoves() {
		
		if (hasXWon() != null) {
			return new ArrayList<MinimaxMove>(0);
		}
		
		
		ArrayList<MinimaxMove> result = new ArrayList<MinimaxMove>(9);
		for (int col = 0; col < 3; col++) {
			for (int row = 0; row < 3; row++) {
				if (board[col][row] == null) {
					result.add(new TTTMove(col, row, isXsTurn));
				}
			}
		}
		return result;
	}


	/**
	 * 
	 * @return null if nobody has won yet
	 */
	public Boolean hasXWon() {
		// Check for win conditions
		for (int row = 0; row < 3; row++) {
			if (getValueAt(0, row) != null &&
				getValueAt(0, row) == getValueAt(1, row) &&
				getValueAt(0, row) == getValueAt(2, row))
			{
				return getValueAt(0,row);
			}
		}
		
		for (int col = 0; col < 3; col++) {
			if (getValueAt(col, 0) != null &&
				getValueAt(col, 0) == getValueAt(col, 1) &&
				getValueAt(col, 0) == getValueAt(col, 2))
			{
				return getValueAt(col, 0);
			}
		}
		
		// Check diagonal (backslash)
		if (getValueAt(0,0) != null && 
			getValueAt(0,0) == getValueAt(1,1) &&
			getValueAt(1,1) == getValueAt(2,2))
		{
			return getValueAt(0, 0);
		}
		
		// Check diagonal (forward slash)
		if (getValueAt(0,2) != null && 
			getValueAt(0,2) == getValueAt(1,1) &&
			getValueAt(1,1) == getValueAt(2,0))
		{
			return getValueAt(0, 2);
		}

		return null;
	}


	public Boolean getValueAt(int col, int row) {
		return board[col][row];  // may be null
	}

	
	//TODO @Override
	public MinimaxPlayer whoHasPriority() {
		if (isXsTurn) {
			return xPlayer;
		}
		else {
			return oPlayer;
		}
	}

	
	public void set(int col, int row, Boolean x) {
		board[col][row] = x;
	}

	
	public void givePriorityToOtherTeam() {
		isXsTurn = (!isXsTurn);
	}
	
	
	public TTTPlayer getXPlayer() {
		return xPlayer;
	}
	
	
	public TTTPlayer getOPlayer() {
		return oPlayer;
	}


	public void givePriorityToXTeam(boolean b) {
		isXsTurn = b;
	}


	public void setRow(int row, String contents) {
		if (contents.length() != 3) {
			throw new IllegalArgumentException("contents must have length 3");
		}
		
		for (int col = 0; col < 3; col++) {
			Boolean val = null;
			Character coal = Character.toUpperCase(contents.charAt(col)); 
			if (coal == 'X') {
				val = true;
			}
			else if (coal == 'O') {
				val = false;
			}
			// Otherwise, stick with null.
			
			set(col, row, val);
		}
	}


	//TODO @Override
	public byte[] getDigest() {
		MessageDigest sha256;
		try {
			sha256 = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		
		byte[] digest = sha256.digest((this.toString()).getBytes());

		return digest;
	}
	
}

