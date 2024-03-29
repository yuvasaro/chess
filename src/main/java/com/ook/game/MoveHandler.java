package com.ook.game;

import java.awt.Point;
import java.util.regex.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Map;

/**
 * A class that handles chess move input
 */
public class MoveHandler {
    /* Indices of components of the move notation in a list returned by
    parseMove() 
    
    Format: [piece (null for pawn), letter/number (or null), x or null, square,
    =<piece promoted to> or null, castle (O-O or O-O-O) or null, +/# (or null)] 
    */

    // String representation of the piece being moved
    public static final int PIECE = 0;

    /* In case two of the same pieces can move to the same square, we specify
    with a letter or number the piece on a certain row or column (for example,
    "Rad1" means "Rook on column a to square d1" 
    
    This string stores that letter or number */
    public static final int SPECIFIER = 1;

    // Capture - "x" or null
    public static final int CAPTURE = 2;

    // 2 character string (ex. "e4")
    public static final int SQUARE = 3;

    // Promotion (ex. "=Q")
    public static final int PROMOTION = 4;

    // Castle (ex. "O-O")
    public static final int CASTLE = 5;

    // + or #
    public static final int CHECK_MATE = 6;

    // Regex pattern
    public static final String PATTERN =
        "([NBRQK])?([a-h]|[1-8])?(x)?([a-h][1-8])(?:(=[NBQR]))?([+#])?";

    // Castle regex pattern
    private static final String CASTLE_PATTERN = "(O-O)(-O)?([+#])?";

    // Map of letters to coordinates
    private static final Map<String, Integer> letterCoordMapping = Map.of(
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
        String[] moveComponents = new String[7];
        Pattern pattern = Pattern.compile(PATTERN);
        Pattern castlePattern = Pattern.compile(CASTLE_PATTERN);
        Matcher matcher = pattern.matcher(move);
        Matcher castleMatcher = castlePattern.matcher(move);

        // Castle pattern takes priority
        if (castleMatcher.find()) {
            String castleString = castleMatcher.group(1); // O-O
            if (castleMatcher.group(2) != null) { // O-O-O
                castleString += castleMatcher.group(2);
            }
            moveComponents[CASTLE] = castleString;
            moveComponents[CHECK_MATE] = castleMatcher.group(3);
        } 
        // Else regular move components
        else if (matcher.find()) {
            moveComponents[PIECE] = matcher.group(1);
            moveComponents[SPECIFIER] = matcher.group(2);
            moveComponents[CAPTURE] = matcher.group(3);
            moveComponents[SQUARE] = matcher.group(4);
            moveComponents[PROMOTION] = matcher.group(5);
            moveComponents[CHECK_MATE] = matcher.group(6);
        }

        return moveComponents;
    }

    /**
     * Checks whether the move components theoretically represent a valid move
     * @param moveComponents the array of move components
     * @return whether the move is theoretically valid
     */
    public static boolean validMoveComponents(String moveInput, 
            String[] moveComponents) {
        // Entire input must be just the move (avoids confusion and bugs)
        String joined = Stream.of(moveComponents)
            .filter(s -> s != null && !s.isEmpty())
            .collect(Collectors.joining(""));;
        if (!joined.equals(moveInput)) {
            return false;
        }

        // Unpack move components
        String piece = moveComponents[PIECE];
        String specifier = moveComponents[SPECIFIER];
        String capture = moveComponents[CAPTURE];
        String square = moveComponents[SQUARE];
        String promotion = moveComponents[PROMOTION];
        String castle = moveComponents[CASTLE];

        // Move must either have a square or be a castle
        if (square == null && castle == null) {
            return false;
        }

        // Castle: only castle and checkMate can be not null
        if (castle != null) {
            if (piece != null || specifier != null || capture != null || 
                    square != null || promotion != null) {
                return false;
            }
        }

        // Note: Now implied that square is not null

        // Capture: There must be a piece signified before the "x"
        else if (capture != null && specifier == null && piece == null) {
            return false;
        }

        // There can't be a specifier without a piece or capture
        else if (specifier != null && piece == null && capture == null) {
            return false;
        }

        // Promotion: square and promotion must be not null, 
        // specifier, capture, and checkMate can be not null
        else if (promotion != null) {
            if (piece != null) {
                return false;
            }

            // Check that the promotion is on the correct rank
            Point promotionCoords = toCoords(square);
            if (promotionCoords.y != 7 && promotionCoords.y != 0) {
                return false;
            }
        }

        return true;
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

        return new Point(letterCoord, numberCoord);
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
     * Converts a move to move notation
     * @param piece the moved piece
     * @param type the type of the piece
     * @param specifier the specifier of the piece
     * @param initialCoords the initial coordinates of the piece
     * @param destination the destination of the piece
     * @param captured the piece that was captured during the move
     * @return the move notation for the move (ex. "Bc4")
     */
    public static String toMoveNotation(Piece piece, int type, String specifier, Point initialCoords,
                                        Point destination, Piece captured) {
        String moveNotation = "";

        if (type == Piece.PAWN) {
            if (captured == null) { // Simple pawn move (ex. "e4")
                moveNotation += toSquare(destination);
            } else { // Pawn capture (ex. "dxc3")
                moveNotation += toSquare(initialCoords).substring(0, 1) + "x" + toSquare(destination);
            }

            // Promotion
            String promotedTypeLetter = null;
            if (piece.getType() != Piece.PAWN) {
                for (String letter : Piece.letterPieceMapping.keySet()) {
                    int newType = Piece.letterPieceMapping.get(letter);
                    if (newType == piece.getType()) {
                        promotedTypeLetter = letter;
                        break;
                    }
                }
                moveNotation += "=" + promotedTypeLetter;
            }

            return moveNotation;
        }

        // Castle
        if (type == Piece.KING) {
            if (destination.x == initialCoords.x + 2) { // Short castle
                return "O-O";
            } else if (destination.x == initialCoords.x - 2) { // Long castle
                return "O-O-O";
            }
        }

        // Get piece string
        String pieceString = null;
        for (String letter : Piece.letterPieceMapping.keySet()) {
            if (Piece.letterPieceMapping.get(letter) == piece.getType()) {
                pieceString = letter;
                break;
            }
        }
        moveNotation += pieceString + specifier;

        // Check capture
        if (captured != null) {
            moveNotation += "x";
        }

        moveNotation += toSquare(destination);
        return moveNotation;
    }
}
