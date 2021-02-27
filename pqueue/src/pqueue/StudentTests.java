package pqueue;

import org.junit.Test;
import pqueue.exceptions.InvalidCapacityException;
import pqueue.exceptions.InvalidPriorityException;
import pqueue.heaps.ArrayMinHeap;
import pqueue.heaps.EmptyHeapException;
import pqueue.heaps.LinkedMinHeap;
import pqueue.heaps.MinHeap;
import pqueue.priorityqueues.EmptyPriorityQueueException;
import pqueue.priorityqueues.LinearPriorityQueue;
import pqueue.priorityqueues.MinHeapPriorityQueue;
import pqueue.priorityqueues.PriorityQueue;

import static org.junit.Assert.*;

/**
 * {@link StudentTests} is a {@code jUnit} testing library which you should extend with your own tests.
 *
 * @author  <a href="https://github.com/JasonFil">Jason Filippou</a> and --- Keshab Acharya! ----
 */
public class StudentTests {

    private static String throwableInfo(Throwable thrown){
        return "Caught a " + thrown.getClass().getSimpleName() +
                " with message: " + thrown.getMessage();
    }

    private MinHeap<String> myHeap;
    private PriorityQueue<String> myQueue;

    
    
    @Test
    public void initAndAddOneElement() throws InvalidPriorityException {
        try {
            myHeap = new ArrayMinHeap<>();
            myQueue = new MinHeapPriorityQueue<>();
        } catch(Throwable t){
            fail(throwableInfo(t));
        }
        assertTrue("After initialization, all MinHeap and PriorityQueue implementations should report that they are empty.",
                myHeap.isEmpty() && myQueue.isEmpty());
        assertTrue("After initialization, all MinHeap and PriorityQueue implementations should report a size of 0.",
                (myHeap.size() == 0) && (myQueue.size() == 0));
        myHeap.insert("Mary");
        assertEquals("After inserting an element, ArrayMinHeap instances should report a size of 1.", 1, myHeap.size());

        // MinHeap::enqueue() declares that it checks InvalidPriorityException if priority <= 0 (from the docs of MinHeap).
        // In this case, we know for sure that InvalidPriorityException should *not* be thrown, since priority = 2 >= 0.
        // To avoid cluttering a code with "dummy" try-catch blocks, we declare InvalidPriorityException as checked from
        // this test as well. This is why we have the throws declaration after the name of the test.
        myQueue.enqueue("Jason", 2);
        assertEquals("After inserting an element, MinHeapPriorityQueue instances should report a size of 1.", 1, myQueue.size());
    }

    // Here is one simple way to write tests that expect an Exception to be thrown. Another, more powerful method is to
    // use the class org.junit.rules.ExpectedException: https://junit.org/junit4/javadoc/4.12/org/junit/rules/ExpectedException.html
    @Test(expected = InvalidCapacityException.class)
    public void ensureInvalidCapacityExceptionThrown() throws InvalidCapacityException{
         myQueue = new LinearPriorityQueue<>(-2);
    }

    @Test(expected = InvalidPriorityException.class)
    public void ensureInvalidPriorityExceptionThrown() throws InvalidPriorityException, InvalidCapacityException{
        myQueue = new LinearPriorityQueue<>(4);
        myQueue.enqueue("Billy", -1);
    }

    @Test
    public void testEnqueingOrder() throws InvalidPriorityException, EmptyPriorityQueueException {
        myQueue = new MinHeapPriorityQueue<>();
        myQueue.enqueue("Ashish", 8);
        myQueue.enqueue("Diana", 2);        // Lower priority, so should be up front.
        myQueue.enqueue("Adam", 2);        // Same priority, but should be second because of FIFO.
        assertEquals("We were expecting Diana up front.", "Diana", myQueue.getFirst());
    }

    @Test
    public void testDequeuingOrder() throws InvalidPriorityException, EmptyPriorityQueueException {
        testEnqueingOrder();    // To populate myQueue with the same elements.
        myQueue.dequeue();      // Now Adam should be up front.
        assertEquals("We were expecting Adam up front.", "Adam", myQueue.getFirst());
    }

    /*
    /* ******************************************************************************************************** */
    /* ********************** YOU SHOULD ADD TO THESE UNIT TESTS BELOW. *************************************** */
    /* ******************************************************************************************************** */
    
    /* Array mean Heap Tests*/
    private MinHeap<Integer> heap;
    @Test
    public void testArrayMeanHeapEmptyConstructor() {
    	try {
            heap = new ArrayMinHeap<Integer>();
        } catch(Throwable t){
            fail(throwableInfo(t));
        }
    	assertTrue("After initialization, all MinHeap and PriorityQueue implementations should report that they are empty.",
                heap.isEmpty());
        assertTrue("After initialization, all MinHeap and PriorityQueue implementations should report a size of 0.",
                (heap.size() == 0));
        
        try {
            heap = new ArrayMinHeap<Integer>(5);
        } catch(Throwable t){
            fail(throwableInfo(t));
        }
        assertTrue("After initialization, all MinHeap and PriorityQueue implementations should report a size of 0.",
                (heap.size() == 1));
        	
    }
    
