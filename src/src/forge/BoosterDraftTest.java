package forge;

import forge.deck.Deck;

/**
 * <p>BoosterDraftTest class.</p>
 *
 * @author Forge
 * @version $Id: $
 */
class BoosterDraftTest implements BoosterDraft {
    int n = 3;

    /**
     * <p>getDecks.</p>
     *
     * @return an array of {@link forge.deck.Deck} objects.
     */
    public Deck[] getDecks() {
        return null;
    }

    /**
     * <p>nextChoice.</p>
     *
     * @return a {@link forge.CardList} object.
     */
    public CardList nextChoice() {
        n--;
        ReadDraftBoosterPack pack = new ReadDraftBoosterPack();
        return pack.getBoosterPack();
    }

    /** {@inheritDoc} */
    public void setChoice(Card c) {
        System.out.println(c.getName());
    }

    /**
     * <p>hasNextChoice.</p>
     *
     * @return a boolean.
     */
    public boolean hasNextChoice() {
        return n > 0;
    }

    /**
     * <p>getChosenCards.</p>
     *
     * @return a {@link forge.CardList} object.
     */
    public CardList getChosenCards() {
        return null;
    }

    /**
     * <p>getUnchosenCards.</p>
     *
     * @return a {@link forge.CardList} object.
     */
    public CardList getUnchosenCards() {
        return null;
    }
}
