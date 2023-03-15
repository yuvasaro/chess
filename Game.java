import java.util.Scanner;
import java.util.ArrayList;
import java.awt.Point;
import java.lang.reflect.Constructor;
import java.util.Map;

/**
 * The chess game engine
 */
public class Game {
    private boolean whiteToPlay;
    private Board board;
    private Map<String, String> letterPieceMapping = Map.of(
        "N", "Knight",
        "B", "Bishop",
        "R", "Rook",
        "Q", "Queen",
        "K", "King"
    );

    /**
     * Creates a new game with white as the first player
     */
    public Game() {
        whiteToPlay = true;
        board = new Board();
    }

    /**
     * Starts the game, handles game logic
     */
    public void start() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Start game.\n");

        // Game loop
        do {
            String whoPlays = whiteToPlay ? "White" : "Black";
            boolean validMove = false;
            String input = null;
            String[] moveComponents = null;

            System.out.println();
            System.out.println(board);
            System.out.println();

            do {
                // Take input
                System.out.print(String.format("%s to play: ", whoPlays));
                input = scanner.next();

                // Parse move
                moveComponents = MoveHandler.parseMove(input);

                // Validate move and execute it if valid
                validMove = move(moveComponents);
            } while (!validMove);

            // Toggle whiteToPlay to change to next player's turn
            whiteToPlay = !whiteToPlay;

        } while (!gameEnd());

        System.out.println("\nEnd game.");

