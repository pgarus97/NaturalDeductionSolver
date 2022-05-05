package controller;

import model.PredicateParseTree;
import model.PredicateParseTree.PredNode;

import java.util.ArrayList;

import model.ParseTree;
import model.PredicateParser;
import model.Proof;
import view.MainView;
import view.RuleView;

//<summary>
//The controller that handles the rule applications for first order logic
//</summary>

public class PredicateRuleController extends RuleController implements PredicateRuleHandler{

	//links all the data

	public void link(MainView view, RuleView ruleView, Proof predicateNaturalDeductionModel, PredicateParser parser) {
        this.view = view;
        this.ruleView = ruleView;
        this.mainproof = predicateNaturalDeductionModel;
        this.parser = parser;
        ruleView.setPredicateRuleHandler(this);
    }
	
	// forall elimination
	
	@Override
	public void applyAllQuantifierElimination(String line1, String substitution) {
		int input1 = Integer.parseInt(line1);
		if(checkErrors(input1)) {
			if(!parser.checkSyntax("P("+substitution+")")) { //checkSyntax of P(term) and 
				ruleView.setErrorLabel("The given substitution is not a term!");
				return;
			}
			PredicateParseTree temp1 = (PredicateParseTree) mainproof.getTree(input1).getTreeCopy();
			if(temp1.getRoot().getData().equals("∀")) { 
				ArrayList<String> termVariables = ((PredicateParser) parser).getTermVariables(substitution);
				boolean freeVariableCheck = true;
		    	int subBind = ((PredNode) temp1.getRoot()).getBindingNumber();
				String subVariable = ((PredNode) temp1.getRoot()).getBindingValue();
		    	((PredicateParseTree) temp1).removeRootQuantifier();
				//check if all variables in term are free in formula
				for(String e : termVariables) {
					if(!temp1.checkVariable((PredNode)temp1.getRoot(), e, subBind)) {
						freeVariableCheck = false;
					}
				}
				if(freeVariableCheck){ 
					PredicateParseTree subTerm = (PredicateParseTree) parser.parseIntoTree("P("+substitution+")").getLeftSubtree();
					temp1.substituteTermAll(subTerm,subBind); //substitute whole terms and not only variable
					temp1.setOrigin("∀"+subVariable+" e"+" Line: "+line1+" With: ["+subVariable+"/"+substitution+"]");
					if(checkTreeErrors(temp1)) {
						mainproof.addTreeToActiveDeduction(mainproof.getActiveCurrentTreesSize()-1,temp1);				
						view.setSolution(mainproof.outputEverything());
						checkFinalSolution();
					}
				}else {
						ruleView.setErrorLabel("The substitution "+substitution+" is not free in the formula!");
						return;
				}
			}else {
				ruleView.setErrorLabel("There is no universal quantifier in the selected formula in Line "+ line1);
				return;
			}
		}
	}

	// exists introduction
	@Override
	public void applyExistQuantifierIntroduction(String line1, String term, String substitution) {
		int input1 = Integer.parseInt(line1);
		if(checkErrors(input1)) {
			if(!parser.checkSyntax("P("+term+")")) { //checkSyntax of P(term) 
				ruleView.setErrorLabel("The given term's syntax is wrong!");
				return;
			}else if(!substitution.matches("(x|y|z)(\\d*)")){
				ruleView.setErrorLabel("The given substitution is not a variable!");
				return;
			}
			PredicateParseTree temp1 = (PredicateParseTree) mainproof.getTree(input1).getTreeCopy();
			PredicateParseTree termTree = (PredicateParseTree) parser.parseIntoTree("P("+term+")").getLeftSubtree();

			
			if(temp1.checkTerm(temp1.getRoot(), termTree.getRoot())){ 
				//check if substitution is free in formula; not needed
				if(temp1.checkVariable((PredNode) temp1.getRoot(),substitution,0)) {
					temp1.addExist(substitution, termTree);
					temp1.setOrigin("∃"+substitution+" i"+" Line: "+line1 + " With: ["+substitution+"/"+term+"]");
					if(checkTreeErrors(temp1)) {
						mainproof.addTreeToActiveDeduction(mainproof.getActiveCurrentTreesSize()-1,temp1);
						view.setSolution(mainproof.outputEverything());
						checkFinalSolution();
					}
				}else {
					ruleView.setErrorLabel("You cannot use this rule because the term is not free for "+substitution+" in the formula!");
					return;
				}
			}else{
				ruleView.setErrorLabel("You cannot use this rule because the targeted term is not in the formula or contains bound variables!");
				return;
			}
		}
	}
	
