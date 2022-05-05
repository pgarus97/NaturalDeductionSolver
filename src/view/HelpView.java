package view;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;

//<summary>
//help window in textual form on pressing the help button
//</summary>

public class HelpView extends BorderPane{

	 private Label help = new Label("Welcome to the Help-Window! \n"
	 		+ "Here you will find general information on the usage of this software. \n"
	 		+ "\n"
	 		+ "We define our sets of allowed characters in the following: \n"
	 		+ "\n"
	 		+ "Characters for Propositions: A,B,C,D,E,F,G,H,I,J,K \n"
	 		+ "Characters for Predicates: L,M,N,O,P,Q,R,S,T,U,V,W \n"
	 		+ "Characters for variables: x,y,z with corresponding integers as indicies \n"
	 		+ "Characters for functions: f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w \n"
	 		+ "Characters for operators (and shortcuts): \n"
	 		+ "¬ (neg,not) \n"
	 		+ "∧(land) \n"
	 		+ "∨ (lor) \n"
	 		+ "→ (implies, to, rightarrow) \n"
	 		+ " (forall) \n"
	 		+ "∃ (exists) \n"
	 		+ "\n"
	 		+ "You can also use the buttons labeled with operators to copy them into the selected textfield. \n"
	 		+ "\n"
	 		+ "To switch between first-order and propositional logic, press the buttons on top of the Natural Deduction Solver Window. \n"
	 		+ "You can write formulas of the corresponding logic into the two given textfields labeled with 'Premises' and 'Conclusion'. \n"
	 		+ "If you want to have multiple premises inside the proof, please split you inputted formulas with a semicolon ';' \n"
	 		+ "Formulas have to be braced accordingly to ones intention, else the software braces them itself in regards of the precendence order. \n"
	 		+ "Contents of predicates and functions also require parentheses, e.g. P(x) and Q(f(y)). \n"
	 		+ "\n"
	 		+ "A syntactically correct formula could look like this : P(x,y) ∧ ∀y Q(y). \n"
	 		+ "\n"
	 		+ "If you want to start the proof, press the 'Solve' Button in the Natural Deduction Solver Window. \n"
	 		+ "You can now use the different inference rules in the Inference Rule Window. \n"
	 		+ "Just click or hover over a rule to see its definition and then write down the necessary inputs for the rule. \n"
	 		+ "The inputs consist either of line numbers inside the proof or new formulas that have to be inputted. \n"
	 		+ "You can see which corresponding formula you have to input for a specific rule above the input textfield. \n"
	 		+ "\n"
	 		+ "Upon pressing the 'Apply' button, the inference rule will be applied with given inputs and the proof should get updated. \n"
	 		+ "\n"
	 		+ "The 'Auto-Solve' button activates the auto-solver, an algorithm that tries to create a constructive proof itself. \n"
	 		+ "It only works for formulas without disjunctions and the elimination rule for contradictions. \n"
	 		+ "If you want to use it with disjunctions, please write equivalent formulas with implications and negations. \n"
	 		+ "\n"
	 		+ "The 'Close Subproof' button allows you close a subproof that you cannot or do not want to complete. \n"
	 		+ "I reverts the proof to the previous state before applying the rule introducing the subproof.");
	 
	 private ScrollPane solutionScroll = new ScrollPane(help);

	 
	 public HelpView() {
		 solutionScroll.setMaxHeight(200);
		 setCenter(solutionScroll);
	 }
}