    @Test
    public void testMinArrayInsert() throws EmptyHeapException {
    	heap = new ArrayMinHeap<Integer>();
    	heap.insert(5);
    	heap.insert(8);
    	heap.insert(10);
    	heap.insert(3);
    	assertTrue(heap.size() == 4);
    	heap.insert(1);
    	assertTrue(heap.getMin() == 1);
    	
    	
    } 
    //Test Delete, isEmpty, size(), getMin(), toString()
    @Test
    public void testArrayMinArrayDelete() throws EmptyHeapException {
    	heap = new ArrayMinHeap<Integer>();
    	heap.insert(5);  	
    	heap.insert(8);
    	heap.insert(10);
    	heap.insert(3);
    	assertTrue(heap.size() == 4);
    	heap.insert(1);
    	assertTrue(heap.getMin() == 1);
    	assertEquals("The raw array as a string should look like: ", "1 3 10 8 5 ", heap.toString()); 	
    	assertTrue(heap.deleteMin() == 1);
    	assertEquals("After removing the min which is 1: ", "3 5 10 8 ", heap.toString()); 
    	assertTrue(heap.deleteMin() == 3);
    	assertEquals(heap.toString(), "5 8 10 ");
    	assertTrue(heap.deleteMin() == 5);
    	assertEquals(heap.toString(), "8 10 ");
    	assertTrue(heap.deleteMin() == 8);
    	assertEquals(heap.toString(), "10 ");
    	assertTrue(heap.deleteMin() == 10);
    	assertTrue(heap.isEmpty());

    } 
    //====================================================================================================
    /* LinkedMinHeap Tests*/
    //====================================================================================================
    @Test
    public void testLinkedMinHeapConstructor() {
    	try {
            heap = new LinkedMinHeap<Integer>();
        } catch(Throwable t){
            fail(throwableInfo(t));
        }
    	assertTrue("After initialization, all MinHeap and PriorityQueue implementations should report that they are empty.",
                heap.isEmpty());
        assertTrue("After initialization, all MinHeap and PriorityQueue implementations should report a size of 0.",
                (heap.size() == 0));
        
        try {
            heap = new LinkedMinHeap<Integer>(5);
        } catch(Throwable t){
            fail(throwableInfo(t));
        }
        assertTrue("After initialization, all MinHeap and PriorityQueue implementations should report a size of 0.",
                (heap.size() == 1));
        	
    }
    
    @Test
    public void testLinkedMinHeapInsert() throws EmptyHeapException {
    	heap = new LinkedMinHeap<Integer>();
    	heap.insert(2);
    	heap.insert(5);
    	heap.insert(3);
    	heap.insert(8);
    	heap.insert(6);
    	
    } 
    
    //===================================================================================================//
    
    /*Testing for the Linear Priority Queue class*/
    private PriorityQueue<String> pqueue;
    @Test
    public void testLinearQueueConstructor() {
    	try {
            pqueue = new LinearPriorityQueue<String>();
        } catch(Throwable t){
            fail(throwableInfo(t));
        }
    	
    }
    @Test
    public void testEnqueueAndDequeue() throws InvalidPriorityException, EmptyPriorityQueueException{
    	pqueue = new LinearPriorityQueue<String>();
    	assertTrue(pqueue.isEmpty());
    	assertTrue(pqueue.size() == 0);
    	pqueue.enqueue("A", 1);
    	pqueue.enqueue("D", 3);
    	pqueue.enqueue("B", 2);
    	pqueue.enqueue("C", 2);
    	pqueue.enqueue("F", 1);
    	//System.out.println(pqueue.toString());
    	//assertEquals(pqueue.toString(), "A,1-> F,1-> B,2-> C,2-> D,3");
    	assertTrue(pqueue.dequeue() == "A");
    	assertTrue(pqueue.dequeue() == "F");
    	assertTrue(pqueue.dequeue() == "B");
    	//assertEquals(pqueue.toString(), "C,2-> D,3");
    	//System.out.println(pqueue.toString());
    	
    	
    }
    
    @Test(expected = InvalidPriorityException.class)
    public void InvalidEnqueue() throws InvalidPriorityException, EmptyPriorityQueueException{
    	pqueue = new LinearPriorityQueue<String>();
    	pqueue.enqueue("A", -1);
    	pqueue.dequeue();
    }
    

    
    
    
}