	//forall introduction
	@Override
	public void applyAllQuantifierIntroduction(String formula) {
		if(checkErrors(formula)) {
			ParseTree result = parser.parseIntoTree(formula);
			if(checkTreeErrors(result)) {
				if(result.getRoot().getData().matches("∀")) {
					String freshVariable = mainproof.getFreshVariable();
					PredicateParseTree newConclusion = (PredicateParseTree) result.getTreeCopy();
					newConclusion.substituteVariableAll(freshVariable);
					newConclusion.setOrigin("conclusion for ∀i with fresh variable: "+freshVariable);
					newConclusion.setLocked(true);
					result.setOrigin("∀i with subproof above");
					result.setLocked(true);
					Proof tempDeduction1 = new Proof(newConclusion);
					tempDeduction1.addTree(newConclusion);
					mainproof.addDeductionIndexToActive(mainproof.getActiveCurrentTreesSize()-1,tempDeduction1);
					mainproof.addTreeToActiveDeduction(mainproof.getActiveCurrentTreesSize()-1, result);
					mainproof.getActiveDeduction().push(tempDeduction1);
					view.setSolution(mainproof.outputEverything());
					checkFinalSolution();
				}else {
					ruleView.setErrorLabel("Your given formula does not have a universal quantifier!");
					return;
				}
			}			
		}
	}

	//exist elimination
	@Override
	public void applyExistQuantifierElimination(String line1, String formula) {
		int input1 = Integer.parseInt(line1);
		if(checkErrors(input1,formula)) {
			PredicateParseTree newPremise = (PredicateParseTree) mainproof.getTree(input1).getTreeCopy();
			if(newPremise.getRoot().getData().equals("∃")) {
				PredicateParseTree result = (PredicateParseTree) parser.parseIntoTree(formula);
				if(checkTreeErrors(result)) {
					PredicateParseTree newConclusion = (PredicateParseTree) result.getTreeCopy();
					String freshVariable = mainproof.getFreshVariable();
					newPremise.substituteVariableAll(freshVariable);
					newPremise.setOrigin("assumption with fresh variable "+freshVariable);
					newConclusion.setOrigin("conclusion for ∃e");
					newConclusion.setLocked(true);
					result.setOrigin("∃e subproof above");
					result.setLocked(true);
					Proof tempDeduction1 = new Proof(newConclusion);
					tempDeduction1.addTree(newPremise);
					tempDeduction1.addTree(newConclusion);
					mainproof.addDeductionIndexToActive(mainproof.getActiveCurrentTreesSize()-1,tempDeduction1);
					mainproof.addTreeToActiveDeduction(mainproof.getActiveCurrentTreesSize()-1, result);
					mainproof.getActiveDeduction().push(tempDeduction1);
					view.setSolution(mainproof.outputEverything());
					checkFinalSolution();
				}
			}else {
				ruleView.setErrorLabel("You can not use the existential quantifier elimination rule for line "+line1 +".It has no existential Quantifier!");
				return;
			}	
		}	
	}
		
	//auto-solver functionality
	
	//simplifies the conclusion and starts main algorithm
	public void autoSolve() {
		System.out.println("start autoSolve");
		ruleView.setAutoSolver(true);
		//checks for the length of conclusion tree times if there are implications or a negation in the conclusion 
		//for early assumption rules;
		String lastVariable = null;
		boolean stagnate = false;
		while(!stagnate) {
			int currentCount = mainproof.getActiveDeduction().size();
			String conclusionRoot = mainproof.getActiveConclusionTree().getRoot().getData();
			if(conclusionRoot.equals("∀") && !mainproof.getSolved()) {
				System.out.println("auto solve check for conclusion all Quantifier");
				lastVariable = mainproof.getFreshVariable();
				applyAllQuantifierIntroduction(mainproof.getActiveConclusionTree().braceTreeIntoString());
			}else if(conclusionRoot.equals("→") && !mainproof.getSolved()) {
				System.out.println("auto solve check for conclusion implication");
				applyImplicationIntroduction(mainproof.getActiveConclusionTree().braceTreeIntoString());
			}else if(conclusionRoot.equals("¬") && !mainproof.getSolved()) {
				System.out.println("auto solve check for conclusion negation");
				applyNegationIntroduction(mainproof.getActiveConclusionTree().braceTreeIntoString());
			}
			if(mainproof.getActiveDeduction().size() == currentCount) {
				stagnate = true;
			}
		}	
		//apply all eliminations till there are no more "new" trees
		applyEliminations(lastVariable);
		//start of introduction algorithm
		if((mainproof.getActiveDeduction().size()>=1 )&& !mainproof.getSolved()) {
			ParseTree activeConclusion = mainproof.getActiveConclusionTree();
			recursiveSolve(activeConclusion,0);
		}
		ruleView.setAutoSolver(false);
		return;
	}
		
