package phonebook.hashes;

import phonebook.utils.KVPair;
import phonebook.utils.PrimeGenerator;
import phonebook.utils.Probes;

/**
 * <p>{@link QuadraticProbingHashTable} is an Openly Addressed {@link HashTable} which uses <b>Quadratic
 * Probing</b> as its collision resolution strategy. Quadratic Probing differs from <b>Linear</b> Probing
 * in that collisions are resolved by taking &quot; jumps &quot; on the hash table, the length of which
 * determined by an increasing polynomial factor. For example, during a key insertion which generates
 * several collisions, the first collision will be resolved by moving 1^2 + 1 = 2 positions over from
 * the originally hashed address (like Linear Probing), the second one will be resolved by moving
 * 2^2 + 2= 6 positions over from our hashed address, the third one by moving 3^2 + 3 = 12 positions over, etc.
 * </p>
 *
 * <p>By using this collision resolution technique, {@link QuadraticProbingHashTable} aims to get rid of the
 * &quot;key clustering &quot; problem that {@link LinearProbingHashTable} suffers from. Leaving more
 * space in between memory probes allows other keys to be inserted without many collisions. The tradeoff
 * is that, in doing so, {@link QuadraticProbingHashTable} sacrifices <em>cache locality</em>.</p>
 *
 * @author Keshab Acharya!
 *
 * @see HashTable
 * @see SeparateChainingHashTable
 * @see OrderedLinearProbingHashTable
 * @see LinearProbingHashTable
 * @see CollisionResolver
 */
public class QuadraticProbingHashTable extends OpenAddressingHashTable {

    /* ********************************************************************/
    /* ** INSERT ANY PRIVATE METHODS OR FIELDS YOU WANT TO USE HERE: ******/
    /* ********************************************************************/

	private int tombCount;
    /* ******************************************/
    /*  IMPLEMENT THE FOLLOWING PUBLIC METHODS: */
    /* **************************************** */

    /**
     * Constructor with soft deletion option. Initializes the internal storage with a size equal to the starting value of  {@link PrimeGenerator}.
     * @param soft A boolean indicator of whether we want to use soft deletion or not. {@code true} if and only if
     *               we want soft deletion, {@code false} otherwise.
     */
    public QuadraticProbingHashTable(boolean soft) {
    	primeGenerator = new PrimeGenerator();
        table = new KVPair[primeGenerator.getCurrPrime()];
        softFlag = soft;  
        tombCount = 0;
        count = 0;
        
    }

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
    		table = bigger; //point reference of table to bigger table
    		count -= tombCount;
  		}
    	probeCount += putHelper(table, key, value);
    	this.count++;
    	return new Probes(value, probeCount);
    	
    }
    
    private int putHelper(KVPair[] arr, String key, String value) {
    	
    	int index = (key.hashCode() & 0x7fffffff) % arr.length, probeCount = 1;
    	int hashed = index;
    	//if the index we are inserting is empty
    	if(arr[index] == null) {
    		arr[index] = new KVPair(key, value);
    	} else {   		
    		
    		while(arr[index] != null) {
    			
    			probeCount++;
    			index = (hashed + (probeCount-1) + ((probeCount-1)*(probeCount-1))) % arr.length;
    			
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
    			probeCount++;
    			index = (hash(key) + (probeCount-1) + ((probeCount-1)*(probeCount-1))) % table.length;	
    		}
    		return new Probes(null, probeCount);
    	}
    }

    @Override
    public Probes remove(String key) {
    	
    	if(key == null) {
    		return new Probes(null, 0);
    	} else {	
    		int index = hash(key), probeCount = 1;
    		int hashed = index; //changed
    		
    		if(!softFlag) { // hard deletion
    			while(table[index] != null) { //|("hi", 2)| ("hello, 12) | ("bye", 13)| null |("no", 3)| null |("yes", 22)|
        			
        			if(table[index].getKey().equals(key)) {
        				
        				KVPair temp = table[index];
        				table[index] = null; //deleting
        				index = 0;
        				
        				//Reinsertion of all the elements
        				KVPair[] copy = new KVPair[primeGenerator.getCurrPrime()];
        	    		for(KVPair pair: table) {
        	    			probeCount++; //for deleting from the old table
        	    			if(pair != null && pair != TOMBSTONE) {
        	    				probeCount += putHelper(copy, pair.getKey(), pair.getValue());
        	    				//for reinserting into the new table
        	    			}
        	    		}
        	    		count--;
        	    		table = copy; //point reference of table to bigger table
        				return new Probes(temp.getValue(), probeCount);
        			}
        			probeCount++;
        			index = (hashed + (probeCount-1) + ((probeCount-1)*(probeCount-1))) % table.length;				
        		}
    			
    			
    		} else { // soft deletion
    			while(table[index] != null) { //|1| 2| 3| null |4| null |7|
    				
    				KVPair temp = table[index];
        			if(table[index].getKey().equals(key)) {
        				table[index] = TOMBSTONE;
        				this.tombCount++;
        				return new Probes(temp.getValue(), probeCount);
        			}
        			probeCount++;
        			index = (hashed + (probeCount-1) + ((probeCount-1)*(probeCount-1))) % table.length;			
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
    	int index = hash(key), probeCount = 1;
    	while(table[index] != null) {
    		if(table[index].getKey().equals(key)) {
    			return true;
    		}
    		probeCount++;
    		index = (hash(key) + (probeCount-1) + ((probeCount-1)*(probeCount-1))) % table.length;
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
    public int size(){
       return count;
    }

    @Override
    public int capacity() {
       return table.length;
    }

}