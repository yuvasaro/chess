import java.awt.Point;

/**
 * A class that handles chess move input
 */
public class MoveHandler {
    /* Indices of components of the move notation in a list returned by parseMove() 
    
    Format: ["piece", "letter/number (or null)", "square", "+/# (or null)"] */

    // String representation of the piece being moved
    private static final int PIECE_INDEX = 0;

    /* In case two of the same pieces can move to the same square, we specify
    with a letter or number the piece on a certain row or column (for example,
    "Rad1" means "Rook on column a to square d1" 
    
    This string stores that letter or number */
    private static final int PIECE_SPECIFIER_INDEX = 1;

    // 2 character string (ex. "e4")
    private static final int SQUARE_INDEX = 2;

    // + or #
    private static final int CHECK_MATE_INDEX = 3;

    /**
     * Uses regular expressions to extract the piece and square (and check/
     * checkmate) of the move
     */
    public static String[] parseMove(String move) {
        // TODO
        return null;
    }

    /**
     * Converts a string representation of a square to board coordinates
     * @param square the square string
     * @return the coordinates of the given square on the board
     */
    public static Point toCoords(String square) {
        // TODO
        return null;
    }
}
