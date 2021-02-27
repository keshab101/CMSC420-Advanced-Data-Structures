package spatial.nodes;

import spatial.exceptions.UnimplementedMethodException;
import spatial.kdpoint.KDPoint;
import spatial.knnutils.BoundedPriorityQueue;
import spatial.knnutils.NNData;
import spatial.trees.CentroidAccuracyException;
import spatial.trees.PRQuadTree;

import java.util.ArrayList;
import java.util.Collection;

/** <p>A {@link PRQuadGrayNode} is a gray (&quot;mixed&quot;) {@link PRQuadNode}. It
 * maintains the following invariants: </p>
 * <ul>
 *      <li>Its children pointer buffer is non-null and has a length of 4.</li>
 *      <li>If there is at least one black node child, the total number of {@link KDPoint}s stored
 *      by <b>all</b> of the children is greater than the bucketing parameter (because if it is equal to it
 *      or smaller, we can prune the node.</li>
 * </ul>
 *
 * <p><b>YOU ***** MUST ***** IMPLEMENT THIS CLASS!</b></p>
 *
 *  @author --- Keshab Acharya! ---
 */
public class PRQuadGrayNode extends PRQuadNode{


    /* ******************************************************************** */
    /* *************  PLACE ANY  PRIVATE FIELDS AND METHODS HERE: ************ */
    /* ********************************************************************** */
	private PRQuadNode NW, NE, SW, SE;
    /* *********************************************************************** */
    /* ***************  IMPLEMENT THE FOLLOWING PUBLIC METHODS:  ************ */
    /* *********************************************************************** */

    /**
     * Creates a {@link PRQuadGrayNode}  with the provided {@link KDPoint} as a centroid;
     * @param centroid A {@link KDPoint} that will act as the centroid of the space spanned by the current
     *                 node.
     * @param k The See {@link PRQuadTree#PRQuadTree(int, int)} for more information on how this parameter works.
     * @param bucketingParam The bucketing parameter fed to this by {@link PRQuadTree}.
     * @see PRQuadTree#PRQuadTree(int, int)
     */
    public PRQuadGrayNode(KDPoint centroid, int k, int bucketingParam){
        super(centroid, k, bucketingParam); // Call to the super class' protected constructor to properly initialize the object!
        
        NW = NE = SW = SE = null;
        
    }


    /**
     * <p>Insertion into a {@link PRQuadGrayNode} consists of navigating to the appropriate child
     * and recursively inserting elements into it. If the child is a white node, memory should be allocated for a
     * {@link PRQuadBlackNode} which will contain the provided {@link KDPoint} If it's a {@link PRQuadBlackNode},
     * refer to {@link PRQuadBlackNode#insert(KDPoint, int)} for details on how the insertion is performed. If it's a {@link PRQuadGrayNode},
     * the current method would be called recursively. Polymorphism will allow for the appropriate insert to be called
     * based on the child object's runtime object.</p>
     * @param p A {@link KDPoint} to insert into the subtree rooted at the current {@link PRQuadGrayNode}.
     * @param k The side length of the quadrant spanned by the <b>current</b> {@link PRQuadGrayNode}. It will need to be updated
     *          per recursive call to help guide the input {@link KDPoint}  to the appropriate subtree.
     * @return The subtree rooted at the current node, potentially adjusted after insertion.
     * @see PRQuadBlackNode#insert(KDPoint, int)
     */
    @Override
    public PRQuadNode insert(KDPoint p, int k) {
    	
    	if(k < 0) {
    		throw new CentroidAccuracyException("K can't be negative");
    	}
    	
    	int cent = (int)Math.pow(2, k-2);
    	int xCent, yCent;
	
    	if(p.coords[0] >= centroid.coords[0]) { //point will go either in NE or SE since x is greater than centroid x
    		xCent = centroid.coords[0] + cent;  //determine x coord of the centroid
    		if(p.coords[1] >= centroid.coords[1]) { //point has to go to NE
    			//Deal with NE
    			yCent = centroid.coords[1] + cent;
    			if(NE == null) {
    				NE = new PRQuadBlackNode(new KDPoint(xCent, yCent), k-1, bucketingParam, p);
    			} else {
    				NE = NE.insert(p, k);
    			}
    		} else { //x is greater but y is smaller
    			//Deal with SE
    			yCent = centroid.coords[1] - cent;
    			if(SE == null) {
    				SE = new PRQuadBlackNode(new KDPoint(xCent, yCent), k-1, bucketingParam, p);
    			} else {
    				SE = SE.insert(p, k);
    			}
    		}
    	} else { //x is smaller
    		xCent = centroid.coords[0] - cent;
    		if(p.coords[1] < centroid.coords[1]) { //y is also smaller
    			//Deal with SW
    			yCent = centroid.coords[1] - cent;
    			if(SW == null) {
    				SW = new PRQuadBlackNode(new KDPoint(xCent, yCent), k-1, bucketingParam, p);
    			} else {
    				SW = SW.insert(p, k);   				
    			}
    		} else {
    			//Deal with NW
    			yCent = centroid.coords[1] + cent;
    			if(NW == null) {
    				NW = new PRQuadBlackNode(new KDPoint(xCent, yCent), k-1, bucketingParam, p);
    			} else {
    				NW = NW.insert(p, k);
    			}
    		}
    	}
    	return this;
    	
    }

