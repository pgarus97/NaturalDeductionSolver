package controller;

import java.util.ArrayList;

import model.Parser;
import model.PredicateParser;
import model.Proof;
import model.PropositionParser;
import view.MainView;
import view.RuleView;

//<summary>
//Main controller that links the main view with the necessary model elements
//</summary>

public class MainController implements ControllerHandler{

	private MainView view;	 //main window
	private RuleView ruleView;	//rule window
	private PropositionParser propositionalParser;	//parser for propositional logic
	private PredicateParser predicateParser;	//parser for first order logic
	private Parser parser;	//a generic parser element that we use for dynamic changing
	private Proof mainproof;	//main proof that we need to complete


	//gets called on pressing the solve button 
	
	@Override
	public void solve(String premise, String conclusion) {
    	premise = premise.replace(" ", "");
    	conclusion = conclusion.replace(" ", "");
    	view.setSolution("");
		view.setProved("");
		
		//check if a conclusion is entered
		if(conclusion.isEmpty()) {
			view.popError("Please insert a valid conclusion.");
			return;
		}
			mainproof.clear();
			
			ArrayList<String> premises = new ArrayList<String>();
			
			if(!premise.equals("")) {
				premises = parser.splitPremises(premise);	
				
				//check syntax of premises and conclusion
				for(int i = 0; i<premises.size();i++) {
					if(parser.checkSyntax(premises.get(i))!=true) {
						view.popError("The syntax of given premise is incorrect");
						return;
					}
				}
			}
			if(parser.checkSyntax(conclusion)!=true){
				view.popError("The syntax of given conclusion is incorrect");
				return;
			}
			parser.parse(premises, conclusion,mainproof);
			
			view.setSolution(mainproof.outputEverything());
		
		ruleView.setApplyButtons(false);
	}
	
	//links all the data
	
	public void link(PropositionParser propositionalParser,PredicateParser predicateParser, MainView view, RuleView ruleView, Proof mainproof) {
        this.propositionalParser = propositionalParser;
        this.predicateParser = predicateParser;
        this.parser = predicateParser;
        this.view = view;
        this.ruleView = ruleView;
        this.mainproof = mainproof;
        view.setHandler(this);
    }

	@Override
	public void addChar(String text) {
		view.addChar(text);
	}

	@Override
	public void changeState(boolean state) {
		view.setState(state);
		ruleView.setPredicateLogic(state);
		ruleView.checkLogic();
		view.clear();
		mainproof.clear();
		ruleView.setApplyButtons(true);
		if(state) {
    		parser = predicateParser;
    	}else {
    		parser = propositionalParser;
    	}
	}


}
