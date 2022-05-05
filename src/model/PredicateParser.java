package model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.PredicateParseTree.PredNode;

//<summary>
//class that models the parser for predicate logic
//</summary>

public class PredicateParser extends PropositionParser{

	//allowed inputs
	
	String predicates = "L|M|N|O|P|Q|R|S|T|U|V|W";
	String operators = "∧|∨|→";
	String variables = "x|y|z";
	String quantifiers = "∃|∀";
	String functions = "f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u|v|w";
	String negation = "¬";
	String parenthesesOpen = "[(]";
	String parenthesesClose = "[)]";
	String comma =",";
	String indices ="[0-9]";
	

	
	//Algorithm used to determine where to set braces for braceFormula
	public String braceOperator(String input, int initialPosition, String operator) {
		String before ="";
		String after="";
		int position = initialPosition;
		int parenthesesCount = 0;
		if(operator == "¬" || operator == "quantifier") {
			before = input.substring(0,position);
		}else {
			while(true){
				if(checkCharacter(input,position,"parenthesesClose","last")) {
					parenthesesCount++;
					position--;
					continue;
				}
				if(checkCharacter(input,position,"parenthesesOpen","last")) {
					if(parenthesesCount==1) {
						if(checkCharacter(input,position-1,"predicate","last")) {
							before = input.substring(0,position-2);
							break;
						} 
						before = input.substring(0,position-1);
						break;
					}else {
						parenthesesCount--;
						position--;
						continue;
					}
				}
				
				//should never be the case
				if(checkCharacter(input,position,"start/end","last")) {
					break;
				}
					position--;		
			}
		}
		position = initialPosition;
		parenthesesCount = 0;
		
		while(true){
			if(checkCharacter(input,position,"parenthesesOpen","next")) {
				parenthesesCount++;
				position++;
				continue;
			}
			if(checkCharacter(input,position,"parenthesesClose","next")) {
				if(parenthesesCount==1) {
					after = input.substring(position+2,input.length());
					break;
				}else {
					parenthesesCount--;
					position++;
					continue;
				}
			}
			if(checkCharacter(input,position,"start/end","next")) {
				break;
			}
				position++;		
		}
		return before+"("+input.substring(before.length(),position+2)+")"+after;
	}
	
	
	//Algorithm that goes over the given formula "input" and correctly braces all used operators in order of their precedence
	public String braceFormula(String input) {
		String solution = input;
		for(int i = 0;i < solution.length();i++) {
			String current = solution.substring(i, i+1);
			if(current.equals("∀") || current.equals("∃")){
				solution = braceOperator(solution,i,"quantifier");
				i++;
			}
		}
		for(int i = 0;i < solution.length();i++) {
			String current = solution.substring(i, i+1);
			if(current.equals("¬")){
				solution = braceOperator(solution,i,"¬");
				i++;
			}
		}
		for(int i = 0;i < solution.length();i++) {
			String current = solution.substring(i, i+1);
			if(current.equals("∧")){
				solution = braceOperator(solution,i,"∧");
				i++;
			}
		}
		for(int i = 0;i < solution.length();i++) {
			String current = solution.substring(i, i+1);
			if(current.equals("∨")){
				solution = braceOperator(solution,i,"∨");
				i++;
			}
		}
		for(int i = 0;i < solution.length();i++) {
			String current = solution.substring(i, i+1);
			if(current.equals("→")){
				solution = braceOperator(solution,i,"→");
				i++;
			}
		}
		return solution;
	}

