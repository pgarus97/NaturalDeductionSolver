package controller;

import model.ParseTree;
import model.Parser;
import model.Proof;
import view.MainView;
import view.RuleView;

//<summary>
//Abstract class to inherit for logics
//</summary>

public abstract class RuleController {
	protected MainView view;	//main window
	protected RuleView ruleView;	//rule window
	protected Proof mainproof;	//the main proof that needs to be completed
	protected Parser parser;	//a parser object
	
	
	//inference rule functionality

		public void applyConjunctionIntroduction(String line1, String line2) {
			int input1 = Integer.parseInt(line1);
			int input2 = Integer.parseInt(line2);
			if(checkErrors(input1,input2)) {
				ParseTree temp1 = mainproof.getTree(input1).getTreeCopy();
				ParseTree temp2 = mainproof.getTree(input2).getTreeCopy();
				temp1.addNodeAsRoot("∧");
				temp1.append(temp2);
				temp1.setOrigin("∧i "+"Lines: "+line1+" , "+line2);
				if(checkTreeErrors(temp1)) {
					mainproof.addTreeToActiveDeduction(mainproof.getActiveCurrentTreesSize()-1,temp1);
					view.setSolution(mainproof.outputEverything());
					checkFinalSolution();
				}
			}
		}


		public void applyConjunctionElimination(String line1, int type) {
			int input1 = Integer.parseInt(line1);
			if(checkErrors(input1)) {
				ParseTree temp1 = mainproof.getTree(input1).getTreeCopy();
				if(temp1.getRoot().getData().equals("∧")) {
					temp1.setRootToChild(type-1);
					temp1.setOrigin("∧e"+type+" Line: "+line1);
					if(checkTreeErrors(temp1)) {
						mainproof.addTreeToActiveDeduction(mainproof.getActiveCurrentTreesSize()-1,temp1);
						view.setSolution(mainproof.outputEverything());
						checkFinalSolution();
					}
				}else {
					ruleView.setErrorLabel("You can not use the conjunction elimination rule "+type+ "for " +line1+". There is no conjunction!");
					return;
				}
			}
			
		}


		
		public void applyDoubleNegationElimination(String line1) {
			int input1 = Integer.parseInt(line1);
			if(checkErrors(input1)) {
				ParseTree temp1 = mainproof.getTree(input1).getTreeCopy();
				if(temp1.getRoot().getData().equals("¬") && temp1.getRoot().getChildren().get(0).getData().equals("¬")) {
					temp1.setRootToChild(0);
					temp1.setRootToChild(0);
					temp1.setOrigin("¬¬e "+"Line: "+line1);
					if(checkTreeErrors(temp1)) {
						mainproof.addTreeToActiveDeduction(mainproof.getActiveCurrentTreesSize()-1,temp1);			
						view.setSolution(mainproof.outputEverything());
						checkFinalSolution();
					}
				}else {
					ruleView.setErrorLabel("You can not use the ¬¬ elimination rule for " +line1+". There is no double negation!");
					return;
				}		
			}
			
		}


		
		public void applyDoubleNegationIntroduction(String line1) {
			int input1 = Integer.parseInt(line1);
			if(checkErrors(input1)) {
				ParseTree temp1 = mainproof.getTree(input1).getTreeCopy();
				temp1.addNodeAsRoot("¬");
				temp1.addNodeAsRoot("¬");
				temp1.setOrigin("¬¬i "+"Line: "+line1);
				if(checkTreeErrors(temp1)) {
					mainproof.addTreeToActiveDeduction(mainproof.getActiveCurrentTreesSize()-1,temp1);			
					view.setSolution(mainproof.outputEverything());
					checkFinalSolution();
				}
			}
		}


		
		public void applyImplicationElimination(String line1, String line2) {	
			int input1 = Integer.parseInt(line1);
			int input2 = Integer.parseInt(line2);
			if(checkErrors(input1,input2)) {
				ParseTree temp1 = mainproof.getTree(input1).getTreeCopy();
				ParseTree temp2 = mainproof.getTree(input2).getTreeCopy();
				if(temp2.getRoot().getData().equals("→")) {
					if(temp1.compare(temp2.getRoot().getChildren().get(0))==false){
						ruleView.setErrorLabel("The two formulas in Line "+line1+ " and "+line2+" do not match!");
						return;
					}else {
						ParseTree temp3 = temp2.getRightSubtree();
						temp3.setOrigin("→e "+"Lines: "+line1+" , "+line2);
						if(checkTreeErrors(temp3)) {
							mainproof.addTreeToActiveDeduction(mainproof.getActiveCurrentTreesSize()-1,temp3);
							view.setSolution(mainproof.outputEverything());
							checkFinalSolution();
						}
					}
				}else {
					ruleView.setErrorLabel("You can not use the implication elimination rule for line " +line1+ " and line "+line2+". There is no implication in the formula.");
					return;
				}
			}	
		}
		


