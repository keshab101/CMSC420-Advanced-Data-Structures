package pqueue.priorityqueues; // ******* <---  DO NOT ERASE THIS LINE!!!! *******


/* *****************************************************************************************
 * THE FOLLOWING IMPORTS WILL BE NEEDED BY YOUR CODE, BECAUSE WE REQUIRE THAT YOU USE
 * ANY ONE OF YOUR EXISTING MINHEAP IMPLEMENTATIONS TO IMPLEMENT THIS CLASS. TO ACCESS
 * YOUR MINHEAP'S METHODS YOU NEED THEIR SIGNATURES, WHICH ARE DECLARED IN THE MINHEAP
 * INTERFACE. ALSO, SINCE THE PRIORITYQUEUE INTERFACE THAT YOU EXTEND IS ITERABLE, THE IMPORT OF ITERATOR
 * IS NEEDED IN ORDER TO MAKE YOUR CODE COMPILABLE. THE IMPLEMENTATIONS OF CHECKED EXCEPTIONS
 * ARE ALSO MADE VISIBLE BY VIRTUE OF THESE IMPORTS.
 ** ********************************************************************************* */

import pqueue.exceptions.*;
import pqueue.heaps.ArrayMinHeap;
import pqueue.heaps.EmptyHeapException;
import pqueue.heaps.MinHeap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
/**
 * <p>{@link MinHeapPriorityQueue} is a {@link PriorityQueue} implemented using a {@link MinHeap}.</p>
 *
 * <p>You  <b>must</b> implement the methods of this class! To receive <b>any credit</b> for the unit tests
 * related to this class, your implementation <b>must</b> use <b>whichever</b> {@link MinHeap} implementation
 * among the two that you should have implemented you choose!</p>
 *
 * @author  ---- Keshab Acharya ----
 *
 * @param <T> The Type held by the container.
 *
 * @see LinearPriorityQueue
 * @see MinHeap
 * @see PriorityQueue
 */
public class MinHeapPriorityQueue<T> implements PriorityQueue<T>{

	/* ***********************************************************************************
	 * Write any private data elements or private methods for MinHeapPriorityQueue here...*
	 * ***********************************************************************************/
	private class Elements implements Comparable<Elements>{
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
		public int compareTo(MinHeapPriorityQueue<T>.Elements o) {
			
			if(this.priority < o.priority) {
				return -1;
			} else if(this.priority > o.priority) {
				return 1;
			} else {
				return 0;
			}
		}

	}

	private boolean modified;
	private ArrayMinHeap<Elements> minQueue;

	/* *********************************************************************************************************
	 * Implement the following public methods. You should erase the throwings of UnimplementedMethodExceptions.*
	 ***********************************************************************************************************/
		/**
	 * Simple default constructor.
	 */
	public MinHeapPriorityQueue(){
		minQueue = new ArrayMinHeap<Elements>();
		modified = false;
	}

	@Override
	public void enqueue(T element, int priority) throws InvalidPriorityException {	// DO *NOT* ERASE THE "THROWS" DECLARATION!
		if(priority < 1) {
			throw new InvalidPriorityException("Invalid Priority");
		}
		Elements elem = new Elements(element, priority);
		minQueue.insert(elem);
		modified = true;
	}

	@Override
	public T dequeue() throws EmptyPriorityQueueException {		// DO *NOT* ERASE THE "THROWS" DECLARATION!
		
		if(isEmpty()) {
			throw new EmptyPriorityQueueException("Queue is already empty");
		}else {
			try {
				return minQueue.deleteMin().getValue();
			} catch (EmptyHeapException e) {
				e.printStackTrace();
			}
			modified = true;
		}
		return null;
	}

	@Override
	public T getFirst() throws EmptyPriorityQueueException {	// DO *NOT* ERASE THE "THROWS" DECLARATION!
		if(isEmpty()) {
			throw new EmptyPriorityQueueException("Queue is already empty");
		}else {
			try {
				return minQueue.getMin().getValue();
			} catch(EmptyHeapException e){
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public int size() {
		return minQueue.size();
	}

	@Override
	public boolean isEmpty() {
		return minQueue.size() == 0 ? true: false;
	}
	
	public String toString() {
		String result = "";
		Iterator<Elements> it = minQueue.iterator();
		while(it.hasNext()) {
			result += it.next().getValue() + ", "
					+ it.next().getPriority() + " ";
		}
		return result;
	}

//	public String toString() {
//		String result = "";
//		for(int i = 0; i < size(); i++) {
//			result += minQueue
//			result += minQueue[i].getValue() + ","
//					+ queue.get(i).getPriority();
//			if(i < size()-1) {
//				result += "-> ";
//			}
//		}
//		return result;
//	}

	@Override
	public Iterator<T> iterator() {
		this.modified = false;
		
		return new Iterator<T>() {	
			int index = 0;
			
            public boolean hasNext() {
                return index < size();
            }
 
			public T next() {
				if(modified) {
					throw new ConcurrentModificationException();
				}else {
					
					ArrayList<Elements> list = new ArrayList<Elements>();
					Iterator<Elements> it = minQueue.iterator();
					while(it.hasNext()) {
						list.add(it.next());
					}
					Collections.sort(list);
					return list.get(index++).getValue();
				}
            }

        };
	}

}
