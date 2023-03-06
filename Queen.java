import java.util.ArrayList;
import java.awt.Point;

/**
 * Queen class
 */
public class Queen extends Piece {
    // String representation
    private static final String letter = "q";

    /**
     * Queen constructor
     * @param team white or black
     * @param row the x coordinate
     * @param col the y coordinate
     */
    public Queen(Team team, int row, int col) {
        super(letter, team, row, col);
    }

    /**
     * Queen move:
     * @param board the chessboard
     * @return a list of possible squares to move to
     */
    public ArrayList<Point> getMoves(Board board) {
        // TODO

        return null;
    }
}

