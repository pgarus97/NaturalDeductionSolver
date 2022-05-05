package model;

//<summary>
//Abstract ParseTree class that gets inherited by the different parse trees of different logics
//</summary>

public abstract class ParseTree {
	
	protected Node root;	//root node of the tree
    protected Node lastNode;	//last changed node
    protected boolean locked = false;	//locked status
    protected String origin = "premise";	//origin how the tree got created

	//deep copies the tree
	public abstract ParseTree getTreeCopy();
	
	public abstract ParseTree getRightSubtree();
	
	public abstract ParseTree getLeftSubtree();

	public abstract ParseTree createContradiction();
			    		
    //connects the root of a tree with the root of your tree ; deep copy
    public abstract void append(ParseTree newSubTree);
    
    //connects the root of a tree with the last node of your tree
    public abstract void appendOverwrite(ParseTree newSubTree);
    
    //Adds a node as a children to the last used Node
	    public abstract void addNodeToLastNode(String data);
 
	//Adds a node as the new root of the tree
	public abstract void addNodeAsRoot(String data);
		   
	//Wrapper method
    public String braceTreeIntoString() {
    	return braceTreeIntoStringContainer(this.root);
    }
		    
    //returns the node values of the tree in a string inm correct order
    public abstract String braceTreeIntoStringContainer(Node root);
    
    //compares this tree beginning by the root with another root node
    public boolean compare(Node root) {
    	if(braceTreeIntoString().equals(braceTreeIntoStringContainer(root))) {
    		return true;
    	}else {
    		return false;
    	}
    }
    
    //checks if a node with a given String is inside the tree
    public boolean contains(Node root, String data) {
    	if(braceTreeIntoStringContainer(root).contains(data)) {
    		return true;
    	}else {
    		return false;
    	}
    }

    //cuts off root and gives it to child at index
    public void setRootToChild(int index) {
		root.children.get(index).parent = null;
		root = root.children.get(index);
	}
    
    //getter and setter
    
    public Node getRoot(){
    	return this.root;
    }

	public boolean getLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

}


		

