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
     * @param board the chessboard
     * @param team white or black
     * @param row the x coordinate
     * @param col the y coordinate
     */
    public Queen(Board board, Team team, int row, int col) {
        super(board, letter, team, row, col);
    }

    /**
     * Queen move:
     * @return a list of possible squares to move to
     */
    public ArrayList<Point> getMoves() {
        // TODO

        return null;
    }
}