		//method for the introduction rule of a disjunction (Version 1 and Version 2) 
		public void applyDisjunctionIntroduction(String line1, String formula, int type) {
			int input1 = Integer.parseInt(line1);
			if(checkErrors(input1,formula)) {
				ParseTree temp1 = mainproof.getTree(input1).getTreeCopy();
				ParseTree temp2 = parser.parseIntoTree(formula);
				if(type == 1) {
					temp1.addNodeAsRoot("∨");
					temp1.append(temp2);
					temp1.setOrigin("∨i"+type+" Line: "+line1+" With: "+ formula);
					if(checkTreeErrors(temp1)) {
						mainproof.addTreeToActiveDeduction(mainproof.getActiveCurrentTreesSize()-1,temp1);
					}
				}else if(type == 2){
					temp2.addNodeAsRoot("∨");
					temp2.append(temp1);
					temp2.setOrigin("∨i"+type+" Line: "+line1+" With: "+ formula);
					if(checkTreeErrors(temp2)) {
						mainproof.addTreeToActiveDeduction(mainproof.getActiveCurrentTreesSize()-1,temp2);
					}
				}
				view.setSolution(mainproof.outputEverything());
				checkFinalSolution();
			}		
		}

		public void applyNegationElimination(String line1, String line2) {
			int input1 = Integer.parseInt(line1);
			int input2 = Integer.parseInt(line2);
			if(checkErrors(input1,input2)) {
				ParseTree temp1 = mainproof.getTree(input1).getTreeCopy();
				ParseTree temp2 = mainproof.getTree(input2).getTreeCopy();
					if(temp2.getRoot().getData().equals("¬")) {
						if(temp1.compare(temp2.getRoot().getChildren().get(0))==false){
							ruleView.setErrorLabel("The two formulas in Line "+line1+ " and "+line2+" do not match!");
							return;
						}else {
							ParseTree temp3 = temp1.createContradiction();
							temp3.setOrigin("¬e "+"Lines: "+line1+" , "+ line2);
							if(checkTreeErrors(temp3)) {
								mainproof.addTreeToActiveDeduction(mainproof.getActiveCurrentTreesSize()-1,temp3);					
								view.setSolution(mainproof.outputEverything());
								checkFinalSolution();
							}
						}
					}else {
						ruleView.setErrorLabel("You can not use the negation elimination rule for line " +line1+ " and line "+line2+". There is no negation in the second formula!");
						return;
					}
				}

		}			
		


		
		public void applyLEM(String formula) {
			if(checkErrors(formula)) {
				ParseTree temp1 = parser.parseIntoTree(formula);
				ParseTree temp2 = parser.parseIntoTree(formula);
				temp1.addNodeAsRoot("∨");
				temp2.addNodeAsRoot("¬");
				temp1.append(temp2);
				temp1.setOrigin("LEM "+"With: "+ formula);
				if(checkTreeErrors(temp1)) {
					mainproof.addTreeToActiveDeduction(mainproof.getActiveCurrentTreesSize()-1,temp1);		
					view.setSolution(mainproof.outputEverything());
					checkFinalSolution();
				}
			}
		}


		
		public void applyMT(String line1, String line2) {
			int input1 = Integer.parseInt(line1);
			int input2 = Integer.parseInt(line2);
			if(checkErrors(input1,input2)) {
				ParseTree temp1 = mainproof.getTree(input1).getTreeCopy();
				ParseTree temp2 = mainproof.getTree(input2).getTreeCopy();
					if(temp1.getRoot().getData().equals("→") && temp2.getRoot().getData().equals("¬")) {
							ParseTree temp1RightSide = temp1.getRightSubtree();
							if(temp1RightSide.compare(temp2.getRoot().getChildren().get(0))==false){
								ruleView.setErrorLabel("You can not use the modus tollens rule for line " +line1+ " and line "+line2);
								return;
							}else{				
								ParseTree temp3 = temp1.getLeftSubtree();
								temp3.addNodeAsRoot("¬");
								temp3.setOrigin("MT "+"Lines: "+line1+" , "+ line2);
								if(checkTreeErrors(temp3)) {
									mainproof.addTreeToActiveDeduction(mainproof.getActiveCurrentTreesSize()-1,temp3);						
									view.setSolution(mainproof.outputEverything());
									checkFinalSolution();	
								}
							}
					}else {
						ruleView.setErrorLabel("You can not use the modus tollens rule for line " +line1+ " and line "+line2);
						return;
					}
				}
			}


		
		public void applyContradictionElimination(String line1, String formula) {
			int input1 = Integer.parseInt(line1);
			if(checkErrors(input1,formula)) {
				ParseTree temp2 = parser.parseIntoTree(formula);
				if(mainproof.getTree(input1).getRoot().getData().equals("⊥")) {
					temp2.setOrigin("⊥e "+"Line: "+line1+" With: "+ formula);
					if(checkTreeErrors(temp2)) {
						mainproof.addTreeToActiveDeduction(mainproof.getActiveCurrentTreesSize()-1,temp2);			
						view.setSolution(mainproof.outputEverything());
						checkFinalSolution();	
					}
				}else {
					ruleView.setErrorLabel("You can not use the contradiction elimination rule for line "+line1 + " There is no contradiction!");
					return;
				}
			}	
		}


		
		public void applyImplicationIntroduction(String formula) {
			if(checkErrors(formula)) {
				ParseTree result = parser.parseIntoTree(formula);
				if(result.getRoot().getData().equals("→")) {
					if(checkTreeErrors(result)) {
						ParseTree newConclusion = result.getRightSubtree();
						ParseTree newPremise = result.getLeftSubtree();
						Proof tempDeduction = new Proof(newConclusion);	
						newPremise.setOrigin("assumption");
						newConclusion.setLocked(true);
						newConclusion.setOrigin("conclusion with →i"); 
						result.setLocked(true);
						result.setOrigin("→i for box above");
						mainproof.getActiveDeduction().peek().addDeductionIndex(mainproof.getActiveCurrentTreesSize()-1,tempDeduction);
						tempDeduction.addTreeToActiveDeduction(newPremise);	
						tempDeduction.addTreeToActiveDeduction(newConclusion);
						mainproof.addTreeToActiveDeduction(mainproof.getActiveCurrentTreesSize()-1, result);
						mainproof.getActiveDeduction().push(tempDeduction);
						view.setSolution(mainproof.outputEverything());
						checkFinalSolution();
					}
				}else {
					ruleView.setErrorLabel("You can not use the implication introduction rule for your formula! There is no implication in the formula!");
					return;
				}
			}
		}


		
		public void applyCopy(String line1) {
			int input1 = Integer.parseInt(line1);
			if(checkErrors(input1)) {
				ParseTree temp1 = mainproof.getTree(input1).getTreeCopy();
				temp1.setOrigin("COPY "+"Line: "+line1);
				if(checkTreeErrors(temp1)) {
					mainproof.addTreeToActiveDeduction(mainproof.getActiveCurrentTreesSize()-1,temp1);
					view.setSolution(mainproof.outputEverything());
					checkFinalSolution();
				}
			}		
		}
		
		
		public void applyNegationIntroduction(String formula) {
			if(checkErrors(formula)) {
				ParseTree result = parser.parseIntoTree(formula);
				if(result.getRoot().getData().equals("¬")) {
					if(checkTreeErrors(result)) {
						ParseTree newPremise = result.getLeftSubtree();
						ParseTree newConclusion = result.createContradiction();
						Proof tempDeduction = new Proof(newConclusion);	
						newPremise.setOrigin("assumption");
						newConclusion.setLocked(true);
						newConclusion.setOrigin("conclusion with ¬i");
						result.setLocked(true);
						result.setOrigin("¬i for box above");
						mainproof.addDeductionIndexToActive(mainproof.getActiveCurrentTreesSize()-1,tempDeduction);
						tempDeduction.addTree(newPremise);	
						tempDeduction.addTree(newConclusion);
						mainproof.addTreeToActiveDeduction(mainproof.getActiveCurrentTreesSize()-1, result);
						mainproof.getActiveDeduction().push(tempDeduction);			
						view.setSolution(mainproof.outputEverything());
						checkFinalSolution();
					}
				}else {
					ruleView.setErrorLabel("You can not use the negation introduction rule for your formula! There is no negation in the formula");
					return;
				}	
			}
		}
		
		
		public void applyPBC(String formula) {
			if(checkErrors(formula)) {
				ParseTree result = parser.parseIntoTree(formula);
				if(checkTreeErrors(result)) {
					ParseTree newPremise = result.getTreeCopy();
					newPremise.addNodeAsRoot("¬");
					ParseTree newConclusion = result.createContradiction();
					Proof tempDeduction = new Proof(newConclusion);	
					newPremise.setOrigin("assumption");
					newConclusion.setLocked(true);
					newConclusion.setOrigin("conclusion with PBC");
					result.setLocked(true);
					result.setOrigin("PBC for box above");
					mainproof.addDeductionIndexToActive(mainproof.getActiveCurrentTreesSize()-1,tempDeduction);
					tempDeduction.addTree(newPremise);	
					tempDeduction.addTree(newConclusion);
					mainproof.addTreeToActiveDeduction(mainproof.getActiveCurrentTreesSize()-1, result);
					mainproof.getActiveDeduction().push(tempDeduction);
					view.setSolution(mainproof.outputEverything());
					checkFinalSolution();
				}
			}
		}
			
		
		public void applyDisjunctionElimination(String line1,String formula) {
			int input1 = Integer.parseInt(line1);
			if(checkErrors(input1,formula)) {
				ParseTree result = parser.parseIntoTree(formula);
				if(checkTreeErrors(result)) {
					ParseTree newConclusion = result.getTreeCopy();
					ParseTree newConclusion2 = result.getTreeCopy();
					ParseTree disjunctionTree = mainproof.getTree(input1).getTreeCopy();
					if(disjunctionTree.getRoot().getData().equals("∨")) {
						ParseTree leftSide = disjunctionTree.getLeftSubtree();
						ParseTree rightSide = disjunctionTree.getRightSubtree();
						Proof subproof1 = new Proof(newConclusion);
						Proof subproof2 = new Proof(newConclusion2);	
						leftSide.setOrigin("assumption");
						rightSide.setOrigin("assumption");
						subproof2.setDisjunctionBox(true);
						rightSide.setLocked(true);
						newConclusion.setLocked(true);
						newConclusion.setOrigin("conclusion with ∨e"); 
						newConclusion2.setLocked(true);
						newConclusion2.setOrigin("conclusion with ∨e");
						result.setLocked(true);
						result.setOrigin("∨e for box above");
						mainproof.addDeductionIndexToActive(mainproof.getActiveCurrentTreesSize()-1,subproof1);
						mainproof.addDeductionIndexToActive(mainproof.getActiveCurrentTreesSize()-1,subproof2);
						subproof1.addTree(leftSide);	
						subproof1.addTree(newConclusion);
						subproof2.addTree(rightSide);	
						subproof2.addTree(newConclusion2);
						mainproof.addTreeToActiveDeduction(mainproof.getActiveCurrentTreesSize()-1, result);
						mainproof.getActiveDeduction().push(subproof2);
						mainproof.getActiveDeduction().push(subproof1);
						view.setSolution(mainproof.outputEverything());
						checkFinalSolution();
					}else {
						ruleView.setErrorLabel("You can not use the disjunction elimination rule for line "+line1 + "The formula does not contain a disjunction!");
						return;
					}
				}
			}
		}
		
		
		//helper-functions
		public boolean checkErrors(int input1, int input2) {
			if(checkFinalSolution()) {
				return false;
			}
			if(input1>mainproof.getCurrentCount()-1) {
				ruleView.setErrorLabel("There is no such index for the first line");
				return false;
			}
			if(input2>mainproof.getCurrentCount()-1) {
				ruleView.setErrorLabel("There is no such index for the second line");
				return false;
			}
			if(mainproof.getTree(input1) == null) {
				ruleView.setErrorLabel("The first index is locked! It still needs to be proven!");
				return false;
			}
			if(mainproof.getTree(input2) == null) {
				ruleView.setErrorLabel("The second index is locked! It still needs to be proven!");
				return false;
			}
			return true;
		}
		
