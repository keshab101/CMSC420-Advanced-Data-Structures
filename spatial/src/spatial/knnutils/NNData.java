package spatial.knnutils;

/**<p>{@link NNData} is a simple "struct-like" class that stores
 * intermediate results of nearest neighbor queries. </p>
 *
 * <p><b>YOU SHOULD ***NOT*** EDIT THIS CLASS!</b> If you do, you risk <b>not passing our tests!</b></p>
 *
 * @author <a href = "https://github.com/jasonfilippou/">Jason Filippou</a>
 *
 * @param <T> The type of {@link Object} held by the container.
 *
 * @see KNNComparator
 */
public class NNData<T> {
	
	/**
	 * The current best guess about the element closest to an anchor element in nearest-neighbor
	 * searching. Declared public to facilitate access by client code.
	 */
	private T bestGuess;
	
	/**
	 * The distance between the current best guess and
	 * the anchor element. Declared public to facilitate access by client code.
	 */
	private double bestDist;
	
	/**
	 * Simple constructor that stores its arguments.
	 * @param bestGuess The current best guess.
	 * @param bestDist The euclideanDistance between the current best guess and the &quot;anchor&quot; element.
	 */
	public NNData(T bestGuess, double bestDist){
		this.bestGuess = bestGuess;
		this.bestDist = bestDist;
	}

	/**
	 * Simple mutator.
	 * @param newBestGuess The element of type {@code T} which is the new best guess element so far.
	 * @param newBestDist The distance of {@code newBestGuess} from a search centroid.
	 */
	public void update(T newBestGuess, double newBestDist){
		bestGuess = newBestGuess;
		bestDist = newBestDist;
	}

	/**
	 * Simple accessor for element {@code T} that is the best guess so far.
	 * @return The best guess so far.
	 */
	public T getBestGuess(){
		return bestGuess;
	}

	/**
	 * Simple accessor for best distance found so far.
	 * @return The best distance found so far.
	 */
	public double getBestDist(){
		return bestDist;
	}

}
