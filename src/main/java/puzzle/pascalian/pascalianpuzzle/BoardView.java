package puzzle.pascalian.pascalianpuzzle;

import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class BoardView implements ListChangeListener {

    private double currentScale;
    private static int startingRowLength;
    private GridPane board;
    private static BoardController boardController;
    private static ArrayList<Hexagon> hexagons;
    private static int rows;
    private static AtomicBoolean updating;
    private static Thread animationThread;
    private static int animationSleepMillis;
    private final static int MAX_ANIMATION_SLEEP_MILLIS = 1000;

    /**
     * Constructor.
     * @param controller the BoardController to display the view of.
     */
    public BoardView(BoardController controller){

        board = new GridPane();
        hexagons = new ArrayList<>();
        updating = new AtomicBoolean(false);
        boardController = controller;
        boardController.watchList(this);

        // Getting initial board size and scale
        startingRowLength = boardController.getStartingRowLength();
        setScaleFromSize(startingRowLength);

        setupBoard();

        animationSleepMillis = 200;
    }

    /**
     * Sets the board to an initial state.
     */
    private void initBoard(){
        // Clearing row counter
        rows = 0;
        // Clearing arrays
        boardController.emptyIndices();
        hexagons.clear();
        // If first initialization, initializing the board, otherwise clear the board
        if(board == null) {
            board = new GridPane();
        }else{
            board.getChildren().clear();
        }
        // Setting padding and alignment
        board.setPadding(new Insets(20,20,20,20));
        board.setAlignment(Pos.CENTER);

    }

    /**
     * Sets up the board. Initializes the board, adds the starting
     * row and any remaining rows to complete the triangle.
     */
    protected void setupBoard(){
        // Setting update flag
        updating.compareAndSet(false, true);

        initBoard();
        addStartingRow(board);
        for(int row = 1; row < startingRowLength; row++) {
            addNextRow(board);
        }
        // clearing update flag
        updating.compareAndSet(true, false);
    }
    /**
     * Sets up the board. Initializes the board, adds the starting
     * row and any remaining rows to complete the triangle. The
     * board resulting from this method will have a set color
     * pattern after setup.
     */
    protected void setupSpecialBoard(){
        // Setting update flag
        updating.compareAndSet(false, true);
        initBoard();
        addSpecialStartingRow(board);
        for(int row = 1; row < startingRowLength; row++) {
            addNextRow(board);
        }
        // Clearing update flag
        updating.compareAndSet(true, false);
    }

    /**
     * Creates a starting row of hexagons with a simple color pattern.
     * The middle hexagon(s) are colored differently than the rest.
     * @param pane the GridPane object to add the row to.
     */
    private void addSpecialStartingRow(GridPane pane){
        HBox startingRow = new HBox();
        startingRow.setPadding(new Insets(0,0,-5 * currentScale,0)); // T R B L (bottom = -5 * SCALE to remove padding for next row down)

        for(int i = 0; i < startingRowLength; i++) {
            int colorIndex = 2; // default color of hexagons

            // Setting a middle hexagon(s) a different color
            if(boardController.checkIfMiddleIndex(i)){
                colorIndex = 1;
            }
            // Creating the hexagon
            Hexagon hexagon = new Hexagon(colorIndex, currentScale);
            hexagon.setRow(0);
            hexagons.add(hexagon);

            // Updating the controller
            boardController.addColorIndex(hexagon.getColorIndex());

            // Adding the hexagon to the HBox
            startingRow.getChildren().add(hexagon);
        }
        // Adding the starting row to the GridPane
        pane.addRow(rows, startingRow);
        // Setting current row state
        rows = 1;
    }

    /**
     * Adds a row of starting hexagons to a GridPane. The colors
     * of the hexagons are randomized.
     * @param pane the GridPane to add the starting row to.
     */
    private void addStartingRow(GridPane pane){
        Random random = new Random();

        // Setting up the HBox to hold the hexagons
        HBox startingRow = new HBox();
        startingRow.setAlignment(Pos.CENTER);
        startingRow.setPadding(new Insets(0,0,-5 * currentScale,0)); // T R B L (bottom = -5 * SCALE to remove padding for next row down)

        for(int i = 0; i < startingRowLength; i++) {
            // Getting random color
            int colorIndex = random.nextInt(3);
            // Creating the hexagon
            Hexagon hexagon = new Hexagon(colorIndex, currentScale);
            hexagon.setRow(0);
            hexagons.add(hexagon);

            // Updating the controller
            boardController.addColorIndex(hexagon.getColorIndex());

            // Adding the hexagon to the HBox
            startingRow.getChildren().add(hexagon);
        }
        // Adding the starting row to the GridPane
        pane.addRow(rows, startingRow);
        // Setting current row state
        rows = 1;
    }

    /**
     * Adds a row of hexagons to the grid pane. If the previous row was
     * colored, this method will ensure proper coloring of the hexagons
     * in this row.
     * @param pane the GridPane object to add the row of hexagons to.
     */
    private void addNextRow(GridPane pane){
        if(!updating.get()){ return; }
        int startingParentIndex = boardController.getRowIndex(rows);

        HBox nextRow = new HBox();
        nextRow.setAlignment(Pos.CENTER);
        nextRow.setPadding(new Insets(0,0,-5 * currentScale,0)); // T R B L

        for(int i = 0; i < startingRowLength - rows; i++){
            int leftParent = startingParentIndex + i;
            int rightParent = leftParent + 1;
            int leftColorIndex = boardController.getColorFromIndex(leftParent);
            int rightColorIndex = boardController.getColorFromIndex(rightParent);

            // Creating a transparent hexagon
            Hexagon hexagon = new Hexagon(BoardController.NOT_A_COLOR, currentScale);
            hexagon.setRow(rows);

            // Getting color based on parents
            int colorIndex = hexagon.getNextColorIndex(leftColorIndex, rightColorIndex);
            hexagon.setColor(colorIndex);

            // Adding the hexagon to the row
            nextRow.getChildren().add(hexagon);

            // Updating the controller
            boardController.addColorIndex(hexagon.getColorIndex());
            hexagons.add(hexagon);
        }
        pane.addRow(rows, nextRow);
        rows++;
    }

    /**
     * Sets a single row of the board to the appropriate colors and updates the colorIndices
     * accordingly. For rows other than the first, the parents are checked to determine the
     * color of each hexagon.
     * @param currentRow the row to set the correct colors on.
     */
    protected static void setRowColors(int currentRow){
        if(currentRow == 0){ // Updating the first row doesn't require looking at parents
            for(int index = 0; index < startingRowLength; index++){
                int colorIndex = hexagons.get(index).getColorIndex();
                boardController.setColorIndex(index, colorIndex);
            }
        }else { // All other rows, need to check parents for colors
            int leftParentIndex;
            int hexagonIndex;
            int rowStartIndex = boardController.getRowIndex(currentRow);
            for (int i = 0; i < startingRowLength - currentRow; i++) { // getting the leftmost parent

                if (i >= hexagons.size()) { break; } // Array out of bounds protection

                // Getting next left parent index
                leftParentIndex = rowStartIndex + i;
                // Formula to derive the current right child hexagon to the left parent
                hexagonIndex = leftParentIndex + startingRowLength - currentRow + 1;
                // Getting parent colors
                int leftParentColor = boardController.getColorFromIndex(leftParentIndex);
                int rightParentColor = boardController.getColorFromIndex(leftParentIndex + 1);
                // Getting old color from color index with derived hexagon index
                int oldColorIndex = boardController.getColorFromIndex(hexagonIndex);
                // Getting current hexagon
                Hexagon currentHexagon = hexagons.get(hexagonIndex);
                // Getting calculated new color from the current hexagon
                int newColorIndex = currentHexagon.getNextColorIndex(leftParentColor, rightParentColor);
                // If color changed, updating arrays
                if (newColorIndex != oldColorIndex) {
                    boardController.setColorIndex(hexagonIndex, newColorIndex);
                    hexagons.get(hexagonIndex).setColor(newColorIndex);
                }
            }
        }
    }

    /**
     * @return the GridPane object representing the board.
     */
    protected GridPane getBoard(){
        return board;
    }

    /**
     * Clears the board.
     */
    protected void clearBoard(){
        for (Hexagon hexagon : hexagons) {
            hexagon.setColor(-1);
        }
        boardController.clearBoard();
    }

    /**
     * Sets the updating flag to true.
     */
    protected static void setUpdating(){
        updating.compareAndSet(false, true);
    }

    /**
     * Updates the colors of the hexagons on the board.
     * Can be called statically.
     */
    protected synchronized static void updateBoard(){
        if(!updating.get()){ return; } // if updating flag is not set, then return

        // Animation Thread allows for animation flow control separate from user input (main thread)
        animationThread = new Thread(()->{
            // Finding first hexagon that changed color
            int firstPosChanged = 0;

            for(int i = 0; i < boardController.getIndexSize(); i++){
                Hexagon hexagon = hexagons.get(i);
                int hexagonColor = hexagon.getColorIndex();
                int indexColor = boardController.getColorFromIndex(i);
                if(hexagonColor != indexColor){
                    boardController.setColorIndex(i, hexagonColor);
                    firstPosChanged = i;
                    break;
                }
            }

            int startingRow = hexagons.get(firstPosChanged).getRow(); // Getting starting row to update (no need to update rows not changing)
            if(startingRow != 0){ // if not top row, incrementing to children of the row that changed
                startingRow++;
            }
            // Updating each row
            for(int row = startingRow; row < rows; row++){
                setRowColors(row);
                // "Slowing" for animation purposes
                try {
                    Thread.sleep(animationSleepMillis);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // Clearing updating flag
            updating.compareAndSet(true, false);
        });
        animationThread.start();
    }

    @Override
    public void onChanged(Change change) {
            //change.next();
            //int changeIndex = change.getFrom();
            //System.out.println("From: " + changeIndex); // prints the index of the changed hexagon
    }

    /**
     * Updates animation speed based on a slider value from 0 to 1.
     * @param sliderValue slider value to update speed to.
     */
    protected void updateAnimationSpeed(double sliderValue){
        animationSleepMillis = (int) (MAX_ANIMATION_SLEEP_MILLIS * sliderValue);
    }

    protected void changeBoardSize(int newSize){
        boardController.setStartingRowLength(newSize);
        startingRowLength = newSize;
        setScaleFromSize(newSize);
        setupBoard();
    }

    /**
     * Maps the size to scale on an equation that approximates the largest scale
     * for sizes that will fit in the default window size.
     * The equation used is 12 / [ (x + 0.5) log(e) ]
     * This was determined by experimentally getting (size, scale) points, then
     * using desmos to map an equation onto the points.
     * @param size the number of hexagons in the starting row
     */
    private void setScaleFromSize(int size){
        double logpart = Math.log10(Math.E);
        double denomenator = (size + 0.5) * logpart;
        currentScale = 12 / denomenator;
    }

    /**
     * @return the starting row length value.
     */
    protected int getStartingRowLength(){
        return boardController.getStartingRowLength();
    }

    /**
     * @return the minimum starting row length value.
     */
    protected int getMinStartingRowLength(){
        return boardController.getMinStartingRowLength();
    }

    /**
     * @return the maximum starting row length value.
     */
    protected int getMaxStartingRowLength(){
        return boardController.getMaxStartingRowLength();
    }
}
