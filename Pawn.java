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
        Point oneSquareAhead = new Point(coords.x + 1, coords.y);
        if (board.getPiece(oneSquareAhead) == null) {
            possibleMoves.add(oneSquareAhead);
        }

        // Two squares ahead
        if (!hasMoved) {
            Point twoSquaresAhead = new Point(coords.x + 2, coords.y);
            if (board.getPiece(twoSquaresAhead) == null) {
                possibleMoves.add(twoSquaresAhead);
            }
        }

        // Capture diagonally one square
        Point leftDiagonal = new Point(coords.x + 1, coords.y - 1);
        Piece leftDiagonalPiece = board.getPiece(leftDiagonal);
        Point rightDiagonal = new Point(coords.x + 1, coords.y + 1);
        Piece rightDiagonalPiece = board.getPiece(rightDiagonal);

        // Check that there is a piece diagonally that is on the opposite team
        if (leftDiagonalPiece instanceof Piece && 
                leftDiagonalPiece.getTeam() != this.getTeam()) {
            possibleMoves.add(leftDiagonal);
        }
        if (rightDiagonalPiece instanceof Piece && 
                rightDiagonalPiece.getTeam() != this.getTeam()) {
            possibleMoves.add(rightDiagonal);
        }

        return possibleMoves;
    }
}