		public boolean checkErrors(int input1) {
			if(checkFinalSolution()) {
				return false;
			}
			if(input1>mainproof.getCurrentCount()-1) {
				ruleView.setErrorLabel("There is no such index for the first line");
				return false;
			}
			if(mainproof.getTree(input1) == null) {
				ruleView.setErrorLabel("The first index is locked! It still needs to be proven!");
				return false;
			}
			return true;
		}
		
		
		public boolean checkErrors(int input1, String formula) {
			if(checkFinalSolution()) {
				return false;
			}
			if(input1>mainproof.getCurrentCount()-1) {
				ruleView.setErrorLabel("There is no such index for the first line");
				return false;
			}
			
			if(mainproof.getTree(input1) == null) {
				ruleView.setErrorLabel("The first index is locked! It still needs to be proven!");
				return false;
			}
			if(parser.checkSyntax(formula) == false) {
				ruleView.setErrorLabel("The syntax of given formula is incorrect!");
				return false;
			}
			return true;
		}
		public boolean checkErrors(String formula) {
			if(checkFinalSolution()) {
				return false;
			}
			if(parser.checkSyntax(formula) == false) {
				ruleView.setErrorLabel("The syntax of given formula is incorrect!");
				return false;
			}
			return true;
		}
		
		public boolean checkTreeErrors(ParseTree newTree) {
			if(!mainproof.checkDuplicateTree(newTree)) {
				return true;
			}else {
				ruleView.setErrorLabel("There is already an equivalent formula inside the active proof!");
				return false;
			}
		}
				
