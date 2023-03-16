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
    private Piece lastMoved = null;
    private Team winner;
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
        winner = null;
        try {
            board.saveAsImage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts the game, handles game logic
     */
    public void start() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Start game.");

        // Game loop
        do {
            String whoPlays = whiteToPlay ? "White" : "Black";
            boolean validMove = false;
            String input = null;

            System.out.println();
            System.out.println(board);

            do {
                // Take input
                System.out.print(String.format("%s to play: ", whoPlays));
                input = scanner.nextLine();
                
                // Validate move and execute it if valid
                validMove = move(input);
                if (!validMove) {
                    System.out.println("Invalid move.");
                }
            } while (!validMove);

            try {
                board.saveAsImage();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Toggle whiteToPlay to change to next player's turn
            whiteToPlay = !whiteToPlay;

        } while (!gameEnd());

        scanner.close();

        System.out.println("\nEnd game.");
        System.out.println();

        // Print winner
        if (winner == Team.WHITE) {
            System.out.println("White wins!");
        } else if (winner == Team.BLACK) {
            System.out.println("Black wins!");
        } else {
            System.out.println("It's a draw!");
        }
    }

    /**
     * Checks whether a given move is valid and makes the move if it is
     * @param moveComponents a string array of the move's components
     * @return whether the move was valid and successfully executed
     */
    private boolean move(String moveInput) {
        // Parse move
        String[] moveComponents = MoveHandler.parseMove(moveInput);

        // Check if the move notation represents a valid move
        if (!MoveHandler.validMoveComponents(moveInput, moveComponents)) {
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
            return promotion(moveComponents, notNull);
        }
        // All other moves
        else {
            return regularMove(moveComponents);
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

        // Move king and rook to destination
        board.movePiece(king, kingDestination);
        board.movePiece(rook, rookDestination);
        
        // Move the pieces back if the king moves into check, return false
        if (isInCheck(board)) {
            board.undoMovePiece(king, kingCoords, null);
            board.undoMovePiece(rook, rookCoords, null);
            return false;
        }

        lastMoved = king;
        return true;
    }

    /**
     * Executes the promotion maneuver
     * @param moveComponents a string array of the move's components
     * @param notNull a boolean array representing the null/not null move 
     *                components
     * @return whether the promotion was successful
     */
    private boolean promotion(String[] moveComponents, boolean[] notNull) {
        ArrayList<Piece> teamPieces = getPieces(board, "team");
        ArrayList<Piece> oppositeTeamPieces = getPieces(board, "oppositeTeam");

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
        for (Piece piece : teamPieces) {
            if (piece instanceof Pawn) {
                pawnCoords = ((Pawn) piece).getCoords();
                pawnSquare = MoveHandler.toSquare(pawnCoords);

                // Check if current pawn square matches pawn letter
                if (pawnSquare.substring(0, 1).equals(pawnLetter)) {
                    // Check that pawn is on the right rank
                    if (whiteToPlay && pawnCoords.y != 6) { // 7th rank
                        return false;
                    } else if (!whiteToPlay && pawnCoords.y != 1) { // 2nd rank
                        return false;
                    }
                    pawn = (Pawn) piece;
                    break;
                }
            }
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
        capture(captured, oppositeTeamPieces, false);

        // Make sure moving the pawn doesn't cause the king to be in check
        if (isInCheck(board)) {
            board.undoMovePiece(pawn, pawnCoords, captured);
            capture(captured, oppositeTeamPieces, true);
            return false;
        }

        // Remove pawn from team pieces and board
        teamPieces.remove(pawn);
        board.setPiece(null, destination);

        // Setup promoted piece
        board.setUpPiece(promoted);
        lastMoved = promoted;
        return true;
    }

    /**
     * Executes a regular piece move
     * @param moveComponents an array of the move's components
     * @return whether the move was successful
     */
    private boolean regularMove(String[] moveComponents) {
        ArrayList<Piece> teamPieces = getPieces(board, "team");
        ArrayList<Piece> oppositeTeamPieces = getPieces(board, "oppositeTeam");

        // Get move information
        String square = moveComponents[MoveHandler.SQUARE];
        Point destination = MoveHandler.toCoords(square);
        String pieceString = moveComponents[MoveHandler.PIECE];
        String specifier = moveComponents[MoveHandler.SPECIFIER];
        String capture = moveComponents[MoveHandler.CAPTURE];

        // Specific to pawn - get pawn letter
        String pawnString = null;
        if (pieceString == null) {
            pieceString = "P";
            pawnString = (capture == null) ? square.substring(0, 1) : specifier;
        }
        Piece pieceToMove = null;
        Point pieceCoords = null;

        if (pieceString == "P") {
            // Pawn at 8th rank as white or 1st rank as black without promotion 
            // is illegal
            if ((whiteToPlay && destination.y == 7) || 
                    (!whiteToPlay && destination.y == 0)) {
                return false;
            }
        }
        
        // Get all pieces that could be the one to move
        ArrayList<Piece> candidates = new ArrayList<>();
        for (Piece piece : teamPieces) {
            if (piece.toString().toUpperCase().equals(pieceString)) {
                if (piece instanceof Pawn && 
                        !((Pawn) piece).getPawnSpecifier().equals(pawnString)) {
                    continue;
                }
                candidates.add(piece);
            }
        }

        // Get only candidates that can move to the destination square
        for (int i = 0; i < candidates.size(); i++) {
            Piece piece = candidates.get(i);
            if (!piece.getMoves().contains(destination)) {
                candidates.remove(piece);
                i--;
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
            if (specifier == null) {
                return false;
            }

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

        pieceCoords = pieceToMove.getCoords();

        // EN PASSANT bruh
        Point enPassantVictimCoords = null;
        boolean enPassant = false;

        if (pieceToMove instanceof Pawn) {
            // Check en passant
            Piece willBeCaptured = board.getPiece(destination);
            enPassantVictimCoords = checkEnPassant(board, pieceToMove, 
                pieceCoords, willBeCaptured, destination);
            enPassant = (enPassantVictimCoords != null);

            // If en passant, capture en passant victim pawn
            if (enPassant) {
                board.setPiece(null, enPassantVictimCoords);
            } 
            // Otherwise if the destination is on a different column and the 
            // move is not a capture, it's an illegal move
            else if (willBeCaptured == null && destination.x != pieceCoords.x) {
                return false;
            }
        }

        // Move the piece and remove any captured pieces
        Piece captured = board.movePiece(pieceToMove, destination);
        capture(captured, oppositeTeamPieces, false);

        // Make sure moving the piece doesn't cause the king to be in check
        if (isInCheck(board)) {
            if (enPassant) {
                board.undoMovePiece(pieceToMove, pieceCoords, null);
                board.setPiece(captured, enPassantVictimCoords);
            } else {
                board.undoMovePiece(pieceToMove, pieceCoords, captured);
            }
            capture(captured, oppositeTeamPieces, true); // Undo capture
            return false;
        }

        lastMoved = pieceToMove;
        return true;
    }

    /**
     * Gets the pieces for the team of the current turn or the opposite team
     * @param theBoard the board to get the pieces from
     * @param whichTeam current team or opposite team
     * @return the pieces of the team signified in whichTeam
     */
    private ArrayList<Piece> getPieces(Board theBoard, String whichTeam) {
        ArrayList<Piece> whitePieces = theBoard.getWhitePieces();
        ArrayList<Piece> blackPieces = theBoard.getBlackPieces();

        // Set team pieces and opposite team pieces based on whose turn it is
        ArrayList<Piece> teamPieces;
        ArrayList<Piece> oppositeTeamPieces;
        if (whiteToPlay) {
            teamPieces = whitePieces;
            oppositeTeamPieces = blackPieces;
        } else {
            teamPieces = blackPieces;
            oppositeTeamPieces = whitePieces;
        }

        // Return pieces based on team string
        if (whichTeam.equals("team")) {
            return teamPieces;
        } else if (whichTeam.equals("oppositeTeam")) {
            return oppositeTeamPieces;
        } else {
            return null;
        }
    }

    /**
     * Removes or undo removes a piece from a team's pieces
     * @param piece the piece to remove or add back
     * @param pieces the ArrayList of the team's pieces
     * @param undo whether to undo the capture
     */
    private void capture(Piece piece, ArrayList<Piece> pieces, 
            boolean undo) {
        if (piece != null) {
            if (undo) { // Add piece back
                pieces.add(piece);
            } else { // Remove piece
                pieces.remove(piece);
            }
        }
    }

    /**
     * Checks whether the move is en passant
     * @param theBoard the board to check
     * @param pieceToMove the piece to check
     * @param pieceCoords the initial piece coordinates
     * @param captured the given captured piece
     * @param destination the destination square
     * @return a point if there is en passant, otherwise null
     */
    private Point checkEnPassant(Board theBoard, Piece pieceToMove, 
            Point pieceCoords, Piece captured, Point destination) {
        // EN PASSANT bruh
        Point enPassantVictimCoords = null;
        Pawn enPassantVictim = null;

        if (pieceToMove instanceof Pawn) {
            if (captured == null) {
                // Captured pawn coordinates
                if (destination.x == pieceCoords.x + 1) {
                    enPassantVictimCoords = new Point(
                        pieceCoords.x + 1, pieceCoords.y);
                } else if (destination.x == pieceCoords.x - 1) {
                    enPassantVictimCoords = new Point(
                        pieceCoords.x - 1, pieceCoords.y);
                }

                // If enPassantVictimCoords is set, check if the victim was the 
                // last moved piece
                if (enPassantVictimCoords != null) {
                    enPassantVictim = (Pawn) theBoard.getPiece(
                        enPassantVictimCoords);
                    if (lastMoved != enPassantVictim) {
                        enPassantVictimCoords = null;
                    }
                }
            }
        }

        // Return victim coords if it is set, else null
        return enPassantVictimCoords;
    }

    /**
     * Returns whether the current player is in check
     * @return whether the current player is in check
     */
    private boolean isInCheck(Board theBoard) {
        ArrayList<Piece> teamPieces = getPieces(theBoard, "team");
        ArrayList<Piece> oppositeTeamPieces = getPieces(
            theBoard, "oppositeTeam");
        Point kingCoords = null;

        // Get team king coords
        for (Piece piece : teamPieces) {
            if (piece instanceof King) {
                kingCoords = piece.getCoords();
                break;
            }
        }

        // Check if opposite team pieces are attacking the king
        for (Piece piece : oppositeTeamPieces) {
            if (piece.getMoves().contains(kingCoords)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns whether the game has ended or not
     * @return whether the game has ended or not
     */
    private boolean gameEnd() {
        // Duplicate the board and get its pieces
        Board duplicateBoard = new Board(board);
        ArrayList<Piece> teamPieces = getPieces(duplicateBoard, "team");
        ArrayList<Piece> oppositeTeamPieces = getPieces(
            duplicateBoard, "oppositeTeam");
        
        // Check whether the team is in check and if they have no moves
        boolean inCheck = isInCheck(duplicateBoard);
        boolean hasNoMoves = true;

        // Check every possible move
        for (Piece piece : teamPieces) {
            Point initialCoords = piece.getCoords();
            
            for (Point destination : piece.getMoves()) {
                // EN PASSANT bruh
                Point enPassantVictimCoords = null;
                boolean enPassant = false;

                if (piece instanceof Pawn) {
                    // Check en passant
                    Piece willBeCaptured = duplicateBoard.getPiece(destination);
                    enPassantVictimCoords = checkEnPassant(duplicateBoard, 
                        piece, initialCoords, willBeCaptured, destination);
                    enPassant = (enPassantVictimCoords != null);

                    // If en passant, capture en passant victim pawn
                    if (enPassant) {
                        duplicateBoard.setPiece(null, enPassantVictimCoords);
                    } 
                    // Otherwise if the destination is on a different column and the 
                    // move is not a capture, it's an illegal move
                    else if (willBeCaptured == null && 
                            destination.x != initialCoords.x) {
                        continue;
                    }
                }

                // Move piece
                Piece captured = duplicateBoard.movePiece(piece, destination);
                capture(captured, oppositeTeamPieces, false);

                // Check if in check
                if (!(captured instanceof King || isInCheck(duplicateBoard))) {
                    hasNoMoves = false;
                    break;
                }

                // Move piece back
                if (enPassant) {
                    duplicateBoard.undoMovePiece(piece, initialCoords, null);
                    duplicateBoard.setPiece(captured, enPassantVictimCoords);
                } else {
                    duplicateBoard.undoMovePiece(piece, initialCoords, 
                        captured);
                }
                capture(captured, oppositeTeamPieces, true);
            }

            // If found a possible move, break
            if (!hasNoMoves) {
                break;
            }
        }

        // There is at least one legal move
        if (!hasNoMoves) {
            return false;
        }

        // Note: Now implied that there are no legal moves

        // Checkmate - winner is opposite team
        if (inCheck) {
            winner = whiteToPlay ? Team.BLACK : Team.WHITE;
            return true;
        }
        // Stalemate - winner stays null
        else {
            return true;
        }
    }
}
