package view;


import java.util.ArrayList;

import controller.ControllerHandler;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


//<summary>
//This is the main window class 
//</summary>

public class MainView extends BorderPane{
	
	private ControllerHandler handler;
	private boolean state = true; //default state of parser on predicate;
	private String selected = "premise"; //variable for operator insertion buttons (textfield focus)
		
	//GUI
	
	//buttons
    private Button solveButton = new Button("Solve");
    
    //Bottom Buttons & Layout
    private Button negationButton = new Button("¬");
    private Button conjunctionButton = new Button("∧");
    private Button disjunctionButton = new Button("∨");
    private Button implicationButton = new Button("→");
    private Button allQuantifierButton = new Button("∀");
    private Button existQuantifierButton = new Button("∃");
    private Button helpButton = new Button("Help");

    
    private Label keyboardDescription = new Label("Hover over the buttons for the corresponding shortcut to manually write into the textfield!");
    
    


    //Input-Fields
    private Label inputDescription = new Label("Split multiple premises with ; \n ");
    private TextField premise = new TextField();
    private TextField conclusion = new TextField();
    private Label deduction = new Label(" ⊢ ");
    private HBox formula = new HBox(premise, deduction, conclusion);

    private Label provedLabel = new Label();
    private Label solution = new Label();
    private ScrollPane solutionScroll = new ScrollPane();
    
    //Top-Toolbar
    private Button propositionalButton = new Button("Propositional Logic");
    private Button predicateButton = new Button("First-order Logic");
    private Label currentState = new Label("Currently using: First-order Logic");
    private ToolBar toolbar = new ToolBar(predicateButton, propositionalButton, currentState);


    //Structure
    private VBox inputLayout = new VBox(toolbar, inputDescription, formula);
    private HBox buttonInputLayout = new HBox(negationButton,conjunctionButton,disjunctionButton,implicationButton,allQuantifierButton,existQuantifierButton);
    private HBox buttonBottomLayout = new HBox(buttonInputLayout, helpButton);
    private VBox bottomLayout = new VBox(provedLabel,keyboardDescription,buttonBottomLayout);
    
    
	private ArrayList<Button> buttonList = new ArrayList<Button>();
   
    //Main call for proof visualization window 
    
    public MainView() { 
    	buttonBottomLayout.setSpacing(30);
    	solutionScroll.setContent(solution);
    	inputDescription.setStyle("-fx-font: normal bold 15 Langdon; ");
    	predicateButton.setDisable(true); //on default on predicate logic

    	//tooltips
    	
    //	Tooltip negT = new Tooltip("neg");
    //	negT.setStyle("-fx-font: normal bold 30 Langdon; ");
    	negationButton.setTooltip(new Tooltip("neg / not"));
    	conjunctionButton.setTooltip(new Tooltip("land"));
    	disjunctionButton.setTooltip(new Tooltip("lor"));
    	implicationButton.setTooltip(new Tooltip("rightarrow / implies / to"));
    	allQuantifierButton.setTooltip(new Tooltip("forall"));
    	existQuantifierButton.setTooltip(new Tooltip("exists"));
    	conclusion.setTooltip(new Tooltip("Please enter your conclusion here!"));
    	premise.setTooltip(new Tooltip("Please enter your premises here!"));
    	propositionalButton.setTooltip(new Tooltip("Click here to switch to propositional logic!"));
    	predicateButton.setTooltip(new Tooltip("Click here to switch to first-order logic!"));
    	helpButton.setTooltip(new Tooltip("Click here for help and more information!"));
    	
    	//textfield addons (shortcuts,prompttext etc)
    	
    	premise.setPromptText("Premises");
    	premise.setPrefWidth(262.5);
    	premise.textProperty().addListener((obs, oldValue, newValue) -> {	
    			Platform.runLater(() -> { 
        			checkText(newValue,premise);
    	        }); 
        });

    	
    	conclusion.setPromptText("Conclusion");
    	conclusion.setPrefWidth(262.5);
    	conclusion.textProperty().addListener((obs, oldValue, newValue) -> {	
    		Platform.runLater(() -> { 
    			checkText(newValue,conclusion);
    		 }); 
        });
    	
    	premise.focusedProperty().addListener((obs, oldValue, newValue) -> {	
    		selected = "premise";
        });
    	
    	conclusion.focusedProperty().addListener((obs, oldValue, newValue) -> {	
    		selected = "conclusion";
        });
    	
    	//layout
    	
    	setRight(solveButton);
    	setTop(inputLayout);
    	setCenter(solutionScroll);
    	setBottom(bottomLayout);
    	
    	// button functionality
    	
    	solveButton.setOnAction(e -> {    		
    		handler.solve(premise.getText(), conclusion.getText());
    		});
    	
    	for(Node b : buttonInputLayout.getChildren()) {
			buttonList.add((Button) b);
			
    	}
    	
    	for(Button b: buttonList) {
    		b.setOnAction(e -> {	 
        			handler.addChar(b.getText());
    		});
    	}
    	
    	propositionalButton.setOnAction(e -> {
    		handler.changeState(false);
    		currentState.setText("Currently using: Propositional Logic");		
    		});
    	
    	predicateButton.setOnAction(e -> {
    		handler.changeState(true);
    		currentState.setText("Currently using: Predicate Logic");
    		});
    	
    	helpButton.setOnAction(e -> {
    		Stage stage = new Stage();
    		HelpView helpView = new HelpView();
            stage.setTitle("Help-Window");
            stage.setScene(new Scene(helpView));
            stage.show();
    		}); 	
    }

