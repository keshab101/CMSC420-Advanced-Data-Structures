package phonebook;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import phonebook.hashes.*;
import phonebook.utils.KVPair;
import phonebook.utils.NoMorePrimesException;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.*;
import static phonebook.hashes.CollisionResolver.*;


/**
 * <p> {@link StudentTests} is a place for you to write your tests for {@link Phonebook} and all the various
 * {@link HashTable} instances.</p>
 *
 * @author Keshab Acharya!
 * @see Phonebook
 * @see HashTable
 * @see SeparateChainingHashTable
 * @see LinearProbingHashTable
 * @see QuadraticProbingHashTable
 */
public class StudentTests {

    private Phonebook pb;
    private CollisionResolver[] resolvers = {SEPARATE_CHAINING, LINEAR_PROBING, ORDERED_LINEAR_PROBING, QUADRATIC_PROBING};
    private HashMap<String, String> testingPhoneBook;
    private static final long SEED = 47;
    private static final Random RNG = new Random(SEED);
    private static final int NUMS = 1000;
    private static final int UPPER_BOUND = 100;

    private String format(String error, CollisionResolver namesToPhones, CollisionResolver phonesToNames) {
        return error + "Collision resolvers:" + namesToPhones + ", " + phonesToNames + ".";
    }


    private String errorData(Throwable t) {
        return "Received a " + t.getClass().getSimpleName() + " with message: " + t.getMessage() + ".";
    }

    @Before
    public void setUp() {
        testingPhoneBook = new HashMap<>();
        testingPhoneBook.put("Arnold", "894-59-0011");
        testingPhoneBook.put("Tiffany", "894-59-0011");
        testingPhoneBook.put("Jessie", "705-12-7500");
        testingPhoneBook.put("Mary", "888-1212-3340");
    }

    @After
    public void tearDown() {
        testingPhoneBook.clear();
    }


    // Make sure that all possible phonebooks we can create will report empty when beginning.
    @Test
    public void testBehaviorWhenEmpty() {
        for (CollisionResolver namesToPhones : resolvers) {
            for (CollisionResolver phonesToNames : resolvers) {
                pb = new Phonebook(namesToPhones, phonesToNames);
                assertTrue(format("Phonebook should be empty", namesToPhones, phonesToNames), pb.isEmpty());
            }
        }
    }

    // See if all of our hash tables cover the simple example from the writeup.
    @Test
    public void testOpenAddressingResizeWhenInsert() {
        SeparateChainingHashTable sc = new SeparateChainingHashTable();
        LinearProbingHashTable lp = new LinearProbingHashTable(false);
        QuadraticProbingHashTable qp = new QuadraticProbingHashTable(false);
        assertEquals("Separate Chaining hash should have a capacity of 7 at startup.", 7, sc.capacity());
        assertEquals("Linear Probing hash should have a capacity of 7 at startup.", 7, lp.capacity());
        assertEquals("Quadratic Probing hash should have a capacity of 7 at startup.", 7, qp.capacity());
        for (Map.Entry<String, String> entry : testingPhoneBook.entrySet()) { // https://docs.oracle.com/javase/10/docs/api/java/util/Map.Entry.html
            sc.put(entry.getKey(), entry.getValue());
            lp.put(entry.getKey(), entry.getValue());
            qp.put(entry.getKey(), entry.getValue());
        }
        assertEquals("Separate Chaining hash should have a capacity of 7 after inserting 4 elements.", 7, sc.capacity());
        assertEquals("Linear Probing hash should have a capacity of 7 after inserting 4 elements.", 7, lp.capacity());
        assertEquals("Quadratic Probing hash should have a capacity of 7 after inserting 4 elements.", 7, qp.capacity());

        sc.put("DeAndre", "888-1212-3340");
        assertEquals("Separate Chaining hash should still have a capacity of 7 after inserting 5 elements.", 7, sc.capacity());
        sc.enlarge();
        assertEquals("Separate Chaining hash should have a capacity of 13 after first call to enlarge().", 13, sc.capacity());
        sc.enlarge();
        assertEquals("Separate Chaining hash should have a capacity of 23 after second call to enlarge().", 23, sc.capacity());
        sc.shrink();
        assertEquals("Separate Chaining hash should have a capacity of 13 after two calls to enlarge() and one to shrink().",
                13, sc.capacity());
        sc.shrink();
        assertEquals("Separate Chaining hash should have a capacity of 7 after two calls to enlarge() and two to shrink().",
                7, sc.capacity());
        lp.put("DeAndre","888-1212-3340" );
        assertEquals("Linear Probing hash should have a capacity of 13 after inserting 5 elements.",
                13, lp.capacity());
        qp.put("DeAndre","888-1212-3340" );
        assertEquals("Quadratic Probing hash should have a capacity of 13 after inserting 5 elements.",
                13, qp.capacity());

        // The following two deletions should both fail and thus not affect capacity.

        lp.remove("Thomas");
        assertEquals("Linear Probing hash with starting capacity of 7 should have a capacity of 13 after " +
                "five insertions and a failed deletion.", 13, lp.capacity());
        qp.remove("Thomas" );
        assertEquals("Quadratic Probing hash with starting capacity of 7 should have a capacity of 13 after " +
                "five insertions and a failed deletion.", 13, qp.capacity());
    }

