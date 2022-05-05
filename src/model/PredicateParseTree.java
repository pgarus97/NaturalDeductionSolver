package model;

import java.util.ArrayList;
import java.util.Set;


//<summary>
//tree structure that represents the parse tree of a formula in predicate logic
//</summary>

public class PredicateParseTree extends ParseTree{
	    private int bindingCount = 1; //count of binding quantifiers / highest binding quantifier
	    private int maxIndex = 0; //saves highest index after a variable for getting fresh ones
	    

	    
	    //constructor for a tree with a given String as the data of a new root and an int for the brujin number of given root
	    public PredicateParseTree(String rootData, int brujin, String brujinValue) {
	        root = new PredNode();
	        lastNode = root;
	        root.parent = null;
	        root.data = rootData;
	        ((PredNode) root).setBindingNumber(brujin);
	        ((PredNode) root).setBindingValue(brujinValue);
	        root.children = new ArrayList<Node>();
	    }

	    //constructor for a tree with a given Node as root
	    public PredicateParseTree(PredNode root) {
	    	this.root = root;
	    	lastNode = root;
	    	bindVariables(root);	    
	    }
	    
	    //subclass Node (single nodes of the tree)
	    public static class PredNode extends Node{ 
	        private int bindingNumber;	//binding number of the node (0 in case of free)
	        private String bindingValue;	//binding value of quantifier nodes
	        
	        
	        //getter and setter of PredNode
	        public String getBindingValue() {
	        	return bindingValue;
	        }
	        
	        public void setBindingValue(String bindingValue) {
	        	this.bindingValue = bindingValue;
	        }
	        
	        public int getBindingNumber() {
	        	return bindingNumber;
	        }
	        
	        public void setBindingNumber(int bindingNumber) {
	        	this.bindingNumber = bindingNumber;
	        }

	        
	        //deep copies all children from given children list of a root
	        public void setChildren(ArrayList<Node> rootChildren) {
	        	if(rootChildren == null) {
	        		return;
	        	}
	        	for(int i=0;i<rootChildren.size();i++) {
	        		PredNode temp = new PredNode();
	        		temp.data = rootChildren.get(i).data;
	        		temp.bindingNumber = ((PredNode) rootChildren.get(i)).getBindingNumber();
	        		temp.bindingValue = ((PredNode) rootChildren.get(i)).getBindingValue();
	        		temp.parent = this;
	        		temp.children = new ArrayList<Node>();
	        		this.children.add(temp);
	        		if(!rootChildren.get(i).children.isEmpty()) {
		        		temp.setChildren(rootChildren.get(i).children);
	        		}
	        	}
	        }
	    }
	     
	    //helper functions
	    
	    //deep copies the tree
		public PredicateParseTree getTreeCopy() {
			PredicateParseTree copy = new PredicateParseTree(getRoot().getData(),((PredNode) getRoot()).getBindingNumber(),((PredNode) getRoot()).getBindingValue());
			copy.setOrigin(this.origin);
			copy.getRoot().setChildren(getRoot().getChildren());
			copy.bindingCount = bindingCount;
			copy.maxIndex = maxIndex;
			//copy.bindVariables((PredNode)copy.getRoot());
			return copy;
		}
	    		
	    //connects the root of a tree with the root of your tree ; deep copy
	    public void append(ParseTree newSubTree) {
	    	PredNode temp = new PredNode();
	    	temp.data = ((PredicateParseTree) newSubTree).getRoot().getData();
	    	temp.bindingNumber = ((PredNode) ((PredicateParseTree) newSubTree).getRoot()).getBindingNumber();
	    	temp.bindingValue = ((PredNode) ((PredicateParseTree) newSubTree).getRoot()).getBindingValue();
    		temp.children = new ArrayList<Node>();
		    temp.setChildren(((PredicateParseTree) newSubTree).getRoot().getChildren());
	    	lastNode.children.add(temp);
	    	temp.setParent(lastNode);
	    }
	    
	    //connects the root of a tree with the last node of your tree
	    public void appendOverwrite(ParseTree newSubTree) {
	    	PredNode temp = new PredNode();
	    	temp = (PredNode) ((PredicateParseTree) newSubTree).getRoot();
	    	this.root = temp;
	    }
	    