        scanner.close();
    }

    /**
     * Checks whether a given move is valid and makes the move if it is
     * @param moveComponents a string array of the move's components
     * @return whether the move was valid and successfully executed
     */
    private boolean move(String[] moveComponents) {
        // Check if the move notation represents a valid move
        if (!MoveHandler.validMoveComponents(moveComponents)) {
            return false;
        }

        // Translate move components to array of booleans (not null = true)
        boolean[] notNull = new boolean[moveComponents.length];
        for (int i = 0; i < moveComponents.length; i++) {
            notNull[i] = (moveComponents[i] != null);
        }

        ArrayList<Piece> pieces;
        // Get white pieces array if white to play
        if (whiteToPlay) {
            pieces = board.getWhitePieces();
        } 
        // Get black pieces array if black to play
        else {
            pieces = board.getBlackPieces();
        }

        // Castle
        if (notNull[MoveHandler.CASTLE]) {
            return castle(moveComponents, pieces);
        }
        // Promotion
        else if (notNull[MoveHandler.PROMOTION]) {
            return promotion(moveComponents, notNull, pieces);
        }
        // All other moves
        else {
            return regularMove(moveComponents, pieces);
        }
    }

    /**
     * Executes the castle maneuver
     * @param moveComponents a string array of the move's components
     * @param pieces an ArrayList of pieces for the current team
     * @return whether the castle was successful
     */
    private boolean castle(String[] moveComponents, ArrayList<Piece> pieces) {
        String castleString = moveComponents[MoveHandler.CASTLE];
        King king = null;
        Rook rook = null;
        Point kingCoords;
        Point rookCoords;
        Point kingDestination;
        Point rookDestination;
        int castleDirection; // 1 for kingside, -1 for queenside

        if (castleString.equals("O-O")) { // Short castle
            // Get king and h-rook (x=7)
            for (Piece piece : pieces) {
                if (piece instanceof King) {
                    king = (King) piece;
                } else if (piece instanceof Rook) {
                    if (((Rook) piece).getCoords().x == 7) {
                        rook = (Rook) piece;
                    }
                }
            }
            castleDirection = 1;
        } else { // Long castle
            // Get king and a-rook (x=0)
            for (Piece piece : pieces) {
                if (piece instanceof King) {
                    king = (King) piece;
                } else if (piece instanceof Rook) {
                    if (((Rook) piece).getCoords().x == 0) {
                        rook = (Rook) piece;
                    }
                }
            }
            castleDirection = -1;
        }

        // Calculate destination coords
        kingCoords = king.getCoords();
        kingDestination = new Point(
            kingCoords.x + 2 * castleDirection, kingCoords.y);
        rookDestination = new Point(
            kingDestination.x - castleDirection, kingDestination.y);

        // If rook doesn't exist, castling is impossible
        if (rook == null) {
            return false;
        }

        // Check that neither piece has already moved
        if (king.hasMoved() || rook.hasMoved()) {
            return false;
        }

        // Check that there are no pieces in between the king and the rook
        rookCoords = rook.getCoords();
        Point temp = new Point(
            kingCoords.x + castleDirection, kingCoords.y);
        while (!temp.equals(rookCoords)) {
            if (board.getPiece(temp) != null) {
                return false;
            }
            temp.translate(castleDirection, 0);
        }

        // Castle
        board.movePiece(king, kingDestination);
        board.movePiece(rook, rookDestination);
        return true;
    }

    /**
     * Executes the promotion maneuver
     * @param moveComponents a string array of the move's components
     * @param notNull a boolean array representing the null/not null move 
     *                components
     * @param pieces an ArrayList of pieces for the current team
     * @return whether the promotion was successful
     */
    private boolean promotion(String[] moveComponents, boolean[] notNull,
            ArrayList<Piece> pieces) {
        String square = moveComponents[MoveHandler.SQUARE];
        String promotion = moveComponents[MoveHandler.PROMOTION];

        // Info of promoting pawn
        String pawnLetter;
        Piece pawn = null;
        Point pawnCoords = null;
        String pawnSquare = null;

        // Promotion fields
        Point destination = MoveHandler.toCoords(square);
        Piece promoted = null;

        // Determine letter of pawn
        if (notNull[MoveHandler.SPECIFIER]) { // ex. axb8=Q
            pawnLetter = moveComponents[MoveHandler.SPECIFIER];
        } else {
            pawnLetter = square.substring(0, 1); // ex. b8=Q
        }

        // Find pawn
        for (Piece piece : pieces) {
            if (piece instanceof Pawn) {
                pawnCoords = ((Pawn) piece).getCoords();
                pawnSquare = MoveHandler.toSquare(pawnCoords);

                // Check if current pawn square matches pawn letter
                if (pawnSquare.substring(0, 1).equals(pawnLetter)) {
                    pawn = (Pawn) piece;
                }
            }
        }

        // Check that pawn is on the right rank
        if (whiteToPlay || pawnCoords.y != 6) { // 7th rank
            return false;
        } else if (pawnCoords.y != 1) { // 2nd rank
            return false;
        }

        // Get string word of promotion piece class (ex. "Queen")
        String promotePieceString = letterPieceMapping.get(
            promotion.substring(1));

        try {
            // Get promote piece class and constructor
            Class<?> promotePieceClass = Class.forName(promotePieceString);
            Constructor<?> constructor = promotePieceClass.getConstructor(
                Board.class, Team.class, int.class, int.class);

            promoted = (Piece) constructor.newInstance(
                board,
                (whiteToPlay) ? Team.WHITE : Team.BLACK,
                destination.x,
                destination.y
            );
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        // Get captured piece and remove from opposite team pieces
        Piece captured = board.movePiece(pawn, destination);
        if (captured != null) {
            if (whiteToPlay) {
                board.getBlackPieces().remove(captured);
            } else {
                board.getWhitePieces().remove(captured);
            }
        }

        // Remove pawn from team pieces and board
        pieces.remove(pawn);
        board.setPiece(null, pawnCoords);

        // Setup promoted piece
        board.setUpPiece(promoted);
        return true;
    }

    /**
     * Executes a regular piece move
     * @param moveComponents an array of the move's components
     * @param pieces an ArrayList of pieces for the current team
     * @return whether the move was successful
     */
    private boolean regularMove(String[] moveComponents, 
            ArrayList<Piece> pieces) {
        Point destination = MoveHandler.toCoords(
                moveComponents[MoveHandler.SQUARE]);
        String pieceString = moveComponents[MoveHandler.PIECE];
        String specifier = moveComponents[MoveHandler.SPECIFIER];
        if (pieceString == null) { // Pawn
            pieceString = "P";
        }
        Piece pieceToMove = null;
        
        // Get all pieces that could be the one to move
        ArrayList<Piece> candidates = new ArrayList<>();
        for (Piece piece : pieces) {
            if (piece.toString().toUpperCase().equals(pieceString)) {
                candidates.add(piece);
            }
        }

        // Get only candidates that can move to the destination square
        for (int i = 0; i < candidates.size(); i++) {
            Piece piece = candidates.get(i);
            if (!piece.getMoves().contains(destination)) {
                candidates.remove(piece);
            }
        }

        // If no candidates, return false
        if (candidates.isEmpty()) {
            return false;
        }
        // If only one piece, select it
        if (candidates.size() == 1) {
            pieceToMove = candidates.get(0);
        } 
        // Otherwise use specifier to select piece
        else {
            for (Piece piece : candidates) {
                String currentSquare = MoveHandler.toSquare(
                    piece.getCoords());

                // If specifier (letter or number) matches piece square, 
                // select it
                if (currentSquare.substring(0, 1).equals(specifier) || 
                        currentSquare.substring(1).equals(specifier)) {
                    pieceToMove = piece;
                    break;
                }
            }
        }

        // Move the piece and remove any captured pieces
        Piece captured = board.movePiece(pieceToMove, destination);
        if (captured != null) {
            if (whiteToPlay) {
                board.getBlackPieces().remove(captured);
            } else {
                board.getWhitePieces().remove(captured);
            }
        }
        return true;
    }

    /**
     * Returns whether the current player is in check
     * @return whether the current player is in check
     */
    private boolean isInCheck() {
        // TODO
        return false;
    }

    /**
     * Returns whether the game has ended or not
     * @return whether the game has ended or not
     */
    private boolean gameEnd() {
        // TODO
        return false;
    }
}
