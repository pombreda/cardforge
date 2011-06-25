package forge;


/**
 * <p>BoosterPack class.</p>
 *
 * @author Forge
 * @version $Id: $
 */
public class BoosterPack {
    /**
     * <p>getBoosterPack.</p>
     *
     * @param number a int.
     * @return a {@link forge.CardList} object.
     */
    public static CardList getBoosterPack(int number) {
        CardList all = new CardList();
        for (int i = 0; i < number; i++)
            all.addAll(getBoosterPack());

        return all;
    }

    /**
     * <p>getBoosterPack.</p>
     *
     * @return a {@link forge.CardList} object.
     */
    public static CardList getBoosterPack() {
        CardList all = AllZone.getCardFactory().getAllCards();
        CardList pack = new CardList();

        for (int i = 0; i < 10; i++)
            pack.add(all.get(MyRandom.random.nextInt(all.size())));

        for (int i = 0; i < 5; i++)
            pack.add(AllZone.getCardFactory().copyCard(pack.get(i)));

        return pack;
    }//getBoosterPack()
}
