/**
 * Pawn class
 */
public class Pawn extends Piece {
    // String representation
    private static final String letter = "p";

    /**
     * Pawn constructor
     * @param team white or black
     * @param row the x coordinate
     * @param col the y coordinate
     */
    public Pawn(Team team, int row, int col) {
        super(letter, team, row, col);
    }

    /**
     * Pawn move: 1 square forward (2 squares if first turn), capture
     * diagonally 1 square, promotion at the opposite side
     */
    public boolean move(String move) {
        // TODO
        return false;
    }
}
