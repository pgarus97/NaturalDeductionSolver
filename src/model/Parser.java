package model;

import java.util.ArrayList;

//<summary>
//Abstract parser class that gets inherited by the different logics
//</summary>

public abstract class Parser {
	//Algorithm used to determine where to set braces for braceFormula
	public abstract String braceOperator(String input, int initialPosition, String operator);
	
	
	//Algorithm that goes over the given formula "input" and correctly braces all used operators in order of their precedence
	public abstract String braceFormula(String input);

	//method that processes the given formula input into a Tree data structure for predicate logic
    public abstract ParseTree parseIntoTree(String input);
    
    //checks if current character matches one of predefined vocabularies for propositional logic
	//returns integers from 0-6 according to which type the character is. 0 is the error case.
	public abstract int checkVocab(String current);
    
	//Algorithm that checks if a given character at position i in the String input stands before or after (direction)
	//a operator, proposition, etc. (type). Returns true if last/next character of given character matches with type.
	public abstract boolean checkCharacter(String input, int i, String type, String direction);
    
    //Algorithm that goes through the given String "input" and determines if it is a correct formed formula 
	//within the rules of propositional logic grammar
	public abstract boolean checkSyntax(String input);

	//public wrapper method for parsing the different premises and the conclusion into trees and saving them as a PredicateNaturalDeduction Class
	public abstract void parse(ArrayList<String> premises, String conclusion, Proof model);
		
	//splits the premises based on the ; character in the input and saves the separate Strings in an ArrayList
	public ArrayList<String> splitPremises(String input) {
		ArrayList<String> premises = new ArrayList<String>();
		int beginning = 0;
		for(int i=0;i<input.length();i++) {
			String current = input.substring(i, i+1);
			if(current.equals(";")){
				premises.add(input.substring(beginning,i));
				beginning = i+1;
			}
		}
		if(beginning != input.length()) {
			premises.add(input.substring(beginning,input.length()));
		}
		if(premises.isEmpty()) {
			premises.add(input);
		}
		return premises;
	}



}