    // An example of a stress test to catch any insertion errors that you might get.
    @Test
    public void insertionStressTest() {
        HashTable sc = new SeparateChainingHashTable();
        HashTable lp = new LinearProbingHashTable(false);
        HashTable qp = new QuadraticProbingHashTable(false);
        for (int i = 0; i < NUMS; i++) {
            String randomNumber = Integer.toString(RNG.nextInt(UPPER_BOUND));
            String randomNumber2 = Integer.toString(RNG.nextInt(UPPER_BOUND));
            try {
                sc.put(randomNumber, randomNumber2);
            } catch (NoMorePrimesException ignored) {
                // To have this exception thrown is not a problem; we have a finite #primes to generate resizings for.
            } catch (Throwable t) {
                fail("Separate Chaining hash failed insertion #" + i + ". Error message: " + errorData(t));
            }

            try {
                lp.put(randomNumber, randomNumber2);
            } catch (NoMorePrimesException ignored) {
                // To have this exception thrown is not a problem; we have a finite #primes to generate resizings for.
            } catch (Throwable t) {
                fail("Linear Probing hash failed insertion #" + i + ". Error message: " + errorData(t));
            }


            try {
                qp.put(randomNumber, randomNumber2);
            } catch (NoMorePrimesException ignored) {
                // To have this exception thrown is not a problem; we have a finite #primes to generate resizings for.
            } catch (Throwable t) {
                fail("Quadratic Probing hash failed insertion #" + i + ". Error message: " + errorData(t));
            }
        }

    }

    @Test
    public void testSCProbes() {
        SeparateChainingHashTable sc = new SeparateChainingHashTable();

        assertEquals(1, sc.put("Arnold", "894-59-0011").getProbes());
        assertEquals(1, sc.put("Tiffany", "894-59-0011").getProbes());
        assertEquals(1, sc.put("Jessie", "705-12-7500").getProbes());
        assertEquals(1, sc.put("Mary", "888-1212-3340").getProbes());

        assertEquals(1, sc.get("Arnold").getProbes());
        assertEquals("894-59-0011", sc.get("Arnold").getValue());
        assertEquals(1, sc.get("Tiffany").getProbes());
        assertEquals(2, sc.get("Jessie").getProbes());
        assertEquals(1, sc.get("Mary").getProbes());

        // Search fail
        assertEquals(2, sc.get("Jerry").getProbes());
        assertEquals(2, sc.remove("Jerry").getProbes());
        assertNull(sc.remove("Jerry").getValue());

        assertEquals(1, sc.remove("Arnold").getProbes());
        assertEquals(1, sc.remove("Tiffany").getProbes());
        assertEquals(1, sc.remove("Jessie").getProbes());
        assertEquals(1, sc.remove("Mary").getProbes());

    }


