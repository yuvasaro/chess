import java.util.ArrayList;
import java.awt.Point;

/**
 * King class
 */
public class King extends Piece {
    // String representation
    private static final String LETTER = "k";

    // Path to piece image
    private static final String IMAGE = "assets/king_%s.png";

    // Whether the king has moved at least once
    private boolean hasMoved;

    /**
     * King constructor
     * @param board the chessboard
     * @param team white or black
     * @param row the x coordinate
     * @param col the y coordinate
     */
    public King(Board board, Team team, int row, int col) {
        super(board, LETTER, IMAGE, team, row, col);
        hasMoved = false;
    }

    /**
     * King constructor that duplicates another king
     * @param otherKing the other king to copy
     * @param otherBoard the board of the other king
     */
    public King(King otherKing, Board otherBoard) {
        super(otherKing, otherBoard);
    }

    /**
     * King move: 1 square in all directions
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
     * Gets a list containing the possible square (or empty list)
     * @param dx x direction
     * @param dy y direction
     * @return a list of one or zero possible moves in the given direction
     */
    private ArrayList<Point> getMovesInDirection(int dx, int dy) {
        ArrayList<Point> possibleMoves = new ArrayList<>();
        Point coords = getCoords();
        Board board = getBoard();

        Point possibleCoords = new Point(coords.x + dx, coords.y + dy);
        Piece otherPiece = board.getPiece(possibleCoords);

        // Add square if there is no piece or the piece is on the opposite team
        if (board.isInBounds(possibleCoords)) {
            if (otherPiece == null) {
                possibleMoves.add(possibleCoords);
            } else if (this.getTeam() != otherPiece.getTeam()) {
                possibleMoves.add(possibleCoords);
            }
        }

        return possibleMoves;
    }

    /**
     * Returns whether the king has moved
     * @return whether the king has moved
     */
    public boolean hasMoved() {
        return hasMoved;
    }

    /**
     * Moves the king to its new square and sets hasMoved to true
     * @param newCoords the new coordinates to move to
     */
    public void move(Point newCoords) {
        super.move(newCoords);
        if (!hasMoved) {
            hasMoved = true;
        }
    }
}
