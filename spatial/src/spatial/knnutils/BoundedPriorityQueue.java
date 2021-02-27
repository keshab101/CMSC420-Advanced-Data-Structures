package spatial.knnutils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;


/**
 * <p>{@link BoundedPriorityQueue} is a priority queue whose number of elements
 * is bounded. Insertions are such that if the queue's provided capacity is surpassed,
 * its length is not expanded, but rather the maximum priority element is ejected
 * (which could be the element just attempted to be enqueued).</p>
 *
 * <p><b>YOU ***** MUST ***** IMPLEMENT THIS CLASS!</b></p>
 *
 * @author  <a href = "https://github.com/jasonfillipou/">Jason Filippou</a>
 *
 * @see PriorityQueue
 * @see PriorityQueueNode
 */
public class BoundedPriorityQueue<T> implements PriorityQueue<T>{

	/* *********************************************************************** */
	/* *************  PLACE YOUR PRIVATE FIELDS AND METHODS HERE: ************ */
	/* *********************************************************************** */
	public class Elements {
		private T value;
		private double priority;
		public Elements(T value, double priority) {
			this.value = value;
			this.priority = priority;
		}
		public double getPriority() {
			return priority;
		}
		public T getValue() {
			return value;
		}
	}
	
	private ArrayList<Elements> queue;
	private int capacity;
	private int count;
	private boolean modified;

	/* *********************************************************************** */
	/* ***************  IMPLEMENT THE FOLLOWING PUBLIC METHODS:  ************ */
	/* *********************************************************************** */

	/**
	 * Constructor that specifies the size of our queue.
	 * @param size The static size of the {@link BoundedPriorityQueue}. Has to be a positive integer.
	 * @throws IllegalArgumentException if size is not a strictly positive integer.
	 */
	public BoundedPriorityQueue(int size) throws IllegalArgumentException{
		
		if(size <= 0) {
			throw new IllegalArgumentException("size can't be negative");
		}
		queue = new ArrayList<Elements>(size);
		capacity = size;
		count = 0;
		modified = false;
	}

	/**
	 * <p>Enqueueing elements for BoundedPriorityQueues works a little bit differently from general case
	 * PriorityQueues. If the queue is not at capacity, the element is inserted at its
	 * appropriate location in the sequence. On the other hand, if the object is at capacity, the element is
	 * inserted in its appropriate spot in the sequence (if such a spot exists, based on its priority) and
	 * the maximum priority element is ejected from the structure.</p>
	 * 
	 * @param element The element to insert in the queue.
	 * @param priority The priority of the element to insert in the queue.
	 */
	@Override
	public void enqueue(T element, double priority) {
		
		if(count < capacity) {
			queue.add(new Elements(element, priority));
			//sorting the queue
			for(int i = queue.size() -1; i > 0; i--) {
				while(queue.get(i).getPriority() < queue.get(i-1).getPriority()) {
					Collections.swap(queue, i, i-1);
				}
			}
			count++;
		} else {//finding new position for the new element if it exist
			
			if(priority >= queue.get(count-1).getPriority()) {
				return;
			}
			
			int i = queue.size() -1;
			while(i >= 0 && priority < queue.get(i).getPriority()) {
				if(i == queue.size()-1) {
					queue.set(i, new Elements(element, priority));
				} else {
					Collections.swap(queue, i, i+1);		
				}
				i--;
			}
		}
		modified = true;
	}

	@Override
	public T dequeue() {
		if(isEmpty()) return null;
		T retVal = queue.get(0).getValue();
		queue.remove(0);
		count--;
		modified = true;
		return retVal;
		
	}

	@Override
	public T first() {
		if(isEmpty()) return null;
		return queue.get(0).getValue();
	}
	
	/**
	 * Returns the last element in the queue. Useful for cases where we want to 
	 * compare the priorities of a given quantity with the maximum priority of 
	 * our stored quantities. In a minheap-based implementation of any {@link PriorityQueue},
	 * this operation would scan O(n) nodes and O(nlogn) links. In an array-based implementation,
	 * it takes constant time.
	 * @return The maximum priority element in our queue, or null if the queue is empty.
	 */
	public T last() {
		if(isEmpty()) return null;
		return queue.get(count-1).getValue();
	}

	/**
	 * Inspects whether a given element is in the queue. O(N) complexity.
	 * @param element The element to search for.
	 * @return {@code true} iff {@code element} is in {@code this}, {@code false} otherwise.
	 */
	public boolean contains(T element)
	{
		for(Elements e: queue) {
			if(e.getValue().equals(element)) {
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
	public boolean isEmpty() {
		return count == 0? true: false;
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