    /**
     * <p>Deleting a {@link KDPoint} from a {@link PRQuadGrayNode} consists of recursing to the appropriate
     * {@link PRQuadBlackNode} child to find the provided {@link KDPoint}. If no such child exists, the search has
     * <b>necessarily failed</b>; <b>no changes should then be made to the subtree rooted at the current node!</b></p>
     *
     * <p>Polymorphism will allow for the recursive call to be made into the appropriate delete method.
     * Importantly, after the recursive deletion call, it needs to be determined if the current {@link PRQuadGrayNode}
     * needs to be collapsed into a {@link PRQuadBlackNode}. This can only happen if it has no gray children, and one of the
     * following two conditions are satisfied:</p>
     *
     * <ol>
     *     <li>The deletion left it with a single black child. Then, there is no reason to further subdivide the quadrant,
     *     and we can replace this with a {@link PRQuadBlackNode} that contains the {@link KDPoint}s that the single
     *     black child contains.</li>
     *     <li>After the deletion, the <b>total</b> number of {@link KDPoint}s contained by <b>all</b> the black children
     *     is <b>equal to or smaller than</b> the bucketing parameter. We can then similarly replace this with a
     *     {@link PRQuadBlackNode} over the {@link KDPoint}s contained by the black children.</li>
     *  </ol>
     *
     * @param p A {@link KDPoint} to delete from the tree rooted at the current node.
     * @return The subtree rooted at the current node, potentially adjusted after deletion.
     */
    @Override
    public PRQuadNode delete(KDPoint p) {
        
    	PRQuadBlackNode newNode = new PRQuadBlackNode(centroid, k, bucketingParam);
    	newNode.points = new KDPoint[bucketingParam];
    	ArrayList<KDPoint> values = new ArrayList<KDPoint>();
    	
    	int bCount = 0, gCount = 0;
    	for (PRQuadNode node: getChildren()) {
    		if( node != null && node.getClass().equals(PRQuadGrayNode.class)) {
    			gCount++;
    		} else if(node != null && node.getClass().equals(PRQuadBlackNode.class)) {
    			bCount++;
    			
    			for(KDPoint val: ((PRQuadBlackNode)node).points) {
    				
    				if(val != null) {
    					values.add(val);
    				}
    			}
    		}
    	}
    	values.remove(p);
    	
    	if(p.coords[0] >= centroid.coords[0] && p.coords[1] >= centroid.coords[0]) {
    		
    		if(NE == null) {
    			return this;
    		} 
    		NE = NE.delete(p);
    		if (bCount == 1) return NE;
    		
    		return deleteHelper(gCount, values, newNode);
    		
    	} else if(p.coords[0] < centroid.coords[0] && p.coords[1] >= centroid.coords[1]) {
    		if(NW == null) {
    			return this;
    		} 
    		NW = NW.delete(p);
    		if (bCount == 1) return NW;
    		
    		return deleteHelper(gCount, values, newNode);
    		
    	} else if (p.coords[0] < centroid.coords[0] && p.coords[1] < centroid.coords[1]) {
    		if(SW == null) {
    			return this;
    		} 
    		SW = SW.delete(p);
    		if (bCount == 1) return SW;
    		
    		return deleteHelper(gCount, values, newNode);
    		
    	} else if((p.coords[0] >= centroid.coords[0] && p.coords[1] < centroid.coords[1])){
    		if(SE == null) {
    			return this;
    		} 
    		SE = SE.delete(p);
    		if (bCount == 1) return SE;
    		
    		return deleteHelper(gCount, values, newNode);
    	}
    	return this;
    }
    private PRQuadNode deleteHelper(int gCount, ArrayList<KDPoint> values, PRQuadBlackNode newNode) {
    	
    	if (count() <= bucketingParam && gCount < 1) {
			
			int i = 0;
			for (KDPoint v: values) {
				newNode.points[i++] = v;
				newNode.count++;
			}
			return newNode;	
 		} else {
 			return this;
 		}	
    }

