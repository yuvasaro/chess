/**
 * Rook class
 */
public class Rook extends Piece {
    // String representation
    private static final String letter = "r";

    /**
     * Rook constructor
     * @param team white or black
     * @param row the x coordinate
     * @param col the y coordinate
     */
    public Rook(Team team, int row, int col) {
        super(
            // Value of str is based on team (lower/upper case)
            (team == Team.WHITE) ? letter : letter.toUpperCase(), 
            team, 
            row, 
            col);
    }

    /**
     * Rook move:
     */
    public boolean move(String move) {
        // TODO
        return false;
    }
}
