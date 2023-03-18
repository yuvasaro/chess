package ai;

import java.util.Map;

import game.Game;
import game.Board;
import game.Piece;

/**
 * Chess AI class
 */
public class ChessAI {
    private Game game;
    private Board board;
    private Map<String, Integer> pieceValues = Map.of(
        "P", 1,
        "N", 3,
        "B", 3,
        "R", 5,
        "Q", 9,
        "K", 0
    );

    /**
     * ChessAI constructor
     * @param board the chessboard
     */
    public ChessAI(Game game) {
        this.game = game;
    }

    /**
     * Evaluates the current board position
     * @return an integer representing which team has more material
     */
    public int evaluatePosition() {
        int whiteEval = 0;
        int blackEval = 0;

        // Get white and black material evaluations
        for (Piece piece : board.getWhitePieces()) {
            whiteEval += pieceValues.get(piece.toString());
        }
        for (Piece piece : board.getBlackPieces()) {
            blackEval += pieceValues.get(piece.toString());
        }

        // + means white is winning, - means black is winning
        return whiteEval - blackEval;
    }

}