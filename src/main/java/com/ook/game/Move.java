package com.ook.game;

import java.awt.*;

/**
 * Class that represents a chess move
 */
public class Move {
    // Instance variables
    private final Piece piece;
    private final Point initialCoords;
    private final Point destination;
    private Piece captured;
    private Point capturedCoords;
    private int score;

    /**
     * Move constructor
     * @param piece the piece to move
     * @param initialCoords the initial coordinates of the piece
     * @param destination the destination of the piece
     * @param captured the captured enemy piece (null if no piece captured)
     */
    public Move (Piece piece, Point initialCoords, Point destination, Piece captured, Point capturedCoords) {
        this.piece = piece;
        this.initialCoords = initialCoords;
        this.destination = destination;
        this.captured = captured;
        this.capturedCoords = capturedCoords;
    }

    /**
     * Getter for piece
     * @return the piece of the move
     */
    public Piece getPiece() {
        return piece;
    }

    /**
     * Getter for initialCoords
     * @return the initial coordinates of the move
     */
    public Point getInitialCoords() {
        return initialCoords;
    }

    /**
     * Getter for destination
     * @return the destination of the move
     */
    public Point getDestination() {
        return destination;
    }

    /**
     * Getter for captured
     * @return the captured piece of the move
     */
    public Piece getCaptured() {
        return captured;
    }

    /**
     * Setter for captured
     * @param captured the captured piece of the move
     */
    public void setCaptured(Piece captured) {
        this.captured = captured;
    }

    /**
     * Getter for capturedCoords
     * @return the coordinates of the captured piece
     */
    public Point getCapturedCoords() {
        return capturedCoords;
    }

    /**
     * Setter for capturedCoords
     * @param capturedCoords the coordinates of the captured piece
     */
    public void setCapturedCoords(Point capturedCoords) {
        this.capturedCoords = capturedCoords;
    }

    /**
     * Gets the move score guess
     * @return the move score guess
     */
    public int getScore() {
        return score;
    }

    /**
     * Sets the move score guess
     * @param score the move score guess
     */
    public void setScore(int score) {
        this.score = score;
    }
}
