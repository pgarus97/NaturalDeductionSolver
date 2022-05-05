package controller;

//<summary>
//Interface for structure of MainController
//</summary>

public interface ControllerHandler {
	
	void solve(String premise, String conclusion);
	void addChar(String text);
	void changeState(boolean state);

}
