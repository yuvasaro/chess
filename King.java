/**
 * King class
 */
public class King extends Piece {
    // String representation
    private static final String letter = "k";

    /**
     * King constructor
     * @param team white or black
     * @param row the x coordinate
     * @param col the y coordinate
     */
    public King(Team team, int row, int col) {
        super(
            // Value of str is based on team (lower/upper case)
            (team == Team.WHITE) ? letter : letter.toUpperCase(), 
            team, 
            row, 
            col);
    }

    /**
     * King move:
     */
    public boolean move(String move) {
        // TODO
        return false;
    }
}