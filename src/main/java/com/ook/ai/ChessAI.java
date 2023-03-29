package com.ook.ai;

import com.ook.game.*;

import java.util.Scanner;
import java.util.*;
import java.awt.Point;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Chess AI class
 */
public class ChessAI {
    private static final Map<Integer, Integer> pieceValues = Map.of(
            Piece.PAWN, 100,
            Piece.KNIGHT, 300,
            Piece.BISHOP, 300,
            Piece.ROOK, 500,
            Piece.QUEEN, 900,
            Piece.KING, 20000
    );

    private static final Map<Integer, Integer[]> pieceSquareTables = Map.of(
            Piece.PAWN, new Integer[]{
                    0,  0,  0,  0,  0,  0,  0,  0,
                    50, 50, 50, 50, 50, 50, 50, 50,
                    10, 10, 20, 30, 30, 20, 10, 10,
                    5,  5, 10, 25, 25, 10,  5,  5,
                    0,  0, 10, 25, 25, 10,  0,  0,
                    5,  0, 10,  0,  0, 10,  0,  5,
                    5, 10, 10,-20,-20, 10, 10,  5,
                    0,  0,  0,  0,  0,  0,  0,  0
            },
            Piece.KNIGHT, new Integer[]{
                    -50,-40,-30,-30,-30,-30,-40,-50,
                    -40,-20,  0,  0,  0,  0,-20,-40,
                    -30,  0, 10, 15, 15, 10,  0,-30,
                    -30,  5, 15, 20, 20, 15,  5,-30,
                    -30,  0, 15, 20, 20, 15,  0,-30,
                    -30,  5, 20, 15, 15, 20,  5,-30,
                    -40,-20,  0,  5,  5,  0,-20,-40,
                    -50,-40,-30,-30,-30,-30,-40,-50,
            },
            Piece.BISHOP, new Integer[]{
                    -20,-10,-10,-10,-10,-10,-10,-20,
                    -10,  0,  0,  0,  0,  0,  0,-10,
                    -10,  0,  5, 10, 10,  5,  0,-10,
                    -10,  5,  5, 10, 10,  5,  5,-10,
                    -10,  0, 20, 10, 10, 20,  0,-10,
                    -10, 15, 15, 10, 10, 15, 15,-10,
                    -10,  5,  0,  0,  0,  0,  5,-10,
                    -20,-10,-10,-10,-10,-10,-10,-20,
            },
            Piece.ROOK, new Integer[]{
                    0,  0,  0,  0,  0,  0,  0,  0,
                    5, 10, 10, 10, 10, 10, 10,  5,
                    -5,  0,  0,  0,  0,  0,  0, -5,
                    -5,  0,  0,  0,  0,  0,  0, -5,
                    -5,  0,  0,  0,  0,  0,  0, -5,
                    -5,  0,  0,  0,  0,  0,  0, -5,
                    -5,  0,  0,  0,  0,  0,  0, -5,
                    0,  0,  10, 10, 10, 10,  0,  0
            },
            Piece.QUEEN, new Integer[]{
                    -20,-10,-10, -5, -5,-10,-10,-20,
                    -10,  0,  0,  0,  0,  0,  0,-10,
                    -10,  0,  5,  5,  5,  5,  0,-10,
                    -5,  0,  5,  5,  5,  5,  0, -5,
                     0,  0,  5,  5,  5,  5,  0, -5,
                    -10,  5,  5,  5,  5,  5,  0,-10,
                    -10,  0,  5,  0,  0,  0,  0,-10,
                    -20,-10,-10, -5, -5,-10,-10,-20
            },
            Piece.KING, new Integer[]{
                    -30,-40,-40,-50,-50,-40,-40,-30,
                    -30,-40,-40,-50,-50,-40,-40,-30,
                    -30,-40,-40,-50,-50,-40,-40,-30,
                    -30,-40,-40,-50,-50,-40,-40,-30,
                    -20,-30,-30,-40,-40,-30,-30,-20,
                    -10,-20,-20,-20,-20,-20,-20,-10,
                    10,  10,  0,  0,  0,  0, 10, 10,
                    30,  50, 70,  0,  0, 10, 70, 30
            },
            Piece.KING_ENDGAME, new Integer[]{
                    -50,-40,-30,-20,-20,-30,-40,-50,
                    -30,-20,-10,  0,  0,-10,-20,-30,
                    -30,-10, 20, 30, 30, 20,-10,-30,
                    -30,-10, 30, 40, 40, 30,-10,-30,
                    -30,-10, 30, 40, 40, 30,-10,-30,
                    -30,-10, 20, 30, 30, 20,-10,-30,
                    -30,-30,  0,  0,  0,  0,-30,-30,
                    -50,-30,-30,-30,-30,-30,-30,-50
            }
    );

