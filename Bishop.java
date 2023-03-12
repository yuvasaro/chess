import java.util.ArrayList;
import java.awt.Point;

/**
 * Bishop class
 */
public class Bishop extends Piece {
    // String representation
    private static final String letter = "b";

    /**
     * Bishop constructor
     * @param board the chessboard
     * @param team white or black
     * @param row the x coordinate
     * @param col the y coordinate
     */
    public Bishop(Board board, Team team, int row, int col) {
        super(board, letter, team, row, col);
    }

    /**
     * Bishop move:
     * @return a list of possible squares to move to
     */
    public ArrayList<Point> getMoves() {
        // TODO

        return null;
    }
}