		public boolean checkFinalSolution() {
			if(mainproof.getSolved()) {
				view.proved();
				return true;
			}
			return false;
		}

		//functionality for the close proof button
		public void closeProof() {
			if(mainproof.getActiveDeduction().size()>1) {
				if(mainproof.getActiveDeduction().get(mainproof.getActiveDeduction().size()-2).getDisjunctionBox()==true) {
					if(mainproof.getActiveDeduction().size()>2) {
						mainproof.getActiveDeduction().pop();
						mainproof.getActiveDeduction().pop();
						mainproof.getActiveDeduction().peek().getCurrentTrees().remove(mainproof.getActiveDeduction().peek().getCurrentTrees().size()-2);
						mainproof.getActiveDeduction().peek().getCurrentTrees().remove(mainproof.getActiveDeduction().peek().getCurrentTrees().size()-2);
						mainproof.getActiveDeduction().peek().getCurrentTrees().remove(mainproof.getActiveDeduction().peek().getCurrentTrees().size()-2);
					}else {	
						mainproof.getActiveDeduction().pop();
						mainproof.getActiveDeduction().peek().getCurrentTrees().remove(mainproof.getActiveDeduction().peek().getCurrentTrees().size()-2);
						mainproof.getActiveDeduction().peek().getCurrentTrees().remove(mainproof.getActiveDeduction().peek().getCurrentTrees().size()-2);
						mainproof.getActiveDeduction().peek().getCurrentTrees().remove(mainproof.getActiveDeduction().peek().getCurrentTrees().size()-2);
					}
				}else {
					mainproof.getActiveDeduction().pop();
					mainproof.getActiveDeduction().peek().getCurrentTrees().remove(mainproof.getActiveDeduction().peek().getCurrentTrees().size()-2);
					mainproof.getActiveDeduction().peek().getCurrentTrees().remove(mainproof.getActiveDeduction().peek().getCurrentTrees().size()-2);
				}
				view.setSolution(mainproof.outputEverything());
			}else {
				ruleView.setErrorLabel("There is no subproof to close!");

			}
		}
		
}
