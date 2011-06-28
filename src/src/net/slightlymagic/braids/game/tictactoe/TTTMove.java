package net.slightlymagic.braids.game.tictactoe;

import net.slightlymagic.braids.game.ai.minimax.MinimaxMove;

public class TTTMove implements MinimaxMove {
	private int column;
	private int row;
	private boolean isX;
	public TTTMove(int column, int row, boolean isX) {
		this.column = column;
		this.row = row;
		this.isX = isX;
	}
	public int getColumn() {
		return column;
	}
	public int getRow() {
		return row;
	}
	public boolean isX() {
		return isX;
	}
	
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("TTTMove(");
		buf.append(column);
		buf.append(',');
		buf.append(row);
		buf.append(',');
		
		if (isX) {
			buf.append('X');
		}
		else {
			buf.append('O');
		}
		buf.append(")");
		
		return buf.toString();
	}
	
	@Override
	public boolean equals(Object that2) {
		if (that2 == null) {
			return false;
		}
		
		if (!(that2 instanceof TTTMove)) {
			return false;
		}
		
		TTTMove that = (TTTMove) that2;
		
		if (this.column == that.column && 
			this.row == that.row &&
			this.isX == that.isX)
		{
			return true;
		}
		else 
		{
			return false;
		}
	}
}
