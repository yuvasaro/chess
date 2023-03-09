import java.awt.Point;
import java.util.regex.*;
import java.util.Map;

/**
 * A class that handles chess move input
 */
public class MoveHandler {
    /* Indices of components of the move notation in a list returned by parseMove() 
    
    Format: [piece (null for pawn), letter/number (or null), x or null, square, =<piece promoted to> or null, +/# (or null)] */

    // String representation of the piece being moved
    public static final int PIECE_INDEX = 0;

    /* In case two of the same pieces can move to the same square, we specify
    with a letter or number the piece on a certain row or column (for example,
    "Rad1" means "Rook on column a to square d1" 
    
    This string stores that letter or number */
    public static final int SPECIFIER_INDEX = 1;

    // Capture - "x" or null
    public static final int CAPTURE_INDEX = 2;

    // 2 character string (ex. "e4")
    public static final int SQUARE_INDEX = 3;

    // Promotion (ex. "=Q")
    public static final int PROMOTION_INDEX = 4;

    // + or #
    public static final int CHECK_MATE_INDEX = 5;

    // Regex pattern
    public static final String PATTERN = 
        "(^[NBRQK])?([a-h])?(x)?([a-h][1-8])(?:([=][NBQR]))?([+#])?";

    public static final Map<String, Integer> letterCoordMapping = Map.of(
        "a", 0,
        "b", 1,
        "c", 2,
        "d", 3,
        "e", 4,
        "f", 5,
        "g", 6,
        "h", 7
    );

    /**
     * Uses regular expressions to extract the piece and square (and check/
     * checkmate) of the move
     */
    public static String[] parseMove(String move) {
        // Move ex. Ndxc7+ (Knight on d takes c7 check)
        // Piece = "N", specifier = "d", capture = "x", square = "c7", 
        // promotion = null, check/mate = "+"
        String[] moveComponents = new String[6];
        Pattern pattern = Pattern.compile(PATTERN);
        Matcher matcher = pattern.matcher(move);

        // Add move components to list
        while (matcher.find()) {
            moveComponents[PIECE_INDEX] = matcher.group(1);
            moveComponents[SPECIFIER_INDEX] = matcher.group(2);
            moveComponents[CAPTURE_INDEX] = matcher.group(3);
            moveComponents[SQUARE_INDEX] = matcher.group(4);
            moveComponents[PROMOTION_INDEX] = matcher.group(5);
            moveComponents[CHECK_MATE_INDEX] = matcher.group(6);
        }

        return moveComponents;
    }

    /**
     * Converts a string representation of a square to board coordinates
     * @param square the square string
     * @return the coordinates of the given square on the board
     */
    public static Point toCoords(String square) {
        // Extract letter and number (ex. "e4" --> "e", "4")
        String letter = square.substring(0, 1);
        String number = square.substring(1);

        // Convert letter and number to coordinates
        int letterCoord = letterCoordMapping.get(letter);
        int numberCoord = Integer.parseInt(number) - 1;

        return new Point(numberCoord, letterCoord);
    }

    /**
     * Converts a pair of board coordinates to a string of the square
     * @param coords the coordinates of the square
     * @return the string representation of the square
     */
    public static String toSquare(Point coords) {
        // Get the corresponding letter for the x coord
        String letter = "";
        for (String key : letterCoordMapping.keySet()) {
            if (letterCoordMapping.get(key) == coords.x) {
                letter = key;
                break;
            }
        }
        // Number is y coord + 1
        int number = coords.y + 1;
        
        // Return string of square (ex. "e4")
        return letter + number;
    }

    /**
     * Main method for testing
     * @param args
     */
    public static void main(String[] args) {
        String[] move = parseMove("Ndxc7+");
        for (String component : move) {
            System.out.println(component);
        }

        System.out.println();

        Point coords = toCoords("a1");
        System.out.println(coords.x + ", " + coords.y);
        System.out.println(toSquare(coords));
    }
}