    private final String name;
    private Game game;
    private Board board;
    private boolean isPlayingWhite;
    private final int searchDepth = 5;
    private ArrayList<String> openings;
    private final String moveRegex = "(%1$d\\.) %2$s %2$s";

    /**
     * ChessAI constructor
     */
    public ChessAI(String name) {
        this.name = name;
        openings = new ArrayList<>();

        // Read opening book into string
        Scanner reader = null;
        try {
            reader = new Scanner(getClass().getResourceAsStream("/openings.txt"));
        } catch (Exception ignored) {}
        while (reader.hasNext()) {
            openings.add(reader.nextLine());
        }
    }

    /**
     * Sets the game the AI is playing in
     * @param game a Game object
     */
    public void setGame(Game game) {
        this.game = game;
        this.board = game.getBoard();
    }

    /**
     * Sets the team the AI is on
     * @param isPlayingWhite whether the AI is playing white
     */
    public void setTeam(boolean isPlayingWhite) {
        this.isPlayingWhite = isPlayingWhite;
    }

    /**
     * Returns whether the AI is playing white
     * @return whether the AI is playing white
     */
    public boolean isPlayingWhite() {
        return isPlayingWhite;
    }

    /**
     * Gets the AI's name
     * @return the AI's name
     */
    public String getName() {
        return name;
    }

    /**
     * Chooses a move to play
     */
    public void move() {
        // Filter openings by moves played in this game
        openings.removeIf(s -> !s.startsWith(game.getPGNString()));
        String move = null;
        boolean bookMovePlayed = false;

        if (!openings.isEmpty()) {
            // Find next move from opening
            String chosenOpening = openings.get((int) (Math.random() * openings.size()));
            int moveNumber = game.getMoveNumber();
            String regex = String.format(moveRegex, moveNumber, MoveHandler.PATTERN);
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(chosenOpening);

            if (matcher.find()) { // If a match is found, play the move
                String[] result = matcher.group(0).split(" ");
                move = isPlayingWhite ? result[1] : result[2];
                bookMovePlayed = game.move(move);
            }
        }

        if (bookMovePlayed) {
            // Print out last move and save board
            game.getIO().print("I play " + move);
            game.saveBoardAndMove(move, game.getLastMoved(), game.getLastMovedInitialCoords(), true);
        } else { // If there are no more book moves, play based on minimax search
            Object[] evaluation = minimax(board, searchDepth, Integer.MIN_VALUE, Integer.MAX_VALUE, true,
                    isPlayingWhite, null);
            Move bestMove = (Move) evaluation[0];

            // Get parameters for game move piece method
            Point initialCoords = bestMove.getInitialCoords();
            Piece piece = board.get(initialCoords);
            int type = piece.getType();
            Point destination = bestMove.getDestination();
            Point capturedCoords = bestMove.getCapturedCoords();
            Piece captured = board.get(capturedCoords);

            // Get specifier if needed
            int team = isPlayingWhite ? Piece.WHITE : Piece.BLACK;
            String specifier = "";
            for (Piece teamPiece : board.getTeamPieces(team)) {
                if (teamPiece != piece && teamPiece.getType() == piece.getType()) {
                    for (Move teamPieceMove : Piece.getMoves(board, teamPiece)) {
                        if (teamPieceMove.getDestination().equals(bestMove.getDestination())) {
                            Point teamPieceCoords = teamPiece.getLocation();
                            if (initialCoords.x == teamPieceCoords.x) {
                                specifier = MoveHandler.toSquare(initialCoords).substring(0, 1);
                            } else {
                                specifier = MoveHandler.toSquare(initialCoords).substring(1);
                            }
                        }
                    }
                }
            }

            // Check castle
            if (piece.getType() == Piece.KING) {
                if (destination.x == initialCoords.x + 2) { // Short castle
                    Point rookCoords = new Point(initialCoords.x + 3, initialCoords.y);
                    Point rookDestination = new Point(destination.x - 1, destination.y);
                    game.legallyMovePiece(board, new Move(board.get(rookCoords), rookCoords, rookDestination,
                                    null, null), isPlayingWhite);
                } else if (destination.x == initialCoords.x - 2) { // Long castle
                    Point rookCoords = new Point(initialCoords.x - 4, initialCoords.y);
                    Point rookDestination = new Point(destination.x + 1, destination.y);
                    game.legallyMovePiece(board, new Move(board.get(rookCoords), rookCoords, rookDestination,
                                    null, null), isPlayingWhite);
                }
            }
            game.legallyMovePiece(board, bestMove, isPlayingWhite);

            // Check promotion
            if (piece.getType() == Piece.PAWN && ((isPlayingWhite && destination.y == 7) ||
                    (!isPlayingWhite && destination.y == 0))) {
                piece.setType(Piece.QUEEN); // Promote to queen by default (can optimize this later)
            }

            // Print out last move and save board
            String lastMove = MoveHandler.toMoveNotation(piece, type, specifier, initialCoords, destination, captured);
            game.getIO().print(String.format("I play %s.", lastMove));
            game.saveBoardAndMove(lastMove, piece, initialCoords, true);
        }

        // Check game end, prompt next move from player
        game.toggleTurn();
        game.checkGameEnd();
        if (!game.ended()) {
            game.promptNextMove();
        }
    }

