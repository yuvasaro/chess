/**
 * Knight class
 */
public class Knight extends Piece {
    // String representation
    private static final String letter = "n";

    /**
     * Knight constructor
     * @param team white or black
     * @param row the x coordinate
     * @param col the y coordinate
     */
    public Knight(Team team, int row, int col) {
        super(letter, team, row, col);
    }

    /**
     * Knight move:
     */
    public boolean move(String move) {
        // TODO
        return false;
    }
}
