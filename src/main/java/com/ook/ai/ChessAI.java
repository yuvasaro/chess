package com.ook.ai;

import com.ook.game.*;

import java.util.ArrayList;
import java.awt.Point;
import java.util.HashMap;

/**
 * Chess AI class
 */
public class ChessAI {
    private String name;
    private Game game;
    private Board board;
    private boolean isPlayingWhite;
    private ArrayList<Piece> teamPieces;
    private HashMap<Point, Piece> possibleMoves;
    private String lastMove;

    /**
     * ChessAI constructor
     */
    public ChessAI(String name) {
        this.name = name;
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
        this.teamPieces = isPlayingWhite ? board.getWhitePieces() : board.getBlackPieces();
        this.possibleMoves = new HashMap<>();
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
     * toString() method
     * @return string representation of the AI
     */
    public String toString() {
        return "Name: " + name + "\nPlaying White: " + isPlayingWhite;
    }

    /**
     * Gets the AI's last move
     * @return the AI's last move
     */
    public String getLastMove() {
        return lastMove;
    }

    /**
     * Chooses a move to play
     * @return true if move was successful
     */
    public boolean move() {
        possibleMoves.clear();
        for (Piece piece : teamPieces) {
            for (Point move : piece.getMoves()) {
                possibleMoves.put(move, piece);
            }
        }

        boolean legalMove = false;
        Point move = null;
        Piece piece = null;
        Point initialCoords = null;
        Piece captured = null;

        while (!legalMove) {
            int random = (int) (Math.random() * possibleMoves.size());
            move = (Point) possibleMoves.keySet().toArray()[random];
            piece = possibleMoves.get(move);
            initialCoords = piece.getCoords();
            captured = board.getPiece(move);
            legalMove = game.legallyMovePiece(board, piece, move);
            if (!legalMove) {
                possibleMoves.remove(move);
            }
            if (possibleMoves.isEmpty()) {
                return false;
            }
        }

        lastMove = MoveHandler.toMoveNotation(piece, initialCoords,move, captured);
        game.getIO().print("I play " + lastMove);
        game.saveBoardAndMove(lastMove, piece, initialCoords, true);
        game.toggleTurn();
        game.checkGameEnd();
        if (!game.ended()) {
            game.promptNextMove();
        }
        return true;
    }
}
