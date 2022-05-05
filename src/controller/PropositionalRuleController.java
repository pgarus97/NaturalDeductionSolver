package controller;
import model.PropositionParser;
import model.ParseTree;
import model.Proof;
import view.MainView;
import view.RuleView;

//<summary>
// Inference Rule applying class for propositional logic
//</summary>

public class PropositionalRuleController extends RuleController implements RuleHandler{

	//links all the data

	public void link(MainView view, RuleView ruleView, Proof propositionalNaturalDeductionModel, PropositionParser parser) {
        this.view = view;
        this.ruleView = ruleView;
        this.mainproof = propositionalNaturalDeductionModel;
        this.parser = parser;
        ruleView.setPropositionalRuleHandler(this);
    }

		
	//auto-solver functionality
	
	//simplifies the conclusion and starts main algorithm
	public void autoSolve() {
		System.out.println("start autoSolve");
		ruleView.setAutoSolver(true);
		//checks for the length of conclusion tree times if there are implications or a negation in the conclusion 
		//for early assumption rules;
		boolean stagnate = false;
		while(!stagnate) {
			System.out.println("stagnatewhile");
			int currentCount = mainproof.getActiveDeduction().size();
			String conclusionRoot = mainproof.getActiveConclusionTree().getRoot().getData();
			if(conclusionRoot.equals("→") && !mainproof.getSolved()) {
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
		applyEliminations();
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
			System.out.println("first while" + i);
			//if a fitting tree to the current check is found within the proof
			if(activeConclusion.compare(mainproof.getTree(i).getRoot())) {
				applyCopy(Integer.toString(i));
				return i;
			}
		}
		String rootData = activeConclusion.getRoot().getData();
		System.out.println("RootData" + rootData);
		System.out.println("BraceInto " +activeConclusion.braceTreeIntoString());
		switch(rootData) {
		case "∧": 
			//conjunction case
			System.out.println("autoConjCase");
			ParseTree leftSide = activeConclusion.getLeftSubtree();
			ParseTree rightSide = activeConclusion.getRightSubtree();
			applyConjunctionIntroduction(Integer.toString(recursiveSolve(leftSide,0)),Integer.toString(recursiveSolve(rightSide,0)));
			return mainproof.getCurrentCount()-1;
			
		case "¬":
			//double negation case
			if(activeConclusion.getRoot().getChildren().get(0).getData().equals("¬")) {
				System.out.println("autodoubleNegCase");

				//search formula without double negation
				ParseTree temp = activeConclusion.getLeftSubtree().getLeftSubtree();
				applyDoubleNegationIntroduction(Integer.toString(recursiveSolve(temp,0)));
				return mainproof.getCurrentCount()-1;
			}


			//single literal case
		default:
			if(!mainproof.getSolved()) {
				for(int i=counter;i<mainproof.getCurrentCount();i++) {
					System.out.println("loop"+i);
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
								applyEliminations();

							default: 
								System.out.println("default");
								applyCopy(Integer.toString(i));

						}
					}
				}
			}
		}
			return 0;
		}
	
		//simplifies the premises
		public void applyEliminations() {
			int newTreeCount = mainproof.getCurrentCount();
			boolean changed = true;
			while(changed && !mainproof.getSolved()) {
				System.out.println("eliminate while" );
				int treeCount = mainproof.getCurrentCount();
				//checks all lines with one line input; only "new" ones
				for(int i=(newTreeCount-treeCount);i<treeCount;i++) {
					System.out.println("eliminate while2" );
					applyConjunctionElimination(Integer.toString(i), 1);
					applyConjunctionElimination(Integer.toString(i), 2);
					applyDoubleNegationElimination(Integer.toString(i));
				}
				for(int i=0;i<treeCount-1;i++) {
					for(int j=i+1;j<treeCount;j++) {
						System.out.println("eliminate while3" );
						applyImplicationElimination(Integer.toString(i), Integer.toString(j));
						applyMT(Integer.toString(i), Integer.toString(j));
						applyNegationElimination(Integer.toString(i), Integer.toString(j));
					}
				}
				for(int i=treeCount-1;i>0;i--) {
					for(int j=i-1;j>=0;j--) {
						System.out.println("eliminate while4"+i +j );
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