	    //Adds a node as a children to the last used Node
	    public void addNodeToLastNode(String data, int brujin, String brujinValue) {
	    	
	    	PredNode newNode = new PredNode();
	    	newNode.data = data;
	    	newNode.bindingNumber = brujin;
	    	newNode.bindingValue = brujinValue;
	        newNode.children = new ArrayList<Node>();
	        
	        newNode.parent = lastNode;
	        lastNode.children.add(newNode);
	        lastNode = newNode;
	    }
	    
	  //Adds a node as a children to the last used Node
	    public void addNodeToLastNodeNoOverwrite(String data, int brujin, String brujinValue) {
	    	
	    	PredNode newNode = new PredNode();
	    	newNode.data = data;
	    	newNode.bindingNumber = brujin;
	    	newNode.bindingValue= brujinValue;
	        newNode.children = new ArrayList<Node>();
	        
	        newNode.parent = lastNode;
	        lastNode.children.add(newNode);
	    }
	    
	    //sets the lastNode to the parent of the last node
	    public void lastNodeToParent() {
	    	lastNode = (PredNode) lastNode.parent;
	    }
 
	    //Adds a node as the new root of the tree
	    public void addNodeAsRoot(String data, int brujin, String brujinValue) {
	    	PredNode newNode = new PredNode();
	    	newNode.data = data;
	    	newNode.bindingNumber = brujin;
	    	newNode.bindingValue = brujinValue;
	        newNode.children = new ArrayList<Node>();
	    	
	    	root.parent = newNode;
	    	newNode.children.add(root);
	    	root = newNode;
	        lastNode = newNode;

	    }
	    
	    //Adds a node to the last node of the tree
	    @Override
		public void addNodeToLastNode(String data) {	    	
	    	PredNode newNode = new PredNode();
	    	newNode.data = data;
	    	newNode.bindingNumber = 0;
	    	newNode.bindingValue = "";
	        newNode.children = new ArrayList<Node>();
	        
	        newNode.parent = lastNode;
	        lastNode.children.add(newNode);
	        lastNode = newNode;			
		}

	    //Adds a node as the new root of the tree
		@Override
		public void addNodeAsRoot(String data) {
	    	PredNode newNode = new PredNode();
	    	newNode.data = data;
	    	newNode.bindingNumber = 0;
	    	newNode.bindingValue = "";
	        newNode.children = new ArrayList<Node>();
	    	
	    	root.parent = newNode;
	    	newNode.children.add(root);
	    	root = newNode;
	        lastNode = newNode;
		}

	    //returns the node values of the tree in a string in correct order
	    public String braceTreeIntoStringContainer(Node root) {
	    	if(root.data.equals("¬")||root.data.equals("∃")||root.data.equals("∀")) {
	    		return "("+ root.data + ((PredNode) root).getBindingValue() + braceTreeIntoStringContainer(root.getChildren().get(0))+")";
	    	}
	    	if(root.data.matches("∧|∨|→")) {
	    		return "("+braceTreeIntoStringContainer(root.getChildren().get(0))+ root.data +braceTreeIntoStringContainer(root.getChildren().get(1))+")";
	    	}
	    	if(root.data.matches("L|M|N|O|P|Q|R|S|T|U|V|W")) {
	    		String temp = root.data+"(";
	    		for(int i = 0; i<root.children.size();i++) {
	    			if(i+1 == root.children.size()) {
		    			temp += braceTreeIntoStringContainer(root.getChildren().get(i));
	    			}else {
	    				temp += braceTreeIntoStringContainer(root.getChildren().get(i))+",";
	    			}
	    		}
	    		return temp+")";
	    	}
	    	if(root.data.matches("(x|y|z)(\\d*)")||root.data.matches(("⊥"))) {
	    		return root.data;
	    	}
	    	if(root.data.matches("f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u|v|w")) {
	    		String temp = root.data+"(";
	    		for(int i = 0; i<root.children.size();i++) {
	    			if(i+1 == root.children.size()) {
		    			temp += braceTreeIntoStringContainer(root.getChildren().get(i));
	    			}else {
	    				temp += braceTreeIntoStringContainer(root.getChildren().get(i))+",";
	    			}
	    		}
	    		return temp+")";
	    	}
			return "";
		}
	    
	    
	    //goes through the whole tree recursively and searches for variables, then checks if a quantifier binds them. 
	    //If there is a binding quantifier, the binding number gets updated.
	    public void bindVariables(PredNode node) {
	    	if(node.getData().matches("(x|y|z)(\\d*)")) {
	    		PredNode tempRoot = node;
	    		//checks if the index of the variable is a new maxIndex
	    		if(tempRoot.getData().length()>1) {
	    			int index = Integer.parseInt(tempRoot.getData().substring(1));
		    		if(index>maxIndex) {
		    			maxIndex = index;
		    		}
	    		}
	    		while(tempRoot.parent != null) {
	    			if(tempRoot.parent.data.matches("∃|∀")) {
	    				if(((PredNode) tempRoot.parent).getBindingValue().equals(node.data)) {
		    				node.bindingNumber = ((PredNode) tempRoot.parent).getBindingNumber();
		    				break;
		    			}
	    			}
	    			tempRoot = (PredNode) tempRoot.parent;
	    		}
	    		return;
	    	}else {
		    	for(int i = 0; i<node.getChildren().size();i++) {
		    		bindVariables((PredNode) node.getChildren().get(i));
		    	}
	    	}
	    	return;
	    }

