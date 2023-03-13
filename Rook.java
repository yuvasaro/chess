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
     * Rook move: Horizontal and vertical (N, W, S, E)
     * @return a list of possible squares to move to
     */
    public ArrayList<Point> getMoves() {
        ArrayList<Point> possibleMoves = new ArrayList<>();
        
        // North
        possibleMoves.addAll(getMovesInDirection(0, 1));
        // West
        possibleMoves.addAll(getMovesInDirection(-1, 0));
        // South
        possibleMoves.addAll(getMovesInDirection(0, -1));
        // East
        possibleMoves.addAll(getMovesInDirection(1, 0));

        return possibleMoves;
    }

    /**
     * Gets a list of possible squares to move to in one direction
     * @param dx x direction
     * @param dy y direction
     * @return a list of possible moves in the given direction
     */
    private ArrayList<Point> getMovesInDirection(int dx, int dy) {
        ArrayList<Point> possibleMoves = new ArrayList<>();
        Point coords = getCoords();
        Board board = getBoard();

        Point possibleCoords = new Point(coords.x + dx, coords.y + dy);
        Piece otherPiece = board.getPiece(possibleCoords);

        // Add all squares in path while there are no pieces in the way
        while (otherPiece == null && board.isInBounds(possibleCoords)) {
            possibleMoves.add(possibleCoords);

            // Get next point and piece in the current direction
            possibleCoords = new Point(
                possibleCoords.x + dx, possibleCoords.y + dy);
            otherPiece = board.getPiece(possibleCoords);
        }

        // Check the piece that the while loop encountered
        if (board.isInBounds(possibleCoords)) {
            // Add possible move if other piece is on the opposite team
            if (this.getTeam() != otherPiece.getTeam()) {
                possibleMoves.add(possibleCoords);
            }
        }

        return possibleMoves;
    }
}
