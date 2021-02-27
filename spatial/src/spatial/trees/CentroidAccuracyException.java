package spatial.trees;

/**
 * {@link CentroidAccuracyException} is an interesting {@link RuntimeException} which is thrown
 * by {@link PRQuadTree} whenever an insertion of a point leads to the splitting of a centroid with
 * a side length of 2. Splitting a centroid of that side length will lead to 4 new centroids, each with a side
 * length of 1, and with corners at consecutive integer coordinates. This means that the centroid will
 * have a fractional component, which is a problem for us since {@link spatial.kdpoint.KDPoint}s have
 * {@code int} accuracy. So we are essentially keeping the tree at a maximum height of
 * <em>O(log_4(K))</em>, where <em>K</em> is the side length of the space we want to index. This means
 * that our PR-QuadTree cannot index an infinite number of {@link spatial.kdpoint.KDPoint}s, but
 * that is fine.
 *
 * @author  <a href ="https://github.com/jasonfilippou">Jason Filippou</a>
 * @see PRQuadTree
 */
public class CentroidAccuracyException extends RuntimeException {
    /**
     * Constructs a new runtime exception with {@code null} as its
     * detail message.  The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     */
    public CentroidAccuracyException(String message) {
        super(message);
    }
}
