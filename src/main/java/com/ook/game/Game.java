package com.ook.game;

import java.util.ArrayList;
import java.awt.Point;
import java.time.LocalDate;
import java.util.HashMap;

import com.ook.ai.ChessAI;
import com.ook.io.ChessGameIO;

/**
 * The chess game engine
 */
public class Game {
    // Instance variables
    private ChessGameIO io;
    private final LocalDate date;
    private boolean whiteToPlay;
    private final Board board;
    private String whiteName;
    private String blackName;
    private ChessAI ai;
    private boolean gameEnd;
    private boolean resign;
    private boolean drawOffered;
    private boolean drawAccepted;
    private final String movePrompt = "%s to play. ";
    private final String drawPrompt = "%s, accept draw? (Yes/No) ";
    private int moveNumber = 1;
    private Piece lastMoved;
    private Point lastMovedInitialCoords = null;
    private int winner;
    private String pgn = "";

    /**
     * No-arg constructor with instructions common to other constructors
     */
    private Game() {
        date = LocalDate.now();
        whiteToPlay = true;
        board = new Board();
    }

    /**
     * Creates a new game with white as the first player
     * @param io a ChessGameIO object that routes input/output
     * @param whiteName the white player's name
     * @param blackName the black player's name
     */
    public Game(ChessGameIO io, String whiteName, String blackName) {
        this();
        this.io = io;
        this.whiteName = whiteName;
        this.blackName = blackName;
        setUpGameFiles();
    }

    /**
     * Creates a new player vs AI game
     * @param io a ChessGameIO object
     * @param playerName the player's name
     * @param playerIsPlayingWhite whether the player is playing white
     * @param ai a ChessAI object
     */
    public Game(ChessGameIO io, String playerName, boolean playerIsPlayingWhite, ChessAI ai) {
        this();
        this.io = io;
        this.ai = ai;

        // Set up AI and player names
        ai.setGame(this);
        if (playerIsPlayingWhite) {
            this.whiteName = playerName;
            this.blackName = ai.getName();
        } else {
            this.whiteName = ai.getName();
            this.blackName = playerName;
        }
        ai.setTeam(!playerIsPlayingWhite);

        setUpGameFiles();
    }