    @Override
    public boolean search(KDPoint p){
    	
    	if (k < 0) {
            throw new CentroidAccuracyException("K can't be negative");
        }
        if(p == null) {
             return false;
        }
        int cent = (int)Math.pow(2, k);
        if(p.coords[0] > centroid.coords[0] + cent || p.coords[0] < centroid.coords[0] - cent ||
        		p.coords[1] > centroid.coords[1] + cent || p.coords[1] < centroid.coords[1] - cent) {
        	return false;
        }
        if ((p.coords[0] >= centroid.coords[0] && p.coords[1] >= centroid.coords[1]) ||  
                (p.coords[0] == centroid.coords[0] && p.coords[1] > centroid.coords[1])) {
             if (NE == null) return false;
             
             return NE.search(p);
        } else if(p.coords[0] < centroid.coords[0] && p.coords[1] >= centroid.coords[1]) {
             if (NW == null) return false;
                
             return NW.search(p);
       
        } else if (p.coords[0] < centroid.coords[0] && p.coords[1] < centroid.coords[1]) {
             if (SW == null) return false;
    
             return SW.search(p);
        } else if((p.coords[0] >= centroid.coords[0] && p.coords[1] < centroid.coords[1]) ||
                (p.coords[0] == centroid.coords[0] && p.coords[1] < centroid.coords[1])) {
            if (SE == null) return false;
                 
            return SE.search(p);
        } else {
            return false;
        }
    }

    @Override
    public int height(){
       
    	int nwHeight = 0, neHeight = 0, swHeight = 0, seHeight = 0;
    	
    	if(NW != null) {
    		nwHeight = 1 + NW.height();
    	}
    	if(NE != null) {
    		neHeight = 1 + NE.height();
    	}
    	if(SW != null) {
    		swHeight = 1 + SW.height();
    	}
    	if(SE != null) {
    		seHeight = 1 + SE.height();
    	}
    	
    	return Math.max(Math.max(nwHeight, neHeight), Math.max(swHeight, seHeight));
    }

    @Override
    public int count(){
    	int totalCount = 0;

    	if(NW != null) {
    		totalCount += NW.count();
    	}
    	if(NE != null) {
    		totalCount += NE.count();
    	}
    	if(SW != null) {
    		totalCount += SW.count();
    	}
    	if(SE != null) {
    		totalCount += SE.count();
    	}
        return totalCount;
    }

    /**
     * Returns the children of the current node in the form of a Z-ordered 1-D array.
     * @return An array of references to the children of {@code this}. The order is Z (Morton), like so:
     * <ol>
     *     <li>0 is NW</li>
     *     <li>1 is NE</li>
     *     <li>2 is SW</li>
     *     <li>3 is SE</li>
     * </ol>
     */
    public PRQuadNode[] getChildren(){
        PRQuadNode[] arr = new PRQuadNode[4];
        arr[0] = NW;
        arr[1] = NE;
        arr[2] = SW;
        arr[3] = SE;
        return arr;
    }

    @Override
    public void range(KDPoint anchor, Collection<KDPoint> results,
                      double range) {
        
    	if(anchor.coords[0] >= centroid.coords[0] && anchor.coords[1] >= centroid.coords[1]) {
    		if(NE != null) {
    			NE.range(anchor, results, range);
    		}
    		if(NW != null && NW.doesQuadIntersectAnchorRange(anchor, range))
    			NW.range(anchor, results, range);
    		if(SW != null && SW.doesQuadIntersectAnchorRange(anchor, range))
    			SW.range(anchor, results, range);
    		if(SE != null && SE.doesQuadIntersectAnchorRange(anchor, range)) 
    			SE.range(anchor, results, range);
    		
    	} else if(anchor.coords[0] < centroid.coords[0] && anchor.coords[1] >= centroid.coords[1]) {
    		if(NW != null) {
    			NW.range(anchor, results, range);
    		}
    		if(NE != null && NE.doesQuadIntersectAnchorRange(anchor, range))
    			NE.range(anchor, results, range);
    		if(SW != null && SW.doesQuadIntersectAnchorRange(anchor, range))
    			SW.range(anchor, results, range);
    		if(SE != null && SE.doesQuadIntersectAnchorRange(anchor, range))
    			SE.range(anchor, results, range);
    		
    	} else if(anchor.coords[0] < centroid.coords[0] && anchor.coords[1] < centroid.coords[1]) {
    		if(SW != null) {
    			SW.range(anchor, results, range);
    		}
    		if(NW != null && NW.doesQuadIntersectAnchorRange(anchor, range))
    			NW.range(anchor, results, range);
    		if(NE != null && NE.doesQuadIntersectAnchorRange(anchor, range))
    			NE.range(anchor, results, range);
    		if(SE != null && SE.doesQuadIntersectAnchorRange(anchor, range)) 
    			SE.range(anchor, results, range);
    		
    		
    	} else {
    		if(SE != null) {
    			SE.range(anchor, results, range);
    		}
    		if(NW != null && NW.doesQuadIntersectAnchorRange(anchor, range))
    			NW.range(anchor, results, range);
    		if(NE != null && NE.doesQuadIntersectAnchorRange(anchor, range))
    			NE.range(anchor, results, range);
    		if(SW != null && SW.doesQuadIntersectAnchorRange(anchor, range))
    			SW.range(anchor, results, range);
    		
    	}
    }

