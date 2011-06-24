package net.slightlymagic.braids.game.ai.minimax;

@SuppressWarnings("serial")
public class WrongPlayerException extends RuntimeException {
	public WrongPlayerException(String message) {
		super(message);
	}
}
