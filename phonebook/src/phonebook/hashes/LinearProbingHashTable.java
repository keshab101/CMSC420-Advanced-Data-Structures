package phonebook.hashes;

import phonebook.utils.KVPair;
import phonebook.utils.PrimeGenerator;
import phonebook.utils.Probes;

/**
 * <p>{@link LinearProbingHashTable} is an Openly Addressed {@link HashTable} implemented with <b>Linear Probing</b> as its
 * collision resolution strategy: every key collision is resolved by moving one address over. It is
 * the most famous collision resolution strategy, praised for its simplicity, theoretical properties
 * and cache locality. It <b>does</b>, however, suffer from the &quot; clustering &quot; problem:
 * collision resolutions tend to cluster collision chains locally, making it hard for new keys to be
 * inserted without collisions. {@link QuadraticProbingHashTable} is a {@link HashTable} that
 * tries to avoid this problem, albeit sacrificing cache locality.</p>
 *
 * @author Keshab Acharya!
 *
 * @see HashTable
 * @see SeparateChainingHashTable
 * @see OrderedLinearProbingHashTable
 * @see QuadraticProbingHashTable
 * @see CollisionResolver
 */
public class LinearProbingHashTable extends OpenAddressingHashTable {

    /* ********************************************************************/
    /* ** INSERT ANY PRIVATE METHODS OR FIELDS YOU WANT TO USE HERE: ******/
    /* ********************************************************************/
	private int tombCount;
    /* ******************************************/
    /*  IMPLEMENT THE FOLLOWING PUBLIC METHODS: */
    /* **************************************** */

    /**
     * Constructor with soft deletion option. Initializes the internal storage with a size equal to the starting value of  {@link PrimeGenerator}.
     *
     * @param soft A boolean indicator of whether we want to use soft deletion or not. {@code true} if and only if
     *             we want soft deletion, {@code false} otherwise.
     */
    public LinearProbingHashTable(boolean soft) {
    	primeGenerator = new PrimeGenerator();
        table = new KVPair[primeGenerator.getCurrPrime()];
        softFlag = soft;     
        count = 0;
        tombCount = 0;
        
    }

    /**
     * Inserts the pair &lt;key, value&gt; into this. The container should <b>not</b> allow for {@code null}
     * keys and values, and we <b>will</b> test if you are throwing a {@link IllegalArgumentException} from your code
     * if this method is given {@code null} arguments! It is important that we establish that no {@code null} entries
     * can exist in our database because the semantics of {@link #get(String)} and {@link #remove(String)} are that they
     * return {@code null} if, and only if, their key parameter is {@code null}. This method is expected to run in <em>amortized
     * constant time</em>.
     * <p>
     * Instances of {@link LinearProbingHashTable} will follow the writeup's guidelines about how to internally resize
     * the hash table when the capacity exceeds 50&#37;
     *
     * @param key   The record's key.
     * @param value The record's value.
     * @return The {@link phonebook.utils.Probes} with the value added and the number of probes it makes.
     * @throws IllegalArgumentException if either argument is {@code null}.
     */
    @Override
    public Probes put(String key, String value) {
       
    	if(key == null || value == null) {
    		throw new IllegalArgumentException("Either key or the value is null.");
    	}
    	int probeCount = 0;
    	if(this.count >= table.length/2 + 1) {//resize when we have 4 elements for table of size 7
			
    		KVPair[] bigger = new KVPair[primeGenerator.getNextPrime()];
    		for(KVPair pair: table) {
    			probeCount++; //for deleting from the old table
    			if(pair != null && pair != TOMBSTONE) {
    				probeCount += putHelper(bigger, pair.getKey(), pair.getValue());
    				//for reinserting into the new table
    			}
    		}
    		count -= tombCount;
    		this.tombCount = 0;
    		table = bigger; //point reference of table to bigger table
  		}
    	probeCount += putHelper(table, key, value);
    	this.count++;
    	return new Probes(value, probeCount);
    	
    }
    private int putHelper(KVPair[] arr, String key, String value) {
    	
    	int index = (key.hashCode() & 0x7fffffff) % arr.length, probeCount = 1;
    	//if the index we are inserting is empty
    	if(arr[index] == null) {
    		arr[index] = new KVPair(key, value);
    	} else {
    		
    		while(arr[index] != null) {
    			if(index == arr.length -1) { //if the index is last index, loop back around
    				index = 0;
    			} else {
    				index++;
    			}
    			probeCount++;
    		}
    		arr[index] = new KVPair(key, value);
    		
    	}
    	return probeCount;
    }

