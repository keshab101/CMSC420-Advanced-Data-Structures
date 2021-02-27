package pqueue.heaps; // ******* <---  DO NOT ERASE THIS LINE!!!! *******

import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;

/* *****************************************************************************************
 * THE FOLLOWING IMPORT IS NECESSARY FOR THE ITERATOR() METHOD'S SIGNATURE. FOR THIS
 * REASON, YOU SHOULD NOT ERASE IT! YOUR CODE WILL BE UNCOMPILABLE IF YOU DO!
 * ********************************************************************************** */

import java.util.Iterator;


/**
 * <p>{@link ArrayMinHeap} is a {@link MinHeap} implemented using an internal array. Since heaps are <b>complete</b>
 * binary trees, using contiguous storage to store them is an excellent idea, since with such storage we avoid
 * wasting bytes per {@code null} pointer in a linked implementation.</p>
 *
 * <p>You <b>must</b> edit this class! To receive <b>any</b> credit for the unit tests related to this class,
 * your implementation <b>must</b> be a <b>contiguous storage</b> implementation based on a linear {@link java.util.Collection}
 * like an {@link java.util.ArrayList} or a {@link java.util.Vector} (but *not* a {@link java.util.LinkedList} because it's *not*
 * contiguous storage!). or a raw Java array. We provide an array for you to start with, but if you prefer, you can switch it to a
 * {@link java.util.Collection} as mentioned above. </p>
 *
 * @author -- Keshab Acharya ---
 *
 * @see MinHeap
 * @see LinkedMinHeap
 * @see demos.GenericArrays
 */

public class ArrayMinHeap<T extends Comparable<T>> implements MinHeap<T> {

	/* *****************************************************************************************************************
	 * This array will store your data. You may replace it with a linear Collection if you wish, but
	 * consult this class' 	 * JavaDocs before you do so. We allow you this option because if you aren't
	 * careful, you can end up having ClassCastExceptions thrown at you if you work with a raw array of Objects.
	 * See, the type T that this class contains needs to be Comparable with other types T, but Objects are at the top
	 * of the class hierarchy; they can't be Comparable, Iterable, Clonable, Serializable, etc. See GenericArrays.java
	 * under the package demos* for more information.
	 * *****************************************************************************************************************/
	private	T[] data;

	/* *********************************************************************************** *
	 * Write any further private data elements or private methods for LinkedMinHeap here...*
	 * *************************************************************************************/
	private int size;
	private int INITIAL_CAPACITY = 15;
	private boolean modified;
	
	
	private void swap(T[] arr, int i, int j) {
		T temp = arr[i];
		arr[i] = arr[j];
		arr[j] = temp;
	}
	
	/* *********************************************************************************************************
	 * Implement the following public methods. You should erase the throwings of UnimplementedMethodExceptions.*
	 ***********************************************************************************************************/

	/**
	 * Default constructor initializes the data structure with some default
	 * capacity you can choose.
	 */
	
	@SuppressWarnings("unchecked")
	public ArrayMinHeap(){
		
		data = (T[]) new Comparable[INITIAL_CAPACITY];
		this.size = 0;
		modified = false;
	}

	/**
	 *  Second, non-default constructor which provides the element with which to initialize the heap's root.
	 *  @param rootElement the element to create the root with.
	 */
	
	@SuppressWarnings("unchecked")
	public ArrayMinHeap(T rootElement){
		
		data = (T[]) new Comparable[INITIAL_CAPACITY];
		data[0] = rootElement;
		this.size = 1;
	}

	/**
	 * Copy constructor initializes {@code this} as a carbon copy of the {@link MinHeap} parameter.
	 *
	 * @param other The MinHeap object to base construction of the current object on.
	 */
	@SuppressWarnings("unchecked")
	public ArrayMinHeap(MinHeap<T> other){
		
		this.size = other.size();
		T[] otherData = (T[]) new Comparable[INITIAL_CAPACITY];
		Iterator<T> itr = other.iterator(); 
		int counter = 0;
		while( itr.hasNext()) {
			otherData[counter++] = itr.next();
		}
		this.data = otherData;

	}

