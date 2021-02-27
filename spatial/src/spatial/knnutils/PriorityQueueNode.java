package spatial.knnutils;

/**
 * <p>A PriorityQueueNode is a {@link Comparable} type which is used to wrap around
 * the (data, priority) pairs in a {@link PriorityQueue}. Its overriding of the
 * {@link Comparable#compareTo(Object)} method allows the {@link PriorityQueue} to disambiguate between
 * the same priority elements, thus establishing a strict <em>natural ordering</em> inside the data structure.</p>
 *
 * <p><b>YOU SHOULD ***NOT*** EDIT THIS CLASS!</b> If you do, you risk <b>not passing our tests!</b></p>
 *
 * @author <a href="https://github.com/jasonfilippou/">Jason Filippou</a>
 *
 * @param <T> The type of element contained in the PriorityQueueNode.
 *
 * @see PriorityQueue
 * @see BoundedPriorityQueue
 */
public class PriorityQueueNode<T> implements Comparable<PriorityQueueNode<T>>{

	private T data;
	private double priority;
	private int orderInserted;

	/**
	 * 3-arg constructor.
	 * @param data The element of type T held by the container.
	 * @param priority The element's priority, as provided by the caller. <b>&quot;Smaller&quot;priorities are considered &quot;higher.</b>*
	 * @param insertionOrder The order that the element was inserted in, as provided by the caller. This parameter is important
	 *                       because, in a {@link PriorityQueue}, tie-breakers between elements with the same priority are
	 *                       determined by their insertion order in the {@link PriorityQueue}. <b>It is the caller's responsibility
	 *                       to ensure that this parameter is passed correctly.</b>
	 */
	public PriorityQueueNode(T data, double priority, int insertionOrder){
		this.data = data;
		this.priority = priority;
		orderInserted = insertionOrder;
	}

	/**
	 * 1-arg constructor.
	 * @param data The element of type T held by the container.
	 */
	public PriorityQueueNode(T data){
		this(data, -1, 0);
	}

	/**
	 * Default constructor.
	 */
	public PriorityQueueNode(){
		this(null);
	}

	@Override
	public int compareTo(PriorityQueueNode<T> o) {
		// Remember that a numerically smaller priority
		// is actually considered larger in priority
		// queue terms. Also recall that we are using a 
		// MinHeap, so the smallest elements ascend to the top,
		// not the largest.
		if(priority < o.priority)
			return -1;
		else if(priority > o.priority)
			return 1;
		else {	// Break ties in FIFO order
			if(orderInserted < o.orderInserted)
				return -1;
			else // Covers equality but it's not like we'll ever have equality of two elements.
				return 1;
		}
	}

	/**
	 * Simple getter for contained data element.
	 * @return The element of type T contained by this.
	 */
	public T getData(){
		return data;
	}

	/**
	 * Simple getter for element's priority.
	 * @return The contained element's priority. Smaller {@code double}s represent higher priorities.
	 */
	public double getPriority(){
		return priority;
	}

}