	//method that processes the given formula input into a Tree data structure for predicate logic
    public ParseTree parseIntoTree(String input) {
    	input = braceFormula(input);
    	int brujinCount = 1;
    	int predicateCount = 0;
    	int functionCount = 0;
    	Stack<PredicateParseTree> currentTrees = new Stack<PredicateParseTree>();
    	int index = 0;
    	while(index < input.length()) {
    		//save the current character as a String
    		String current = input.substring(index, index+1);
    		switch(checkVocab(current)) {
    		
    		//End|Error Case
    		case 0:
    			return null;
    			
    		//Case 1: Predicate
    		case 1: 
    			if(currentTrees.size()>1){
    				if(currentTrees.peek().getRoot().getData() == null) {
    					currentTrees.pop();
    					currentTrees.push(new PredicateParseTree(current,0,""));
    				}else{
    					currentTrees.peek().addNodeToLastNode(current,0,"");
    				}
    			}else {
    				currentTrees.push(new PredicateParseTree(current,0,""));
    			}
    			predicateCount++;
    			break;
    			
    			
			//Operator Case
    		case 2:
    			currentTrees.peek().addNodeAsRoot(current,0,"");
    			break;
    			
    		//Variable Case
    		case 3: 
    			if(predicateCount >0) {
    				//while there are indices after the variable
    				while(checkVocab(input.substring(index+1,index+2)) == 11) {
    					current += input.substring(index+1,index+2);
    					index++;
    				}
	    			currentTrees.peek().addNodeToLastNodeNoOverwrite(current, 0,""); 
    			}
    			break;
    			
    			
    		//Quantifier Case
    		case 4: 
    			String variable = input.substring(index+1,index+2);
    			index++; //skips the variable
    			while(checkVocab(input.substring(index+1,index+2)) == 11) {
					variable += input.substring(index+1,index+2);
					index++;
				}
	    			if(currentTrees.peek().getRoot().getData() == null) {
						currentTrees.pop();
    					currentTrees.push( new PredicateParseTree(current, brujinCount,variable));
					}else{
						currentTrees.peek().addNodeToLastNode(current, brujinCount, variable);		
					}	
	    			brujinCount++;
    			break;
    			
    		//Function Case
    		case 5: 
    				currentTrees.peek().addNodeToLastNode(current, 0, "");
    				functionCount++;
    				break;
    				
    		//Negation Case
    		case 6:
	    			if(currentTrees.peek().getRoot().getData() == null) {
						currentTrees.pop();
    					currentTrees.push(new PredicateParseTree(current,0,""));
					}else{
						currentTrees.peek().addNodeToLastNode(current,0,"");
					}	
    			break;
    			
    		//ParenthesesOpen Case
    		case 7:
	    			if(predicateCount>0 && functionCount == 0) {
	    				predicateCount++;
	    			} else if(predicateCount>0 && functionCount > 0) {
	    				functionCount++;
	    			}else {
    				currentTrees.push(new PredicateParseTree(null,0,""));
	    			}
    			break;
    			
    		//ParenthesesClose Case
    		case 8:
    			if(functionCount == 0) {
					if(predicateCount > 2) {
						predicateCount--;
					} else if(predicateCount == 2) {
						predicateCount -= 2;
					} else if (predicateCount == 0) {
						//skips empty subtrees that get created from double brackets f.e. ((A))
		    			if(currentTrees.size()>1){
		    				if(currentTrees.get(currentTrees.size()-2).getRoot().getData()==null) {
			    				PredicateParseTree temp = currentTrees.pop();
			    				currentTrees.get(currentTrees.size()-1).appendOverwrite(temp);
			    			}else {
			    				PredicateParseTree temp = currentTrees.pop();
				    			currentTrees.get(currentTrees.size()-1).append(temp);	
			    			}
		    			}
					}
				} else if(functionCount == 2){
					functionCount -= 2;
					currentTrees.peek().lastNodeToParent();
				} else if(functionCount > 2) {
					functionCount -= 2;
					currentTrees.peek().lastNodeToParent();
				}
    			break;	
    			
    		default: 
    		}
    		index++;
    	}
    	currentTrees.peek().bindVariables((PredNode) currentTrees.peek().getRoot());
    	currentTrees.peek().setBindingCount(brujinCount);
    	Set<Integer> bindNums = new HashSet<Integer>();
    	currentTrees.peek().getBindNumSet((PredNode) currentTrees.peek().getRoot(), bindNums);
    	currentTrees.peek().eliminateRedundantQuantifiers((PredNode) currentTrees.peek().getRoot(), bindNums);
    	return currentTrees.peek();
    	
    }
    