    /**
     * Creates a directory for the game and saves the board image to it
     */
    private void setUpGameFiles() {
        // Create new directory and save board
        FileHandler.makeDirectory(whiteName, blackName);
        try {
            FileHandler.saveAsImage(board, whiteToPlay, null, null,
                    whiteName, blackName, (ai == null || ai.isPlayingWhite()));
            io.update();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Takes the next move, handles game logic
     * @param input the move input
     */
    public void takeNextMove(String input) {
        String whoPlays = whiteToPlay ? "White" : "Black";

        boolean validMove;

        // Start game with null input
        if (input == null) {
            io.print(String.format(movePrompt, whoPlays));
            return;
        }

        // Check for draw accepted
        if (drawOffered) {
            if (input.equals("yes") || input.equals("y")) {
                drawAccepted = true;
            } else if (input.equals("no") ||
                    input.equals("n")) {
                // Reset
                drawOffered = false;
                toggleTurn();
            }
        } else {
            // Check for resign
            if (input.equalsIgnoreCase("resign")) {
                resign = true;
            }
            // Check for draw offered
            else if (input.equalsIgnoreCase("draw")) {
                // Cannot draw against AI
                if (ai != null) {
                    io.print("Can't draw against AI.");
                } else {
                    drawOffered = true;
                    toggleTurn();
                }
            }
            // Validate move and execute it if valid
            else {
                validMove = move(input);
                if (!validMove) {
                    io.print("Invalid move.");
                } else {
                    boolean ioUpdate = (ai == null || ai.isPlayingWhite()); // If playing against AI, don't have to flip board
                    saveBoardAndMove(input, lastMoved, lastMovedInitialCoords, ioUpdate);

                    // Toggle whiteToPlay to change to next player's turn
                    toggleTurn();
                }
            }
        }

        // Check game end
        checkGameEnd();

        if (!gameEnd) { // Prompt next move
            promptNextMove();
        }
    }

    /**
     * Checks whether a given move is valid and makes the move if it is
     * @param moveInput the given player move
     * @return whether the move was valid and successfully executed
     */
    public boolean move(String moveInput) {
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
     * @return whether the castle was successful
     */
    private boolean castle(String[] moveComponents) {
        String castleString = moveComponents[MoveHandler.CASTLE];

        Piece king = null;
        Piece rook = null;
        Point kingCoords = null;
        Point rookCoords = null;
        Point kingDestination = null;
        Point rookDestination = null;

        int castleDirection; // 1 for kingside, -1 for queenside
        int team = whiteToPlay ? Piece.WHITE : Piece.BLACK;

        ArrayList<Piece> teamPieces = board.getTeamPieces(team);
        HashMap<Piece, Integer> moveCounter = board.getMoveCounter();

        if (castleString.equals("O-O")) { // Short castle
            // Get king and h-rook (x=7)
            for (Piece piece : teamPieces) {
                if (piece.getType() == Piece.KING) {
                    king = piece;
                    kingCoords = piece.getLocation();
                    if ((team == Piece.WHITE && kingCoords.y != 0) || (team == Piece.BLACK && kingCoords.y != 7)) {
                        return false;
                    }
                } else if (piece.getType() == Piece.ROOK && piece.getLocation().x == 7) {
                    rook = piece;
                    rookCoords = piece.getLocation();
                }
            }
            castleDirection = 1;
        } else { // Long castle
            // Get king and a-rook (x=0)
            for (Piece piece : teamPieces) {
                if (piece.getType() == Piece.KING) {
                    king = piece;
                    kingCoords = piece.getLocation();
                    if ((team == Piece.WHITE && kingCoords.y != 0) || (team == Piece.BLACK && kingCoords.y != 7)) {
                        return false;
                    }
                } else if (piece.getType() == Piece.ROOK && piece.getLocation().x == 0) {
                    rook = piece;
                    rookCoords = piece.getLocation();
                }
            }
            castleDirection = -1;
        }

        // If rook doesn't exist, castling is impossible
        if (rook == null) {
            return false;
        }

        // If king and rook have moved, can't castle
        if (moveCounter.get(king) > 0 || moveCounter.get(rook) > 0) {
            return false;
        }

        // Calculate destination coords
        kingDestination = new Point(kingCoords.x + 2 * castleDirection, kingCoords.y);
        rookDestination = new Point(kingDestination.x - castleDirection, kingDestination.y);

        // Check if the castle is legal
        Move kingMove = new Move(king, kingCoords, kingDestination, null, null);
        Move rookMove = new Move(rook, rookCoords, rookDestination, null, null);

        boolean kingLegalMove = legallyMovePiece(board, kingMove, whiteToPlay);
        if (kingLegalMove) {
            legallyMovePiece(board, rookMove, whiteToPlay);
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
        int team = whiteToPlay ? Piece.WHITE : Piece.BLACK;
        ArrayList<Piece> teamPieces = board.getTeamPieces(team);

        String square = moveComponents[MoveHandler.SQUARE];
        String promotion = moveComponents[MoveHandler.PROMOTION];

        // Info of promoting pawn
        String pawnLetter;
        Piece pawn = null;
        Point pawnCoords = null;
        String pawnSquare = null;

        // Promotion fields
        Point destination = MoveHandler.toCoords(square);

        // Determine letter of pawn
        if (notNull[MoveHandler.SPECIFIER]) { // ex. axb8=Q
            pawnLetter = moveComponents[MoveHandler.SPECIFIER];
        } else {
            pawnLetter = square.substring(0, 1); // ex. b8=Q
        }

        // Find pawn
        for (Piece piece : teamPieces) {
            if (piece.getType() == Piece.PAWN) {
                pawnCoords = piece.getLocation();
                pawnSquare = MoveHandler.toSquare(pawnCoords);

                // Check if current pawn square matches pawn letter
                if (pawnSquare.substring(0, 1).equals(pawnLetter)) {
                    // Check that pawn is on the right rank
                    if (whiteToPlay && pawnCoords.y != 6) { // 7th rank
                        return false;
                    } else if (!whiteToPlay && pawnCoords.y != 1) { // 2nd rank
                        return false;
                    }
                    pawn = piece;
                    break;
                }
            }
        }

        // Get promotion piece
        int promotePieceValue = Piece.letterPieceMapping.get(promotion.substring(1));

        // Check if the promotion is legal
        Move promotionMove = new Move(pawn, pawnCoords, destination, board.get(destination), destination);
        boolean legalMove = legallyMovePiece(board, promotionMove, whiteToPlay);

        if (legalMove) {
            // Turn pawn into promoted piece
            pawn.setType(promotePieceValue);
            lastMoved = pawn;
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
        int team = whiteToPlay ? Piece.WHITE : Piece.BLACK;
        ArrayList<Piece> teamPieces = board.getTeamPieces(team);

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

        if (pieceString.equals("P")) {
            // Pawn at 8th rank as white or 1st rank as black without promotion is illegal
            if ((whiteToPlay && destination.y == 7) || (!whiteToPlay && destination.y == 0)) {
                return false;
            }
        }
        
        // Get all pieces that could be the one to move
        ArrayList<Piece> candidates = new ArrayList<>();
        for (Piece piece : teamPieces) {
            if (piece.getType() == Piece.letterPieceMapping.get(pieceString)) {
                if (piece.getType() == Piece.PAWN) { // Check pawn specifier from its location
                    Point location = piece.getLocation();
                    if (!MoveHandler.toSquare(location).substring(0, 1).equals(pawnString)) {
                        continue;
                    }
                }
                candidates.add(piece);
            }
        }

        // Get only candidates that can move to the destination square
        for (int i = 0; i < candidates.size(); i++) {
            Piece piece = candidates.get(i);
            boolean isCandidate = false;
            for (Move move : Piece.getMoves(board, piece)) {
                if (move.getDestination().equals(destination)) {
                    isCandidate = true;
                    break;
                }
            }
            if (!isCandidate) {
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

            int specifierMatchCount = 0;
            for (Piece piece : candidates) {
                String currentSquare = MoveHandler.toSquare(piece.getLocation());
                specifierMatchCount = 0;

                // If specifier (letter or number) matches piece square, select it
                if (currentSquare.substring(0, 1).equals(specifier) || currentSquare.substring(1).equals(specifier)) {
                    specifierMatchCount++;
                    pieceToMove = piece;
                }
            }
            // Specifier doesn't match or has too many matches - return false
            if (specifierMatchCount != 1) {
                return false;
            }
        }

        Point pieceCoords = pieceToMove.getLocation();

        // Check if the move is legal
        Move move = new Move(pieceToMove, pieceCoords, destination, board.get(destination.x, destination.y),
                destination);
        boolean legalMove = legallyMovePiece(board, move, whiteToPlay);

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
     * @param move the move
     * @param whiteToPlay whether it is white's turn
     * @return whether the move was legal
     */
    public boolean legallyMovePiece(Board theBoard, Move move, boolean whiteToPlay) {
        int oppTeam = !whiteToPlay ? Piece.WHITE : Piece.BLACK;
        ArrayList<Piece> opps = theBoard.getTeamPieces(oppTeam);

        // Get piece and coordinates information
        Piece pieceToMove = move.getPiece();
        Point initialCoords = move.getInitialCoords();
        Point destination = move.getDestination();

        if (pieceToMove.getType() == Piece.PAWN) {
            // Check en passant
            checkEnPassant(theBoard, move);

            // Otherwise if the destination is on a different column and the 
            // move is not a capture, it's an illegal move
            if (move.getCaptured() == null && destination.x != initialCoords.x) {
                return false;
            }
        }

        // Move the piece and remove any captured pieces
        Point capturedCoords = move.getCapturedCoords();
        if (capturedCoords != null) {
            theBoard.set(capturedCoords, null);
            capture(move.getCaptured(), opps, false);
        }
        theBoard.move(pieceToMove, destination);

        // Make sure moving the piece doesn't cause the king to be in check
        if (isInCheck(theBoard)) {
            undoMovePiece(theBoard, pieceToMove, initialCoords, move.getCaptured(), move.getCapturedCoords(), opps);
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
    public void undoMovePiece(Board theBoard, Piece pieceToMove, Point oldCoords, Piece captured, Point capturedCoords,
            ArrayList<Piece> opps) {
        theBoard.undoMove(pieceToMove, oldCoords);
        if (capturedCoords != null) {
            theBoard.set(capturedCoords, captured);
            capture(captured, opps, true);
        }
    }

    /**
     * Removes or undo removes a piece from a team's pieces
     * @param piece the piece to remove or add back
     * @param pieces the ArrayList of the team's pieces
     * @param undo whether to undo the capture
     */
    private void capture(Piece piece, ArrayList<Piece> pieces, boolean undo) {
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
     * @param move the move
     * @return a point if there is en passant, otherwise null
     */
    public void checkEnPassant(Board theBoard, Move move) {
        Piece pieceToMove = move.getPiece();
        Point pieceCoords = move.getInitialCoords();
        Point destination = move.getDestination();
        Piece captured = move.getCaptured();

        // EN PASSANT bruh
        Point epCoords = null;
        Piece victim = null;

        if (pieceToMove.getType() == Piece.PAWN) {
            if (captured == null) {
                // Captured pawn coordinates
                if (destination.x == pieceCoords.x + 1) {
                    epCoords = new Point(pieceCoords.x + 1, pieceCoords.y);
                } else if (destination.x == pieceCoords.x - 1) {
                    epCoords = new Point(pieceCoords.x - 1, pieceCoords.y);
                }

                // If epCoords is set, check if the victim was the last moved piece
                if (epCoords != null) {
                    victim = theBoard.get(epCoords.x, epCoords.y);
                    if (victim.getType() == Piece.PAWN && lastMoved == victim &&
                            board.getMoveCounter().get(victim).equals(1)) {
                        // Set en passant captured piece and coordinates
                        move.setCaptured(victim);
                        move.setCapturedCoords(epCoords);
                    }
                }
            }
        }
    }

    /**
     * Returns whether the current player is in check
     * @param theBoard the chessboard
     * @return whether the current player is in check
     */
    public boolean isInCheck(Board theBoard) {
        int team = whiteToPlay ? Piece.WHITE : Piece.BLACK;
        int oppTeam = !whiteToPlay ? Piece.WHITE : Piece.BLACK;
        ArrayList<Piece> teamPieces = theBoard.getTeamPieces(team);
        ArrayList<Piece> opps = theBoard.getTeamPieces(oppTeam);
        Point kingCoords = null;

        // Get team king coords
        for (Piece piece : teamPieces) {
            if (piece.getType() == Piece.KING) {
                kingCoords = piece.getLocation();
                break;
            }
        }

        // Check if opposite team pieces are attacking the king
        for (Piece piece : opps) {
            for (Move move : Piece.getMoves(board, piece)) {
                if (move.getDestination().equals(kingCoords)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Checks whether the game has ended or not
     */
    public void checkGameEnd() {
        if (!(resign || (drawOffered && drawAccepted))) {
            // Duplicate the board and get its pieces
            int team = whiteToPlay ? Piece.WHITE : Piece.BLACK;
            int oppTeam = !whiteToPlay ? Piece.WHITE : Piece.BLACK;
            ArrayList<Piece> teamPieces = board.getTeamPieces(team);
            ArrayList<Piece> opps = board.getTeamPieces(oppTeam);

            // Check whether the team is in check and if they have no moves
            boolean inCheck = isInCheck(board);
            boolean hasNoMoves = true;

            // Check if there is a possible move
            for (Piece piece : teamPieces) {
                Point initialCoords = piece.getLocation();
                for (Move move : Piece.getMoves(board, piece)) {
                    boolean legalMove = legallyMovePiece(board, move, whiteToPlay);

                    // Undo move after checking
                    if (legalMove) {
                        hasNoMoves = false;
                        undoMovePiece(board, piece, initialCoords, move.getCaptured(), move.getDestination(), opps);
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
                gameEnd = false;
                return;
            }

            // Note: Now implied that there are no legal moves

            // Checkmate - winner is opposite team
            if (inCheck) {
                winner = whiteToPlay ? Piece.BLACK : Piece.WHITE;
            }
            // Otherwise Stalemate - winner stays null
        }

        gameEnd = true;
        endGame();
    }

    /**
     * Game end procedure
     */
    private void endGame() {
        io.closeInputStream(); // Stop taking input

        // Determine winner if a player resigned
        if (resign) {
            if (whiteToPlay) {
                winner = Piece.BLACK;
            } else {
                winner = Piece.WHITE;
            }
        }

        // Need to do an extra update for resign/draw or if AI is playing
        if (resign || (drawOffered && drawAccepted) || (ai != null && (whiteToPlay == ai.isPlayingWhite()))) {
            io.update();
        }

        // Print winner
        String result;
        if (winner == Piece.WHITE) {
            result = "1-0";
            io.print(whiteName + " wins!");
        } else if (winner == Piece.BLACK) {
            result = "0-1";
            io.print(blackName + " wins!");
        } else {
            result = "1/2-1/2";
            io.print("It's a draw!");
        }
        pgn += result;

        // Save PGN
        FileHandler.savePGN(date, whiteName, blackName, result, pgn);
    }

    /**
     * Saves the board image and calls io.update()
     */
    public void saveBoardAndMove(String input, Piece lastMoved, Point lastMovedInitialCoords, boolean ioUpdate) {
        // Add move to pgn
        if (whiteToPlay) {
            pgn += String.format("%s. %s ", moveNumber, input);
        } else {
            pgn += String.format("%s ", input);
        }

        // Save board as image
        try {
            FileHandler.saveAsImage(board, !whiteToPlay, lastMovedInitialCoords, lastMoved, whiteName,
                    blackName, (ai == null || ai.isPlayingWhite()));
            if (ioUpdate) {
                io.update();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Increment move number
        if (!whiteToPlay) {
            moveNumber++;
        }
    }

    /**
     * Prompts the next move
     */
    public void promptNextMove() {
        String whoPlays = whiteToPlay ? "White" : "Black";
        if (drawOffered) {
            io.print(String.format(drawPrompt, whoPlays));
        } else {
            io.print(String.format(movePrompt, whoPlays));
        }
    }

    /**
     * Returns whose turn it is
     * @return true for white to play, false for black to play
     */
    public boolean whiteToPlay() {
        return whiteToPlay;
    }

    /**
     * Toggles whose turn it is
     */
    public void toggleTurn() {
        whiteToPlay = !whiteToPlay;
    }

    /**
     * Returns whether the game has ended
     * @return whether the game has ended
     */
    public boolean ended() {
        return gameEnd;
    }

    /**
     * Gets the game's board
     * @return the Board object associated with this Game
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Gets the game's IO
     * @return a ChessGameIO object
     */
    public ChessGameIO getIO() {
        return io;
    }

    /**
     * Gets the PGN string of the game
     * @return the PGN string
     */
    public String getPGNString() {
        return pgn;
    }

    /**
     * Gets the move number of the game
     * @return the move number of the game
     */
    public int getMoveNumber() {
        return moveNumber;
    }

    /**
     * Returns the last moved piece
     * @return the last moved piece
     */
    public Piece getLastMoved() {
        return lastMoved;
    }

    /**
     * Returns the initial coordinates of the last moved piece
     * @return the initial coordinates of the last moved piece
     */
    public Point getLastMovedInitialCoords() {
        return lastMovedInitialCoords;
    }
}
