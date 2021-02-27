package spatial.kdpoint;

/** <p>{@link KDPoint} is a class that represents a k-dimensional point in Euclidean
 * space, where <em>k</em> is a positive integer. It provides methods for initialization,
 * copy construction, equality checks and distance calculations. The precision of {@link KDPoint}s
 * is arbitrary.</p>
 * 
 * <p><b>YOU SHOULD ***NOT*** EDIT THIS CLASS!</b> If you do, you risk <b>not passing our tests!</b></p>
 *
 * @author <a href="https://github.com/jasonfilippou">Jason Filippou</a>
 */
public class KDPoint {
	
	/** To make matters simple for client code, we will allow the {@link KDPoint}'s
	 * coordinates to be publicly accessible. This makes {@link KDPoint}s <b>mutable</b>,
	 * so deep copies will be required wherever we copy {@link KDPoint}s.
	 */
	public int[] coords;

	/**
	 * The point {@code (0, 0)}
	 */
	public static final KDPoint  ZERO = new KDPoint(0, 0);

	/**
	 * The point {@code (0, 1)}
	 */
	public static final KDPoint  ZEROONE = new KDPoint(0, 1);

	/**
	 * The point {@code (1 , 1)}
	 */
	public static final KDPoint  ONEONE = new KDPoint(1, 1);
	 
	/**
	 * The point {@code (1, 0)}
	 */
	public static final KDPoint  ONEZERO = new KDPoint(1, 0);

	/**
	 * The point {@code (1 , -1)}
	 */
	public static final KDPoint  ONEMINUSONE = new KDPoint(1, -1);

	/**
	 * The point {@code (0 , -1)}
	 */
	public static final KDPoint  ZEROMINUSONE = new KDPoint(0, -1);

	/**
	 * The point {@code (-1 , -1)}
	 */
	public static final KDPoint  MINUSONEMINUSONE = new KDPoint(-1, -1);
	
	/**
	 * The point{@code (-1 , 0)}
	 */
	public static final KDPoint  MINUSONEZERO = new KDPoint(-1, 0);

	/**
	 * The point{@code (-1 , 1))}
	 */
	public static final KDPoint  MINUSONEONE = new KDPoint(-1, 1);

	/**
	 * A static method that returns a {@link KDPoint} instance that describes the Cartesian origin
	 * corresponding to the dimensionality of the space provided.
	 *
	 * @param dim  The dimensionality of the space.
	 * @return The origin of the space, with appropriate dimensionality.
	 * @throws InvalidDimensionalityException if dim is less than or equal to 0.
	 */
	public static KDPoint getOriginInDim(int dim) throws InvalidDimensionalityException {
		if(dim <= 0)
			throw new InvalidDimensionalityException("Invalid dimensionality provided: " + dim + ".");
		else
			return new KDPoint(new int[dim]);	// The parameter array is flushed to zeroes by the compiler.
	}
	
	/**
	 * Default constructor initializes this as a 2D {@link KDPoint} describing
	 * the Cartesian origin.
	 */
	public KDPoint(){
		this(0, 0);
	}

	/**
	 * Initialize a {@link KDPoint} with some {@code int} values. The dimensionality
	 * of the point is implicitly given by the length of the argument {@code vals}.
	 * @param vals The values with which to initialize the {@link KDPoint}.* @see System#arraycopy(Object, int, Object, int, int)
	 */
	public KDPoint(int... vals){
		coords = new int[vals.length];
		System.arraycopy(vals, 0, coords, 0, vals.length);
	}
	
	/**
	 * Initialize a {@link KDPoint} based on an already existing {@link KDPoint}. Since {@link KDPoint} is a
	 * <b>mutable</b> class, <b>all new {@link KDPoint} instances</b> should be created by this copy-constructor!
	 * @param p The {@link KDPoint} on which we will base the creation of this.
	 */
	public KDPoint(KDPoint p){
		this(p.coords);
	}

	@Override
	public int hashCode() {
		int hash= 0;
		for(int i = 0; i < coords.length; i++)
			hash+= coords[i]  *  Math.pow(2,i);
		return hash;
	}

	@Override
	public boolean equals(Object o){
		if(o == null)
			return false;
		if(o.getClass() != this.getClass())
			return false;
		KDPoint oCasted = (KDPoint)o; // No ClassCastExceptions here because of above check
		if(oCasted.coords.length != coords.length)
			return false;
		for(int i = 0; i < coords.length; i++)
			if(!(coords[i] == (oCasted.coords[i])))
				return false;
		return true;
	}
	
	/**
	 * Calculate the <b>Euclidean distance</b> between this and p.
	 * @param p The {@link KDPoint} to calculate the distance to.
	 * @return The <b>Euclidean distance</b> between the two {@link KDPoint}s.
	 * @throws RuntimeException if the dimensionality of the two KDPoints is different.
	 */
	public double euclideanDistance(KDPoint p) throws RuntimeException{ // TODO: Make faster
		if(coords.length != p.coords.length)
			throw new RuntimeException("Cannot calculate the Euclidean Distance between KDPoints of different dimensionalities.");
		double sum = 0.0;
		for(int i = 0; i < coords.length; i++)
			sum = sum + Math.pow((coords[i] - p.coords[i]), 2);
		return Math.sqrt(sum);
	}
	
	/**
	 * A static version of distance calculations. Since the Euclidean distance is symmetric,
	 * it's somewhat awkward to have to specify a start and end point, as {@link #euclideanDistance(KDPoint) euclideanDistance} does,
	 * so we provide this option as well.
	 * @param p1 One of the two {@link KDPoint}s to calculate the distance of.
	 * @param p2 One of the two {@link KDPoint}s to calculate the distance of.
	 * @return The Euclidean distance between p1 and p2.
	 */
	public static double euclideanDistance(KDPoint p1, KDPoint p2){
		return p1.euclideanDistance(p2);
	}
	
	@Override
	public String toString(){ {
			StringBuilder retVal = new StringBuilder("(");
			for(int i = 0; i < coords.length; i++){
				retVal.append(coords[i]);
				if(i < coords.length - 1)
					retVal.append(", ");
			}
			return retVal +")";
		}
	}
}