	/**
	 * Standard {@code equals()} method. We provide it for you: DO NOT ERASE! Consider its implementation when implementing
	 * {@link #ArrayMinHeap(MinHeap)}.
	 * @return {@code true} if the current object and the parameter object
	 * are equal, with the code providing the equality contract.
	 * @see #ArrayMinHeap(MinHeap)
	 */
	@Override
	public boolean equals(Object other){
		if(other == null || !(other instanceof MinHeap))
			return false;
		Iterator<T> itThis = iterator();
		Iterator<T> itOther = ((MinHeap<T>) other).iterator();
		while(itThis.hasNext())
			if(!itThis.next().equals(itOther.next()))
				return false;
		return !itOther.hasNext();
	}


	@SuppressWarnings("unchecked")
	@Override
	public void insert(T element) {
		if(size >= INITIAL_CAPACITY) {
			T[] bigger = (T[]) new Comparable[INITIAL_CAPACITY+1];
			for(int i = 0; i < size; i++) {
				bigger[i] = data[i];
			}
			this.data = bigger;
			INITIAL_CAPACITY += 1;
		}
		if(isEmpty()) {
			data[0] = element;
			size++;
			return;
		}
		data[size++] = element;
		int parent = ((size-1)-1)/2;
		int child = size-1;
		
		//While the parent is greater than the child, do a swap
		while((data[parent]).compareTo(data[child]) > 0) {
			swap(data, parent, child);
			child = parent;
			parent = (child-1)/2;
		}
		this.modified = true;
			
	}

	@SuppressWarnings("unchecked")
	@Override
	public T deleteMin() throws EmptyHeapException { // DO *NOT* ERASE THE "THROWS" DECLARATION!
		if(isEmpty()) {
			throw new EmptyHeapException("Empty Heap");
		}
		
		//If the heap has only one item
		T min = data[0]; /*Save the min before swapping*/
		if(size == 1) {
			T [] empty = (T[]) new Comparable[INITIAL_CAPACITY];
			data = empty;
			this.size = 0;
			return min;
		}
		//Making a new copy of the array with last element removed
		swap(data, 0, size-1);
		T [] copy = (T[]) new Comparable[INITIAL_CAPACITY];
		for(int i = 0; i < size -1; i++) {
			copy[i] = data[i];
		}
		this.size--;
		data = copy;
		
		int curr = 0;
		int left = 2*curr+1, right = 2*curr+2;
		
		while(data[left] != null) {
			if(data[right] == null) {
				if(data[left].compareTo(data[curr]) < 0) {
					swap(data, left, curr);
					curr = left;
					left = 2*curr+1;
				}
				return min;
			}
			if(data[left].compareTo(data[right]) < 0) {
				if(data[left].compareTo(data[curr])< 0) {
					swap(data, left, curr);
					curr = left;
					left = 2*curr+1;
					right = 2*curr+2;
				}else {
					return min;
				}
				

			} else {
				if (data[right].compareTo(data[curr]) < 0) {
					swap(data, right, curr);
					curr = right;
					left = 2*curr+1;
					right = 2*curr+1;
				}else {
					return min;
				}
				
			}	
		}
		this.modified = true;
		return min;		
	}
	
	@Override
	public T getMin() throws EmptyHeapException {	// DO *NOT* ERASE THE "THROWS" DECLARATION!
		if(isEmpty()) {
			throw new EmptyHeapException("Empty Heap");
		}else {
			return data[0];
		}
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return size == 0 ? true : false;
	}

	public String toString() {
		
		String result = "";
		for(int i = 0; i < size; i++) {
			result += data[i] + " ";
		}
		return result;

	}
	/**
	 * Standard equals() method.
	 * @return {@code true} if the current object and the parameter object
	 * are equal, with the code providing the equality contract.
	 */
	
	@Override
	public Iterator<T> iterator() {
		this.modified = false;
		return new Iterator<T>() {
            int startindex = 0;

            public boolean hasNext() {
                return startindex < size();
            }
 
			public T next() {
				if(modified) {
					throw new ConcurrentModificationException();
				}else {
					ArrayList<T> list = new ArrayList<T>();
					for(int i = 0; i < size(); i++) {
						list.add(data[i]);
					}
					Collections.sort(list);
					return list.get(startindex++);
				}
            }

        };
	}
	

}
