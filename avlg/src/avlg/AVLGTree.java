package avlg;

import avlg.exceptions.EmptyTreeException;
import avlg.exceptions.InvalidBalanceException;

/** <p>{@link AVLGTree}  is a class representing an <a href="https://en.wikipedia.org/wiki/AVL_tree">AVL Tree</a> with
 * a relaxed balance condition. Its constructor receives a strictly  positive parameter which controls the <b>maximum</b>
 * imbalance allowed on any subtree of the tree which it creates. So, for example:</p>
 *  <ul>
 *      <li>An AVL-1 tree is a classic AVL tree, which only allows for perfectly balanced binary
 *      subtrees (imbalance of 0 everywhere), or subtrees with a maximum imbalance of 1 (somewhere). </li>
 *      <li>An AVL-2 tree relaxes the criteria of AVL-1 trees, by also allowing for subtrees
 *      that have an imbalance of 2.</li>
 *      <li>AVL-3 trees allow an imbalance of 3.</li>
 *      <li>...</li>
 *  </ul>
 *
 *  <p>The idea behind AVL-G trees is that rotations cost time, so maybe we would be willing to
 *  accept bad search performance now and then if it would mean less rotations. On the other hand, increasing
 *  the balance parameter also means that we will be making <b>insertions</b> faster.</p>
 *
 * @author Keshab Acharya!
 *
 * @see EmptyTreeException
 * @see InvalidBalanceException
 * @see StudentTests
 */
public class AVLGTree<T extends Comparable<T>> {

    /* ********************************************************* *
     * Write any private data elements or private methods here...*
     * ********************************************************* */
	class Node {
		
		private T data;
		private int height, balance;
		Node left, right;
		
		
		public Node(T data){
			this.data = data;
			left = null;
			right = null;
			height = 0;
			balance = 0; 
		}
	
		public void updateBalance() {
			this.balance = getBalance(this);
		}
		public void updateHeight() {
			this.height = getHeight(this);
		}
		
	}
	/* ********  Rotations functions  **************************** 
	 * ***********************************************************
	 * */
	
	private Node rotateLeft(Node target) {
		Node temp = target.right;
		target.right = temp.left;
		temp.left = target;
		return temp;
	}
	private Node rotateRight(Node target) {
		Node temp = target.left;
		target.left = temp.right;
		temp.right = target;
		return temp;
	}
	private Node rotateLeftRight(Node target) {
		target.left = rotateLeft(target.left);
		target = rotateRight(target);
		return target;
	}
	private Node rotateRightLeft(Node target) {
		target.right = rotateRight(target.right);
		target = rotateLeft(target);
		return target;
	}
	/* ********  Rotations functions  **************************** 
	 * ***********************************************************
	 */
	
	/*
	 * Instance Variables
	 * */
	
	
	private Node root;
	private int maxImbalance, count;


    /* ******************************************************** *
     * ************************ PUBLIC METHODS **************** *
     * ******************************************************** */

    /**
     * The class constructor provides the tree with the maximum imbalance allowed.
     * @param maxImbalance The maximum imbalance allowed by the AVL-G Tree.
     * @throws InvalidBalanceException if maxImbalance is a value smaller than 1.
     */
    public AVLGTree(int maxImbalance) throws InvalidBalanceException {
    	if (maxImbalance < 1) {
    		throw new InvalidBalanceException("Height can't be less than 1.");
    		
    	}
    	root = null;
    	this.maxImbalance = maxImbalance;
    	count = 0;

    }

    /**
     * Insert key in the tree. You will <b>not</b> be tested on
     * duplicates! This means that in a deletion test, any key that has been
     * inserted and subsequently deleted should <b>not</b> be found in the tree!
     * s
     * @param key The key to insert in the tree.
     */
    public void insert(T key) {
        if(root == null) {
        	root = new Node(key);
        	root.updateBalance();
        	root.updateHeight();
        	
        } else {
        	root = insertHelper(root, key);
        	updateBalanceAndHeight(root);
     
        }
        count++;
        
    }
    
