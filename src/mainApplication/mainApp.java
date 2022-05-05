package mainApplication;

import controller.MainController;
import controller.PredicateRuleController;
import controller.PropositionalRuleController;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.PredicateParser;
import model.Proof;
import model.PropositionParser;
import view.MainView;
import view.RuleView;

//<summary>
//This is the start of the software, everything needed gets called and linked here
//</summary>

public class mainApp extends Application{

    public static void main(String[] args) { Application.launch(args); }

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		//creates and links all the main parts of the software
		
		PropositionParser propositionalParser = new PropositionParser();   
		PredicateParser predicateParser = new PredicateParser(); 
		Proof mainproof= new Proof(null);

        MainView view = new MainView();
        RuleView ruleView = new RuleView();
        MainController controller = new MainController();
        
        PropositionalRuleController propositionalRuleController = new PropositionalRuleController();
        PredicateRuleController predicateRuleController = new PredicateRuleController();
        
        controller.link(propositionalParser, predicateParser, view, ruleView, mainproof);
        propositionalRuleController.link(view, ruleView, mainproof, propositionalParser);
        predicateRuleController.link(view, ruleView, mainproof, predicateParser);
		
        //sets up the view windows
        
		Scene scene  = new Scene(view);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Natural Deduction Solver");
        primaryStage.setX(0);
        primaryStage.setY(0);
        primaryStage.setHeight(600);
        primaryStage.setWidth(600);
        primaryStage.show();
        
        Stage ruleWindow = new Stage();
		Scene ruleScene = new Scene(ruleView);
		ruleWindow.setTitle("Inference Rule Window"); 
        ruleWindow.setScene(ruleScene);
        ruleWindow.setHeight(600);
        ruleWindow.setWidth(700);
        ruleWindow.setX(600);
        ruleWindow.setY(0);
        ruleWindow.show();

        
        //closes both views on closing a window
        
        ruleWindow.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent e) {
                primaryStage.close();
                ruleWindow.close();
            }
        });
    
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent e) {
                primaryStage.close();
                ruleWindow.close();
            }
        });
    	
	}

}
