import java.util.ArrayList;
import java.awt.Point;

/**
 * Abstract Piece class
 */
public abstract class Piece {
    // Unique ID for all pieces
    private static int currentID = 1;
    
    // Instance variables
    private final String str;
    private final Team team;
    private Point coords;
    private int id;

    /**
     * Piece constructor
     * @param team white or black
     * @param row the x coordinate of the piece
     * @param col the y coordinate of the piece
     */
    public Piece(String str, Team team, int row, int col) {
        // Value of str is based on team (lower/upper case)
        this.str = (team == Team.WHITE) ? str : str.toUpperCase();
        this.team = team;
        this.coords = new Point(row, col);
        this.id = currentID;
        currentID++;
    }

    /**
     * toString() getter for str
     * @return the string representation of the piece
     */
    public String toString() {
        return str;
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
     * Setter for coords
     * @param coords the new position of the piece
     */
    public void setCoords(Point coords) {
        this.coords = coords;
    }

    /**
     * Getter for id
     * @return the ID of the piece
     */
    public int getID() {
        return id;
    }

    /**
     * Move method - separate implementation for each piece
     * @param board the chessboard
     * @return a list of possible squares to move to
     */
    public abstract ArrayList<Point> getMoves(Board board);
}

/**
 * Enum for teams white and black
 */
enum Team {
    WHITE, BLACK
}