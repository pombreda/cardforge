package forge;


import java.util.ArrayList;


/**
 * <p>TestMove class.</p>
 *
 * @author Forge
 * @version $Id: $
 */
class TestMove extends Move {
    private final int myNumber;

    /** Constant <code>classNumber=-1</code> */
    private static int classNumber = -1;
    /** Constant <code>array=</code> */
    private static int[] array;

    /** Constant <code>classIndex=</code> */
    private static int classIndex;
    private int myIndex = -1;

    /**
     * <p>main.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     */
    public static void main(String[] args) {
        test();
    }

    //branching 2
    //fully test depths 1 and 2, one test of depth 3
    /**
     * <p>test.</p>
     */
    public static void test() {
        TestMove t;

        t = new TestMove(new int[]{4, 1, 6, 3, 2, 7, 6, 9});
        test("1", t.max(t, 3, true) == 7);

        t = new TestMove(new int[]{1, 2});
        test("2", t.max(t, 1, true) == 2);

        t = new TestMove(new int[]{2, 1});
        test("3", t.max(t, 1, true) == 2);


        t = new TestMove(new int[]{1, 2, 3, 4});
        test("4", t.max(t, 2, true) == 3);

        t = new TestMove(new int[]{2, 1, 4, 3});
        test("5", t.max(t, 2, true) == 3);

        t = new TestMove(new int[]{4, 3, 1, 2});
        test("6", t.max(t, 2, true) == 3);

        t = new TestMove(new int[]{3, 4, 2, 1});
        test("7", t.max(t, 2, true) == 3);
    }

    /**
     * <p>test.</p>
     *
     * @param message a {@link java.lang.String} object.
     * @param shouldBeTrue a boolean.
     */
    public static void test(String message, boolean shouldBeTrue) {
        if (!shouldBeTrue) throw new RuntimeException(message);
    }

    /**
     * <p>Constructor for TestMove.</p>
     *
     * @param i_array an array of int.
     */
    public TestMove(int i_array[]) {
        this();

        classIndex = 0;
        array = i_array;
    }

    /**
     * <p>Constructor for TestMove.</p>
     */
    public TestMove() {
        myNumber = classNumber;
        classNumber++;
    }

    /**
     * <p>Getter for the field <code>classNumber</code>.</p>
     *
     * @return a int.
     */
    public int getClassNumber() {
        return classNumber;
    }

    /**
     * <p>Getter for the field <code>myNumber</code>.</p>
     *
     * @return a int.
     */
    public int getMyNumber() {
        return myNumber;
    }

    /** {@inheritDoc} */
    @Override
    public Move[] generateMoves() {
        ArrayList<TestMove> list = new ArrayList<TestMove>();

        for (int i = 0; i < 2; i++)
            list.add(new TestMove());

        Move m[] = new Move[list.size()];
        list.toArray(m);
        return m;
    }

    /** {@inheritDoc} */
    @Override
    public int getScore() {
        if (myIndex == -1) {
            myIndex = classIndex;
            classIndex++;
        }
        return array[myIndex];
    }//getScore()
}
