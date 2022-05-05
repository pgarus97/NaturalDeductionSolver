package model;

import java.util.ArrayList;
import java.util.Stack;

//<summary>
//class that models our proof in tabular style
//</summary>

public class Proof {
	private ArrayList currentTrees = new ArrayList();	//current trees and subproofs
	private ParseTree conclusionTree;	//conclusion that needs to be reached in this proof
	private boolean solved = false;	//saves if proof is proven
	private int currentCount;	//integer that tells you how many unlocked trees there are in the Proof (recursively down also in others)
	private boolean disjunctionBox = false;	//boolean that marks a proof being the second disjunctionElimination subproof (special-case)
	private Stack<Proof> activeDeduction = new Stack<Proof>();	//Stack with active subproof on top

	//constructor with conclusion
	public Proof(ParseTree conclusion) {
		conclusionTree = conclusion;
		activeDeduction.push(this);
	}
	
	//resets everything
	public void clear() {
		currentTrees.clear();
		conclusionTree = null;
		solved = false;
		currentCount = 0;
		activeDeduction = new Stack<Proof>();
		activeDeduction.push(this);
	}

	//Wrapper method for output
	public String outputEverything() {
		String output="";
		for(int i=0;i<=activeDeduction.size();i++){
			output = output(0,0,0,this.activeDeduction);
		}
		return output;
	}
	
	//recursive output
	public String output(int line, int line2,int depth,Stack<Proof> activeDeduction) {
		currentCount = updateCurrentCount(this);
		String output= "";
		if(!disjunctionBox) {
			for(int j=0;j<depth;j++) {
				output += "------------------------";
			}
			output +=  "\n";
		}
		for(int i = 0; i<currentTrees.size();i++) {
			if(currentTrees.get(i).getClass().getSuperclass() == ParseTree.class) {
				ParseTree temp = (ParseTree) currentTrees.get(i);
				for(int j=0;j<depth;j++) {
					output += "| 	";
				}
				if(temp.getLocked()) {
					output += "	"+temp.braceTreeIntoString() +"		"+ temp.getOrigin()+"\n";
					line2++;
				}else {
					output += line + "	"+ temp.braceTreeIntoString() +"		"+ temp.getOrigin()+"\n";
					line++;
					line2++;
				}
			}
			if(currentTrees.get(i).getClass() == Proof.class) {
				Proof temp = (Proof) currentTrees.get(i);
				depth++;
				output +=  temp.output(line,line2,depth,activeDeduction);
				depth--;
				line2 += temp.getCurrentTreesSizeAll();
			}
		}
		checkProof(activeDeduction);
		if(solved == false) {
			for(int j=0;j<depth;j++) {
				output += "------------------------";
			}	
			output += "\n";
			return output;
		}else {
			for(int j=0;j<depth;j++) {
				output += "| 	";
			}
			output += "	"+"PROVEN"+ "\n";
			for(int j=0;j<depth;j++) {
				output += "------------------------";
			}	
			return output += "\n";
		}
	}
	
	
	//returns an int that tells you how many trees there are in the Proof(recursively down also in subproofs)
	public int updateCurrentCount(Proof startModel) {
		int tempCount = 0;
		for(int i = 0; i<startModel.currentTrees.size();i++) {
			if(startModel.currentTrees.get(i).getClass().getSuperclass() == ParseTree.class) {
				ParseTree temp = (ParseTree) startModel.currentTrees.get(i);
				if(temp.getLocked()==false) {
					tempCount++;
				}		
			}
			if(startModel.currentTrees.get(i).getClass() == Proof.class) {
				tempCount += updateCurrentCount((Proof) startModel.currentTrees.get(i));
			}
		}
		startModel.currentCount = tempCount;
		return tempCount;
	}
	
	//helper-functions
	
