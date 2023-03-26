package com.ook.game;

import java.util.ArrayList;
import java.awt.Point;

/**
 * Pawn class
 */
public class Pawn extends Piece {
    // String representation
    private static final String LETTER = "P";

    // Path to piece image
    private static final String IMAGE = "pawn_%s.png";

    // Whether the pawn has moved at least once
    private boolean hasMoved;

    // White moves forward, black moves backward
    private int moveDir;

    /**
     * Pawn constructor
     * @param board the chessboard
     * @param team white or black
     * @param row the x coordinate
     * @param col the y coordinate
     */
    public Pawn(Board board, Team team, int row, int col) {
        super(board, LETTER, IMAGE, team, row, col);
        hasMoved = false;
        moveDir = (team == Team.WHITE) ? 1 : -1;
    }

    /**
     * Returns a copy of this pawn for another board
     * @param otherBoard the other board
     */
    public Pawn copyInstance(Board otherBoard) {
        Point coords = getCoords();
        Pawn copy = new Pawn(otherBoard, getTeam(), coords.x, coords.y);
        copy.hasMoved = this.hasMoved;
        return copy;
    }

    /**
     * Pawn move: 1 square forward (2 squares if first turn), capture
     * diagonally 1 square, promotion at the opposite side
     * @return a list of possible squares to move to
     */
    public ArrayList<Point> getMoves() {
        ArrayList<Point> possibleMoves = new ArrayList<>();
        Point coords = getCoords();
        Board board = getBoard();

        // One square ahead
        Point oneSquareAhead = new Point(coords.x, coords.y + moveDir);
        Piece oneAheadPiece = board.getPiece(oneSquareAhead);
        if (oneAheadPiece == null) {
            possibleMoves.add(oneSquareAhead);
        }

        // Two squares ahead
        if (!hasMoved && oneAheadPiece == null &&
                ((coords.y == 1 && getTeam() == Team.WHITE) || (coords.y == 6 && getTeam() == Team.BLACK))) {
            Point twoSquaresAhead = new Point(coords.x, coords.y + 2 * moveDir);
            if (board.getPiece(twoSquaresAhead) == null) {
                possibleMoves.add(twoSquaresAhead);
            }
        }

        // Capture diagonally one square
        Point leftDiagonal = new Point(coords.x - 1, coords.y + moveDir);
        Piece leftDiagonalPiece = board.getPiece(leftDiagonal);
        Point rightDiagonal = new Point(coords.x + 1, coords.y + moveDir);
        Piece rightDiagonalPiece = board.getPiece(rightDiagonal);

        // Check that there is a piece diagonally that is on the opposite team
        if (leftDiagonalPiece instanceof Piece && 
                leftDiagonalPiece.getTeam() != this.getTeam()) {
            possibleMoves.add(leftDiagonal);
        }
        if (rightDiagonalPiece instanceof Piece && 
                rightDiagonalPiece.getTeam() != this.getTeam()) {
            possibleMoves.add(rightDiagonal);
        }

        // EN PASSANT bruh
        Team team = getTeam();
        // 5th rank as white or 4th rank as black
        if (team == Team.WHITE && coords.y == 4 || 
                team == Team.BLACK && coords.y == 3) {
            // Get pieces to the left and right
            Point left = new Point(coords.x - 1, coords.y);
            Piece leftPiece = board.getPiece(left);
            Point right = new Point(coords.x + 1, coords.y);
            Piece rightPiece = board.getPiece(right);

            // Check if left and right pieces are enemy pawns
            if (leftPiece instanceof Pawn) {
                if (leftPiece.getTeam() != this.getTeam()) {
                    if (leftPiece.moveCount() == 1) { // First move, 2 squares
                        possibleMoves.add(leftDiagonal);
                    }
                }
            }
            if (rightPiece instanceof Pawn) {
                if (rightPiece.getTeam() != this.getTeam()) {
                    if (rightPiece.moveCount() == 1) { // First move, 2 squares
                        possibleMoves.add(rightDiagonal);
                    }
                }
            }
        }

        return possibleMoves;
    }

    /**
     * Gets the pawn's specifier letter (ex. "e")
     * @return the pawn specifier
     */
    public String getPawnSpecifier() {
        return MoveHandler.toSquare(getCoords()).substring(0, 1);
    }
}