    @Override
    public Probes get(String key) {
    	
    	if(key == null) {
    		return new Probes(null, 0);
    	} else {
    		int index = hash(key), probeCount = 1;
    		while(table[index] != null) { //|1| 2| 3| null |4| null |7|
    			
    			if(table[index].getKey().equals(key)) {
    				return new Probes(table[index].getValue(), probeCount);
    			}
    			if(index == table.length -1) { //if the index is last index, loop back around
    				index = 0;
    			} else {
    				index++;
    			}
    			probeCount++;
    		}
    		return new Probes(null, probeCount);
    	}
    }


    /**
     * <b>Return</b> and <b>remove</b> the value associated with key in the {@link HashTable}. If key does not exist in the database
     * or if key = {@code null}, this method returns {@code null}. This method is expected to run in <em>amortized constant time</em>.
     *
     * @param key The key to search for.
     * @return The {@link phonebook.utils.Probes} with associated value and the number of probe used. If the key is {@code null}, return value {@code null}
     * and 0 as number of probes; if the key dones't exists in the database, return {@code null} and the number of probes used.
     */
    @Override
    public Probes remove(String key) {
        
    	if(key == null) {
    		return new Probes(null, 0);
    	} else {
    		int index = hash(key), probeCount = 1;
    		if(softFlag) { // soft deletion
    			
    			while(table[index] != null) { //|1| 2| 3| null |4| null |7|
    				
    				KVPair temp = table[index];
        			if(table[index].getKey().equals(key)) {
        				table[index] = TOMBSTONE;
        				this.tombCount++;
        				return new Probes(temp.getValue(), probeCount);
        			}
        			if(index == table.length -1) { //if the index is last index, loop back around
        				index = 0;
        			} else {
        				index++;
        			}
        			probeCount++;
        		}
    			
    		} else { // hard deletion
    			while(table[index] != null) { //|("hi", 2)| ("hello, 12) | ("bye", 13)| null |("no", 3)| null |("yes", 22)|
        			
        			if(table[index].getKey().equals(key)) {
        				KVPair retVal = table[index];
        				table[index++] = null; //deleting
        				if(index >= table.length) {
        					index = 0;
        				}
        				probeCount++; //to check the next element
        				while(table[index] != null) { //reinserting keys after deleted value
        					
    						KVPair temp = table[index];
    						table[index] = null;
    						probeCount += putHelper(table, temp.getKey(), temp.getValue());
    						probeCount++; //count for going back to while loop to check next index
    						index++;
        				}
        				count--;
        				return new Probes(retVal.getValue(), probeCount);
        			}
        			if(index == table.length -1) { //if the index is last index, loop back around
        				index = 0;
        			} else {
        				index++;
        			}
        			probeCount++;
        		}
    		}
    		return new Probes(null, probeCount);
    	}
    	
    }

    @Override
    public boolean containsKey(String key) {
        
    	if(key == null) {
    		return false;
    	}
    	if(table[hash(key)] == null) {
    		return false;
    	}
    	int index = hash(key);
    	while(table[index] != null) {
    		if(table[index].getKey().equals(key)) {
    			return true;
    		}
    		index++;
    	}
    	return false;
    }

    @Override
    public boolean containsValue(String value) {
       
    	for(KVPair pair: table) {
    		if(pair != null && pair.getValue().equals(value)) {
	    		return true;
    		}
    	}
    	return false;
    }

    @Override
    public int size() {
        return count-tombCount;
    }

    @Override
    public int capacity() {
        return table.length;
    }
}