    private Node insertHelper(Node curr, T element) {
    	
    	if(curr == null) {
    		return new Node(element);
    	} else if(element.compareTo(curr.data) < 0) {
    		curr.left = insertHelper(curr.left, element);
    		
    		updateBalanceAndHeight(curr);
    		
    		if(this.getBalance(curr) > maxImbalance) {
    			if(element.compareTo(curr.left.data) < 0) {		
    	    		curr = rotateRight(curr);
    	    	}else {   			
    	    		curr = rotateLeftRight(curr);		
    	    	}
    		}
    		updateBalanceAndHeight(curr);
    		
    	} else if(element.compareTo(curr.data) > 0) {
    		curr.right = insertHelper(curr.right, element);
    		
    		updateBalanceAndHeight(curr);
    		
    		if(this.getBalance(curr) < maxImbalance*-1) {
    			if(element.compareTo(curr.right.data) > 0) {		
    	    		curr = rotateLeft(curr);
    	    	}else {   			
    	    		curr = rotateRightLeft(curr);		
    	    	}
    		}
    		
    		updateBalanceAndHeight(curr);
    	}	
    	
    	return curr;
    }
    
    private int getBalance(Node n) {		
		return getHeight(n.left) - getHeight(n.right);
	}
	
	private int getHeight(Node n) {
		if(n == null) {
			return -1;
		} else if(n.left == null && n.right == null) {
			return 0;
		} else {
			return Math.max(getHeight(n.left), getHeight(n.right)) + 1;
		}
	}
    
    private void updateBalanceAndHeight(Node curr) {
    	
    	if(curr != null) {
    		curr.updateBalance();
    		curr.updateHeight();
    	}
    	if(curr.left != null) {
    		updateBalanceAndHeight(curr.left);
    	}
    	if(curr.right != null) {
    		updateBalanceAndHeight(curr.right);
    	}
    }
    
    /**
     * Delete the key from the data structure and return it to the caller.
     * @param key The key to delete from the structure.
     * @return The key that was removed, or {@code null} if the key was not found.
     * @throws EmptyTreeException if the tree is empty.
     */
    public T delete(T key) throws EmptyTreeException {
    	if(isEmpty()) {
    		throw new EmptyTreeException("The tree is empty");
    	} else {
    		
    		if(this.search(key) != null) {
    			Node deleted = deleteHelper(root, key);
    			
        		root = deleted;
        		count--;
        	    return key;
    		}
    		return null;
	    }
    	
    }
    
    
    private Node deleteHelper(Node curr, T key) {
    	
    	if(curr == null) {
    		return null;
    	}
    	
    	if(curr.data.compareTo(key) > 0) {
    		
    		curr.left = deleteHelper(curr.left, key);
    		
    	} else if(curr.data.compareTo(key) < 0) {
    		curr.right = deleteHelper(curr.right, key);
    		
    	} else if(curr.data.compareTo(key) == 0){
    		
    		if(curr.left == null && curr.right == null) {
    			curr = null;
    			return curr;
    		} else if(curr.right == null) {
    			curr = curr.left;
    		}else if (curr.left == null) {
    			curr = curr.right;
    		} else {
    			Node successor = findInOrderSuccessor(curr.right);
    			curr.data = successor.data;
    			Node temp = successor;
    		
    			curr.right = deleteHelper(curr.right, temp.data);
    			if(successor.left != null || successor.right != null) {
    				
    				Node nextSuccessor = findInOrderSuccessor(successor.right);
    				successor.data = nextSuccessor.data;
    			}	
    		}
    	}
    	
		updateBalanceAndHeight(root);
		if(Math.abs(getBalance(curr)) > maxImbalance) {
			if(getBalance(curr) < 0) { //right side is heavier
				if(curr.right.balance >= 0) {
	    			curr = rotateRightLeft(curr);	
	    		} else {
	    			curr = rotateLeft(curr);
	    		}
			} else { // left side is heavier
				if(curr.left.balance <= 0) {	
					curr = rotateLeftRight(curr);	 	
	    		}else {   			
	    			curr = rotateRight(curr);	
	    		}
			}
		}
		updateBalanceAndHeight(root);
		return curr;
    	
    }
    

    public Node findInOrderSuccessor(Node curr) {
    	
    	Node successor = curr;
    	while(successor.left != null) {
    		successor = successor.left;
    	}
    	return successor;
    		
    }