	//checks if any non locked tree equals the conclusion tree and solves the proof (locks it)
	public boolean checkProof(Stack<Proof> activeProofs) {
		for(int i = 0; i<currentTrees.size();i++) {
			if(currentTrees.get(i).getClass().getSuperclass().equals(ParseTree.class)) {
				ParseTree tempTree =(ParseTree) currentTrees.get(i);
				if(tempTree.getLocked()==false) {
					if(tempTree.compare(conclusionTree.getRoot()) && solved == false) {
						solved = true;
						for(int j=0;j<currentTrees.size();j++) {
							if(currentTrees.get(j).getClass().getSuperclass().equals(ParseTree.class)) {
								ParseTree tempTree2 = (ParseTree) currentTrees.get(j);
								tempTree2.setLocked(true);
							}
						}
						if(activeProofs.size()>1) {
							activeProofs.pop();
							activeProofs.peek().updateTreeLocks();
						}
						break;
					}	
				}
			}
		}
		return solved;
	}
	

	//updates the tree locks f.e. if a subproof has been proven
	public void updateTreeLocks() {
		if(currentTrees.get(0).getClass().getSuperclass().equals(ParseTree.class) && disjunctionBox==true) {
			//unlock the premise for the disjunction elimination case
			ParseTree tempTree2 = (ParseTree) currentTrees.get(0);
			tempTree2.setLocked(false);	
		}
		for(int i = 1; i<currentTrees.size();i++) {
			if(currentTrees.get(i).getClass().getSuperclass() == ParseTree.class) {
				ParseTree temp = (ParseTree) currentTrees.get(i);
					if(temp.getLocked()==true && currentTrees.get(i-1).getClass() == Proof.class) {
						Proof tempDeduction = (Proof) currentTrees.get(i-1);
						if(tempDeduction.solved) {
							temp.setLocked(false);
							updateCurrentCount(this);
					}
				}
			}
		}
	}

	//gets a fresh (not yet used variable outside subproof)
	public String getFreshVariable() {
		int currentIndex = 0;
		ArrayList<ParseTree> allTrees = new ArrayList<ParseTree>();
		allTrees = getTreeArrayIgnoreLock(allTrees,this);
		for(int i=0;i<allTrees.size();i++) {
			int currentMaxIndex = ((PredicateParseTree)allTrees.get(i)).getMaxIndex();
			if(currentMaxIndex>currentIndex) {
				currentIndex = currentMaxIndex;
			}
		}
		return "x"+(currentIndex+1); //returns currently just x, can be an arbitrary variable name
	}
	
	//checks if there is a duplicate tree inside the proof
	public boolean checkDuplicateTree(ParseTree newTree) {
		ArrayList<ParseTree> current = new ArrayList<ParseTree>();
		current = getActiveTreeArray(current);
		for(int i=0;i<current.size();i++) {
			if(newTree.compare(current.get(i).getRoot())){
				return true;
			}
		}
		return false;
	}
	
	//getter and setter and adding to data structures and other adding support functions
	
	// adds a proof to the stack
	public void addDeduction(Proof deduction) {
		currentTrees.add(deduction);
		updateCurrentCount(this);
	}
	
	// adds a proof to the stack at index
	public void addDeductionIndex(int index, Proof deduction) {
		currentTrees.add(index,deduction);
		updateCurrentCount(this);
	}
	
	// adds a proof to the active proofs stack
	public void addDeductionToActive(Proof deduction) {
		activeDeduction.peek().currentTrees.add(deduction);
		activeDeduction.peek().updateCurrentCount(this);
	}
	
	// adds a proof to the active proofs stack at index
	public void addDeductionIndexToActive(int index, Proof deduction) {
		activeDeduction.peek().currentTrees.add(index,deduction);
		activeDeduction.peek().updateCurrentCount(this);
	}
	
	//gets an unlocked tree at index (line number)
	public ParseTree getTree(int index) {
		if(index < currentCount) {
			
			ArrayList<ParseTree> current = new ArrayList<ParseTree>();
			current = getTreeArray(current,this);
			ParseTree temp = current.get(index);
			if(temp.getLocked()==false) {
				return temp;
			}else {
				return null;
			}
		}
		return null;
	}
	