    /**
     * Gets all possible moves for the current position
     * @return a HashMap of all possible moves and their pieces
     */
    public ArrayList<Move> getPossibleMoves(Board theBoard, int team) {
        ArrayList<Move> possibleMoves = new ArrayList<>();

        // Get all possible moves
        for (Piece piece : theBoard.getTeamPieces(team)) {
            for (Move move : Piece.getMoves(theBoard, piece)) {
                if (piece.getType() == Piece.PAWN) {
                    boolean enPassant = game.checkEnPassant(theBoard, move);
                    Point initialCoords = move.getInitialCoords();
                    Point destination = move.getDestination();
                    Piece captured = move.getCaptured();
                    Point capturedCoords = move.getCapturedCoords();

                    // If not en passant and the destination is on a different column and the
                    // move is not a capture, it's an illegal move
                    if (!enPassant) {
                        if (captured == null && destination.x != initialCoords.x) {
                            continue;
                        }
                        // Or if something is captured but the destination and captured coordinates are different,
                        // it's illegal
                        else if (capturedCoords != null && !capturedCoords.equals(destination)) {
                            continue;
                        }
                    }
                }
                // Check en passant
                boolean enPassant = game.checkEnPassant(theBoard, move);

                // Add move
                possibleMoves.add(move);
            }
        }

        return possibleMoves;
    }

    /**
     * Evaluates the current position
     * @param theBoard the board to evaluate the position for
     * @param isMaximizerWhite whether white is trying to maximize the evaluation
     * @return the position evaluation in terms of material
     */
    public int evaluatePosition(Board theBoard, boolean isMaximizerWhite) {
        int whiteEval = 0;
        int blackEval = 0;
        int multiplier = 3;
        boolean endgame = (board.getTeamPieces(Piece.WHITE).size() == 8 &&
                board.getTeamPieces(Piece.BLACK).size() == 8); // Decide whether it's endgame by counting pieces

        // Add up material for both sides
        for (Piece piece : theBoard.getTeamPieces(Piece.WHITE)) {
            Point loc = piece.getLocation();

            whiteEval += pieceValues.get(piece.getType());

            // Add piece square table value
            int tableLoc = loc.x + (Board.SIZE - loc.y - 1) * Board.SIZE;
            if (piece.getType() == Piece.KING && endgame) {
                whiteEval += multiplier * pieceSquareTables.get(Piece.KING_ENDGAME)[tableLoc];
            } else {
                whiteEval += multiplier * pieceSquareTables.get(piece.getType())[tableLoc];
            }
        }
        for (Piece piece : theBoard.getTeamPieces(Piece.BLACK)) {
            Point loc = piece.getLocation();

            blackEval += pieceValues.get(piece.getType());

            // Add piece square table value
            int tableLoc = loc.x + (Board.SIZE - loc.y - 1) * Board.SIZE;
            if (piece.getType() == Piece.KING && endgame) {
                blackEval += multiplier * pieceSquareTables.get(Piece.KING_ENDGAME)[tableLoc];
            } else {
                blackEval += multiplier * pieceSquareTables.get(piece.getType())[tableLoc];
            }
        }

        // Return difference based on whose turn it is
        return isMaximizerWhite ? whiteEval - blackEval : blackEval - whiteEval;
    }

