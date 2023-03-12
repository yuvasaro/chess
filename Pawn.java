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

    // White moves forward, black moves backward
    private int moveDir;

    /**
     * Pawn constructor
     * @param board the chessboard
     * @param team white or black
     * @param row the x coordinate
     * @param col the y coordinate
     */
    public Pawn(Board board, Team team, int row, int col) {
        super(board, letter, team, row, col);
        hasMoved = false;
        moveDir = (team == Team.WHITE) ? 1 : -1;
    }

    /**
     * Pawn move: 1 square forward (2 squares if first turn), capture
     * diagonally 1 square, promotion at the opposite side
     * @return a list of possible squares to move to
     */
    public ArrayList<Point> getMoves() {
        ArrayList<Point> possibleMoves = new ArrayList<>();
        Point coords = getCoords();
        Board board = getBoard();

        // One square ahead
        Point oneSquareAhead = new Point(coords.x, coords.y + moveDir);
        if (board.getPiece(oneSquareAhead) == null) {
            possibleMoves.add(oneSquareAhead);
        }

        // Two squares ahead
        if (!hasMoved) {
            Point twoSquaresAhead = new Point(coords.x, coords.y + 2 * moveDir);
            if (board.getPiece(twoSquaresAhead) == null) {
                possibleMoves.add(twoSquaresAhead);
            }
        }

        // Capture diagonally one square
        Point leftDiagonal = new Point(coords.x - 1, coords.y + moveDir);
        Piece leftDiagonalPiece = board.getPiece(leftDiagonal);
        Point rightDiagonal = new Point(coords.x + 1, coords.y + moveDir);
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

    /**
     * Sets hasMoved to true when the pawn has moved
     */
    public void setMoved() {
        hasMoved = true;
    }
}
