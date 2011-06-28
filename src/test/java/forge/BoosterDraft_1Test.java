package forge;

import org.testng.annotations.Test;


/**
 * Unit test for simple App.
 */
@Test(groups = {"UnitTest"})
public class BoosterDraft_1Test {

    /**
     *
     */
    @Test
    public void BoosterDraft_1Test1() throws Exception {
        BoosterDraft_1 draft = new BoosterDraft_1();
        while (draft.hasNextChoice()) {
            CardList list = draft.nextChoice();
            System.out.println(list.size());
            draft.setChoice(list.get(0));
        }
    }
}