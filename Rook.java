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
     * @param team white or black
     * @param row the x coordinate
     * @param col the y coordinate
     */
    public Rook(Team team, int row, int col) {
        super(letter, team, row, col);
    }

    /**
     * Rook move:
     * @param board the chessboard
     * @return a list of possible squares to move to
     */
    public ArrayList<Point> getMoves(Board board) {
        // TODO

        return null;
    }
}
