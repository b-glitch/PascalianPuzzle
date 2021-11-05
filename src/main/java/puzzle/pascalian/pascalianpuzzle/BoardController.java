package puzzle.pascalian.pascalianpuzzle;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.ArrayList;

public class BoardController {

    private final int MIN_STARTING_ROW_LENGTH = 2;
    private final int MAX_STARTING_ROW_LENGTH = 60; // 60
    private int pascalinacciCount;
    private static int startingRowLength = 60; //53
    public static final int NOT_A_COLOR = -1;
    private static ArrayList<Integer> colorIndices;
    private ObservableList<Integer> obsColorIndices;

    public BoardController(){

        colorIndices = new ArrayList<>();
        obsColorIndices = FXCollections.observableArrayList(colorIndices);

        setPascalinacciCount();
        System.out.println(pascalinacciCount);
    }

    /**
     * Checks if the given index is the middle of the starting row. For
     * an odd number of hexagons, there is one value which will return true,
     * for an even number of hexagons, the two middle positions will return true.
     * @param index the index to check
     * @return true if the given index is in the middle of the starting row.
     */
    protected boolean checkIfMiddleIndex(int index){
        boolean isMiddle = false;
        int iHalf = (startingRowLength / 2);
        double dHalf = (double) startingRowLength / 2.0;
        if(index == iHalf){ // true for "middle" position. If odd, will be only middle position
            isMiddle = true;
        }else if(index == iHalf - 1 && iHalf == dHalf){ // for even, true for second "middle" position
            isMiddle = true;
        }
        return isMiddle;
    }

    /**
     * Calculates which row the given index is in.
     * @param indexToFind the index to find the row of.
     * @return the row that the give index is in.
     */
    protected static int getRowIndex(int indexToFind){
        int index = 0;
        for(int row = 1; row < indexToFind; row++){
            index += startingRowLength - row + 1;
        }
        return index;
    }

    protected int getIndexSize(){
        //return colorIndices.size();
        return obsColorIndices.size();
    }

    protected void emptyIndices(){
        //colorIndices.clear();
        obsColorIndices.clear();
    }

    /**
     * Clears the board
     */
    protected void clearBoard(){
        for(int i = 0; i < obsColorIndices.size(); i++){
            //colorIndices.set(i, -1);
            obsColorIndices.set(i, -1);
        }
    }

    protected void addColorIndex(int colorIndex){
        //colorIndices.add(colorIndex);
        obsColorIndices.add(colorIndex);
    }

    protected int getColorFromIndex(int index){
        //return colorIndices.get(index);
        return obsColorIndices.get(index);
    }

    protected void setColorIndex(int index, int color){
        //colorIndices.set(index, color);
        obsColorIndices.set(index, color);
    }

    protected int getStartingRowLength(){
        return startingRowLength;
    }

    protected void setStartingRowLength(int length){
        if(length <= MIN_STARTING_ROW_LENGTH) {
            startingRowLength = MIN_STARTING_ROW_LENGTH;
        }else if(length >= MAX_STARTING_ROW_LENGTH){
            startingRowLength = MAX_STARTING_ROW_LENGTH;
        }else {
            startingRowLength = length;
        }
    }

    protected void watchList(ListChangeListener listener){
        obsColorIndices.addListener(listener);
    }

    protected int getPascalinacciNumber(int n){
        if(n == 0) {
            return 2;
        }
        else{
            return (getPascalinacciNumber(n - 1)) * 3 - 2;
        }
    }

    protected int getMaxStartingRowLength(){
        return MAX_STARTING_ROW_LENGTH;
    }

    protected int getMinStartingRowLength(){
        return MIN_STARTING_ROW_LENGTH;
    }

    private void setPascalinacciCount(){
        int n = -1;
        int p;
        do{
            p = getPascalinacciNumber(++n);
        }while(p <= MAX_STARTING_ROW_LENGTH);
        pascalinacciCount = --n;
    }

    protected int getPascalinacciCount(){
        return pascalinacciCount;
    }
}
