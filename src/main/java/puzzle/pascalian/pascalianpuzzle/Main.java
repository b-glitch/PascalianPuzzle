package puzzle.pascalian.pascalianpuzzle;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;


public class Main extends Application {

    private final int HEIGHT = 900;
    private final int WIDTH = 800;

    private BoardView boardView;
    private ControlsView controlsView;
    private BoardController boardController;

    @Override
    public void start(Stage stage) throws IOException {

        boardController = new BoardController();
        boardView = new BoardView(boardController);
        controlsView = new ControlsView(boardView, WIDTH);

        BorderPane window = new BorderPane();
        GridPane board = boardView.getBoard();
        VBox controlsBar = controlsView.getControlsBar();

        window.setTop(controlsBar);
        window.setCenter(board);

        Scene scene = new Scene(window, WIDTH, HEIGHT);
        scene.getStylesheets().add("file:src/main/java/puzzle/pascalian/pascalianpuzzle/stylesheet.css");

        stage.setTitle("Pascallian Puzzle");
        stage.setScene(scene);
        stage.show();
    }

}
