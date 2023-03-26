package com.ook.game;

import java.awt.Point;
import java.util.ArrayList;

/**
 * Board class
 */
public class Board {
    public static final int SIZE = 8;
    
    // Board and pieces
    private Piece[][] board;
    private ArrayList<Piece> whitePieces;
    private ArrayList<Piece> blackPieces;

    /**
     * Board constructor
     */
    public Board() {
        board = new Piece[SIZE][SIZE];
        whitePieces = new ArrayList<>();
        blackPieces = new ArrayList<>();

        // Piece order: Rook Knight Bishop Queen King Bishop Knight Rook

        // Set up White pieces
        setUpPiece(new Rook(this, Team.WHITE, 0, 0));
        setUpPiece(new Knight(this, Team.WHITE, 1, 0));
        setUpPiece(new Bishop(this, Team.WHITE, 2, 0));
        setUpPiece(new Queen(this, Team.WHITE, 3, 0));
        setUpPiece(new King(this, Team.WHITE, 4, 0));
        setUpPiece(new Bishop(this, Team.WHITE, 5, 0));
        setUpPiece(new Knight(this, Team.WHITE, 6, 0));
        setUpPiece(new Rook(this, Team.WHITE, 7, 0));

        for (int i = 0; i < SIZE; i++) {
            setUpPiece(new Pawn(this, Team.WHITE, i, 1));
        }

        // Set up Black pieces
        setUpPiece(new Rook(this, Team.BLACK, 0, 7));
        setUpPiece(new Knight(this, Team.BLACK, 1, 7));
        setUpPiece(new Bishop(this, Team.BLACK, 2, 7));
        setUpPiece(new Queen(this, Team.BLACK, 3, 7));
        setUpPiece(new King(this, Team.BLACK, 4, 7));
        setUpPiece(new Bishop(this, Team.BLACK, 5, 7));
        setUpPiece(new Knight(this, Team.BLACK, 6, 7));
        setUpPiece(new Rook(this, Team.BLACK, 7, 7));

        for (int i = 0; i < SIZE; i++) {
            setUpPiece(new Pawn(this, Team.BLACK, i, 6));
        }
    }

    /**
     * Board constructor that duplicates another board
     * @param otherBoard the other board to copy
     */
    public Board(Board otherBoard) {
        board = new Piece[SIZE][SIZE];
        whitePieces = new ArrayList<>();
        blackPieces = new ArrayList<>();

        // Duplicate each piece on the other board
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                Piece otherPiece = otherBoard.getPiece(new Point(i, j));
                if (otherPiece == null) {
                    continue;
                }

                // Create a copy of the other piece
                Piece duplicatePiece = otherPiece.copyInstance(this);
                setUpPiece(duplicatePiece);
            }
        }
    }

    /**
     * Adds a piece to the board and the piece array for its team
     * @param piece the piece to add
     */
    public void setUpPiece(Piece piece) {
        Point coords = piece.getCoords();
        Team team = piece.getTeam();

        setPiece(piece, coords);
        if (team == Team.WHITE) {
            whitePieces.add(piece);
        } else {
            blackPieces.add(piece);
        }
    }

    /**
     * toString() for the board
     * @return a string displaying the chessboard
     */
    public String toString() {
        String display = "";

        for (int j = SIZE - 1; j >= 0; j--) {
            for (int i = 0; i < SIZE; i++) {
                Piece piece = board[i][j];
                if (piece == null) {
                    display += ". ";
                } else {
                    display += board[i][j] + " ";
                }
            }
            display += "\n";
        }

        return display;
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
     * Getter for whitePieces
     * @return ArrayList of white pieces
     */
    public ArrayList<Piece> getWhitePieces() {
        return whitePieces;
    }

    /**
     * Getter for blackPieces
     * @return ArrayList of black pieces
     */
    public ArrayList<Piece> getBlackPieces() {
        return blackPieces;
    }

    /**
     * Gets the pieces ArrayList for the current team
     * @param whiteToPlay whether it is white's turn
     * @return the ArrayList of pieces for the current team
     */
    public ArrayList<Piece> getTeamPieces(boolean whiteToPlay) {
        return whiteToPlay ? whitePieces : blackPieces;
    }

    /**
     * Returns whether the given coordinates are in bounds
     * @param coords the coordinates to check
     * @return whether the coordinates are in bounds
     */
    public boolean isInBounds(Point coords) {
        return coords.x >= 0 && coords.x < SIZE && 
            coords.y >= 0 && coords.y < SIZE;
    }

    /**
     * Gets the piece on a given square's coordinates
     * @param coords the coordinates of the square
     * @return the piece on the square (null if no piece)
     */
    public Piece getPiece(Point coords) {
        if (!isInBounds(coords)) {
            return null;
        }
        return board[coords.x][coords.y];
    }

    /**
     * Sets the piece on a given square
     * @param piece the piece to set
     * @param coords the coordinates to put the piece on
     */
    public void setPiece(Piece piece, Point coords) {
        if (!isInBounds(coords)) {
            return;
        }
        board[coords.x][coords.y] = piece;
    }

    /**
     * Moves a piece by changing the piece's location on the board
     * @param piece the piece to move
     * @param newCoords the new position to move the piece to
     * @return the piece that was captured during the move
     */
    public void movePiece(Piece piece, Point newCoords) {
        Point currentCoords = piece.getCoords();

        // Move given piece and return the original piece on that square
        setPiece(piece, newCoords);
        setPiece(null, currentCoords);
        piece.move(newCoords); // Changes coords of Piece object
    }

    /**
     * Undoes a piece move
     * @param piece the piece to move back
     * @param oldCoords the old position to move the piece to
     */
    public void undoMovePiece(Piece piece, Point oldCoords) {
        // Move piece back and reset captured piece
        setPiece(piece, oldCoords);
        piece.undoMove(oldCoords); // Changes coords of Piece object
    }
}
