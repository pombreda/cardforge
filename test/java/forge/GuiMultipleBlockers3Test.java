package forge;

import org.testng.annotations.Test;

/**
 * Created by IntelliJ IDEA.
 * User: dhudson
 */
@Test(timeOut = 1000, enabled = false)
public class GuiMultipleBlockers3Test {

    /**
     *
     *
     */
    @Test(timeOut = 1000, enabled = false)
    public void GuiMultipleBlockers3Test1() {
        CardList list = new CardList();
        list.add(AllZone.getCardFactory().getCard("Elvish Piper", null));
        list.add(AllZone.getCardFactory().getCard("Lantern Kami", null));
        list.add(AllZone.getCardFactory().getCard("Frostling", null));
        list.add(AllZone.getCardFactory().getCard("Frostling", null));

        for (int i = 0; i < 2; i++) {
            new Gui_MultipleBlockers3(null, list, i + 1, null);
        }
    }
}
