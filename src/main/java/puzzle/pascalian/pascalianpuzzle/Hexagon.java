package puzzle.pascalian.pascalianpuzzle;

import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

/**
 * This class represents a Hexagon with additional functionality
 * to handle color changing, scaling, and user-interactive color shifting.
 *
 * @author Kris Rangel
 */
public class Hexagon extends Polygon {

    private double scale;
    private Color fillColor;
    private int colorIndex;
    private Color borderColor;
    private int row;
    // The points that make up the hexagon (these will be adjusted if the hexagon is scaled)
    private Double[] points = new Double[] {
            20.0,  5.0,
            30.0,  5.0,
            35.0, 15.0,
            30.0, 25.0,
            20.0, 25.0,
            15.0, 15.0
    };

    /**
     * Constructor.
     * @param colorIndex the color to make the Hexagon
     *                   0-Blue, 1-Red, 2-Yellow, or -1 for transparent.
     * @param scale the scale of the hexagon relative to default of 1.0.
     */
    public Hexagon(int colorIndex, double scale){
        super();
        // Initial values
        setColor(colorIndex);
        scale(scale);
        row = 0;
        borderColor = Color.BLACK;
        strokeProperty().setValue(borderColor);

        // Rotating the hexagon so sides are on the left and right
        rotateProperty().set(90);
        // Hexagon On click event
        setOnMouseClicked(e-> shiftColor(e.getButton()));
    }

    /**
     * Adjusts the scale of the hexagon.
     * @param scale the magnitude to scale the hexagon by
     *              (1.0 will be default scaling).
     */
    private void scale(double scale){
        this.scale = scale;
        double borderWidth = 1.0;
        // Clearing any previous points
        this.getPoints().clear();
        // Setting scale
        this.strokeWidthProperty().set(borderWidth * scale); // Adjusting border width scaling
        for(int p = 0; p < points.length; p++){
            this.getPoints().add(points[p] * scale); // Adding newly scaled points
        }
    }

    /**
     * Calculates the color of a child hexagon given the colors of the
     * parents. (0-Blue, 1-Red, 2-Yellow, or -1 for Transparent)
     * If the parents are different (are 2 of 0,1,2), the child
     * will be the color neither parents are. If the parents are
     * the same (e.i. 1 and 1) then the child will be the same as
     * the parents. If either parent is transparent, the child will
     * be transparent.
     * @param leftColorIndex the color index of the left parent
     * @param rightColorIndex the color index of the right parent
     * @return
     */
    public int getNextColorIndex(int leftColorIndex, int rightColorIndex){
        int nextColorIndex = -1;
        if(leftColorIndex == -1 || rightColorIndex == -1) {
            return colorIndex;
        }
        if(leftColorIndex == rightColorIndex){
            nextColorIndex = leftColorIndex;
        }else {
            int parentCompositeIndex = leftColorIndex + rightColorIndex;
            switch (parentCompositeIndex){
                case 1 -> { // 0 and 1 are parents
                    nextColorIndex = 2;
                    break;
                }
                case 2 -> { // 0 and 2 are parents
                    nextColorIndex = 1;
                    break;
                }
                case 3 -> { // 1 and 2 are parents
                    nextColorIndex = 0;
                    break;
                }
            }
        }

        return nextColorIndex;
    }

    /**
     * Sets the color of the hexagon.
     * @param index the color index of the hexagon:
     *             0-Blue, 1-Red, 2-Yellow, or -1 for Transparent
     */
    public void setColor(int index){
        this.colorIndex = index;
        switch (index) {

            case -1 -> {
                fillColor = Color.TRANSPARENT;
            }
            case 0 -> {
                fillColor = Color.BLUE;
                break;
            }
            case 1 -> {
                fillColor = Color.RED;
                break;
            }
            case 2 -> {
                fillColor = Color.YELLOW;
                break;
            }
            default -> {
                fillColor = Color.BLACK;
                System.out.println("Unexpected value: " + colorIndex);
            }
        }
        fillProperty().setValue(fillColor);
    }

    public int getColorIndex(){
        return colorIndex;
    }

    /**
     * Changes the color from Blue->Red->Yellow->Blue with a left click and
     * from Blue->Yellow->Red->Blue (reverse) with a right click.
     * @param mb the mouse button clicked: MouseButton.PRIMARY (left click) or
     *           MouseButton.SECONDARY (right click) are valid options. Any
     *           other input does nothing.
     */
    private void shiftColor(MouseButton mb){
        int oldColorIndex = colorIndex;
        if(mb == MouseButton.PRIMARY){
            setColor( (colorIndex + 1) % 3);
        }else if (mb == MouseButton.SECONDARY){
            setColor( (colorIndex +2) % 3);
        }else{
            return;
        }

        BoardView.setUpdating();
        BoardView.updateBoard();
    }

    /**
     * Sets the current row value of the hexagon.
     * @param row the row index of the hexagon.
     */
    public void setRow(int row){ this.row = row; }

    /**
     * Gets the current row value of the hexagon.
     * @return the current row value stored in the hexagon.
     */
    public int getRow(){ return this.row; }
}