    @Override
    public NNData<KDPoint> nearestNeighbor(KDPoint anchor, NNData<KDPoint> n)  {
    	
    	if(n.getBestDist() == -1) {
    		n.update(null, 32000);
    	}
    	if(anchor.coords[0] >= centroid.coords[0] && anchor.coords[1] >= centroid.coords[1]) {
    		if(NE != null) {
    			NE.nearestNeighbor(anchor, n);
    		}
    		if(NW != null && NW.doesQuadIntersectAnchorRange(anchor, n.getBestDist()))
    			NW.nearestNeighbor(anchor, n);
    			
    		if(SW != null && SW.doesQuadIntersectAnchorRange(anchor, n.getBestDist())) 
    			SW.nearestNeighbor(anchor, n);
    		
    		if(SE != null && SE.doesQuadIntersectAnchorRange(anchor, n.getBestDist()))
    			SE.nearestNeighbor(anchor, n);
    		
    	} else if(anchor.coords[0] < centroid.coords[0] && anchor.coords[1] >= centroid.coords[1]) {
    		if(NW != null && NW.doesQuadIntersectAnchorRange(anchor, n.getBestDist()))
    			NW.nearestNeighbor(anchor, n);
    	
    		if(NE != null && NE.doesQuadIntersectAnchorRange(anchor, n.getBestDist()))
    			NE.nearestNeighbor(anchor, n);
    		if(SW != null && SW.doesQuadIntersectAnchorRange(anchor, n.getBestDist()))
    			SW.nearestNeighbor(anchor, n);
    		if(SE != null && SE.doesQuadIntersectAnchorRange(anchor, n.getBestDist()))
    			SE.nearestNeighbor(anchor, n);
    		
    	} else if(anchor.coords[0] < centroid.coords[0] && anchor.coords[1] < centroid.coords[1]) {
    		if(SW != null && SW.doesQuadIntersectAnchorRange(anchor, n.getBestDist())) {
    			SW.nearestNeighbor(anchor, n);
    		}
    		if(NW != null && NW.doesQuadIntersectAnchorRange(anchor, n.getBestDist()))
    			NW.nearestNeighbor(anchor, n);
    		if(NE != null && NE.doesQuadIntersectAnchorRange(anchor, n.getBestDist()))
    			NE.nearestNeighbor(anchor, n);
    		if(SE != null && SE.doesQuadIntersectAnchorRange(anchor, n.getBestDist()))
    			SE.nearestNeighbor(anchor, n);
    		
    		
    	} else {
    		if(SE != null && SE.doesQuadIntersectAnchorRange(anchor, n.getBestDist())) {
    			SE.nearestNeighbor(anchor, n);
    		}
    		if(NW != null && NW.doesQuadIntersectAnchorRange(anchor, n.getBestDist())) 
    			NW.nearestNeighbor(anchor, n);
    		if(NE != null && NE.doesQuadIntersectAnchorRange(anchor, n.getBestDist())) 
    			NE.nearestNeighbor(anchor, n);
    		if(SW != null && SW.doesQuadIntersectAnchorRange(anchor, n.getBestDist())) 
    			SW.nearestNeighbor(anchor, n);
    		
    	}
    	return n;
    	
    }