	//main algorithm
	public int recursiveSolve(ParseTree activeConclusion,int counter) {
		//check all premises and applied rules
		for(int i = 0;i<mainproof.getCurrentCount();i++) { 
			//if a fitting tree to the current check is found within the proof
			if(activeConclusion.compare(mainproof.getTree(i).getRoot())) {
				applyCopy(Integer.toString(i));
				return i;
			}
		}
		String rootData = activeConclusion.getRoot().getData();
		switch(rootData) {
		case "∧": 
			//conjunction case
			ParseTree leftSide = activeConclusion.getLeftSubtree();
			ParseTree rightSide =activeConclusion.getRightSubtree();
			applyConjunctionIntroduction(Integer.toString(recursiveSolve(leftSide,0)),Integer.toString(recursiveSolve(rightSide,0)));
			return mainproof.getCurrentCount()-1;
			
		case "¬":
			//double negation case
			if(activeConclusion.getRoot().getChildren().get(0).getData().equals("¬")) {
				//search formula without double negation
				ParseTree temp = activeConclusion.getLeftSubtree().getLeftSubtree();
				applyDoubleNegationIntroduction(Integer.toString(recursiveSolve(temp,0)));
				return mainproof.getCurrentCount()-1;
			}
		
		
		//single literal case
		default:
			if(!mainproof.getSolved()) {
				for(int i=counter;i<mainproof.getCurrentCount();i++) {
					ParseTree temp = (ParseTree) mainproof.getTree(i);
					String searchedValue = activeConclusion.getRoot().getData();
					if(temp.contains(temp.getRoot(),searchedValue)) { 
							String rootValue = temp.getRoot().getData();
							switch(rootValue) {
							case "→":
								if(temp.contains(temp.getRoot().getChildren().get(0),searchedValue)) {
									ParseTree rightTree = temp.getRightSubtree();
									rightTree.addNodeAsRoot("¬");
									applyMT(Integer.toString(recursiveSolve(rightTree,i+1)),Integer.toString(i));
								}else if (temp.contains(temp.getRoot().getChildren().get(1),searchedValue)) {
									ParseTree leftTree = temp.getLeftSubtree();
									applyImplicationElimination(Integer.toString(recursiveSolve(leftTree,i+1)),Integer.toString(i));
								}
								applyEliminations(null);

							default: 
								applyCopy(Integer.toString(i));

						}
					}
				}
			}
		}
			return 0;
		}
	
		//simplifies the premises
		public void applyEliminations(String lastVariable) {
			int newTreeCount = mainproof.getCurrentCount();
			boolean changed = true;
			while(changed && !mainproof.getSolved()) {
				int treeCount = mainproof.getCurrentCount();
				//checks all lines with one line input; only "new" ones
				for(int i=(newTreeCount-treeCount);i<treeCount;i++) {
					//need permutations of all lines for double line rules
					if(lastVariable != null) {
						applyAllQuantifierElimination(Integer.toString(i), lastVariable);
					}
					applyConjunctionElimination(Integer.toString(i), 1);
					applyConjunctionElimination(Integer.toString(i), 2);
					applyDoubleNegationElimination(Integer.toString(i));
				}
				for(int i=0;i<treeCount-1;i++) {
					for(int j=i+1;j<treeCount;j++) {
						applyImplicationElimination(Integer.toString(i), Integer.toString(j));
						applyMT(Integer.toString(i), Integer.toString(j));
						applyNegationElimination(Integer.toString(i), Integer.toString(j));
					}
				}
				for(int i=treeCount-1;i>0;i--) {
					for(int j=i-1;j>=0;j--) {
						applyImplicationElimination(Integer.toString(i), Integer.toString(j));
						applyMT(Integer.toString(i), Integer.toString(j));
						applyNegationElimination(Integer.toString(i), Integer.toString(j));
					}
				}
				newTreeCount = mainproof.getCurrentCount();
				changed = !(treeCount==newTreeCount);
			}
		}
}






