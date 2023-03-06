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
     * @param team white or black
     * @param row the x coordinate
     * @param col the y coordinate
     */
    public Bishop(Team team, int row, int col) {
        super(letter, team, row, col);
    }

    /**
     * Bishop move:
     * @param board the chessboard
     * @return a list of possible squares to move to
     */
    public ArrayList<Point> getMoves(Board board) {
        // TODO

        return null;
    }
}
