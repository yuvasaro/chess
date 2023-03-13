import java.util.ArrayList;
import java.awt.Point;


/**
 * Knight class
 */
public class Knight extends Piece {
    // String representation
    private static final String letter = "n";

    /**
     * Knight constructor
     * @param board the chessboard
     * @param team white or black
     * @param row the x coordinate
     * @param col the y coordinate
     */
    public Knight(Board board, Team team, int row, int col) {
        super(board, letter, team, row, col);
    }

    /**
     * Knight move: L-shape; 2 squares one way, 1 square perpendicular
     * @return a list of possible squares to move to
     */
    public ArrayList<Point> getMoves() {
        ArrayList<Point> possibleMoves = new ArrayList<>();
        Point coords = getCoords();
        Board board = getBoard();

        // All 8 possible squares
        possibleMoves.add(new Point(coords.x - 2, coords.y + 1));
        possibleMoves.add(new Point(coords.x - 2, coords.y - 1));
        possibleMoves.add(new Point(coords.x + 2, coords.y + 1));
        possibleMoves.add(new Point(coords.x + 2, coords.y - 1));
        possibleMoves.add(new Point(coords.x - 1, coords.y + 2));
        possibleMoves.add(new Point(coords.x + 1, coords.y + 2));
        possibleMoves.add(new Point(coords.x - 1, coords.y - 2));
        possibleMoves.add(new Point(coords.x + 1, coords.y - 2));

        for (int i = 0; i < possibleMoves.size(); i++) {
            // Remove out of bounds moves
            if (!board.isInBounds(possibleMoves.get(i))) {
                possibleMoves.remove(i);
                i--;
                continue;
            }

            Piece otherPiece = board.getPiece(possibleMoves.get(i));

            // Empty square is valid
            if (otherPiece == null) {
                continue;
            }

            // Cannot go to square with piece of same team
            if (otherPiece.getTeam() == this.getTeam()) {
                possibleMoves.remove(i);
                i--;
            }
        }

        return possibleMoves;
    }
}
