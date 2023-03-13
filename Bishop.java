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
     * Bishop move: Diagonal; NW, NE, SW, SE
     * @return a list of possible squares to move to
     */
    public ArrayList<Point> getMoves() {
        ArrayList<Point> possibleMoves = new ArrayList<>();
        
        // Northwest
        possibleMoves.addAll(getMovesInDirection(-1, 1));
        // Northeast
        possibleMoves.addAll(getMovesInDirection(1, 1));
        // Southwest
        possibleMoves.addAll(getMovesInDirection(-1, -1));
        // Southeast
        possibleMoves.addAll(getMovesInDirection(1, -1));

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