    //checks if current character matches one of predefined vocabularies for propositional logic
	//returns integers from 0-6 according to which type the character is. 0 is the error case.
	public int checkVocab(String current) {
			String[] vocabulary = {predicates,operators,variables,quantifiers,functions,negation,parenthesesOpen,parenthesesClose,comma,"_",indices};
			for(int i = 0; i < vocabulary.length; i++) {
				Pattern pattern = Pattern.compile(vocabulary[i]);
		    	Matcher matcher = pattern.matcher(current);
		    	if(matcher.matches()) {
		    		return i+1;
		    	}
			}
			//error case
	    	return 0; 
		}
    
	//Algorithm that checks if a given character at position i in the String input stands before or after (direction)
	//a operator, proposition, etc. (type). Returns true if last/next character of given character matches with type.
	public boolean checkCharacter(String input, int i, String type, String direction) {
		String check ="";
		//add special symbol to start and end in order to be able to determine if last/next character was first/last one
		input ="_"+input+"_";
			if(direction.equals("last")) {
				check = input.substring(i, i+1);
			}else if(direction.equals("next")){
				check = input.substring(i+2,i+3);
			}
			switch(type) {
			case "predicate":
				return checkVocab(check)==1;
				
			case "operator": 
				return checkVocab(check)==2;
				
			case "variables":
				return checkVocab(check)==3;
				
			case "quantifiers":
				return checkVocab(check)==4;
				
			case "functions":
				return checkVocab(check)==5;
				
			case "negation":
				return checkVocab(check)==6;
				
			case "parenthesesOpen":
				return checkVocab(check)==7;
				
			case "parenthesesClose":
				return checkVocab(check)==8;
				
			case "comma":
				return checkVocab(check)==9;
				
			case "start/end":
				return checkVocab(check)==10;
				
			case "index":
				return checkVocab(check)==11;

				
			default:
				return false;
			}
	}
    
