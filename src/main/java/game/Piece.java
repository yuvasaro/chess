package game;

import java.util.ArrayList;
import java.awt.Point;

/**
 * Abstract Piece class
 */
public abstract class Piece {
    // Instance variables
    private final Board board;
    private final String str;
    private final Team team;
    private Point coords;
    private String image;
    private boolean hasMoved;
    private int numMoves;

    /**
     * Piece constructor
     * @param team white or black
     * @param row the x coordinate of the piece
     * @param col the y coordinate of the piece
     */
    public Piece(Board board, String str, String image, Team team, 
            int row, int col) {
        // Value of str is based on team (lower/upper case)
        this.board = board;
        this.str = str;
        this.team = team;
        this.coords = new Point(row, col);
        this.image = (team == Team.WHITE) ? 
            String.format(image, "white") : String.format(image, "black");
        this.hasMoved = false;
    }

    /**
     * toString() getter for str
     * @return the string representation of the piece
     */
    public String toString() {
        return str;
    }

    /**
     * Getter for board
     * @return the chessboard
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Getter for team
     * @return the team the piece belongs to
     */
    public Team getTeam() {
        return team;
    }

    /**
     * Getter for coords
     * @return the position of the piece
     */
    public Point getCoords() {
        return coords;
    }

    /**
     * Returns the path to the PNG image of the piece
     * @return the path to the PNG image of the piece
     */
    public String getImagePath() {
        return image;
    }

    /**
     * Returns whether the piece has moved
     * @return whether the piece has moved
     */
    public boolean hasMoved() {
        return hasMoved;
    }

    /**
     * Returns the piece's number of moves
     * @return the number of times this piece has moved
     */
    public int moveCount() {
        return numMoves;
    }

    /**
     * Sets the moved state
     * @param moved the new moved state
     */
    public void setMoved(boolean moved) {
        hasMoved = moved;
    }

    /**
     * Moves the piece to a new square on the board
     * @param newCoords the new coordinates to move to
     */
    public void move(Point newCoords) {
        coords = newCoords;
        numMoves++;
        if (!hasMoved) {
            setMoved(true);
        }
    }

    /**
     * Undoes the piece's move
     * @param oldCoords the old coordinates of the piece
     */
    public void undoMove(Point oldCoords) {
        coords = oldCoords;
        numMoves--;
        if (numMoves == 0) {
            setMoved(false);
        }
    }

    /**
     * Move method - separate implementation for each piece
     * @return a list of possible squares to move to
     */
    public abstract ArrayList<Point> getMoves();

    /**
     * Returns a copy of this piece for another board
     * @param otherBoard the other board
     * @return the copy of the piece
     */
    public abstract Piece copyInstance(Board otherBoard);
}

/**
 * Enum for teams white and black
 */
enum Team {
    WHITE, BLACK
}