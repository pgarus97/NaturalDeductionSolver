package model;

import java.util.ArrayList;
import java.util.Stack;
import java.util.regex.*;

//<summary>
//class that models the parser for propositional logic
//</summary>

public class PropositionParser extends Parser{
	
	//predefined allowed vocabulary
	String propositionals = "A|B|C|D|E|F|G|I|J|K";
	String operators = "∧|∨|→";
	String negation = "¬";
	String parenthesesOpen = "[(]";
	String parenthesesClose = "[)]";
	
	
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
			case "proposition":
				return checkVocab(check)==1;
				
			case "operator": 
				return checkVocab(check)==2;
				
			case "negation":
				return checkVocab(check)==3;
				
			case "parenthesesOpen":
				return checkVocab(check)==4;
				
			case "parenthesesClose":
				return checkVocab(check)==5;
				
			case "start/end":
				return checkVocab(check)==6;

				
			default:
				return false;
			}
	}
	
	//Algorithm used to determine where to set braces for braceFormula
	public String braceOperator(String input, int initialPosition, String operator) {
		String before ="";
		String after="";
		int position = initialPosition;
		int parenthesesCount = 0;
		if(operator == "¬") {
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
						before = input.substring(0,position-1);
						break;
					}else {
						parenthesesCount--;
						position--;
						continue;
					}
				}
				if(checkCharacter(input,position,"proposition","last") && parenthesesCount==0) {
					before = input.substring(0,position-1);
					break;
				}
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
			if(checkCharacter(input,position,"proposition","next") && parenthesesCount==0) {
				after = input.substring(position+2,input.length());
				break;
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
	

	//Algorithm that goes through the given String "input" and determines if it is a correct formed formula 
	//within the rules of propositional logic grammar
	public boolean checkSyntax(String input) {
		int parenthesesCheck = 0;
		for(int i = 0; i < input.length(); i++) {
			String current = input.substring(i, i+1);
			//check which character is the current one for a syntax check
			switch(checkVocab(current)) {
			
			//Error Case
			case 0:
				return false;
			
			//Case 1: Proposition
			case 1:
				//after a proposition it has to follow with an operator or an opening parentheses or the end
				//and it is not allowed to have a closing parentheses in front of a Proposition
				if((checkCharacter(input, i, "operator", "next")
				|| checkCharacter(input, i, "parenthesesClose", "next")
				|| checkCharacter(input, i, "start/end", "next") 
				)&&checkCharacter(input, i, "parenthesesClose", "last")==false){
					continue;
				}else {
					System.out.println("ERROR proposition else");
					return false;
					}
			
			//Case 2: Operator
			case 2: 
				//after an operator it is not allowed to follow with another operator or a closing parentheses
				//and it has to have either closing parentheses or an proposition in front
				if((checkCharacter(input, i, "proposition", "next")
				|| checkCharacter(input, i, "parenthesesOpen", "next")
				|| checkCharacter(input, i, "negation", "next"))
				&& (checkCharacter(input, i, "proposition", "last")
				|| checkCharacter(input, i, "parenthesesClose", "last"))){
					continue;	
				}else {
					System.out.println("ERROR operator ELSE");
					return false;
				}
				
			//Case 3: Negation
			case 3:
				//after a negation there has to follow a proposition or an opening parentheses or another negation
				//it is not allowed for a negation to be after a closing parentheses or a proposition
				if((checkCharacter(input, i, "proposition", "next")
				|| checkCharacter(input, i, "parenthesesOpen", "next")
				|| checkCharacter(input, i, "negation", "next")
				) && checkCharacter(input, i, "proposition", "last") == false
				&& checkCharacter(input, i, "parenthesesClose", "last") == false){
					continue;
				}else {
					System.out.println("ERROR negation else");
					return false;
				}
			
			//Case 4: Opening Parentheses
			case 4:
				//after an opening parentheses it is not allowed to have an operator or the end
				//it is not allowed for an opening parentheses to stand in front of a proposition
				if(checkCharacter(input, i, "operator", "next") == false
				&& checkCharacter(input, i , "start/end", "next") == false
				&& checkCharacter(input, i, "proposition", "last") == false){
					parenthesesCheck++;
					continue;
				}else {
					System.out.println("ERROR opening parent else");
					return false;
				}
				
			case 5:
				//after an closing parentheses it is not allowed to have a proposition or a negation
				//it is necessary to stand in front of a proposition
				if(checkCharacter(input, i, "proposition", "next") == false
				&& checkCharacter(input, i, "negation", "next") == false
				&& checkCharacter(input, i, "proposition", "last")
				|| checkCharacter(input, i, "parenthesesClose", "last")){
					parenthesesCheck--;
					//more closing parentheses than opening ones
					if(parenthesesCheck < 0) {
						return false;
					}
					continue;
				}else {
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

	//checks if current character matches one of predefined vocabularies for propositional logic
	//returns integers from 0-6 according to which type the character is. 0 is the error case.
	public int checkVocab(String current) {
			String[] vocabulary = {propositionals,operators,negation,parenthesesOpen,parenthesesClose,"_"};
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
		
	//Algorithm that processes the given formula input into a Tree data structure for propositional logic
	public ParseTree parseIntoTree(String input) {
			input = braceFormula(input);
	    	Stack<PropositionalParseTree> currentTrees = new Stack<PropositionalParseTree>();
	    	//currentTrees.add(new PropositionalParseTree(null));
	    	int index = 0;
	    	while(index < input.length()) {
	    		//save the current character as a String
	    		String current = input.substring(index, index+1);
	    		switch(checkVocab(current)) {
	    		
	    		//End|Error Case
	    		case 0:
	    			System.out.println("Case0 ERROR");
	    			return null;
	    			
	    		//Proposition Case	
	    		case 1: 
	    			if(currentTrees.size()>1){
	    				if(currentTrees.peek().getRoot().getData() == null) {
	    					currentTrees.pop();
	    					currentTrees.push(new PropositionalParseTree(current));
	    				}else{
	    					currentTrees.peek().addNodeToLastNode(current);
	    				}
	    			}else {
	    				currentTrees.push(new PropositionalParseTree(current));
	    			}
	    				break;
	    			
	    			
				//Operator Case
	    		case 2:
	    			//in front of an operator has to be a closing parentheses or a closing parenthesis  			
	    			currentTrees.peek().addNodeAsRoot(current);
	    			break;
	    			
	    		//Negation Case
	    		case 3:
			    			if(currentTrees.peek().getRoot().getData() == null) {
								currentTrees.pop();
		    					currentTrees.push(new PropositionalParseTree(current));
							}else{
								currentTrees.peek().addNodeToLastNode(current);
							}	
	    			break;
	    			
	    		//ParenthesesOpen Case
	    		case 4:
	    				currentTrees.push(new PropositionalParseTree((String)null));
	    			break;
	    			
	    		//ParenthesesClose Case
	    		case 5:
		    			//skips empty subtrees that get created from double brackets f.e. ((A))
	    			if(currentTrees.size()>1){
	    				if(currentTrees.get(currentTrees.size()-2).getRoot().getData()==null) {
		    				PropositionalParseTree temp = currentTrees.pop();
		    				currentTrees.get(currentTrees.size()-1).appendOverwrite(temp);
		    			}else {
		    				PropositionalParseTree temp = currentTrees.pop();
			    			currentTrees.get(currentTrees.size()-1).append(temp);	
		    			}
	    			}
	    			break;	
	    			
	    		default: 
	    		}
	    		index++;
	    	}
	    	return currentTrees.peek();
	    }
	
	//public wrapper method for parsing the different premises and the conclusion into trees and saving them as a PropositionNaturalDeduction Class
	public void parse(ArrayList<String> premises, String conclusion, Proof model) {
		PropositionalParseTree temp = (PropositionalParseTree) parseIntoTree(conclusion);
		temp.setLocked(true);
		temp.setOrigin("conclusion");
		model.setConclusionTree(temp);
		if(!premises.isEmpty()) {
			for(int i = 0; i<premises.size();i++) {
				model.addTree(parseIntoTree(premises.get(i)));		
			}
		}
		model.addTree(temp);
	}

	
}
