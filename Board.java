import java.awt.Point;
import java.util.Map;

/**
 * Board class
 */
public class Board {
    public static final int SIZE = 8;

    private Piece[][] board;
    private Map<String, Integer> pieceValues = Map.of(
        "p", 1,
        "n", 3,
        "b", 3,
        "r", 5,
        "q", 9,
        "k", 0
    );

    /**
     * Board constructor
     */
    public Board() {
        board = new Piece[SIZE][SIZE];

        // Piece order: Rook Knight Bishop Queen King Bishop Knight Rook

        // Set up White pieces
        board[0][0] = new Rook(this, Team.WHITE, 0, 0);
        board[1][0] = new Knight(this, Team.WHITE, 1, 0);
        board[2][0] = new Bishop(this, Team.WHITE, 2, 0);
        board[3][0] = new Queen(this, Team.WHITE, 3, 0);
        board[4][0] = new King(this, Team.WHITE, 4, 0);
        board[5][0] = new Bishop(this, Team.WHITE, 5, 0);
        board[6][0] = new Knight(this, Team.WHITE, 6, 0);
        board[7][0] = new Rook(this, Team.WHITE, 7, 0);

        for (int i = 0; i < SIZE; i++) {
            board[i][1] = new Pawn(this, Team.WHITE, i, 1);
        }

        // Set up Black pieces
        board[0][7] = new Rook(this, Team.BLACK, 0, 7);
        board[1][7] = new Knight(this, Team.BLACK, 1, 7);
        board[2][7] = new Bishop(this, Team.BLACK, 2, 7);
        board[3][7] = new Queen(this, Team.BLACK, 3, 7);
        board[4][7] = new King(this, Team.BLACK, 4, 7);
        board[5][7] = new Bishop(this, Team.BLACK, 5, 7);
        board[6][7] = new Knight(this, Team.BLACK, 6, 7);
        board[7][7] = new Rook(this, Team.BLACK, 7, 7);

        for (int i = 0; i < SIZE; i++) {
            board[i][6] = new Pawn(this, Team.BLACK, i, 6);
        }
    }

    /**
     * toString() for the board
     * @return a string displaying the chessboard
     */
    public String toString() {
        String display = "";

        for (int j = SIZE - 1; j >= 0; j--) {
            for (int i = 0; i < SIZE; i++) {
                Piece piece = board[i][j];
                if (piece == null) {
                    display += ". ";
                } else {
                    display += board[i][j] + " ";
                }
            }
            display += "\n";
        }

        return display;
    }

    /**
     * Getter for board
     * @return the 2D array of pieces
     */
    public Piece[][] getBoard() {
        return board;
    }

    /**
     * Returns whether the given coordinates are in bounds
     * @param coords the coordinates to check
     * @return whether the coordinates are in bounds
     */
    public boolean isInBounds(Point coords) {
        return coords.x >= 0 && coords.x < SIZE && 
            coords.y >= 0 && coords.y < SIZE;
    }

    /**
     * Gets the piece on a given square's coordinates
     * @param coords the coordinates of the square
     * @return the piece on the square (null if no piece)
     */
    public Piece getPiece(Point coords) {
        if (!isInBounds(coords)) {
            return null;
        }
        return board[coords.x][coords.y];
    }

    /**
     * Gets the value (in points) of a piece
     * @param piece the piece
     * @return the value of the piece
     */
    public int getPieceValue(Piece piece) {
        return pieceValues.get(piece.toString().toLowerCase());
    }

    /**
     * Moves a piece by changing the piece's location on the board
     * @param piece the piece to move
     * @param newCoords the new position to move the piece to
     * @return whether the move was successful
     */
    public boolean movePiece(Piece piece, Point newCoords) {
        // TODO
        return false;
    }

    /**
     * Main method for testing
     * @param args
     */
    public static void main(String[] args) {
        Board board = new Board();
        System.out.println(board);

        Piece bishop = board.getPiece(new Point(2, 0));
        System.out.println(bishop);
        System.out.println(bishop.getCoords());
        System.out.println(bishop.getMoves());
    }
}
