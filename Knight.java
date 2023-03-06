import java.util.ArrayList;
import java.awt.Point;

/**
 * Knight class
 */
public class Knight extends Piece {
    // String representation
    private static final String letter = "n";

    /**
     * Knight constructor
     * @param team white or black
     * @param row the x coordinate
     * @param col the y coordinate
     */
    public Knight(Team team, int row, int col) {
        super(letter, team, row, col);
    }

    /**
     * Knight move:
     * @param board the chessboard
     * @return a list of possible squares to move to
     */
    public ArrayList<Point> getMoves(Board board) {
        // TODO

        return null;
    }
}
