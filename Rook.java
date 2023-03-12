import java.util.ArrayList;
import java.awt.Point;

/**
 * Rook class
 */
public class Rook extends Piece {
    // String representation
    private static final String letter = "r";

    /**
     * Rook constructor
     * @param board the chessboard
     * @param team white or black
     * @param row the x coordinate
     * @param col the y coordinate
     */
    public Rook(Board board, Team team, int row, int col) {
        super(board, letter, team, row, col);
    }

    /**
     * Rook move:
     * @return a list of possible squares to move to
     */
    public ArrayList<Point> getMoves() {
        // TODO

        return null;
    }
}
