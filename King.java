import java.util.ArrayList;
import java.awt.Point;

/**
 * King class
 */
public class King extends Piece {
    // String representation
    private static final String letter = "k";

    /**
     * King constructor
     * @param team white or black
     * @param row the x coordinate
     * @param col the y coordinate
     */
    public King(Team team, int row, int col) {
        super(letter, team, row, col);
    }

    /**
     * King move:
     * @param board the chessboard
     * @return a list of possible squares to move to
     */
    public ArrayList<Point> getMoves(Board board) {
        // TODO

        return null;
    }
}
