package spatial.kdpoint;

/**
 * An exception thrown by {@link KDPoint} instances when the user provides an invalid dimensionality parameter (non-positive).
 *
 * @author  <a href="https://github.com/jasonfilippou">Jason Filippou</a>
 *
 * @see KDPoint
 * @see KDPoint#getOriginInDim(int)
 */
public class InvalidDimensionalityException extends Exception {
    public InvalidDimensionalityException(String msg){
        super(msg);
    }
}
