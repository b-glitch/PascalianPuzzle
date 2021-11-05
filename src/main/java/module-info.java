module puzzle.pascalian.pascalianpuzzle {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;


    opens puzzle.pascalian.pascalianpuzzle to javafx.fxml;
    exports puzzle.pascalian.pascalianpuzzle;
}