    /**
     * Minimax algorithm to calculate best next move
     * @param theBoard the chessboard
     * @param depth the depth of moves to search
     * @param alpha the best evaluation the maximizer can achieve for the current position
     * @param beta the best evaluation the minimizer can achieve for the current position
     * @param maximizer whether the current search is for the maximizer
     * @param isMaximizerWhite whether white is trying to maximize the evaluation
     * @return the best move and its evaluation
     */
    public Object[] minimax(Board theBoard, int depth, int alpha, int beta, boolean maximizer,
                            boolean isMaximizerWhite, ArrayDeque<Object[]> bestMoveEvaluations) {
        if (depth <= 0) { // Return just the evaluation of the current position
            return new Object[] {null, evaluatePosition(theBoard, isMaximizerWhite)};
        }

        // If best move has already been calculated, return it
        if (bestMoveEvaluations != null && !bestMoveEvaluations.isEmpty()) {
            Deque<Object[]> bestMoveEvalsCopy = new ArrayDeque<>(bestMoveEvaluations);
            return bestMoveEvalsCopy.removeFirst();
        }

        Move bestMove;

        if (maximizer) { // Maximizing player
            int team = isPlayingWhite ? Piece.WHITE : Piece.BLACK;
            int opps = isPlayingWhite ? Piece.BLACK : Piece.WHITE;

            int maxEval = Integer.MIN_VALUE;
            ArrayList<Move> possibleMoves = orderMoves(team, getPossibleMoves(theBoard, team));
            if (possibleMoves.size() == 0) { // Has no moves = game ended
                if (game.isInCheck(theBoard)) { // Getting checkmated
                    return new Object[] {null, Integer.MIN_VALUE};
                }
                return new Object[] {null, 0}; // Draw
            }

            bestMove = possibleMoves.get((int) (Math.random() * possibleMoves.size()));

            // Loop through possible moves
            for (Move move : possibleMoves) {
                Piece piece = move.getPiece();
                Point initialCoords = move.getInitialCoords();
                Piece captured = move.getCaptured();
                Point capturedCoords = move.getCapturedCoords();

                // Make the move
                boolean validMove = game.legallyMovePiece(theBoard, move, isPlayingWhite);
                if (!validMove) {
                    continue;
                }

                // Get the evaluation by recursively calling minimax at depth - 1
                Object[] evaluation = minimax(theBoard, depth - 1, alpha, beta, false, isMaximizerWhite,
                        bestMoveEvaluations);

                // Undo the move
                game.undoMovePiece(theBoard, piece, initialCoords, captured, capturedCoords,
                        theBoard.getTeamPieces(opps));

                // Store max evaluation and best move
                int evaluationValue = (int) evaluation[1];
                if (evaluationValue > maxEval) {
                    maxEval = evaluationValue;
                    bestMove = move;
                }

                // Prune branch as needed
                alpha = Integer.max(alpha, evaluationValue);
                if (beta <= alpha) {
                    break;
                }
            }
            return new Object[]{bestMove, maxEval};
        } else { // Minimizing player
            int team = isPlayingWhite ? Piece.BLACK : Piece.WHITE;
            int opps = isPlayingWhite ? Piece.WHITE : Piece.BLACK;

            int minEval = Integer.MAX_VALUE;
            ArrayList<Move> possibleMoves = orderMoves(team, getPossibleMoves(theBoard, team));
            if (possibleMoves.size() == 0) { // Has no moves = game ended
                if (game.isInCheck(theBoard)) { // Getting checkmated
                    return new Object[] {null, Integer.MAX_VALUE};
                }
                return new Object[] {null, 0}; // Draw
            }

            bestMove = possibleMoves.get((int) (Math.random() * possibleMoves.size()));

            // Loop through possible moves
            for (Move move : possibleMoves) {
                Piece piece = move.getPiece();
                Point initialCoords = move.getInitialCoords();
                Piece captured = move.getCaptured();
                Point capturedCoords = move.getCapturedCoords();

                // Make the move
                boolean validMove = game.legallyMovePiece(theBoard, move, !isPlayingWhite);
                if (!validMove) {
                    continue;
                }

                // Get the evaluation by recursively calling minimax at depth - 1
                Object[] evaluation = minimax(theBoard, depth - 1, alpha, beta, true, isMaximizerWhite,
                        bestMoveEvaluations);

                // Undo the move
                game.undoMovePiece(theBoard, piece, initialCoords, captured, capturedCoords,
                        theBoard.getTeamPieces(opps));

                // Store min evaluation and best move
                int evaluationValue = (int) evaluation[1];
                if (evaluationValue < minEval) {
                    minEval = evaluationValue;
                    bestMove = move;
                }

                // Prune branch as needed
                beta = Integer.min(beta, evaluationValue);
                if (beta <= alpha) {
                    break;
                }
            }
            return new Object[]{bestMove, minEval};
        }
    }

