package phonebook.hashes;

import java.util.Iterator;
import phonebook.exceptions.UnimplementedMethodException;
import phonebook.utils.KVPair;
import phonebook.utils.KVPairList;
import phonebook.utils.PrimeGenerator;
import phonebook.utils.Probes;

/**<p>{@link SeparateChainingHashTable} is a {@link HashTable} that implements <b>Separate Chaining</b>
 * as its collision resolution strategy, i.e the collision chains are implemented as actual
 * Linked Lists. These Linked Lists are <b>not assumed ordered</b>. It is the easiest and most &quot; natural &quot; way to
 * implement a hash table and is useful for estimating hash function quality. In practice, it would
 * <b>not</b> be the best way to implement a hash table, because of the wasted space for the heads of the lists.
 * Open Addressing methods, like those implemented in {@link LinearProbingHashTable} and {@link QuadraticProbingHashTable}
 * are more desirable in practice, since they use the original space of the table for the collision chains themselves.</p>
 *
 * @author Keshab Acharya!
 * @see HashTable
 * @see SeparateChainingHashTable
 * @see LinearProbingHashTable
 * @see OrderedLinearProbingHashTable
 * @see CollisionResolver
 */
public class SeparateChainingHashTable implements HashTable{

    /* ****************************************************************** */
    /* ***** PRIVATE FIELDS / METHODS PROVIDED TO YOU: DO NOT EDIT! ***** */
    /* ****************************************************************** */

    private KVPairList[] table;
    private int count;
    private PrimeGenerator primeGenerator;

    // We mask the top bit of the default hashCode() to filter away negative values.
    // Have to copy over the implementation from OpenAddressingHashTable; no biggie.
    private int hash(String key){
        return (key.hashCode() & 0x7fffffff) % table.length;
    }

    /* **************************************** */
    /*  IMPLEMENT THE FOLLOWING PUBLIC METHODS:  */
    /* **************************************** */
    /**
     *  Default constructor. Initializes the internal storage with a size equal to the default of {@link PrimeGenerator}.
     */
    public SeparateChainingHashTable(){
    	primeGenerator = new PrimeGenerator();
        table = new KVPairList[primeGenerator.getCurrPrime()];
        count = 0;
    }

    @Override
    public Probes put(String key, String value) {
        
    	if(key == null || value == null) {
    		throw new IllegalArgumentException("Either key or the value is null.");
    	}
    	int keyPlace = this.hash(key);
    	if(table[keyPlace] == null) {
    		table[keyPlace] = new KVPairList();
    	}
    	table[keyPlace].addBack(key, value);
    	count++;
    	return new Probes(value, 1);
    	
    	
    }
    @Override
    public Probes get(String key) {
    	
    	if(key == null) {
    		return new Probes(null, 0);
    	} else {
    		if(table[hash(key)] == null) {
    			return new Probes(null, 1);
    		} else {
    			return table[hash(key)].getValue(key);
    		}
    	}  
    }

    @Override
    public Probes remove(String key) {
    	
    	if(key == null) {
    		return new Probes(null, 0);
    	} else {
    		if(table[hash(key)] == null) {
    			return new Probes(null, 1);
    		}
    		Probes pr = table[hash(key)].removeByKey(key);
    		if(pr.getValue() != null) {
    			count--;
    		}
    		return pr;
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
        return table[hash(key)].containsKey(key);
    }

    @Override
    public boolean containsValue(String value) {
        for(KVPairList l: table) {
        	if(l != null && l.containsValue(value)){
        		return true;
        	}
        }
        return false;
    }

    @Override
    public int size() {
        return count;
    }

    @Override
    public int capacity() {
        return table.length; // Or the value of the current prime.
    }

    /**
     * Enlarges this hash table. At the very minimum, this method should increase the <b>capacity</b> of the hash table and ensure
     * that the new size is prime. The class {@link PrimeGenerator} implements the enlargement heuristic that
     * we have talked about in class and can be used as a black box if you wish.
     * @see PrimeGenerator#getNextPrime()
     */
    public void enlarge() {
    	
    	KVPairList[] bigger = new KVPairList[primeGenerator.getNextPrime()];
    	for(KVPairList l: table) {
    		if(l != null) {
	    		Iterator<KVPair> it = l.iterator();
	    		while(it.hasNext()) {
	    			KVPair pair = it.next();
	    			int keyPlace = (pair.getKey().hashCode() & 0x7fffffff) % bigger.length;
	    			if(bigger[keyPlace] == null) {
	    				bigger[keyPlace] = new KVPairList();
	    			}
	    			bigger[keyPlace].addBack(pair.getKey(), pair.getValue());
	    		}
    		}
    	}
    	table = bigger;
    	
    }

    /**
     * Shrinks this hash table. At the very minimum, this method should decrease the size of the hash table and ensure
     * that the new size is prime. The class {@link PrimeGenerator} implements the shrinking heuristic that
     * we have talked about in class and can be used as a black box if you wish.
     *
     * @see PrimeGenerator#getPreviousPrime()
     */
    public void shrink(){
    	
    	KVPairList[] smaller = new KVPairList[primeGenerator.getPreviousPrime()];
    	for(KVPairList l: table) {
    		
    		if(l != null) {
	    		Iterator<KVPair> it = l.iterator();
	    		while(it.hasNext()) {
	    			KVPair pair = it.next();
	    			int keyPlace = (pair.getKey().hashCode() & 0x7fffffff) % smaller.length;
	    			if(smaller[keyPlace] == null) {
	    				smaller[keyPlace] = new KVPairList();
	    			}
	    			smaller[keyPlace].addBack(pair.getKey(), pair.getValue());
	    		}
    		}
    	}
    	table = smaller;
    }
}