    @Test
    public void testLProbes() {

        LinearProbingHashTable lp = new LinearProbingHashTable(false);

        assertEquals(1, lp.put("Arnold", "894-59-0011").getProbes());
        assertEquals(1, lp.put("Tiffany", "894-59-0011").getProbes());
        assertEquals(2, lp.put("Jessie", "705-12-7500").getProbes());
        assertEquals(1, lp.put("Mary", "888-1212-3340").getProbes());


        assertEquals(1, lp.get("Arnold").getProbes());
        assertEquals("894-59-0011", lp.get("Arnold").getValue());
        assertEquals(1, lp.get("Tiffany").getProbes());
        assertEquals(2, lp.get("Jessie").getProbes());
        assertEquals(1, lp.get("Mary").getProbes());

        // Search fail
        assertEquals(2, lp.get("Jerry").getProbes());
        assertEquals(2, lp.remove("Jerry").getProbes());
        assertEquals(null, lp.remove("Jerry").getValue());

        assertEquals(3, lp.remove("Jessie").getProbes());
        assertEquals(2, lp.remove("Arnold").getProbes());
        assertEquals(2, lp.remove("Tiffany").getProbes());
        assertEquals(2, lp.remove("Mary").getProbes());



    }

    @Test
    public void testResizeSoftLProbes() {

        LinearProbingHashTable lp = new LinearProbingHashTable(true);
        String[] add1 = new String[]{"Tiffany", "Helen", "Alexander", "Paulette", "Jason", "Money", "Nakeesha", "Ray", "Jing", "Amg"};
        String[] remove1 = new String[]{"Helen", "Alexander", "Paulette", "Jason", "Money", "Nakeesha", "Ray", "Jing", "Amg"};
        String[] add2 = new String[]{"Christine", "Carl"};

        for(String s: add1) {
            lp.put(s, s);
        }

        for (String s: remove1) {
            lp.remove(s);
        }

        for(String s: add2) {
            lp.put(s, s);
        }

        assertEquals("After additions and deletions, and additions again, the capacity should be 23, but get " + lp.capacity() + ".", 23, lp.capacity());

        lp.put("Terry", "new");
        assertEquals("After additions and deletions, and additions again, resize should be triggered and the capacity should be 43, but get " + lp.capacity() + ".", 43, lp.capacity());

    }
    
    /*Hash function for easier testing*/
    private int hashFunction(String key, int tableLength) {
    	 return (key.hashCode() & 0x7fffffff) % tableLength;
    }
    
    @Test
    public void testAllSeparateChaining() {
    	
    	SeparateChainingHashTable sc = new SeparateChainingHashTable();
    	assertTrue(sc.size() == 0);
    	assertTrue(sc.capacity() == 7);
    	
    	assertEquals(1, sc.put("Arnold", "894-59-0011").getProbes());
        assertEquals(1, sc.put("Tiffany", "894-59-0011").getProbes());
        assertTrue(sc.size() == 2);
    	assertTrue(sc.capacity() == 7);
        
        assertEquals(1, sc.put("Jessie", "705-12-7500").getProbes());
        assertEquals(1, sc.put("Mary", "888-1212-3340").getProbes());
        assertTrue(sc.size() == 4);
    	assertTrue(sc.capacity() == 7);
    	
    	
    	assertEquals(2, sc.get("Jessie").getProbes());
    	//Tarek hashes into index 2 which is empty (good for test case)
    	assertEquals(1, sc.get("Tarek").getProbes());
    	assertEquals(1, sc.remove("Tarek").getProbes());
    	assertFalse(sc.containsKey("Tarek"));
    	assertTrue(sc.containsKey("Mary"));
    	assertFalse(sc.containsKey(null));
    	assertTrue(sc.containsValue("888-1212-3340"));
    	assertFalse(sc.containsValue("Keshab"));
    	assertFalse(sc.containsValue(null));
    	
    	sc.enlarge();
    	assertTrue(sc.size() == 4);
    	assertTrue(sc.capacity() == 13);
    	assertFalse(sc.containsKey("Tarek"));
    	
    	sc.shrink();
    	assertTrue(sc.size() == 4);
    	assertTrue(sc.capacity() == 7);
    	assertFalse(sc.containsKey("Tarek"));
    	assertTrue(sc.containsKey("Mary"));
    }
    