    //Algorithm that goes through the given String "input" and determines if it is a correct formed formula 
	//within the rules of propositional logic grammar
	public boolean checkSyntax(String input) {
		int predicateCounter = 0;
		int functionCounter = 0;
		int parenthesesCheck = 0;
		for(int i = 0; i < input.length(); i++) {
			String current = input.substring(i, i+1);
			//check which character is the current one for a syntax check
			switch(checkVocab(current)) {
			
			//Error Case
			case 0:
				return false;
			
			//Case 1: Predicate
			case 1:
				//after a predicate it has to follow with a parenthesesOpen
				//and in front of a predicate there has to be the start,an operator, an opening parentheses or a negation
				//if there is a variable in front there also has to be a quantifier in front of that variable
				if(predicateCounter == 0 && 
				(checkCharacter(input, i, "parenthesesOpen", "next") 
				)&&(checkCharacter(input, i, "start/end", "last")
				||checkCharacter(input, i, "operator", "last")
				||checkCharacter(input, i, "parenthesesOpen", "last")
				|| checkCharacter(input, i, "index", "last")
				||checkCharacter(input, i, "negation", "last"))
				||(checkCharacter(input, i, "variables", "last")
				&& checkCharacter(input, i-1, "quantifiers", "last"))){ 
					predicateCounter++;

					continue;
				}else {
					return false;
					}
			
			//Case 2: Operator
			case 2: 
				//after an operator there has to be a predicate, a quantifier, a negation or a parentheses open next
				//and in front of an operator there has to be a closing parentheses
				if(predicateCounter == 0 && 
				(checkCharacter(input, i, "predicate", "next")
				|| checkCharacter(input, i, "quantifiers", "next")
				|| checkCharacter(input, i, "negation", "next")
				|| checkCharacter(input, i, "parenthesesOpen", "next"))
				&& checkCharacter(input, i, "parenthesesClose", "last")){
					continue;	
				}else {
					return false;
				}
				
			//Case 3: Variable
			case 3:

				//after a variable there has to be a comma or a closing parentheses or an index
				//and in front of a variable there has to be a comma or a parenthesesOpen
				//in case there is a quantifier in front of the variable, it has to follow with either another quantifier
				//or an opening parentheses or a predicate or a negation
				if(checkCharacter(input,i,"index","next") ||
				((checkCharacter(input, i, "comma", "next")
				|| checkCharacter(input, i, "parenthesesClose", "next"))
				&& (checkCharacter(input, i, "comma", "last") 
				|| checkCharacter(input, i, "parenthesesOpen", "last")))
				||(checkCharacter(input, i, "quantifiers", "last")
				&& (checkCharacter(input, i, "quantifiers", "next") 
				||checkCharacter(input, i, "parenthesesOpen", "next")
				||checkCharacter(input, i, "predicate", "next")
				|| checkCharacter(input, i, "negation", "next")))){

					continue;
				}else {
					System.out.println("ERROR variable else");
					return false;
				}
			
			//Case 4: Quantifier
			case 4:
				//after a quantifier there has to follow a variable
				//and in front of the quantifier there has to be the start, an operator, a variable, a negation, or an opening parentheses
				if(checkCharacter(input, i, "variables", "next")
				&& predicateCounter == 0 &&
				(checkCharacter(input, i , "start/end", "last")
				|| checkCharacter(input, i, "operator", "last")
				|| checkCharacter(input, i, "index", "last")
				|| checkCharacter(input, i, "variables", "last")
				|| checkCharacter(input, i, "negation", "last")
				|| checkCharacter(input, i, "parenthesesOpen", "last"))){
					continue;
				}else {
					System.out.println("ERROR quantifier else");
					return false;
				}
				
			//Case 5: Function
			case 5:
				//after a function has to follow an opening parentheses
				//and in front of the function there has to be either an opening parentheses or a comma
				if( predicateCounter>0 &&
				(checkCharacter(input, i, "parenthesesOpen", "next")
				&& (checkCharacter(input, i+1, "parenthesesClose", "next")
				||checkCharacter(input, i+1, "variables", "next")
				||checkCharacter(input, i+1, "functions", "next")
				))
				&&(checkCharacter(input, i, "parenthesesOpen", "last")
				|| checkCharacter(input, i, "comma", "last"))){
					if(functionCounter==0) {
						functionCounter++;
						continue;
					}else {
						continue;
					}
				}else {
					System.out.println("ERROR function else");
					return false;
				}
				
			//Case 6: Negation
			case 6:
				//after a negation has to follow a predicate, a quantifier, a negation or an opening parentheses
				//and in front of the negation has to be an operator, a negation, an opening parentheses or the start
				if( predicateCounter == 0 &&
				(checkCharacter(input, i, "predicate", "next")
				||checkCharacter(input, i, "quantifiers", "next")
				||checkCharacter(input, i, "negation", "next")
				||checkCharacter(input, i, "parenthesesOpen", "next")
				)&&( checkCharacter(input, i, "operator", "last")
				|| checkCharacter(input, i, "negation", "last")
				|| checkCharacter(input, i, "parenthesesOpen", "last")
				|| checkCharacter(input, i, "start/end", "last")
				|| checkCharacter(input, i, "index", "last")
				|| checkCharacter(input, i, "variables", "last"))){
					continue;
				}else {
					System.out.println("ERROR negation else");
					return false;
				}
				
			//Case 7: ParenthesesOpen
			case 7: 

				//after a parenthesesOpen is not allowed to follow an operator, a comma or the end 
				//there is not allowed to be a quantifier, a comma or a closing parentheses in front of the opening parentheses
				if(checkCharacter(input, i, "operator", "next") == false
				&& checkCharacter(input, i, "comma", "next") == false
				&& checkCharacter(input, i, "start/end", "next") == false
				&& checkCharacter(input, i, "quantifiers", "last") == false
				&& checkCharacter(input, i, "comma", "last") == false
				&& checkCharacter(input, i, "parenthesesClose", "last") == false){
					if(predicateCounter == 0) {
						parenthesesCheck++;

						continue;
					} else if(predicateCounter > 0 && functionCounter == 0){
						parenthesesCheck++;
						predicateCounter++;

						continue;
					} else if(predicateCounter > 0 && functionCounter >0) {

						parenthesesCheck++;
						functionCounter++;
						continue;
					}
				}else {
					System.out.println("ERROR opening parentheses else");
					return false;
				}
				

			//Case 8: ParenthesesClose
			case 8: 

				//after a closing parentheses has to follow an operator, a closing parentheses or the end
				//and in front of the closing parentheses has to be a variable, a closing or an opening parentheses
				if((checkCharacter(input, i, "operator", "next")
				||checkCharacter(input, i, "parenthesesClose", "next")
				||checkCharacter(input, i, "start/end", "next")
				||checkCharacter(input, i, "comma", "next")
				)&&( checkCharacter(input, i, "variables", "last")
				|| checkCharacter(input, i, "index", "last")
				|| checkCharacter(input, i, "parenthesesOpen", "last")
				|| checkCharacter(input, i, "parenthesesClose", "last"))){
					if(functionCounter == 0) {
						if(predicateCounter > 2) {
							predicateCounter--;
							parenthesesCheck--;
							continue;
						} else if(predicateCounter == 2) {
							predicateCounter -= 2;
							parenthesesCheck--;
							continue;
						} else if (predicateCounter == 0) {
							parenthesesCheck--;
							continue;
						}
					} else if(functionCounter == 2){
						functionCounter -= 2;
						parenthesesCheck--;
						continue;
					} else if(functionCounter > 2) {
						functionCounter--;
						parenthesesCheck--;
						continue;
					}	
					//more closing parentheses than opening ones
					if(parenthesesCheck < 0) {
						return false;
					}
					continue;
				}else {
					System.out.println("ERROR closing parentheses else");
					return false;
				}
				
			//Case 9: Comma
			case 9: 
				//after a comma has to follow a variable or a function
				//and in front of the comma has to be a variable or a closing parentheses
				if( predicateCounter > 0 &&
				(checkCharacter(input, i, "variables", "next")
				||checkCharacter(input, i, "functions", "next")
				)&&( checkCharacter(input, i, "variables", "last")
				|| checkCharacter(input, i, "index", "last")
				|| checkCharacter(input, i, "parenthesesClose", "last"))){
					continue;
				}else {
					System.out.println("ERROR comma else");
					return false;
				}		
			
			
			//Case 11: Index
			case 11:

				if(( predicateCounter >0 && (checkCharacter(input,i,"comma","next") 
				|| checkCharacter(input,i,"parenthesesClose","next")))
				||(checkCharacter(input,i,"index","next")
				||checkCharacter(input,i,"predicate","next")
				||checkCharacter(input,i,"negation","next")
				||checkCharacter(input,i,"quantifiers","next")
				||checkCharacter(input,i,"parenthesesOpen","next"))
				&&(checkCharacter(input,i,"variables","last")
				||checkCharacter(input,i,"index","last"))) {
					continue;
				}else {
						System.out.println("ERROR index else");
						return false;
				}	
			}	
		}
		
		//there has to be the same amount of opening and closing parentheses and no blank field
			if(parenthesesCheck==0 && input.length()>=1) {
			return true;	
			}else {
				System.out.println("ERROR, wrong parentheses count");
				return false;
			}
	}