	    //substitution algorithms
	    
		//wrapper method for substitution of an variable while deleting the root which is a quantifier
	    public void substituteVariableAll(String substitution) {
	    	int subBrujin = ((PredNode) this.getRoot()).getBindingNumber();
	    	this.root = (PredNode) root.getChildren().get(0);
	    	this.root.parent = null;
	    	lastNode = root; 
	    	substitute((PredNode) this.root, substitution, subBrujin);
	    	bindVariables((PredNode) this.root);
	    }
	    
	  //wrapper method for substitution of an variable while deleting the root which is a quantifier
	    public void substituteTermAll(PredicateParseTree substitution, int subBind) {  	
	    	substituteTerm((PredNode) this.root, (PredNode) substitution.getRoot(), subBind); 
	    	bindVariables((PredNode) this.root);
	    }

	    public void removeRootQuantifier() {
	    	this.root = (PredNode) root.getChildren().get(0);
	    	this.root.parent = null;
	    	lastNode = root;
	    	//bindVariables((PredNode) root);
	    }
	    
	    //
	    private void substituteTerm(PredNode root, PredNode subTerm, int subBind) {
	    	if(root.bindingNumber==subBind) {
	    		root.bindingNumber=0;
	    		root.data = subTerm.getData();
	    		root.setChildren(subTerm.getChildren());
	    	}else {
		    	for(int i = 0; i<root.getChildren().size();i++) {
		    		substituteTerm((PredNode) root.getChildren().get(i), subTerm, subBind);
		    	}
	    	}
	    }
	    
	    private void substitute(PredNode root, String substitution, int subBrujin) {
	    	if(root.bindingNumber==subBrujin) {
	    		root.bindingNumber=0;
	    		root.data = substitution;
	    	}else {
	    		for(int i = 0; i<root.getChildren().size();i++) {
		    		substitute((PredNode) root.getChildren().get(i), substitution, subBrujin); 
		    	}
	    	}	
	    }
	    
	    private void substituteQuantifier(PredNode root, String substitution, PredicateParseTree termTree, int subBrujin) {
	    	if(root.getBindingNumber()==0 && root.getData().equals(termTree.getRoot().getData())) {
	    		if(braceTreeIntoStringContainer(root).equals(braceTreeIntoStringContainer(termTree.getRoot())) && checkMaxBind(root)==0 ) {
		    		root.data = substitution;
		    		root.bindingNumber = subBrujin;
		    		root.children = new ArrayList<Node>();
	    		}
	    	}
	    	for(int i = 0; i<root.getChildren().size();i++) {
	    		substituteQuantifier((PredNode) root.getChildren().get(i), substitution, termTree ,subBrujin);
	    	}
	    }
	    
	    //adds a exist quantifier for a given variable and rebinds all matching unbounded variables to it
		public void addExist(String substitution, PredicateParseTree termTree) {
			addNodeAsRoot("∃", bindingCount+1, substitution);
			substituteQuantifier((PredNode) root.getChildren().get(0), substitution, termTree, bindingCount+1);
			bindVariables((PredNode) this.root);
			bindingCount++;
		}

	
		
