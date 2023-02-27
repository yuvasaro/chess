/**
 * Abstract Piece class
 */
public abstract class Piece {
    // Instance variables
    private final String str;
    private final Team team;
    private int row;
    private int col;

    /**
     * Piece constructor
     * @param team white or black
     * @param row the x coordinate of the piece
     * @param col the y coordinate of the piece
     */
    public Piece(String str, Team team, int row, int col) {
        this.str = str;
        this.team = team;
        this.row = row;
        this.col = col;
    }

    /**
     * Getter for str
     * @return the string representation of the piece
     */
    public String getStr() {
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
     * Getter for row
     * @return the x coordinate of the piece
     */
    public int getRow() {
        return row;
    }

    /**
     * Setter for row
     * @param row the new x coordinate of the piece
     */
    public void setRow(int row) {
        this.row = row;
    }

    /**
     * Getter for col
     * @return the y coordinate of the piece
     */
    public int getCol() {
        return col;
    }

    /**
     * Setter for col
     * @param col the new y coordinate of the piece
     */
    public void setCol(int col) {
        this.col = col;
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