/**
 * Queen class
 */
public class Queen extends Piece {
    // String representation
    private static final String letter = "q";

    /**
     * Queen constructor
     * @param team white or black
     * @param row the x coordinate
     * @param col the y coordinate
     */
    public Queen(Team team, int row, int col) {
        super(
            // Value of str is based on team (lower/upper case)
            (team == Team.WHITE) ? letter : letter.toUpperCase(), 
            team, 
            row, 
            col);
    }

    /**
     * Queen move:
     */
    public boolean move(String move) {
        // TODO
        return false;
    }
}

