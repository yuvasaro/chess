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
     * @param board the chessboard
     * @param team white or black
     * @param row the x coordinate
     * @param col the y coordinate
     */
    public King(Board board, Team team, int row, int col) {
        super(board, letter, team, row, col);
    }

    /**
     * King move:
     * @return a list of possible squares to move to
     */
    public ArrayList<Point> getMoves() {
        // TODO

        return null;
    }
}
