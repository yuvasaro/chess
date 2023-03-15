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
        this.str = (team == Team.WHITE) ? str.toLowerCase() : str.toUpperCase();
        this.team = team;
        this.coords = new Point(row, col);
        this.image = (team == Team.WHITE) ? 
            String.format(image, "white") : String.format(image, "black");
    }

    /**
     * Piece constructor that duplicates another piece
     * @param otherPiece the other piece to copy
     * @param otherBoard the board of the other piece
     */
    public Piece(Piece otherPiece, Board otherBoard) {
        this.board = otherBoard;
        this.str = otherPiece.str;
        this.team = otherPiece.team;
        this.coords = new Point(otherPiece.getCoords());
        this.image = otherPiece.image;
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
     * Moves the piece to a new square on the board
     * @param newCoords the new coordinates to move to
     */
    public void move(Point newCoords) {
        coords = newCoords;
    }

    /**
     * Move method - separate implementation for each piece
     * @return a list of possible squares to move to
     */
    public abstract ArrayList<Point> getMoves();
}

/**
 * Enum for teams white and black
 */
enum Team {
    WHITE, BLACK
}