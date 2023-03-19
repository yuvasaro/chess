package game;

import java.util.Scanner;
import java.util.ArrayList;
import java.awt.Point;
import java.lang.reflect.Constructor;
import java.time.LocalDate;
import java.util.Map;

/**
 * The chess game engine
 */
public class Game {
    // Instance variables
    private LocalDate date;
    private boolean whiteToPlay;
    private Board board;
    private int moveNumber = 0;
    private Piece lastMoved = null;
    private Point lastMovedInitialCoords = null;
    private String whiteName;
    private String blackName;
    private Team winner;
    private String result;
    private String pgn = "";
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
        date = LocalDate.now();
        whiteToPlay = true;
        board = new Board();
    }

    /**
     * Starts the game, handles game logic
     */
    public void start() {
        // Store booleans for resign and draw
        boolean resign = false;
        boolean drawOffered = false;
        boolean drawAccepted = false;

        Scanner scanner = new Scanner(System.in);
        System.out.println("Start game.\n");

        // Get player names
        System.out.print("Who is playing White? ");
        whiteName = scanner.nextLine();
        System.out.print("Who is playing Black? ");
        blackName = scanner.nextLine();

        // Create new directory and save board
        FileHandler.makeDirectory(whiteName, blackName);
        try {
            FileHandler.saveAsImage(board, whiteToPlay, null, null, 
                whiteName, blackName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Game loop
        do {
            String whoPlays = whiteToPlay ? "White" : "Black";
            boolean validMove = false;
            String input = null;

            do {
                // Take input
                System.out.print(String.format("%s to play: ", whoPlays));
                input = scanner.nextLine();

                // Check for resign or draw
                if (input.toLowerCase().equals("resign")) {
                    resign = true;
                    break;
                } else if (input.toLowerCase().equals("draw")) {
                    drawOffered = true;
                    whiteToPlay = !whiteToPlay;
                    whoPlays = whiteToPlay ? "White" : "Black";

                    // Prompt other player
                    String answer = null;
                    do {
                        System.out.print(String.format(
                            "%s, accept draw? (Yes/No): ", whoPlays));
                        answer = scanner.nextLine().toLowerCase();
                        if (answer.equals("yes") || answer.equals("y")) {
                            drawAccepted = true;
                            break;
                        } else if (answer.equals("no") || 
                                answer.equals("n")) {
                            // Reset
                            drawOffered = false;
                        } else {
                            answer = null;
                        }
                    } while (answer == null);

                    if (drawAccepted) {
                        break;
                    } else {
                        validMove = true;
                        continue;
                    }
                }
                
                // Validate move and execute it if valid
                validMove = move(input);
                if (!validMove) {
                    System.out.println("Invalid move.");
                }
            } while (!validMove);

            // Stop game if resign or draw
            if (resign || (drawOffered && drawAccepted)) {
                break;
            }

            // Add move to pgn
            if (whiteToPlay) {
                moveNumber++;
                pgn += String.format("%s. %s ", moveNumber, input);
            } else {
                pgn += String.format("%s ", input);
            }

            // Toggle whiteToPlay to change to next player's turn
            whiteToPlay = !whiteToPlay;

            // Save board image
            try {
                FileHandler.saveAsImage(board, whiteToPlay, 
                    lastMovedInitialCoords, lastMoved, whiteName, blackName);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } while (!gameEnd());

        scanner.close();

        System.out.println("\nEnd game.");
        System.out.println();

        // Determine winner if a player resigned
        if (resign) {
            if (whiteToPlay) {
                winner = Team.BLACK;
            } else {
                winner = Team.WHITE;
            }
        }

        // Print winner
        if (winner == Team.WHITE) {
            result = "1-0";
            System.out.println("White wins!");
        } else if (winner == Team.BLACK) {
            result = "0-1";
            System.out.println("Black wins!");
        } else {
            result = "1/2-1/2";
            System.out.println("It's a draw!");
        }
        pgn += result;

        // Save PGN
        FileHandler.savePGN(date, whiteName, blackName, result, pgn);
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

        // Castle
        if (notNull[MoveHandler.CASTLE]) {
            return castle(moveComponents);
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
    private boolean castle(String[] moveComponents) {
        ArrayList<Piece> team = board.getTeamPieces(whiteToPlay);
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
            for (Piece piece : team) {
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
            for (Piece piece : team) {
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

        // Check if the castle is legal
        boolean kingLegalMove = legallyMovePiece(board, king, kingDestination);
        if (kingLegalMove) {
            legallyMovePiece(board, rook, rookDestination);
        }

        lastMoved = king;
        lastMovedInitialCoords = kingCoords;
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
        ArrayList<Piece> team = board.getTeamPieces(whiteToPlay);

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
        for (Piece piece : team) {
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
            Class<?> promotePieceClass = Class.forName(
                getClass().getPackageName() + "." + promotePieceString);
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

        // Check if the promotion is legal
        boolean legalMove = legallyMovePiece(board, pawn, destination);

        if (legalMove) {
            // Remove pawn from team pieces and board
            team.remove(pawn);
            board.setPiece(null, destination);

            // Setup promoted piece
            board.setUpPiece(promoted);
            lastMoved = promoted;
            lastMovedInitialCoords = pawnCoords;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Executes a regular piece move
     * @param moveComponents an array of the move's components
     * @return whether the move was successful
     */
    private boolean regularMove(String[] moveComponents) {
        ArrayList<Piece> team = board.getTeamPieces(whiteToPlay);

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
        for (Piece piece : team) {
            if (piece.toString().equals(pieceString)) {
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

            // Specifier doesn't match - return false
            if (pieceToMove == null) {
                return false;
            }
        }

        Point pieceCoords = pieceToMove.getCoords();

        // Check if the move is legal
        boolean legalMove = legallyMovePiece(board, pieceToMove, destination);

        if (legalMove) {
            lastMoved = pieceToMove;
            lastMovedInitialCoords = pieceCoords;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Moves a given piece if it is a legal move
     * @param theBoard the chessboard
     * @param pieceToMove the piece to move
     * @param destination the destination of the piece
     * @return whether the move was legal
     */
    private boolean legallyMovePiece(Board theBoard, Piece pieceToMove, 
            Point destination) {
        ArrayList<Piece> opps = theBoard.getTeamPieces(!whiteToPlay);

        // Get piece and coordinates information
        Point pieceCoords = pieceToMove.getCoords();
        Piece captured = theBoard.getPiece(destination);
        Point capturedCoords = destination;

        if (pieceToMove instanceof Pawn) {
            // Check en passant
            boolean enPassant = false;
            Point epCoords = checkEnPassant(theBoard, pieceCoords, captured, 
                destination);
            enPassant = (epCoords != null);

            // If en passant, capture en passant victim pawn
            if (enPassant) {
                captured = theBoard.getPiece(epCoords);
                capturedCoords = epCoords;
            } 
            // Otherwise if the destination is on a different column and the 
            // move is not a capture, it's an illegal move
            else if (captured == null && destination.x != pieceCoords.x) {
                return false;
            }
        }

        // Move the piece and remove any captured pieces
        theBoard.movePiece(pieceToMove, destination);
        capture(captured, opps, false);

        // Make sure moving the piece doesn't cause the king to be in check
        if (isInCheck(theBoard)) {
            undoMovePiece(theBoard, pieceToMove, pieceCoords, captured, 
                capturedCoords, opps);
            return false;
        }

        return true;
    }

    /**
     * Undoes a piece move
     * @param theBoard the chessboard
     * @param pieceToMove the piece to put back
     * @param oldCoords the piece's old coordinates
     * @param captured the piece that was captured
     * @param capturedCoords the captured piece's coordinates
     * @param opps the ArrayList of enemy pieces
     */
    private void undoMovePiece(Board theBoard, Piece pieceToMove, 
            Point oldCoords, Piece captured, Point capturedCoords, 
            ArrayList<Piece> opps) {
        board.undoMovePiece(pieceToMove, oldCoords);
        board.setPiece(captured, capturedCoords);
        capture(captured, opps, true);
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
     * @param pieceCoords the initial piece coordinates
     * @param captured the given captured piece
     * @param destination the destination square
     * @return a point if there is en passant, otherwise null
     */
    private Point checkEnPassant(Board theBoard, Point pieceCoords, 
            Piece captured, Point destination) {
        // EN PASSANT bruh
        Point epCoords = null;
        Pawn victim = null;

        if (captured == null) {
            // Captured pawn coordinates
            if (destination.x == pieceCoords.x + 1) {
                epCoords = new Point(pieceCoords.x + 1, pieceCoords.y);
            } else if (destination.x == pieceCoords.x - 1) {
                epCoords = new Point(pieceCoords.x - 1, pieceCoords.y);
            }

            // If epCoords is set, check if the victim was the last moved piece
            if (epCoords != null) {
                victim = (Pawn) theBoard.getPiece(epCoords);
                if (lastMoved != victim) {
                    return null;
                }
            }
        }

        // Return victim coords
        return epCoords;
    }

    /**
     * Returns whether the current player is in check
     * @param theBoard the chessboard
     * @return whether the current player is in check
     */
    private boolean isInCheck(Board theBoard) {
        ArrayList<Piece> teamPieces = theBoard.getTeamPieces(whiteToPlay);
        ArrayList<Piece> oppositeTeamPieces = theBoard.getTeamPieces(
            !whiteToPlay);
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
        ArrayList<Piece> team = duplicateBoard.getTeamPieces(whiteToPlay);
        
        // Check whether the team is in check and if they have no moves
        boolean inCheck = isInCheck(duplicateBoard);
        boolean hasNoMoves = true;

        // Check if there is a possible move
        for (Piece piece : team) {
            for (Point destination : piece.getMoves()) {
                boolean legalMove = legallyMovePiece(duplicateBoard, piece, 
                    destination);

                // Undo move after checking 
                if (legalMove) {
                    hasNoMoves = false;
                    break;
                }
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
