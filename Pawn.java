import java.util.ArrayList;
import java.awt.Point;

/**
 * Pawn class
 */
public class Pawn extends Piece {
    // String representation
    private static final String letter = "p";

    // Whether the pawn has moved at least once
    private boolean hasMoved;

    /**
     * Pawn constructor
     * @param team white or black
     * @param row the x coordinate
     * @param col the y coordinate
     */
    public Pawn(Team team, int row, int col) {
        super(letter, team, row, col);
        hasMoved = false;
    }

    /**
     * Pawn move: 1 square forward (2 squares if first turn), capture
     * diagonally 1 square, promotion at the opposite side
     * @param board the chessboard
     * @return a list of possible squares to move to
     */
    public ArrayList<Point> getMoves(Board board) {
        ArrayList<Point> possibleMoves = new ArrayList<>();
        Point coords = getCoords();

        // One square ahead
        Point oneSquareAhead = new Point(coords.x, coords.y + 1);
        if (board.getPiece(oneSquareAhead) == null) {
            possibleMoves.add(oneSquareAhead);
        }

        // Two squares ahead
        if (!hasMoved) {
            Point twoSquaresAhead = new Point(coords.x, coords.y + 2);
            if (board.getPiece(twoSquaresAhead) == null) {
                possibleMoves.add(twoSquaresAhead);
            }
        }

        // Capture diagonally one square
        Point leftDiagonal = new Point(coords.x - 1, coords.y + 1);
        Point rightDiagonal = new Point(coords.x + 1, coords.y + 1);
        if (board.getPiece(leftDiagonal) != null) {
            possibleMoves.add(leftDiagonal);
        }
        if (board.getPiece(rightDiagonal) != null) {
            possibleMoves.add(rightDiagonal);
        }

        return null;
    }
}