		//true when variable not in tree or unbound or bound by quantifier bindNumber
		//false when bound in tree
		public boolean checkVariable(PredNode root, String variable, int bindNumber) {
			if(root == null) {
				return true;
			}
			if(root.data.equals(variable) && root.bindingNumber!=0 && root.bindingNumber != bindNumber) {
				return false;
			}else {
				for(Node e : root.getChildren()) {
		    		if(!checkVariable((PredNode) e, variable, bindNumber)) {
		    			return false;
		    		}
		    	}
			}
			return true;
		}
		
		//checks what the highest binding number inside the tree is
		public int checkMaxBind(Node root) {
			int count = 0;
			if(root == null) {
				return count; 
			}
			count = ((PredNode) root).getBindingNumber();
			for(Node e : root.getChildren()) {
				count += checkMaxBind((PredNode) e);
			}
			return count;
		}
		
	//true when term in tree and unbound 
		//false when bound in tree
		public boolean checkTerm(Node root, Node termTree) {
			if(((PredNode) root).getBindingNumber()== 0 && root.getData().equals(termTree.getData())) {
				String term = braceTreeIntoStringContainer(termTree);
	    		if(braceTreeIntoStringContainer(root).equals(term)) {
	    			if(checkMaxBind(root) == 0) {
	    				return true;
	    			}
	    		}
	    	}
	    	for(int i = 0; i<root.getChildren().size();i++) {
	    		if(checkTerm((PredNode) root.getChildren().get(i), termTree)) {
	    			return true;
	    		}
	    	}
			return false;
		}
		
		//getter and setter
		
		public int getBindingCount() {
			return bindingCount;
		}

		public void setBindingCount(int bindingCount) {
			this.bindingCount = bindingCount;
		}
		
	  public int getMaxIndex() {
			return maxIndex;
		}

	  public void setMaxIndex(int maxIndex) {
		  this.maxIndex = maxIndex;
	  }

	//creates a new tree of the right subtree
	@Override
	public ParseTree getRightSubtree() {
		PredicateParseTree copy = new PredicateParseTree((PredNode) this.getRoot().getChildren().get(1));
		copy.setOrigin(this.origin);
		copy.bindingCount = bindingCount;
		copy.maxIndex = maxIndex;
		return copy;
	}

	//creates a new tree of the left subtree
	@Override
	public ParseTree getLeftSubtree() {
		PredicateParseTree copy = new PredicateParseTree((PredNode) this.getRoot().getChildren().get(0));
		copy.setOrigin(this.origin);
		copy.bindingCount = bindingCount;
		copy.maxIndex = maxIndex;
		return copy;
	}
	
	//creates a new contradiction tree
	public ParseTree createContradiction() {
		PredicateParseTree contra = new PredicateParseTree("⊥",0,"");
		contra.setOrigin(this.origin);
		contra.bindingCount = bindingCount;
		contra.maxIndex = maxIndex;
		return contra;
	}
	
	//gets a set of binding numbers that are used inside the tree
	public void getBindNumSet(PredNode root, Set<Integer> bindNums) {
		if(!root.data.matches("∃|∀") && root.bindingNumber!=0) {
    		bindNums.add(root.bindingNumber);
		}
		for(int i = 0; i<root.getChildren().size();i++) {
			getBindNumSet((PredNode) root.getChildren().get(i),bindNums);
			}
	}
	
	//eliminates non binding quantifier nodes
	public void eliminateRedundantQuantifiers(PredNode root, Set<Integer> bindNums) {
		if(root.data.matches("∃|∀") && !bindNums.contains(root.bindingNumber)) {
			root.data = root.getChildren().get(0).getData();
			root.bindingNumber = ((PredNode) root.getChildren().get(0)).getBindingNumber();
			root.bindingValue = ((PredNode) root.getChildren().get(0)).getBindingValue();
			ArrayList<Node> tempChildren = root.getChildren().get(0).getChildren();
			root.children = new ArrayList<Node>();
    		root.setChildren(tempChildren);
    		
    		eliminateRedundantQuantifiers((PredNode) root, bindNums);

		}else {
			for(int i = 0; i<root.getChildren().size();i++) {
	    		eliminateRedundantQuantifiers((PredNode) root.getChildren().get(i), bindNums);
	    	}
		}   
	}
	
	
	}

