package view;


import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import controller.PredicateRuleController;
import controller.PredicateRuleHandler;
import controller.PropositionalRuleController;
import controller.RuleController;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

//<summary>
//This is the rule window class 
//</summary>

public class RuleView extends BorderPane{
	
	private boolean logic = true; // default on predicate logic
	
	private RuleController ruleHandler;
	private PropositionalRuleController propositionalHandler;
	private PredicateRuleController predicateHandler;

	private String ruleState; //saves which rule is selected
	
	private boolean autoSolver = false;
	
	private Label buttonExplanation = new Label("Click on a rule-button to get more information! \n");
	
	//introduction-rules
	private Label introductionText = new Label("Introduction-Rules: ");
	
	private Button negationIntroductionButton = new Button("¬i");
    private Button conjunctionIntroductionButton = new Button("∧i");
    private Button disjunctionIntroductionButtonV1 = new Button("∨i1");
    private Button disjunctionIntroductionButtonV2 = new Button("∨i2");
    private Button implicationIntroductionButton = new Button("→i");
	private Button doubleNegationIntroductionButton = new Button("¬¬i");
	private Button allQuantifierIntroductionButton = new Button("∀i");
	private Button existQuantifierIntroductionButton = new Button("∃i");
    private HBox introductionButtons = new HBox(negationIntroductionButton,conjunctionIntroductionButton,disjunctionIntroductionButtonV1,disjunctionIntroductionButtonV2,implicationIntroductionButton,doubleNegationIntroductionButton,allQuantifierIntroductionButton,existQuantifierIntroductionButton);

    
    //elimination-rules
	private Label eliminationText = new Label("Elimination-Rules: ");
	
	private Button negationEliminationButton = new Button("¬e");
    private Button conjunctionEliminationButtonV1 = new Button("∧e1");
    private Button conjunctionEliminationButtonV2 = new Button("∧e2");
    private Button disjunctionEliminationButton = new Button("∨e");
    private Button implicationEliminationButton = new Button("→e");
	private Button doubleNegationEliminationButton = new Button("¬¬e");
	private Button allQuantifierEliminationButton = new Button("∀e");
	private Button existQuantifierEliminationButton = new Button("∃e");
	private Button contradictionEliminationButton = new Button("⊥e");
    private HBox eliminationButtons = new HBox(negationEliminationButton,conjunctionEliminationButtonV1,conjunctionEliminationButtonV2,disjunctionEliminationButton,implicationEliminationButton,doubleNegationEliminationButton,allQuantifierEliminationButton,existQuantifierEliminationButton, contradictionEliminationButton);
    
    //special-rules
	private Label specialText = new Label("Derived-Rules: ");

	private Button MTButton = new Button("MT");
	private Button PBCButton = new Button("PBC");
	private Button LEMButton = new Button("LEM");
	private Button copyButton = new Button("Copy");
    private HBox specialButtons = new HBox(MTButton,PBCButton,LEMButton,copyButton);
    
    private Button closeButton = new Button("Close Subproof");
    private HBox specialDistinct = new HBox(specialButtons,closeButton);
    
	private Button autoSolveButton = new Button("Auto-Solver");

	private ArrayList<Button> buttonList = new ArrayList<Button>();

	//input fields
	
    private TextField input1 = new TextField();
    private TextField input2 = new TextField();
    private TextField formulaInput = new TextField();
    private TextField termInput = new TextField();
    private TextField substitutionInput = new TextField();
    
    private Label input1Text = new Label();
    private Label input2Text = new Label();
    private Label formulaInputText = new Label();
    private Label termInputText = new Label();
    private Label substitutionInputText = new Label();
    
	//structure
	
    private VBox buttonStructure = new VBox(buttonExplanation,introductionText,introductionButtons,eliminationText,eliminationButtons,specialText, specialDistinct, autoSolveButton);
    
    private ImageView desc = new ImageView();
	
    private Button applyButton = new Button("Apply");
    
    private VBox bottomLayout = new VBox(applyButton);
    
   //main call of the rulewindow
    
