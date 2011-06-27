package forge.card.cardFactory;

public class CardNotFoundException extends Exception {

	private static final long serialVersionUID = 8785315946642295346L;

	public CardNotFoundException() {
	}

	public CardNotFoundException(String arg0) {
		super(arg0);
	}

	public CardNotFoundException(Throwable arg0) {
		super(arg0);
	}

	public CardNotFoundException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