    //setter/getter functions
    
    public void setHandler(ControllerHandler handler) {
        this.handler = handler;
    }
    
    public void setSolution(String solution) {
    	this.solution.setText(solution);
    }
    
    //displays a message that the proof is complete
    public void proved() {
    	if(!provedLabel.getText().equals("The proof has been completed! The given statement is valid!")) {
    	this.provedLabel.setText("The proof has been completed! The given statement is valid!");
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Information Dialog");
		alert.setHeaderText(null);
		alert.setContentText("The statement was successfully proven!");

		alert.showAndWait();
    	}
    }
   
    
    public void setProved(String proved) {
    	this.provedLabel.setText(proved);
    }

	public boolean getState() {
		return state;
	}
	
    //helper function for shortcut buttons
    
    public void addChar(String input) {
    	if(selected.equals("premise")) {
    		premise.setText(premise.getText()+input); 
    		premise.requestFocus();
    		premise.positionCaret(premise.getText().length());	
    	}
    	if(selected.equals("conclusion")) {
        	conclusion.setText(conclusion.getText()+input);
    		conclusion.requestFocus();
    		conclusion.positionCaret(conclusion.getText().length());
    	}

    }
    
    //helper function for keyboard shortcuts
    
    private void checkText(String input, TextField field){
    	String comparison = input;
    	input = input.replace("land", "∧");
        input = input.replace("lor", "∨");
        input = input.replace("neg", "¬");
        input = input.replace("not", "¬");
        input = input.replace("rightarrow", "→");
        input = input.replace("to", "→");
        input = input.replace("implies", "→");
        if(state == true) {
        input = input.replace("forall", "∀");
        input = input.replace("exists", "∃");
        }
        if(!comparison.equals(input)) {
	        field.setText(input);
	        field.positionCaret(field.getLength());
        }
    }
 


	//helper function for switching propositional and predicate logic states
	
	public void setState(boolean state) {
		this.state = state;
		if(state) {
			existQuantifierButton.setDisable(false);
			allQuantifierButton.setDisable(false);
			propositionalButton.setDisable(false);
			predicateButton.setDisable(true);
		} else {
			existQuantifierButton.setDisable(true);
			allQuantifierButton.setDisable(true);
			propositionalButton.setDisable(true);
			predicateButton.setDisable(false);
		}
		
	}

	//displays an error message on errors
	public void popError(String msg) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error Message");
		alert.setHeaderText(null);
		alert.setContentText(msg);

		alert.showAndWait();
	}

	//clear everything
	public void clear() {
		premise.clear();
		conclusion.clear();
		setSolution("");
		setProved("");
	}
}