	//gets a tree (ignoring locks) at index (data structure)
	public ParseTree getTreeIgnoreLock(int index) {
		if(index < currentCount) {
			ArrayList<ParseTree> current = new ArrayList<ParseTree>();
			current = getTreeArray(current,this);
			ParseTree temp = current.get(index);
				return temp;
		}
		return null;
	}
	
	//gets an array of all unlocked trees
	public ArrayList getTreeArray(ArrayList<ParseTree> current, Proof startModel) {
		for(int i = 0; i<startModel.currentTrees.size();i++) {
			if(startModel.currentTrees.get(i).getClass().getSuperclass() == ParseTree.class) {
				ParseTree temp = (ParseTree) startModel.currentTrees.get(i);
				if(temp.getLocked()==false) {
					current.add(temp);
					continue;
				}
			}
			if(startModel.currentTrees.get(i).getClass() == Proof.class) {
				current = getTreeArray(current,(Proof) startModel.currentTrees.get(i));
			}
		}
		return current;
	}
	
	//gets an array of all trees
	public ArrayList getTreeArrayIgnoreLock(ArrayList<ParseTree> current, Proof startModel) {
		for(int i = 0; i<startModel.currentTrees.size();i++) {
			if(startModel.currentTrees.get(i).getClass().getSuperclass() == ParseTree.class) {
				ParseTree temp = (ParseTree) startModel.currentTrees.get(i);
				current.add(temp);				
			}
			if(startModel.currentTrees.get(i).getClass() == Proof.class) {
				current = getTreeArrayIgnoreLock(current,(Proof) startModel.currentTrees.get(i));
			}
		}
		return current;
	}
	
	//gets an array of all trees in the active proof
	public ArrayList getActiveTreeArray(ArrayList<ParseTree> current) {
		for(int i = 0; i<activeDeduction.peek().currentTrees.size();i++) {
			if(activeDeduction.peek().currentTrees.get(i).getClass().getSuperclass() == ParseTree.class) {
				ParseTree temp = (ParseTree) activeDeduction.peek().currentTrees.get(i);
				if(temp.getLocked()==false) {
					current.add(temp);
					continue;
					}
			}
		}
		return current;
	}
	
	//adds tree to active proof 
	public void addTreeToActiveDeduction(ParseTree newTree) {
		activeDeduction.peek().currentTrees.add(newTree);
	}
	
	public void addTree(ParseTree newTree) {
		currentTrees.add(newTree);
	}
	
	public void addTreeToActiveDeduction(int index, ParseTree newTree) {
		activeDeduction.peek().currentTrees.add(index, newTree);
	}
	

	public ParseTree getConclusionTree() {
		return conclusionTree;
	}
	
	public ParseTree getActiveConclusionTree() {
		return activeDeduction.peek().conclusionTree;
	}

	public void setConclusionTree(ParseTree conclusionTree) {
		this.conclusionTree = conclusionTree;
	}

	public ArrayList getCurrentTrees() {
		return currentTrees;
	}
	
	public int getActiveCurrentTreesSize() {
		return getActiveDeduction().peek().getCurrentTrees().size();
	}

	public void setPremiseTrees(ArrayList<ParseTree> premiseTrees) {
		this.currentTrees = premiseTrees;
	}


	public boolean getSolved() {
		return solved;
	}


	public void setSolved(boolean solved) {
		this.solved = solved;
	}


	public Stack<Proof> getActiveDeduction() {
		return activeDeduction;
	}


	public void setActiveDeduction(Stack<Proof> activeDeduction) {
		this.activeDeduction = activeDeduction;
	}
	
	public int getCurrentCount() {
		return currentCount;
	}

	public void setCurrentCount(int currentCount) {
		this.currentCount = currentCount;
	}
	
	public void setDisjunctionBox(boolean disjunctionBox) {
		this.disjunctionBox = disjunctionBox;
	}
	
	public boolean getDisjunctionBox() {
		return this.disjunctionBox;
	}
	
	
	public int getCurrentTreesSizeAll() {
		ArrayList<ParseTree> current = new ArrayList<ParseTree>();
		return getTreeArrayIgnoreLock(current,this).size();
	}
}