    /**
     * <p>Search for key in the tree. Return a reference to it if it's in there,
     * or {@code null} otherwise.</p>
     * @param key The key to search for.
     * @return key if key is in the tree, or {@code null} otherwise.
     * @throws EmptyTreeException if the tree is empty.
     */
    public T search(T key) throws EmptyTreeException {
    	
    	if(isEmpty()) {
    		throw new EmptyTreeException("The tree is empty");
    	} else {
    		return searchHelper(root, key);
    	}
    	
    }
    
    private T searchHelper(Node curr, T key) {
    	
        if(curr == null) {
            return null;
        }

        if(curr.data.compareTo(key) == 0) {
            return key;
        }

        if(key.compareTo(curr.data) > 0 && curr.right != null) {
            return searchHelper(curr.right, key);
        }
        if(key.compareTo(curr.data) < 0 && curr.left != null) {
            return searchHelper(curr.left, key);
        }
        return null;
 
    }
   

    /**
     * Retrieves the maximum imbalance parameter.
     * @return The maximum imbalance parameter provided as a constructor parameter.
     */
    public int getMaxImbalance(){
        return maxImbalance;
    }


    /**
     * <p>Return the height of the tree. The height of the tree is defined as the length of the
     * longest path between the root and the leaf level. By definition of path length, a
     * stub tree has a height of 0, and we define an empty tree to have a height of -1.</p>
     * @return The height of the tree. If the tree is empty, returns -1.
     */
    public int getHeight() {
        return getHeight(root);
    }

    /**
     * Query the tree for emptiness. A tree is empty iff it has zero keys stored.
     * @return {@code true} if the tree is empty, {@code false} otherwise.
     */
    public boolean isEmpty() {
        return root == null ? true : false;
    }

    /**
     * Return the key at the tree's root node.
     * @return The key at the tree's root node.
     * @throws  EmptyTreeException if the tree is empty.
     */
    public T getRoot() throws EmptyTreeException{
    	
    	if(isEmpty()) {
    		throw new EmptyTreeException("The tree is empty");
    	}
        return root.data;
    }


    /**
     * <p>Establishes whether the AVL-G tree <em>globally</em> satisfies the BST condition. This method is
     * <b>terrifically useful for testing!</b></p>
     * @return {@code true} if the tree satisfies the Binary Search Tree property,
     * {@code false} otherwise.
     */
    public boolean isBST() {
    	return isBSTHelper(root);
    }
    private boolean isBSTHelper(Node curr) {
    	
    	if(curr == null) {
    		return true;
    	}
    	
    	if(curr.left != null) {
    		if(curr.left.data.compareTo(curr.data) > 0) return false;
    	}
    	if(curr.right != null) {
    		if(curr.right.data.compareTo(curr.data) < 0) return false;
    	}
    	return isBSTHelper(curr.left) && isBSTHelper(curr.right);
    }


    /**
     * <p>Establishes whether the AVL-G tree <em>globally</em> satisfies the AVL-G condition. This method is
     * <b>terrifically useful for testing!</b></p>
     * @return {@code true} if the tree satisfies the balance requirements of an AVLG tree, {@code false}
     * otherwise.
     */
    public boolean isAVLGBalanced() {
        return isAVLHelper(root);
    }
    private boolean isAVLHelper(Node curr) {
    	
    	if(curr == null) return true;
    	
    	if(Math.abs(getBalance(curr)) > maxImbalance) return false;
    	
    	if(curr.left != null) {
    		if(Math.abs(getBalance(curr.left)) > maxImbalance) return false;
    	}
    	if(curr.right != null) {
    		if(Math.abs(getBalance(curr.right)) > maxImbalance) return false;
    	}
    	return isAVLHelper(curr.left) && isAVLHelper(curr.right);
    }
    

    /**
     * <p>Empties the AVL-G Tree of all its elements. After a call to this method, the
     * tree should have <b>0</b> elements.</p>
     */
    public void clear(){
       root = null;
       count = 0;
    }


    /**
     * <p>Return the number of elements in the tree.</p>
     * @return  The number of elements in the tree.
     */
    public int getCount(){
        return count;
    }
}