    /**
     * Orders a list of moves based on a move score guess
     * @param team the team the moves are for
     * @param moves the list of moves to order
     * @return a list of ordered moves
     */
    public ArrayList<Move> orderMoves(int team, ArrayList<Move> moves) {
        int oppTeam = (team == Piece.WHITE) ? Piece.BLACK : Piece.WHITE;
        ArrayList<Move> movesOrdered = new ArrayList<>();

        for (Move move : moves) {
            int moveScoreGuess = 0;
            Piece piece = move.getPiece();
            Piece captured = move.getCaptured();

            // Prioritize capturing the opps' higher value pieces with our lower value pieces
            if (captured != null) {
                moveScoreGuess = 3 * pieceValues.get(captured.getType()) - pieceValues.get(piece.getType());
            }

            // Penalize if opponent pawns can attack the square
            for (Piece opp : board.getTeamPieces(oppTeam)) {
                if (opp.getType() == Piece.PAWN) {
                    Point oppLocation = opp.getLocation();

                    // Check all opp pawn moves
                    for (Move oppMove : Piece.getPawnMoves(board, opp, oppLocation.x, oppLocation.y)) {
                        Point oppCoords = oppMove.getInitialCoords();
                        Point oppDestination = oppMove.getDestination();
                        if (oppDestination.x != oppCoords.x && oppDestination.equals(move.getDestination())) {
                            moveScoreGuess -= pieceValues.get(piece.getType());
                        }
                    }
                }
            }

            // Reward for promotion
            if (move.getPiece().getType() == Piece.PAWN &&
                    (move.getDestination().y == 7 || move.getDestination().y == 0)) {
                moveScoreGuess += pieceValues.get(Piece.QUEEN);
            }

            move.setScore(moveScoreGuess);

            // Add move to list based on score
            if (movesOrdered.isEmpty()) {
                movesOrdered.add(move);
            } else {
                int i = 0;
                int currentMoveScore = movesOrdered.get(i).getScore();
                while (move.getScore() < currentMoveScore) {
                    i++;
                    if (i == movesOrdered.size()) {
                        movesOrdered.add(move);
                    }
                    currentMoveScore = movesOrdered.get(i).getScore();
                }
                if (i < movesOrdered.size()) {
                    movesOrdered.add(i, move);
                }
            }
        }

        return movesOrdered;
    }

    /**
     * Uses an iterative deepening approach to find the best move in a reasonable amount of time
     * @param theBoard the chessboard
     * @param depth the maximum depth to search
     * @param alpha the best evaluation the maximizer can achieve for the current position
     * @param beta the best evaluation the minimizer can achieve for the current position
     * @param maximizer whether the current search is for the maximizer
     * @param isMaximizerWhite whether white is trying to maximize the evaluation
     * @return the best move based on how many searches were possible
     */
    public Object[] iterativeDeepening(Board theBoard, int depth, int alpha, int beta, boolean maximizer,
                                   boolean isMaximizerWhite) {
        // Set time limit for running minimax
        long timeLimitSeconds = 2;
        long start = System.currentTimeMillis();
        long end = start + timeLimitSeconds * 1000;

        Object[] bestEvaluation = null;
        ArrayDeque<Object[]> bestMoveEvaluations = new ArrayDeque<>();

        // Run until time limit is up
        int i = 1;
        while (System.currentTimeMillis() < end && i < depth) {
            // Start with search depth of 1, then deepen iteratively
            Object[] evaluation = minimax(theBoard, depth, alpha, beta, maximizer, isMaximizerWhite,
                    bestMoveEvaluations);
            bestEvaluation = evaluation;
            bestMoveEvaluations.add(evaluation);
            i++;
        }

        return bestEvaluation;
    }

}
