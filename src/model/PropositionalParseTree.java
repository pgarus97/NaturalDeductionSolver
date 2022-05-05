package model;

import java.util.ArrayList;

//<summary>
//tree structure that represents the parse tree of a formula in propositional logic
//</summary>

public class PropositionalParseTree extends ParseTree{
    
    //constructor for a tree with a given String as the data of a new root
    public PropositionalParseTree(String rootData) {
        root = new PropNode();
        lastNode = root;
        root.data = rootData;
        root.children = new ArrayList<Node>();
    }
    
    //constructor for a tree with a given Node as root
    public PropositionalParseTree(Node root) {
    	this.root = (PropNode) root;
    	lastNode = (PropNode) root;
    }

    //subclass for propositional logic Nodes
    public static class PropNode extends Node{

        
        //deep copies all children from given children list of a root
        public void setChildren(ArrayList<Node> rootChildren) {
        	if(rootChildren == null) {
        		return;
        	}
        	for(int i=0;i<rootChildren.size();i++) {
        		PropNode temp = new PropNode();
        		temp.data = rootChildren.get(i).data;
        		temp.parent = this;
        		temp.children = new ArrayList<Node>();
        		this.children.add(temp);
        		if(!rootChildren.get(i).children.isEmpty()) {
	        		temp.setChildren(rootChildren.get(i).children);
        		}
        	}
        }
    }
       
    //deep copies the tree
	public ParseTree getTreeCopy() {
		PropositionalParseTree copy = new PropositionalParseTree(getRoot().getData());
		copy.setOrigin(this.origin);
		copy.getRoot().setChildren(getRoot().getChildren());
		return copy;
	}
    		
    //connects the root of a tree with the root of your tree ; deep copy
    public void append(ParseTree newSubTree) {
    	PropNode temp = new PropNode();
    	temp.data = ((PropositionalParseTree) newSubTree).getRoot().data;
		temp.children = new ArrayList<Node>();
	    temp.setChildren(((PropositionalParseTree) newSubTree).getRoot().children);
    	root.children.add(temp);
    	temp.setParent(root);
    }
    
    //connects the root of a tree with the last node of your tree
    public void appendOverwrite(ParseTree newSubTree) {
    	PropNode temp = new PropNode();
    	temp = (PropNode) ((PropositionalParseTree) newSubTree).getRoot();
    	this.root = temp;
    }
    
    //Adds a node as a children to the last used Node
    public void addNodeToLastNode(String data) {	    	
	    	PropNode newNode = new PropNode();
	    	newNode.data = data;
	        newNode.children = new ArrayList<Node>();
	        
	        newNode.parent = lastNode;
	        lastNode.children.add(newNode);
	        lastNode = newNode;
	    }
 
	   //Adds a node as the new root of the tree
    public void addNodeAsRoot(String data) {
    	PropNode newNode = new PropNode();
    	newNode.data = data;
        newNode.children = new ArrayList<Node>();
    	
    	root.parent = newNode;
    	newNode.children.add(root);
    	root = newNode;
        lastNode = newNode;
    }

   
    
    //returns the node values of the tree in a string inm correct order
    public String braceTreeIntoStringContainer(Node root) {
    	if(root.data.equals("¬")) {
    		return "("+root.data+braceTreeIntoStringContainer(root.getChildren().get(0))+")";
    	}
    	if(root.data.matches("∧|∨|→")) {
    		return "("+braceTreeIntoStringContainer(root.getChildren().get(0))+root.data+braceTreeIntoStringContainer(root.getChildren().get(1))+")";
    	}
    	if(root.data.matches("A|B|C|D|E|F|G|I|J|K") || root.data.matches(("⊥"))) {
    		return root.data;
    	}
		return "";
	}

    @Override
	public ParseTree getRightSubtree() {
		PropositionalParseTree copy = new PropositionalParseTree((PropNode) this.getRoot().getChildren().get(1));
		return copy;
	}

	@Override
	public ParseTree getLeftSubtree() {
		PropositionalParseTree copy = new PropositionalParseTree((PropNode) this.getRoot().getChildren().get(0));
		return copy;
	}
	
	public ParseTree createContradiction() {
		PropositionalParseTree contra = new PropositionalParseTree("⊥");
		return contra;
	}
    
	
}

