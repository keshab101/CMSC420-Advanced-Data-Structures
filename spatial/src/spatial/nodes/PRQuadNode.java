package spatial.nodes;

import spatial.kdpoint.KDPoint;
import spatial.knnutils.BoundedPriorityQueue;
import spatial.knnutils.NNData;
import spatial.trees.CentroidAccuracyException;
import spatial.trees.PRQuadTree;

import java.util.Collection;

/**
 * <p>{@link PRQuadNode} is an abstract class used to provide the common structure that all
 * implementing subclasses will share.  It is an abstraction over nodes of a Point-Region (PR)- QuadTree.
 * Consult the lecture slides and the textbook to review the different kinds of nodes in a PR-QuadTree, what they
 * should contain and how they should implement insertion and deletion. </p>
 *
 * <p><b>YOU SHOULD ***NOT*** EDIT THIS CLASS!</b> If you do, you risk <b>not passing our tests!</b></p>
 *
 * @author <a href="https://github.com/jasonfilippou/">Jason Filippou</a>
 *
 * @see PRQuadGrayNode
 * @see PRQuadBlackNode
 * @see PRQuadTree
 */
public abstract class PRQuadNode {

    /**
     * A named constant representing infinity.
     */
    public static final double INFTY = -1.0;

    /**
     * The centroid of the current node. Its dimensions allow us to direct incoming {@link KDPoint}s
     * to the appropriate subtree.
     * <b>INVARIANT:</b> {@code centroid != null}
     * @see PRQuadNode#k
     */
    protected KDPoint centroid;

    /**
     * <p>The exponent to which 2 (two) is raised to characterize the length of the current quadrant's size. For example,
     * if k=4, the length of the size of the quadrant that is "spanned" by the current {@link PRQuadNode} is 2^4 = 16.
     * This means that the first time that the space is split, its centroid will be at (0, 0), its bottom left corner will be at
     * (-8, -8) and its top right corner will be at (8,8). </p>
     *
     * <p>Given this parameter, you can probably imagine implementations that do <b>not</b> require using the parameter
     *   {@link PRQuadNode#centroid centroid}. It is <b>completely fine</b> if you do <b>not</b> want to use this parameter at all:
     *   we are just including it there because we feel your code will be cleaner if you do.</p>
     *
     * @see PRQuadNode#centroid
     */
    protected int k;

    /**
     * The bucketing parameter of the {@link PRQuadTree}. Necessary for all derived classes
     * such that merges and splits can be determined on the fly.
     */
    protected int bucketingParam;


    /**
     * protected constructor. Every {@link PRQuadNode}, at the very minimum, requires information
     * about the dimensions of the quadrant it spans, its current centroid (which is computable from the dimensions,
     * yet allows for a cleaner and slightly more efficient implementation) and the bucketing parameter.
     * @param centroid A {@link KDPoint} that represents the center of the space spanned by the current node.
     * @param k  The exponent to which 2 is raised to characterize the side length of the quadrant &quot; spanned &quot;
     *           by this. Refer to {@link PRQuadTree#PRQuadTree(int, int)} for a more thorough explanation
     *           of how the parameter k works.
     * @param bucketingParam The bucketing parameter of the tree. Necessary so that we can store the parameter in freshly
     *                       created {@link PRQuadBlackNode}s!
     *
     * @see KDPoint
     * @see PRQuadBlackNode#PRQuadBlackNode(KDPoint, int, int)
     * @see PRQuadBlackNode#PRQuadBlackNode(KDPoint, int, int, KDPoint)
     * @see PRQuadGrayNode#PRQuadGrayNode(KDPoint, int, int)
     *
     */
    protected PRQuadNode(KDPoint centroid, int k, int bucketingParam){
        this.centroid = centroid;
        this.k = k;
        this.bucketingParam = bucketingParam;
    }

