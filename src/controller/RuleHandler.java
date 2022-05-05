package controller;

//<summary>
//Interface for structure of propositional/all logic inference rules
//</summary>

public interface RuleHandler {
	
void applyConjunctionIntroduction(String line1, String line2);
void applyConjunctionElimination(String line1, int type);
void applyDoubleNegationElimination(String line1);
void applyDoubleNegationIntroduction(String line1);
void applyImplicationElimination(String line1, String line2);
void applyDisjunctionIntroduction(String line1, String formula, int type);
void applyNegationElimination(String line1, String line2);
void applyLEM(String formula);
void applyMT(String line1, String line2);
void applyContradictionElimination(String line1, String formula);
void applyImplicationIntroduction(String formula);
void applyCopy(String line1);
void applyNegationIntroduction(String formula);
void applyPBC(String formula);
void applyDisjunctionElimination(String line1,String formula);
void autoSolve();

}
