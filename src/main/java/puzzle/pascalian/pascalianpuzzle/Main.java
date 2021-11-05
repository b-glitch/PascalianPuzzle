package puzzle.pascalian.pascalianpuzzle;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;


public class Main extends Application {

    private final int HEIGHT = 900;
    private final int WIDTH = 800;
    private final int SLIDER_WIDTH = WIDTH / 2;

    private VBox topBar;
    private BoardView boardView;
    private BoardController boardController;

    @Override
    public void start(Stage stage) throws IOException {

        boardController = new BoardController();
        boardView = new BoardView(boardController);
        BorderPane window = new BorderPane();
        GridPane board = boardView.getBoard();

        setupTopBar();
        window.setTop(topBar);
        window.setCenter(board);

        Scene scene = new Scene(window, WIDTH, HEIGHT);
        scene.getStylesheets().add("file:src/main/java/puzzle/pascalian/pascalianpuzzle/stylesheet.css");

        stage.setTitle("Pascallian Puzzle");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Sets up the controls top bar. These are the control elements above the hexagon board.
     */
    private void setupTopBar(){
        topBar = new VBox();

        // Adding buttons
        HBox buttons = setupButtonBar();
        topBar.getChildren().add(buttons);

        // Adding animation speed slider
        HBox animationSpeedBox = setupAnimationSpeedSlider();
        topBar.getChildren().add(animationSpeedBox);

        // Adding board size slider
        HBox boardSizeBox = setupSizeSlider();
        topBar.getChildren().add(boardSizeBox);

    }

    /**
     * Sets up the board resize slider.
     * @return the HBox containing the board resize slider.
     */
    private HBox setupSizeSlider(){
        HBox sizeSliderBox = new HBox();

        // Setting up label
        String labelTitleText = "Board Size: ";
        String labelText = labelTitleText + boardController.getStartingRowLength();
        Label boardSizeLabel = new Label(labelText);

        // Setting up slider
        Slider boardSizeSlider = new Slider(boardController.getMinStartingRowLength(), boardController.getMaxStartingRowLength(), boardController.getStartingRowLength());
        addBoardSizeSliderListener(boardSizeSlider, boardSizeLabel);

        // Setting up custom tick marks
        Pane tickPane = setupTickPane(boardSizeSlider);
        tickPane.setPadding(new Insets(40,5,5,5)); // Top is set to 40 to lower tick marks below slider

        // Setting up slider stackpane (slider and tick marks)
        StackPane sliderStack = new StackPane();
        sliderStack.getChildren().add(tickPane);
        sliderStack.getChildren().add(boardSizeSlider);
        sliderStack.setMinWidth(SLIDER_WIDTH);
        sliderStack.setPadding(new Insets(-10,0,-10,0));

        // Adding elements to HBox
        sizeSliderBox.getChildren().addAll(boardSizeLabel, sliderStack);
        setHBoxSettings(sizeSliderBox);

        return sizeSliderBox;
    }

    /**
     * Sets up custom tick marks for slider. When a tick mark value is
     * clicked, the slider will be set to the value clicked.
     * @param slider the slider to setup the tick marks for.
     * @return the pane containing the tick marks
     */
    private GridPane setupTickPane(Slider slider){
        GridPane tickPane = new GridPane(); // GridPane was chosen due to ease of alignment across cols and rows

        // Setting up custom tick marks
        int rectWidth = 2;
        int rectHeight = 10;
        int row = 0;

        HBox tick1 = new HBox();
        tick1.getChildren().add(new Label(""));
        tick1.getChildren().add(new Rectangle(rectWidth, rectHeight));
        tick1.setAlignment(Pos.BOTTOM_CENTER);
        tickPane.add(tick1, 1, row);

        HBox tick2 = new HBox();
        tick2.getChildren().add(new Label(""));
        tick2.getChildren().add(new Rectangle(rectWidth, rectHeight));
        tick2.setAlignment(Pos.BOTTOM_CENTER);
        tickPane.add(tick2, 3, row);

        HBox tick3 = new HBox(2);
        tick3.getChildren().add(new Label(""));
        tick3.getChildren().add(new Rectangle(rectWidth, rectHeight));
        tick3.setAlignment(Pos.BOTTOM_CENTER);
        tickPane.add(tick3, 5, row);

        HBox tick4 = new HBox(2);
        tick4.getChildren().add(new Label(""));
        tick4.getChildren().add(new Rectangle(rectWidth, rectHeight));
        tick4.setAlignment(Pos.BOTTOM_CENTER);
        tickPane.add(tick4, 7, row);

        row++;

        // Setting up custom labels that set the slider to the label value when clicked
        Label numLabel0 = new Label(String.valueOf(boardController.getPascalinacciNumber(0)));
        numLabel0.setOnMouseClicked(e->slider.setValue(boardController.getPascalinacciNumber(0)));
        Label numLabel1 = new Label(String.valueOf(boardController.getPascalinacciNumber(1)));
        numLabel1.setOnMouseClicked(e->slider.setValue(boardController.getPascalinacciNumber(1)));
        Label numLabel2 = new Label(String.valueOf(boardController.getPascalinacciNumber(2)));
        numLabel2.setOnMouseClicked(e->slider.setValue(boardController.getPascalinacciNumber(2)));
        Label numLabel3 = new Label(String.valueOf(boardController.getPascalinacciNumber(3)));
        numLabel3.setOnMouseClicked(e->slider.setValue(boardController.getPascalinacciNumber(3)));

        // Adding labels to gridpane
        tickPane.add(new Label(""),                  0, row);
        tickPane.add(numLabel0,                         1, row);
        tickPane.add(new Label(" "),                 2, row);
        tickPane.add(numLabel1,                         3, row);
        tickPane.add(new Label("   "),               4, row);
        tickPane.add(numLabel2,                         5, row);
        tickPane.add(new Label("                 "), 6, row);
        tickPane.add(numLabel3,                         7, row);

        return tickPane;
    }

    private void addBoardSizeSliderListener(Slider boardSizeSlider, Label boardSizeLabel){
        String labelTitleText = "Board Size: ";
        boardSizeSlider.valueProperty().addListener(
                (observableValue, oldValue, newValue) -> {
                    int newLabelValue = newValue.intValue();
                    String newLabelText = labelTitleText + String.valueOf(newLabelValue);
                    if(newLabelValue < 10) {
                        newLabelText += "  ";
                    }
                    boardSizeLabel.setText(newLabelText);
                    boardController.setStartingRowLength(newLabelValue);
                    boardView.changeBoardSize(newLabelValue);
                }
        );
    }

    /**
     * Sets up the Animation Speed Slider.
     * @return an HBox containing the animation speed slider.
     */
    private HBox setupAnimationSpeedSlider(){
        HBox sliderBox = new HBox();
        Label animationSpeedLabel = new Label("Animation Speed");
        Slider animationSpeedSlider = new Slider(0, 1, 0.1);
        animationSpeedSlider.valueProperty().addListener(
                (observableValue, oldValue, newValue) -> boardView.updateAnimationSpeed((double) newValue)
        );
        animationSpeedSlider.setMinWidth(SLIDER_WIDTH);
        sliderBox.getChildren().addAll(animationSpeedLabel, animationSpeedSlider);
        setHBoxSettings(sliderBox);
        return sliderBox;
    }

    /**
     * Sets up the main control buttons.
     * @return an HBox containing the main control buttons.
     */
    private HBox setupButtonBar(){
        Border buttonBorder = new Border(new BorderStroke(Color.BLACK,BorderStrokeStyle.SOLID, null, new BorderWidths(2)));
        HBox buttonBar = new HBox();

        Button clear = setupButton("Clear");
        clear.setOnAction(e-> setupButtonOnAction());
        buttonBar.getChildren().add(clear);

        Button reset = setupButton("Reset");
        reset.setOnAction(e-> resetButtonOnAction());
        buttonBar.getChildren().add(reset);

        Button pattern = setupButton("Pattern");
        pattern.setOnAction(e-> patternButtonOnAction());
        buttonBar.getChildren().add(pattern);

        Button exit = setupButton("Exit");
        exit.setOnAction(e-> System.exit(0));
        buttonBar.getChildren().add(exit);

        setHBoxSettings(buttonBar);
        return buttonBar;
    }

    /**
     *Sets the default spacing for an HBox in the top bar.
     * @param topBarHBox the HBox to set to default settings.
     */
    private void setHBoxSettings(HBox topBarHBox){
        topBarHBox.setPadding(new Insets(20,20,10,20));
        topBarHBox.setAlignment(Pos.CENTER);
        topBarHBox.setSpacing(10);
    }

    /**
     * Sets up the default styling for a button and returns the button.
     * @param text the text on the button.
     * @return the stylized button.
     */
    private Button setupButton(String text){
        Border buttonBorder = new Border(new BorderStroke(Color.BLACK,BorderStrokeStyle.SOLID, null, new BorderWidths(5)));

        Button button = new Button(text);
        button.setShape(new Hexagon(1, 1));
        button.borderProperty().setValue(buttonBorder);
        button.setMinSize(100,100);

        return button;
    }

    /**
     * Clears the board.
     */
    private void setupButtonOnAction(){
        boardView.clearBoard();
    }

    /**
     * Resets the board to a default state.
     */
    private void resetButtonOnAction(){
        boardView.setupBoard();
    }

    /**
     * Sets the board to a specific pattern.
     */
    private void patternButtonOnAction(){
        boardView.setupSpecialBoard();
    }
}
