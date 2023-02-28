import java.awt.Point;

/**
 * Abstract Piece class
 */
public abstract class Piece {
    // Instance variables
    private final String str;
    private final Team team;
    private Point coords;

    /**
     * Piece constructor
     * @param team white or black
     * @param row the x coordinate of the piece
     * @param col the y coordinate of the piece
     */
    public Piece(String str, Team team, int row, int col) {
        this.str = str;
        this.team = team;
        this.coords = new Point(row, col);
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
     * Move method - separate implementation for each piece
     * @return whether the move was valid and successful
     */
    public abstract boolean move(String move);
}

/**
 * Enum for teams white and black
 */
enum Team {
    WHITE, BLACK
}