    /**
     * Inserts the given point in the subtree rooted at the current node. Returns the updated subtree.
     *
     * @param p A {@link KDPoint} to insert into the subtree rooted at the current node.
     * @param k The side length of the quadrant spanned by the current {@link PRQuadNode}. It is important that this value
     *          is <b>updated</b> per recursive call, such that the recursively generated quadrants have the appropriate side
     *          length and can drive {@link KDPoint}s to the appropriate children nodes!
     * @return The subtree rooted at the current node, potentially adjusted after insertion.
     * @throws CentroidAccuracyException if the insertion causes a decomposition of the
     *      subtree that is too &quot; fine &quot; for {@code int} coordinate {@link KDPoint}s can handle.
     */
    public abstract PRQuadNode insert(KDPoint p, int k) throws CentroidAccuracyException;

    /**
     * Deletes the given point from the subtree rooted at the current node. If the
     * point is <b>not</b> in the subtree, <b>no changes</b>  should be performed in the subtree.
     *
     * @param p A {@link KDPoint} to delete from the tree rooted at the current node.
     * @return The subtree rooted at the current node, potentially adjusted after deletion.
     */
    public abstract PRQuadNode delete(KDPoint p);

    /**
     * Searches the subtree rooted at the current node for the provided {@link KDPoint}.
     *
     * @param p The {@link KDPoint} to search for.
     * @return true if p was found in the subtree rooted at the current nodes, false otherwise.
     */
    public abstract boolean search(KDPoint p);

    /**
     * Return the height of the subtree rooted at the current nodes. The height is defined similarly to
     * AVL trees, as follows:
     * <ol>
     *      <li>The height of a null tree (no nodes) is -1 (minus 1).</li>
     *      <li>The height of a tree that consists of a single node (a "stub" tree) is 0 (zero). </li>
     *      <li>The height of a tree that consists of four children is the maximum height of its children <b>plus one</b>.</li>
     * </ol>
     * <p>
     *
     * @return the height of the subtree rooted at the current node.
     */
    public abstract int height();

    /**
     * Return the total number of {@link KDPoint}s contained in the subtree rooted at the current node.
     *
     * @return the total number of {@link KDPoint}s contained in the subtree rooted at the current node.
     */
    public abstract int count();

    /**
     * A getter for the centroid of {@code this}.
     * @return A deep copy of the centroid of the current node.
     */
    public KDPoint getCentroid()
    {
        return new KDPoint(centroid);
    }

    @Override
    public String toString() {
        return centroid.toString();
    }

    /**
     * Credits: <a href="https://stackoverflow.com/questions/401847/circle-rectangle-collision-detection-intersection">
     *     https://stackoverflow.com/questions/401847/circle-rectangle-collision-detection-intersection</a>
     * Accurate square &amp; rectangle intersection. I made modifications to the code. This method is made {@code protected}
     * so that subclasses can see it. DO NOT EDIT THIS METHOD!
     * @param anchor The centroid of the range.
     * @param range The radius of the range query.
     * @return true if the circle generated by the range query
     */
    protected boolean doesQuadIntersectAnchorRange(KDPoint anchor, double range)
    {
        double circleDistanceX = Math.abs(anchor.coords[0] - centroid.coords[0]);
        double circleDistanceY = Math.abs(anchor.coords[1] - centroid.coords[1]);

        double quad_size = Math.pow(2,k-1); // equivalent to width/2, height/2
        if (circleDistanceX > (quad_size + range)) { return false; }
        if (circleDistanceY > (quad_size + range)) { return false; }

        if (circleDistanceX <= (quad_size)) { return true; }
        if (circleDistanceY <= (quad_size)) { return true; }

        double cornerDistanceSq = Math.pow(circleDistanceX - quad_size,2) + Math.pow(circleDistanceY - quad_size,2);

        return (cornerDistanceSq <= Math.pow(range,2));
    }

