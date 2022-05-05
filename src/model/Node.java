package model;

import java.util.ArrayList;

//<summary>
//Abstract node class that both logics/and possible others inherit from
//</summary>

public abstract class Node {
    protected String data;	//saves the literal of a formula
    protected Node parent;	//parent node
    protected ArrayList<Node> children;	//children list
    
    //getter/setter
    public String getData() {
    	return data;
    }
    
    public ArrayList<Node> getChildren(){
    	return this.children;
    }
    
    public void setParent(Node parent) {
    	this.parent = parent;
    }
    
    //deep copies all children from given children list of a root
    public abstract void setChildren(ArrayList<Node> rootChildren);
}