    public RuleView() {
    	specialDistinct.setSpacing(50);
    	setApplyButtons(true);
    	buttonExplanation.setStyle("-fx-font: normal bold 15 Langdon; ");
    	specialText.setStyle("-fx-font: normal bold 12 Langdon; ");
    	eliminationText.setStyle("-fx-font: normal bold 12 Langdon; ");
    	introductionText.setStyle("-fx-font: normal bold 12 Langdon; ");
   
    	//creates buttonlist
    
    	closeButton.setTooltip(new Tooltip("Click here to close an unfinished subproof"));
    	closeButton.setOnAction(e -> {
    			ruleHandler.closeProof();
    	});
    	
    	
    	for(Node b : introductionButtons.getChildren()) {
			buttonList.add((Button) b);
			
    	}
    	
    	for(Node b: eliminationButtons.getChildren()) {
        	buttonList.add((Button) b);
        	
    	}
    	for(Node b: specialButtons.getChildren()) {
        	buttonList.add((Button) b);
        	
    	}
    	
    	//tooltips
    	for(Button b: buttonList) {
      			URL url = getClass().getResource("/resources/"+b.getText()+".png");
    			Image descPic= null;
    			try {
    				descPic = new Image(url.openStream());
    			} catch (IOException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
      			 ImageView temp = new ImageView();
      			 temp.setImage(descPic);
                 Tooltip tempTooltip = new Tooltip();
                 tempTooltip.setGraphic(temp);
                 b.setTooltip(tempTooltip);
           
    	}
    	
    	//prepare input addons
    	
    	//only allow numbers as input for input1 and input2
    	input1.setPromptText("Please insert the number of the first Input");
    	input1.textProperty().addListener((obs, oldValue, newValue) -> {	
    		if (!newValue.matches("\\d*")) {
	            input1.setText(newValue.replaceAll("[^\\d]", ""));
	        }        
    	});
    	
    	input2.setPromptText("Please insert the number of the second Input");
    	input2.textProperty().addListener((obs, oldValue, newValue) -> {	
    		if (!newValue.matches("\\d*")) {
	            input1.setText(newValue.replaceAll("[^\\d]", ""));
	        }        
    	});
    	    	
		formulaInput.setPromptText("Please insert the new formula here");
		formulaInput.textProperty().addListener((obs, oldValue, newValue) -> {
			Platform.runLater(() -> {
			checkText(newValue, formulaInput);
			});
		});
		
		
		termInput.setPromptText("Please insert your term here");
		substitutionInput.setPromptText("Please insert your substitution here");

    	
    	setTop(buttonStructure);
    	
    	//prepares apply button with corresponding button function
    	
    	applyButton.setOnAction(e -> {
    		eliminateSpace();
    			if(ruleState.equals("∧i")) {
            		ruleHandler.applyConjunctionIntroduction(input1.getText(),input2.getText());
        		}
        		if(ruleState.equals("∧e1")) {
        			ruleHandler.applyConjunctionElimination(input1.getText(),1);
        		}
        		if(ruleState.equals("∧e2")) {
        			ruleHandler.applyConjunctionElimination(input1.getText(),2);
        		}
        		if(ruleState.equals("¬¬e")) {
        			ruleHandler.applyDoubleNegationElimination(input1.getText());
        		}
        		if(ruleState.equals("¬¬i")) {
        			ruleHandler.applyDoubleNegationIntroduction(input1.getText());
        		}
        		if(ruleState.equals("→e")) {
        			ruleHandler.applyImplicationElimination(input1.getText(),input2.getText());
        		}
        		if(ruleState.equals("∨i1")) {
        			ruleHandler.applyDisjunctionIntroduction(input1.getText(),formulaInput.getText(),1);
        		}
        		if(ruleState.equals("∨i2")) {
        			ruleHandler.applyDisjunctionIntroduction(input1.getText(),formulaInput.getText(),2);
        		}
        		if(ruleState.equals("LEM")) {
        			ruleHandler.applyLEM(formulaInput.getText());
        		}
        		if(ruleState.equals("¬e")) {
        			ruleHandler.applyNegationElimination(input1.getText(),input2.getText());
        		}
        		if(ruleState.equals("MT")) {
        			ruleHandler.applyMT(input1.getText(),input2.getText());
        		}
        		if(ruleState.equals("⊥e")) {
        			ruleHandler.applyContradictionElimination(input1.getText(),formulaInput.getText());
        		}
        		if(ruleState.equals("∀e")) {
        			((PredicateRuleHandler) ruleHandler).applyAllQuantifierElimination(input1.getText(),termInput.getText());
        		}
        		if(ruleState.equals("∃i")) {
        			((PredicateRuleHandler) ruleHandler).applyExistQuantifierIntroduction(input1.getText(),termInput.getText(),substitutionInput.getText());
        		}
        		if(ruleState.equals("→i")) {
        			ruleHandler.applyImplicationIntroduction(formulaInput.getText());
        		}
        		if(ruleState.equals("Copy")) {
        			ruleHandler.applyCopy(input1.getText());
        		}
        		if(ruleState.equals("¬i")) {
        			ruleHandler.applyNegationIntroduction(formulaInput.getText());
        		}
        		if(ruleState.equals("PBC")) {
        			ruleHandler.applyPBC(formulaInput.getText());
        		}
        		if(ruleState.equals("∨e")) {
        			ruleHandler.applyDisjunctionElimination(input1.getText(), formulaInput.getText());
        		}
        		if(ruleState.equals("∀i")) {
        			((PredicateRuleHandler) ruleHandler).applyAllQuantifierIntroduction(formulaInput.getText()); 
        		}
        		if(ruleState.equals("∃e")) {
        			((PredicateRuleHandler) ruleHandler).applyExistQuantifierElimination(input1.getText(), formulaInput.getText()); 
        		}
    		});
    	
    	//prepare all button clicks
    	
    	autoSolveButton.setOnAction(e -> {
    		if(logic == true) {
    			((PredicateRuleController) ruleHandler).autoSolve();
    		}else {
    			((PropositionalRuleController) ruleHandler).autoSolve();
    		}
    	});
    	
    	//prepares all buttons
    	for(Button b:buttonList) {
    		b.setOnAction(e -> {
    			prepareButton(b.getText());
    		});
    	}
    }
    
    //function that prepares the view after a rule click
    private void prepareButton(String name) {
    	clearInputs();
    	URL url = getClass().getResource("/resources/"+name+".png");
			Image descPic= null;
			try {
				descPic = new Image(url.openStream());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 desc.setImage(descPic);
    	VBox descriptionLayout = new VBox();
    	switch(name) {
    	case "∧e1":
    		input1Text.setText("Line 1 (Φ ∧ ψ):");
    		descriptionLayout = new VBox(input1Text,input1);
    		break;
    	case "Copy":
    		input1Text.setText("Line 1 (Φ):");
    		descriptionLayout = new VBox(input1Text,input1);
    		break;
    	case "∧e2":
    		input1Text.setText("Line 1 (Φ ∧ ψ):");
    		descriptionLayout = new VBox(input1Text,input1);
    		break;

    	case "¬¬i":
    		input1Text.setText("Line 1 (Φ):");
    		descriptionLayout = new VBox(input1Text,input1);
    		break;

    	case "¬¬e":
    		input1Text.setText("Line 1 (¬¬Φ):");
    		descriptionLayout = new VBox(input1Text,input1);
    		break;

    	case "∨e":
    		input1Text.setText("Line 1 (Φ ∨ ψ):");
    		formulaInputText.setText("Formula (χ):");
    		descriptionLayout = new VBox(input1Text,input1,formulaInputText,formulaInput);
    		break;

    	case "∨i1":
    		input1Text.setText("Line 1 (Φ):");
    		formulaInputText.setText("Formula (ψ):");
    		descriptionLayout = new VBox(input1Text,input1,formulaInputText,formulaInput);
    		break;

    	case "∨i2":
    		input1Text.setText("Line 1 (ψ):");
    		formulaInputText.setText("Formula (Φ):");
    		descriptionLayout = new VBox(input1Text,input1,formulaInputText,formulaInput);
    		break;

    	case "⊥e":
    		input1Text.setText("Line 1 (⊥):");
    		formulaInputText.setText("Formula (Φ):");
    		descriptionLayout = new VBox(input1Text,input1,formulaInputText,formulaInput);
    		break;

    	case "∃e":
    		input1Text.setText("Line 1 (∃x Φ):");
    		formulaInputText.setText("Formula (χ):");
    		descriptionLayout = new VBox(input1Text,input1,formulaInputText,formulaInput);
    		break;

    	case "MT":
    		input1Text.setText("Line 1 (Φ → ψ):");
    		input2Text.setText("Line 2 (¬ψ):");
    		descriptionLayout = new VBox(input1Text,input1,input2Text,input2);
    		break;

    	case "→e":
    		input1Text.setText("Line 1 (Φ):");
    		input2Text.setText("Line 2 (Φ → ψ):");
    		descriptionLayout = new VBox(input1Text,input1,input2Text,input2);
    		break;

    	case "∧i":
    		input1Text.setText("Line 1 (Φ):");
    		input2Text.setText("Line 2 (ψ):");
    		descriptionLayout = new VBox(input1Text,input1,input2Text,input2);

    		break;

    	case "¬e":
    		input1Text.setText("Line 1 (Φ):");
    		input2Text.setText("Line 2 (¬Φ):");
    		descriptionLayout = new VBox(input1Text,input1,input2Text,input2);
    		break;

    	case "∃i":
    		input1Text.setText("Line 1 (Φ):");
    		termInputText.setText("Term (t):");
    		substitutionInputText.setText("Substitution (x):");
    		descriptionLayout = new VBox(input1Text,input1,termInputText,termInput,substitutionInputText,substitutionInput);    	
    		break;

    	case "∀e":
    		input1Text.setText("Line 1 (∀x Φ):");
    		termInputText.setText("Term (t):");
    		descriptionLayout = new VBox(input1Text,input1,termInputText,termInput);
    		break;

    	case "¬i":
    		formulaInputText.setText("Formula (¬Φ):");
    		descriptionLayout = new VBox(formulaInputText,formulaInput);
    		break;

    	case "→i":
    		formulaInputText.setText("Formula (Φ → ψ):");
    		descriptionLayout = new VBox(formulaInputText,formulaInput);
    		break;

    	case "∀i":
    		formulaInputText.setText("Formula (∀x Φ):");
    		descriptionLayout = new VBox(formulaInputText,formulaInput);
    		break;

    	case "LEM":
    		formulaInputText.setText("Formula (Φ):");
    		descriptionLayout = new VBox(formulaInputText,formulaInput);
    		break;

    	case "PBC":
    		formulaInputText.setText("Formula (Φ):");
    		descriptionLayout = new VBox(formulaInputText,formulaInput);
    		break;
    	}
		setLeft(desc);
		setCenter(descriptionLayout);
		setBottom(bottomLayout);
		ruleState = name;
    }
    
    //clears all input textfields
    private void clearInputs() {
    	input1.clear();
		input2.clear();
		formulaInput.clear();
		termInput.clear();
		substitutionInput.clear();
    }
    
    //activates and deactivates rules for predicate logic
    public void checkLogic() {
    	if(logic == false) {
			allQuantifierIntroductionButton.setDisable(true);
			existQuantifierIntroductionButton.setDisable(true);
			allQuantifierEliminationButton.setDisable(true);
			existQuantifierEliminationButton.setDisable(true);
			clearPane();
		}else {
			allQuantifierIntroductionButton.setDisable(false);
			existQuantifierIntroductionButton.setDisable(false);
			allQuantifierEliminationButton.setDisable(false);
			existQuantifierEliminationButton.setDisable(false);
			clearPane();
		}
    }
    
    private void clearPane() {
    	setCenter(null);
    	setBottom(null);
    	setLeft(null);
    }
    
    //disables the apply buttons if there is no proof yet
    public void setApplyButtons(boolean disabled) {
    	applyButton.setDisable(disabled);
    	autoSolveButton.setDisable(disabled);
    }
    
    //setter and getter functions
    
    //display an error message on errors
    public void setErrorLabel(String text) {
    	if(!autoSolver) {
    		Alert alert = new Alert(AlertType.ERROR);
    		alert.setTitle("Error Message");
    		alert.setHeaderText(null);
    		alert.setContentText(text);
    		alert.showAndWait();
    	}
    }
    
    public void setAutoSolver(boolean state) {
    	autoSolver = state;
    }
    
    public void setPropositionalRuleHandler(PropositionalRuleController ruleHandler) {
    	this.propositionalHandler = ruleHandler;
    }
    
    public void setPredicateRuleHandler(PredicateRuleController ruleHandler) {
    	this.predicateHandler = ruleHandler;
    	this.ruleHandler = ruleHandler;
    }
    
    //switches between predicate and propositional logic
    public void setPredicateLogic(boolean logic) {
    	this.logic = logic;
    	if(logic) {
    		ruleHandler = predicateHandler;
    	}else {
    		ruleHandler = propositionalHandler;
    	}
    }
    
    //input processing function
    private void checkText(String input, TextField field){
    	String comparison = input;
    	input = input.replace("land", "∧");
        input = input.replace("lor", "∨");
        input = input.replace("neg", "¬");
        input = input.replace("not", "¬");
        input = input.replace("rightarrow", "→");
        input = input.replace("to", "→");
        input = input.replace("implies", "→");
        if(logic == true) {
        input = input.replace("forall", "∀");
        input = input.replace("exists", "∃");
        }
        if(!comparison.equals(input)) {
	        field.setText(input);
	        field.positionCaret(field.getLength());
        }
    }
	 
    //eliminates spaces in inputs
	 public void eliminateSpace() {
 		input1.setText(input1.getText().replace(" ", ""));
 		input2.setText(input2.getText().replace(" ", ""));
 		termInput.setText(termInput.getText().replace(" ", ""));
 		formulaInput.setText(formulaInput.getText().replace(" ", ""));
 		substitutionInput.setText(substitutionInput.getText().replace(" ", ""));
	 }
}
