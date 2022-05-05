package controller;

//<summary>
//Interface for structure of first order logic inference rules
//</summary>

public interface PredicateRuleHandler extends RuleHandler{
	void applyAllQuantifierElimination(String line1, String substitution);
	void applyExistQuantifierIntroduction(String line1, String variable, String substitution);
	void applyAllQuantifierIntroduction(String formula);
	void applyExistQuantifierElimination(String line1, String formula);
	

}