    @Override
    //Completely fixed knearest
    public void kNearestNeighbors(int k, KDPoint anchor, BoundedPriorityQueue<KDPoint> queue) {
    	
    	if(anchor.coords[0] >= centroid.coords[0] && anchor.coords[1] >= centroid.coords[1]) {
    		if(NE != null) 
    			NE.kNearestNeighbors(k, anchor, queue);
    		if(NW != null) {
    			if(queue.last() == null || queue.size() < k) {
    				NW.kNearestNeighbors(k, anchor, queue);
    			} else if(NW.doesQuadIntersectAnchorRange(anchor, queue.last().euclideanDistance(anchor))) {
    				NW.kNearestNeighbors(k, anchor, queue);
    			}
    		}		
    		if(SW != null) {
    			if(queue.last() == null || queue.size() < k) {
    				SW.kNearestNeighbors(k, anchor, queue);
    			} else if(SW.doesQuadIntersectAnchorRange(anchor, queue.last().euclideanDistance(anchor))) {
    				SW.kNearestNeighbors(k, anchor, queue);
    			}
    		}	
    		if(SE != null) {
    			if(queue.last() == null || queue.size() < k) {
    				SE.kNearestNeighbors(k, anchor, queue);
    			} else if(SE.doesQuadIntersectAnchorRange(anchor, queue.last().euclideanDistance(anchor))) {
    				SE.kNearestNeighbors(k, anchor, queue);
    			}
    		}
    		
    		
    	} else if(anchor.coords[0] < centroid.coords[0] && anchor.coords[1] >= centroid.coords[1]) {
    		
    		if(NW != null) 
    			NW.kNearestNeighbors(k, anchor, queue);
    		if(NE != null) {
    			if(queue.last() == null || queue.size() < k) {
    				NE.kNearestNeighbors(k, anchor, queue);
    			} else if(NE.doesQuadIntersectAnchorRange(anchor, queue.last().euclideanDistance(anchor))) {
    				NE.kNearestNeighbors(k, anchor, queue);
    			}
    		}		
    		if(SW != null) {
    			if(queue.last() == null || queue.size() < k) {
    				SW.kNearestNeighbors(k, anchor, queue);
    			} else if(SW.doesQuadIntersectAnchorRange(anchor, queue.last().euclideanDistance(anchor))) {
    				SW.kNearestNeighbors(k, anchor, queue);
    			}
    		}	
    		if(SE != null) {
    			if(queue.last() == null || queue.size() < k) {
    				SE.kNearestNeighbors(k, anchor, queue);
    			} else if(SE.doesQuadIntersectAnchorRange(anchor, queue.last().euclideanDistance(anchor))) {
    				SE.kNearestNeighbors(k, anchor, queue);
    			}
    		}
    		
    	} else if(anchor.coords[0] < centroid.coords[0] && anchor.coords[1] < centroid.coords[1]) {
    		
    		if(SW != null) 
    			SW.kNearestNeighbors(k, anchor, queue);
    		if(NW != null) {
    			if(queue.last() == null || queue.size() < k) {
    				NW.kNearestNeighbors(k, anchor, queue);
    			} else if(NW.doesQuadIntersectAnchorRange(anchor, queue.last().euclideanDistance(anchor))) {
    				NW.kNearestNeighbors(k, anchor, queue);
    			}
    		}		
    		if(NE != null) {
    			if(queue.last() == null || queue.size() < k) {
    				NE.kNearestNeighbors(k, anchor, queue);
    			} else if(NE.doesQuadIntersectAnchorRange(anchor, queue.last().euclideanDistance(anchor))) {
    				NE.kNearestNeighbors(k, anchor, queue);
    			}
    		}	
    		if(SE != null) {
    			if(queue.last() == null || queue.size() < k) {
    				SE.kNearestNeighbors(k, anchor, queue);
    			} else if(SE.doesQuadIntersectAnchorRange(anchor, queue.last().euclideanDistance(anchor))) {
    				SE.kNearestNeighbors(k, anchor, queue);
    			}
    		}
    		
    	} else {
    		
    		if(SE != null) 
    			SE.kNearestNeighbors(k, anchor, queue);
    		if(NW != null) {
    			if(queue.last() == null || queue.size() < k) {
    				NW.kNearestNeighbors(k, anchor, queue);
    			} else if(NW.doesQuadIntersectAnchorRange(anchor, queue.last().euclideanDistance(anchor))) {
    				NW.kNearestNeighbors(k, anchor, queue);
    			}
    		}		
    		if(NE != null) {
    			if(queue.last() == null || queue.size() < k) {
    				NE.kNearestNeighbors(k, anchor, queue);
    			} else if(NE.doesQuadIntersectAnchorRange(anchor, queue.last().euclideanDistance(anchor))) {
    				NE.kNearestNeighbors(k, anchor, queue);
    			}
    		}	
    		if(SW != null) {
    			if(queue.last() == null || queue.size() < k) {
    				SW.kNearestNeighbors(k, anchor, queue);
    			} else if(SW.doesQuadIntersectAnchorRange(anchor, queue.last().euclideanDistance(anchor))) {
    				SW.kNearestNeighbors(k, anchor, queue);
    			}
    		}
    	}
    }
   
}

