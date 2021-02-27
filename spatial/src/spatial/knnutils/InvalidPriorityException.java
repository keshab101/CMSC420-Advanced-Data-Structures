package spatial.knnutils;

/**
 *<p> InvalidPriorityException is an {@link Exception} that is thrown by {@link spatial.knnutils.BoundedPriorityQueue}
 * when the user supplies a priority that is 0 or below.</p>
 *
 * <p>You should <b>not</b> edit this class! It is given to you as a resource for your project.</p>

 * @author  <a href="https://github.com/jasonfilippou">Jason Filippou</a>
 *
 * @see BoundedPriorityQueue
*/
public class InvalidPriorityException extends Exception {
    public InvalidPriorityException(String msg){
        super(msg);
    }
}
