package com.ook.game;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Board class
 */
public class Board {
    public static final int SIZE = 8;
    private final Piece[][] board;
    private final ArrayList<Piece> whitePieces;
    private final ArrayList<Piece> blackPieces;
    private final HashMap<Piece, Integer> moveCounter;

    /**
     * Board constructor
     */
    public Board() {
        board = new Piece[SIZE][SIZE];
        whitePieces = new ArrayList<>();
        blackPieces = new ArrayList<>();
        moveCounter = new HashMap<>();

        // Piece order: Rook Knight Bishop Queen King Bishop Knight Rook

        // Set up White pieces
        setUpPiece(Piece.WHITE, Piece.ROOK, new Point(0, 0));
        setUpPiece(Piece.WHITE, Piece.KNIGHT, new Point(1, 0));
        setUpPiece(Piece.WHITE, Piece.BISHOP, new Point(2, 0));
        setUpPiece(Piece.WHITE, Piece.QUEEN, new Point(3, 0));
        setUpPiece(Piece.WHITE, Piece.KING, new Point(4, 0));
        setUpPiece(Piece.WHITE, Piece.BISHOP, new Point(5, 0));
        setUpPiece(Piece.WHITE, Piece.KNIGHT, new Point(6, 0));
        setUpPiece(Piece.WHITE, Piece.ROOK, new Point(7, 0));

        for (int i = 0; i < SIZE; i++) {
            setUpPiece(Piece.WHITE, Piece.PAWN, new Point(i, 1));
        }

        // Set up Black pieces
        setUpPiece(Piece.BLACK, Piece.ROOK, new Point(0, 7));
        setUpPiece(Piece.BLACK, Piece.KNIGHT, new Point(1, 7));
        setUpPiece(Piece.BLACK, Piece.BISHOP, new Point(2, 7));
        setUpPiece(Piece.BLACK, Piece.QUEEN, new Point(3, 7));
        setUpPiece(Piece.BLACK, Piece.KING, new Point(4, 7));
        setUpPiece(Piece.BLACK, Piece.BISHOP, new Point(5, 7));
        setUpPiece(Piece.BLACK, Piece.KNIGHT, new Point(6, 7));
        setUpPiece(Piece.BLACK, Piece.ROOK, new Point(7, 7));

        for (int i = 0; i < SIZE; i++) {
            setUpPiece(Piece.BLACK, Piece.PAWN, new Point(i, 6));
        }
    }

    /**
     * Board constructor that copies another board
     * @param otherBoard the other chessboard
     */
    public Board(Board otherBoard) {
        board = new Piece[SIZE][SIZE];
        whitePieces = new ArrayList<>();
        blackPieces = new ArrayList<>();
        moveCounter = new HashMap<>();

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                Piece otherPiece = otherBoard.get(i, j);
                if (otherPiece == null) {
                    continue;
                }
                setUpPiece(otherPiece.getTeam(), otherPiece.getType(), new Point(i, j));
            }
        }
    }

    /**
     * Sets up a piece on the board
     * @param team the piece's team
     * @param type the piece's integer value
     * @param location the location on the board
     */
    public void setUpPiece(int team, int type, Point location) {
        Piece piece = new Piece(team, type, location);
        board[location.x][location.y] = piece;
        if (team == Piece.WHITE) {
            whitePieces.add(piece);
        } else {
            blackPieces.add(piece);
        }
        moveCounter.put(piece, 0);
    }

    /**
     * Getter for board
     * @return the 2D array of pieces
     */
    public Piece[][] getBoard() {
        return board;
    }

    /**
     * Returns the board flipped
     * @return the board flipped
     */
    public Piece[][] flipBoard() {
        Piece[][] flipped = new Piece[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                flipped[SIZE - i - 1][SIZE - j - 1] = board[i][j];
            }
        }
        return flipped;
    }

    /**
     * Returns whether the given coordinates are in bounds
     * @param x the x coordinate
     * @param y the y coordinate
     * @return whether the coordinates are in bounds
     */
    public boolean isInBounds(int x, int y) {
        return x >= 0 && x < SIZE &&
            y >= 0 && y < SIZE;
    }

    /**
     * Gets the piece on a given square's coordinates
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the piece on the square (0 if no piece)
     */
    public Piece get(int x, int y) {
        if (!isInBounds(x, y)) {
            return null;
        }
        return board[x][y];
    }

    /**
     * Gets the piece on a given square's coordinates
     * @param coords the coordinates
     * @return the piece on the square (0 if no piece)
     */
    public Piece get(Point coords) {
        if (coords == null) {
            return null;
        }
        if (!isInBounds(coords.x, coords.y)) {
            return null;
        }
        return board[coords.x][coords.y];
    }

    /**
     * Sets the piece at a given square on the board
     * @param coords the coordinates
     * @param piece the piece to set
     */
    public void set(Point coords, Piece piece) {
        if (!isInBounds(coords.x, coords.y)) {
            return;
        }
        board[coords.x][coords.y] = piece;
        if (piece != null) {
            piece.setLocation(coords);
        }
    }

    /**
     * Moves a piece to new location
     * @param piece the piece to move
     * @param coords the coordinates to move the piece to
     */
    public void move(Piece piece, Point coords) {
        Point current = piece.getLocation();
        set(coords, piece);
        set(current, null);
        moveCounter.put(piece, moveCounter.get(piece) + 1);
    }

    /**
     * Undo move a piece to its old location
     * @param piece the piece to move
     * @param coords the coordinates to move the piece back to
     */
    public void undoMove(Piece piece, Point coords) {
        Point current = piece.getLocation();
        set(coords, piece);
        set(current, null);
        moveCounter.put(piece, moveCounter.get(piece) - 1);
    }

    /**
     * Returns the HashMap of pieces for the given team
     * @param team the team to get the pieces for
     * @return the locations and pieces for the given team
     */
    public ArrayList<Piece> getTeamPieces(int team) {
        return (team == Piece.WHITE) ? whitePieces : blackPieces;
    }

    /**
     * Gets the move counter HashMap
     * @return a HashMap of pieces and their move counts
     */
    public HashMap<Piece, Integer> getMoveCounter() {
        return moveCounter;
    }
}
