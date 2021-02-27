package spatial.kdpoint;

import org.junit.*;

import java.util.Random;

import static org.junit.Assert.*;
import static spatial.kdpoint.KDPoint.ZERO;
import static spatial.kdpoint.KDPoint.euclideanDistance;

/**
 * <p>{@link KDPointTests} is a unit testing class for {@link KDPoint}. It has next to nothing
 * to do with your project and is just here to provide some confidence about the fact that
 * {@link KDPoint}s should work as advertised. </p>
 *
 * <p>It might be of interest to you to see how the method {@link Assert#assertEquals(double, double, double)}
 * can be used when comparing doubles in Java. It turns out that comparing doubles in Java is not particularly safe
 * because of precision issues. In fact, it is recommended that one uses {@link java.math.BigDecimal} instead,
 * which offers arbitrary long precision.</p>
 *
 * @author <a href = "https://github.com/jasonfilippou">Jason Filippou</a>
 */
public class KDPointTests {

    private KDPoint origin2D, origin3D;
    private Random r;
    private static final int SEED = 47;
    private static final int MAX_ITER = 100000;
    private static final int MAX_DIM = 1000;
    private static final int MAX_COORD = 100;
    private static final double EPS = Math.pow(10, -8); // An epsilon value for some sqrt() comparisons.

    @Before
    public void setUp() {
        origin2D = new KDPoint(0, 0);
        origin3D = new KDPoint(0, 0, 0);
        r = new Random(SEED); // Re-producible results via static seed.
    }

    @After
    public void tearDown() {
        origin2D = origin3D = null;
        r = null;
    }

    @Test
    public void testKDPointArgFreeConstructor() {
        assertEquals("A freshly created KDPoint should represent the 2D Cartesian origin (0, 0).",
                ZERO, new KDPoint());

    }

    @Test
    public void testKDPointArgConstructor() {
        KDPoint point = new KDPoint(2, -9, 0, -34);
        assertEquals("The length of KDPoint with 4 dimensions must be 4", 4, point.coords.length);
        assertEquals("The first dimension's value should have been 2 for point (2, -9, 0, -34)", 2, point.coords[0]);
        assertEquals("The second dimension's value should have been -9 for point (2, -9, 0, -34)", -9, point.coords[1]);
        assertEquals("The third dimension's value should have been 0 for point (2, -9, 0, -34)", 0, point.coords[2]);
        assertEquals("The fourth dimension's value should have been -34 for point (2, -9, 0, -34)", -34, point.coords[3]);
    }

    @Test
    public void testKDPointKDPoint() {
        assertEquals("KDPoint created from copy constructor should have been equal to the original", new KDPoint(origin2D), origin2D);
        assertEquals("KDPoint created from copy constructor should have been equal to the original", new KDPoint(origin3D), origin3D);
        KDPoint fourDPoint = new KDPoint(-20, 6, 0, -9);
        assertEquals("KDPoint created from copy constructor should have been equal to the original", new KDPoint(fourDPoint), fourDPoint);

    }

    @Test
    public void testKDPointDistanceKDPoint() {

        // Trivial zero distances
        String messageTrivialDistance = "The distance between a point and itself must be 0";
        assertEquals(messageTrivialDistance, 0, origin2D.euclideanDistance(origin2D), 0);
        assertEquals(messageTrivialDistance, 0, origin3D.euclideanDistance(origin3D), 0);
        for (int i = 0; i < MAX_ITER; i++) {
            KDPoint p = new KDPoint(-r.nextInt(MAX_COORD), r.nextInt(MAX_COORD));
            assertEquals(messageTrivialDistance, 0, p.euclideanDistance(p), 0);
        }

        // Let's also check if some exceptions are properly thrown.
        try {
        	origin2D.euclideanDistance(origin3D);
        	fail("Expected RuntimeException");
        } catch (RuntimeException e) {}

        try {
        	origin3D.euclideanDistance(origin2D);
        	fail("Expected RuntimeException");
        } catch (RuntimeException e) {}

        try {
        	euclideanDistance(origin2D, origin3D);
        	fail("Expected RuntimeException");
        } catch (RuntimeException e) {}

        try {
        	euclideanDistance(origin3D, origin2D);
        	fail("Expected RuntimeException");
        } catch (RuntimeException e) {}

        // Simple stuff first, 1-D points!
        KDPoint first = new KDPoint(3), second = new KDPoint(0);
        assertEquals(messageTrivialDistance, 0, first.euclideanDistance(first), 0);
        assertEquals(messageTrivialDistance, 0, second.euclideanDistance(second), 0);
        assertEquals("The  Euclidean distance between two points is wrong", 3,
                first.euclideanDistance(second), 0);
        assertEquals("The  Euclidean distance between two points is wrong", 3,
                second.euclideanDistance(first), 0);
        KDPoint three = new KDPoint(-3);
        assertEquals("The  Euclidean distance between two points is wrong", 3,
                second.euclideanDistance(three), 0);
        assertEquals("The  Euclidean distance between two points is wrong", 3,
                three.euclideanDistance(second), 0);

        // Classic.
        KDPoint oneOne = new KDPoint(1, 1);
        assertEquals("The  Euclidean distance between (1,1) and (0,0) should be root 2", Math.sqrt(2),
                new KDPoint().euclideanDistance(oneOne), EPS);
        KDPoint minusOneOne = new KDPoint(-1, 1);
        assertEquals("The  Euclidean distance between (1,-1) and (0,0) should be root 2", Math.sqrt(2),
                new KDPoint().euclideanDistance(minusOneOne), EPS);
        KDPoint oneMinusOne = new KDPoint(1, -1);
        assertEquals("The  Euclidean distance between (-1,1) and (0,0) should be root 2", Math.sqrt(2),
                new KDPoint().euclideanDistance(oneMinusOne), EPS);
        KDPoint minusOneminusOne = new KDPoint(-1, -1);
        assertEquals("The  Euclidean distance between (-1,-1) and (0,0) should be root 2", Math.sqrt(2),
                new KDPoint().euclideanDistance(minusOneminusOne), EPS);

        // A not so trivial one
        KDPoint complexPointOne = new KDPoint(3, 2, -1);
        KDPoint complexPointTwo = new KDPoint(1, 3, 1);
        assertEquals("The  Euclidean distance between two points is wrong", 3,
                complexPointOne.euclideanDistance(complexPointTwo), 0);
        assertEquals("The  Euclidean distance between two points is wrong", 3,
                complexPointTwo.euclideanDistance(complexPointOne), 0);

        // The same one only with the points' coords negated
        complexPointOne = new KDPoint(-3, -2, 1);
        complexPointTwo = new KDPoint(-1, -3, -1);
        assertEquals("The  Euclidean distance between two points is wrong", 3,
                complexPointOne.euclideanDistance(complexPointTwo), 0);
        assertEquals("The  Euclidean distance between two points is wrong", 3,
                complexPointTwo.euclideanDistance(complexPointOne), 0);
    }