	//public wrapper method for parsing the different premises and the conclusion into trees and saving them as a PredicateNaturalDeduction Class
	public void parse(ArrayList<String> premises, String conclusion, Proof model) {
		PredicateParseTree temp = (PredicateParseTree) parseIntoTree(conclusion);
		temp.setLocked(true);
		temp.setOrigin("conclusion");
		model.setConclusionTree(temp);
		if(!premises.isEmpty()) {
			for(int i = 0; i<premises.size();i++) {
				model.addTreeToActiveDeduction(parseIntoTree(premises.get(i)));
			}
		}	
		model.addTree(temp);
	}
		
	//returns all variables inside a term in form of an arraylist
	public ArrayList<String> getTermVariables(String term){
		ArrayList<String> result = new ArrayList<String>();
		int index = 0;
    	while(index < term.length()) {
    		String current = term.substring(index, index+1);
    		//if its a variable
    		if(checkVocab(current) == 3 && term.length()>1) {
    			//while there is an index afterwards
				while(index < term.length()-1 && checkVocab(term.substring(index+1,index+2)) == 11) {
					current += term.substring(index+1,index+2);
					index++;
				}
    		}
    		if(checkVocab(current) == 3) {
    			result.add(current);
    		}
    		index++;
    	}
		return result;
	}
	
	
}