    /**
     * <p>Executes a range query in the given {@link PRQuadNode}. Given an &quot;anchor&quot; {@link KDPoint},
     * all {@link KDPoint}s that have a {@link KDPoint#euclideanDistance(KDPoint) euclideanDistance} of <b>at most</b> range
     * <b>INCLUSIVE</b> from the anchor point <b>except</b> for the anchor itself should be inserted into the {@link Collection}
     * that is passed.</p>
     *
     * <p>Remember: range queries behave <em>greedily</em> as we go down (approaching the anchor as &quot;fast&quot;
     * as our currDim allows and <em>prune subtrees</em> that we <b>don't</b> have to visit as we backtrack. Consult
     * all of our resources if you need a reminder of how these should work.</p>
     *
     * @param anchor The centroid of the hypersphere that the range query implicitly creates.
     * @param results A {@link Collection} that accumulates all the {@link }
     * @param range The <b>INCLUSIVE</b> range from the &quot;anchor&quot; {@link KDPoint}, within which all the
 *              {@link KDPoint}s that satisfy our query will fall. The euclideanDistance metric used} is defined by
 *              {@link KDPoint#euclideanDistance(KDPoint)}.
     */
    public abstract void range(KDPoint anchor, Collection<KDPoint> results,
                               double range);

    /**
     * <p>Executes a nearest neighbor query, which returns the nearest neighbor, in terms of
     * {@link KDPoint#euclideanDistance(KDPoint)}, from the &quot;anchor&quot; point.</p>
     *
     * <p>Recall that, in the descending phase, a NN query behaves <em>greedily</em>, approaching our
     * &quot;anchor&quot; point as fast as currDim allows. While doing so, it implicitly
     * <b>bounds</b> the acceptable solutions under the current <b>best solution</b>, which is passed as
     * an argument. This approach is known in Computer Science as &quot;branch-and-bound&quot; and it helps us solve an
     * otherwise exponential complexity problem (nearest neighbors) efficiently. Remember that when we want to determine
     * if we need to recurse to a different subtree, it is <b>necessary</b> to compare the euclideanDistance reported by
     * {@link KDPoint#euclideanDistance(KDPoint)} and coordinate differences! Those are comparable with each other because they
     * are the same data type ({@link Double}).</p>
     *
     * @return An object of type {@link NNData}, which exposes the pair (distance_of_NN_from_anchor, NN),
     * where NN is the nearest {@link KDPoint} to the anchor {@link KDPoint} that we found.
     *
     * @param anchor The &quot;ancor&quot; {@link KDPoint}of the nearest neighbor query.
     * @param n An object of type {@link NNData}, which will define a nearest neighbor as a pair (distance_of_NN_from_anchor, NN),
     *      * where NN is the nearest neighbor found.
     *
     * @see NNData
     * @see #kNearestNeighbors(int, KDPoint, BoundedPriorityQueue)
     */
    public abstract NNData<KDPoint> nearestNeighbor(KDPoint anchor, NNData<KDPoint> n);

    /**
     * <p>Executes a nearest neighbor query, which returns the nearest neighbor, in terms of
     * {@link KDPoint#euclideanDistance(KDPoint)}, from the &quot;anchor&quot; point.</p>
     *
     * <p>Recall that, in the descending phase, a NN query behaves <em>greedily</em>, approaching our
     * &quot;anchor&quot; point as fast as currDim allows. While doing so, it implicitly
     * <b>bounds</b> the acceptable solutions under the current <b>worst solution</b>, which is maintained as the
     * last element of the provided {@link BoundedPriorityQueue}. This is another instance of &quot;branch-and-bound&quot;
     * Remember that when we want to determine if we need to recurse to a different subtree, it is <b>necessary</b>
     * to compare the euclideanDistance reported by* {@link KDPoint#euclideanDistance(KDPoint)} and coordinate differences!
     * Those are comparable with each other because they are the same data type ({@link Double}).</p>
     *
     * <p>The main difference of the implementation of this method and the implementation of
     * {@link #nearestNeighbor(KDPoint, NNData)} is the necessity of using the class
     * {@link BoundedPriorityQueue} effectively. Consult your various resources
     * to understand how you should be using this class.</p>
     *
     * @param k The total number of neighbors to retrieve. It is better if this quantity is an odd number, to
     *          avoid ties in Binary Classification tasks.
     * @param anchor The &quot;anchor&quot; {@link KDPoint} of the nearest neighbor query.
     * @param queue A {@link BoundedPriorityQueue} that will maintain at most k nearest neighbors of
     *              the anchor point at all times, sorted by euclideanDistance to the point.
     *
     * @see BoundedPriorityQueue
     */
    public abstract void kNearestNeighbors(int k, KDPoint anchor, BoundedPriorityQueue<KDPoint> queue);
}


