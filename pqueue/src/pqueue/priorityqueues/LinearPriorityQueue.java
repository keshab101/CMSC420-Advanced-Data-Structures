package pqueue.priorityqueues; // ******* <---  DO NOT ERASE THIS LINE!!!! *******

/* *****************************************************************************************
 * THE FOLLOWING IMPORTS ARE HERE ONLY TO MAKE THE JAVADOC AND iterator() METHOD SIGNATURE
 * "SEE" THE RELEVANT CLASSES. SOME OF THOSE IMPORTS MIGHT *NOT* BE NEEDED BY YOUR OWN
 * IMPLEMENTATION, AND IT IS COMPLETELY FINE TO ERASE THEM. THE CHOICE IS YOURS.
 * ********************************************************************************** */

import demos.GenericArrays;
import pqueue.exceptions.*;
import pqueue.fifoqueues.FIFOQueue;
import pqueue.heaps.ArrayMinHeap;

import java.util.*;
/**
 * <p>{@link LinearPriorityQueue} is a {@link PriorityQueue} implemented as a linear {@link java.util.Collection}
 * of common {@link FIFOQueue}s, where the {@link FIFOQueue}s themselves hold objects
 * with the same priority (in the order they were inserted).</p>
 *
 * <p>You  <b>must</b> implement the methods in this file! To receive <b>any credit</b> for the unit tests related to
 * this class, your implementation <b>must</b>  use <b>whichever</b> linear {@link Collection} you want (e.g
 * {@link ArrayList}, {@link LinkedList}, {@link java.util.Queue}), or even the various {@link List} and {@link FIFOQueue}
 * implementations that we provide for you. You can also use <b>raw</b> arrays, but take a look at {@link GenericArrays}
 * if you intend to do so. Note that, unlike {@link ArrayMinHeap}, we do not insist that you use a contiguous storage
 * {@link Collection}, but any one available (including {@link LinkedList}) </p>
 *
 * @param <T> The type held by the container.
 *
 * @author  ---- Keshab Acharya ----
 *
 * @see MinHeapPriorityQueue
 * @see PriorityQueue
 * @see GenericArrays
 */
public class LinearPriorityQueue<T> implements PriorityQueue<T> {

	/* ***********************************************************************************
	 * Write any private data elements or private methods for LinearPriorityQueue here...*
	 * ***********************************************************************************/
	public class Elements {
		private T value;
		private int priority;
		public Elements(T value, int priority) {
			this.value = value;
			this.priority = priority;
		}
		public int getPriority() {
			return priority;
		}
		public T getValue() {
			return value;
		}
	}
	private int capacity;
	private boolean modified;
	private ArrayList<Elements> queue;




	/* *********************************************************************************************************
	 * Implement the following public methods. You should erase the throwings of UnimplementedMethodExceptions.*
	 ***********************************************************************************************************/

	/**
	 * Default constructor initializes the element structure with
	 * a default capacity. This default capacity will be the default capacity of the
	 * underlying element structure that you will choose to use to implement this class.
	 */
	public LinearPriorityQueue(){
		queue = new ArrayList<Elements>();
		modified = false;
	}

	/**
	 * Non-default constructor initializes the element structure with
	 * the provided capacity. This provided capacity will need to be passed to the default capacity
	 * of the underlying element structure that you will choose to use to implement this class.
	 * @see #LinearPriorityQueue()
	 * @param capacity The initial capacity to endow your inner implementation with.
	 * @throws InvalidCapacityException if the capacity provided is less than 1.
	 */
	public LinearPriorityQueue(int capacity) throws InvalidCapacityException{	// DO *NOT* ERASE THE "THROWS" DECLARATION!
		queue = new ArrayList<Elements>();
		if(capacity < 1) {
			throw new InvalidCapacityException("Heaps can't be of a negative size");
		}else {
			this.capacity = capacity;
			modified = false;
		}
		
	}

	@Override
	public void enqueue(T element, int priority) throws InvalidPriorityException{	// DO *NOT* ERASE THE "THROWS" DECLARATION!
		if(priority < 1) {
			throw new InvalidPriorityException("Invalid Priority");
		}
		Elements elem = new Elements(element, priority);
		queue.add(elem);
		
		for(int i = size()-1; i > 0; i--) {
			while(queue.get(i).getPriority() < queue.get(i-1).getPriority()) {
				Collections.swap(queue, i, i-1);
			}
		}
		modified = true;
	}

	@Override
	public T dequeue() throws EmptyPriorityQueueException { 	// DO *NOT* ERASE THE "THROWS" DECLARATION!
		
		if(isEmpty()) {
			throw new EmptyPriorityQueueException("Queue is already empty");
		}else {
			T elem = queue.get(0).getValue();
			queue.remove(0);
			modified = true;
			return elem;
		}
	}

	@Override
	public T getFirst() throws EmptyPriorityQueueException {	// DO *NOT* ERASE THE "THROWS" DECLARATION!
		
		if(isEmpty()) {
			throw new EmptyPriorityQueueException("Queue is already empty");
		}else {
			return queue.get(0).getValue();
		}
	}

	@Override
	public int size() {
		return queue.size();
	}

	@Override
	public boolean isEmpty() {
		return size() == 0 ? true: false;
	}

	public String toString() {
		String result = "";
		for(int i = 0; i < size(); i++) {
			result += queue.get(i).getValue() + ","
					+ queue.get(i).getPriority();
			if(i < size()-1) {
				result += "-> ";
			}
		}
		return result;
	}

	@Override
	public Iterator<T> iterator() {
		this.modified = false;
		return new Iterator<T>() {
			int index = 0;
			
            public boolean hasNext() {
                return index < queue.size();
            }
 
			public T next() {
				if(modified) {
					throw new ConcurrentModificationException();
				}else {
					return queue.get(index++).getValue();
				}
            }

        };
	}

}