    @Test
    public void testKDPointDistanceStatic() {
        // Some trivial ones
        String messageTrivialDistance = "Static euclidean distance(): The distance between a point and itself must be 0";
        assertEquals(messageTrivialDistance, 0, euclideanDistance(origin2D, origin2D), 0); // Recall that the static method has been statically imported, so this works.
        assertEquals(messageTrivialDistance, 0, euclideanDistance(origin3D, origin3D), 0);
        for (int i = 0; i < MAX_ITER; i++) {
            KDPoint p = new KDPoint(-r.nextInt(MAX_COORD), r.nextInt(MAX_COORD));
            assertEquals(messageTrivialDistance, 0, euclideanDistance(p, p), 0);
        }

        // The complex example from the previous test:
        KDPoint complexPointOne = new KDPoint(3, 2, -1);
        KDPoint complexPointTwo = new KDPoint(1, 3, 1);
        assertEquals("The  Euclidean distance (static euclidean distance()) between two points is wrong",
                3, euclideanDistance(complexPointOne, complexPointTwo), 0);
        assertEquals("The  Euclidean distance (static euclidean distance()) between two points is wrong",
                3, euclideanDistance(complexPointTwo, complexPointOne), 0);

        // And, finally, proper exceptions thrown when comparing objects of different
        // dimensionalities:
        for (int i = 0; i < MAX_ITER; i++) {
            int coord = r.nextInt(MAX_COORD);
			try {
				euclideanDistance(new KDPoint(coord), new KDPoint(coord, coord));
				fail("Expected RuntimeException");
			} catch ( RuntimeException e ) {}
        }
    }

    @Test
    public void testKDPointToString() {

        // (1) 1D KDPoints
        for (int i = 0; i < MAX_ITER; i++) {
            int randCoord = r.nextInt(MAX_COORD);
            KDPoint p = new KDPoint(randCoord);
            assertEquals("We failed to generate a proper String-ified representation for "
                            + "the 1D point  #" + i, "(" + randCoord + ")",  p.toString());
            p = new KDPoint(-randCoord);
            assertEquals("We failed to generate a proper String-ified representation for "
                            + "the 1D point  #" + i, "(" + (-randCoord) + ")",  p.toString());
        }


        // (2) 2D KDPoints
        for (int i = 0; i < MAX_ITER; i++) {
            int[] randNums = {r.nextInt(MAX_COORD), r.nextInt(MAX_COORD)};
            KDPoint p = new KDPoint(randNums);
            assertEquals("We failed to generate a proper String-ified representation for "
                    + "the 1D point  #" + i, "(" + randNums[0] + ", "
                    + randNums[1] + ")", p.toString());
            int[] minusRandNums = {-randNums[0], -randNums[1]};
            p = new KDPoint(minusRandNums);
            assertEquals("We failed to generate a proper String-ified representation for "
                    + "the 1D point  #" + i, "(" + minusRandNums[0] + ", "
                    + minusRandNums[1] + ")", p.toString());
        }

        // Could add tests for more dimensions, but it's not like we will be using toString()
        // for anything other than debugging information...
    }


}