    @Test
    public void testAllLinearProbing() {
    	
      LinearProbingHashTable lp = new LinearProbingHashTable(false);

      /*Test put function*/
      assertTrue(lp.size() == 0);
      assertEquals(1, lp.put("Arnold", "894-59-0011").getProbes());
      assertEquals(1, lp.put("Tiffany", "894-59-0011").getProbes());
      assertEquals(2, lp.put("Jessie", "705-12-7500").getProbes());
      assertEquals(1, lp.put("Mary", "888-1212-3340").getProbes());
      assertTrue(lp.size() == 4);
      assertEquals(12, lp.put("Keshab", "443").getProbes());
      
      /*Test get function*/
      assertEquals(1, lp.get("Arnold").getProbes());
      assertEquals(1, lp.get("Jessie").getProbes());
      assertEquals(1, lp.get("Don").getProbes()); //hashes into index 12
      assertEquals(3, lp.get("Muna").getProbes()); //hashes into index 2
      
      /*Test containsKey and containsValue functions*/
      assertFalse(lp.containsKey("keshab")); //hashes into 9
      assertTrue(lp.containsKey("Jessie"));
      assertFalse(lp.containsKey(null));
      assertFalse(lp.containsKey("Muna"));
      
      assertFalse(lp.containsValue(null));
      assertFalse(lp.containsValue("Don"));
      assertTrue(lp.containsValue("443"));
      
      /*Test remove function*/
      assertEquals(6,lp.remove("Mary").getProbes()); //hard deletion
      assertEquals(4, lp.remove("Keshab").getProbes()); //hard deletion
      
    }
    
    @Test
    public void testLinearProbingWithResizing() {
    	
    	LinearProbingHashTable lp = new LinearProbingHashTable(true);
    	String p = "p", b = "b", i = "i", w = "w";
    	
    	
    	assertEquals(1, lp.put(p,p).getProbes());
        assertEquals(2, lp.put(b,b).getProbes());
        assertEquals(3, lp.put(i,i).getProbes());
        assertEquals(4, lp.put(w,w).getProbes());
        assertEquals(4, lp.remove("w").getProbes());
        assertTrue(lp.size() == 4);
        assertEquals(11, lp.put("u", "u").getProbes());
        assertTrue(lp.size() == 4);
        assertTrue(lp.capacity() == 13);
        
        
        
    }
   
   @Test
   public void testQuadraticSoftDeletion() {
	   QuadraticProbingHashTable qp = new QuadraticProbingHashTable(true);
	   assertTrue(qp.size() == 0);
	   
	   
	   assertEquals(1, qp.remove("Arnold").getProbes());
	   assertEquals(1, qp.put("Arnold", "894-59-0011").getProbes());
	   assertEquals(1, qp.remove("Arnold").getProbes());
	   assertEquals(1, qp.size());
	   
	   assertEquals(2, qp.put("Arnold", "894-59-0011").getProbes());
	   assertEquals(1, qp.put("Tiffany", "894-59-0011").getProbes());
	   
	   assertEquals(3, qp.remove("Jessie").getProbes());
	   assertEquals(3, qp.put("Jessie", "705-12-7500").getProbes());
	   assertEquals(3, qp.remove("Jessie").getProbes());
	   
	   //should trigger resizing since the count is 4
	   //should not copy over tombstones
	   assertEquals(10, qp.put("Keshab", "443").getProbes()); //hashes to 5 
	   assertTrue(qp.size() == 3);
	   
	   
   }
   
   @Test
   public void testLinearProbingSoft() {
	   LinearProbingHashTable lp = new LinearProbingHashTable(true);
	   assertTrue(lp.size() == 0);
   		String p = "p", b = "b", i = "i", w = "w";
   	
   	
   	   assertEquals(1, lp.put(p,p).getProbes());
       assertEquals(2, lp.put(b,b).getProbes());
       assertEquals(3, lp.put(i,i).getProbes());
       assertEquals(4, lp.put(w,w).getProbes());
       assertEquals(4, lp.remove("w").getProbes());
       assertTrue(lp.size() == 4);
       assertEquals(11, lp.put("u", "u").getProbes());
       assertTrue(lp.size() == 4);
       assertTrue(lp.capacity() == 13);
	   
   }
  

}
