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
        board[0][0] = new Rook(Team.WHITE, 0, 0);
        board[1][0] = new Knight(Team.WHITE, 0, 1);
        board[2][0] = new Bishop(Team.WHITE, 0, 2);
        board[3][0] = new Queen(Team.WHITE, 0, 3);
        board[4][0] = new King(Team.WHITE, 0, 4);
        board[5][0] = new Bishop(Team.WHITE, 0, 5);
        board[6][0] = new Knight(Team.WHITE, 0, 6);
        board[7][0] = new Rook(Team.WHITE, 0, 7);

        for (int i = 0; i < SIZE; i++) {
            board[i][1] = new Pawn(Team.WHITE, i, 1);
        }

        // Set up Black pieces
        board[0][7] = new Rook(Team.BLACK, 7, 0);
        board[1][7] = new Knight(Team.BLACK, 7, 1);
        board[2][7] = new Bishop(Team.BLACK, 7, 2);
        board[3][7] = new Queen(Team.BLACK, 7, 3);
        board[4][7] = new King(Team.BLACK, 7, 4);
        board[5][7] = new Bishop(Team.BLACK, 7, 5);
        board[6][7] = new Knight(Team.BLACK, 7, 6);
        board[7][7] = new Rook(Team.BLACK, 7, 7);

        for (int i = 0; i < SIZE; i++) {
            board[i][6] = new Pawn(Team.BLACK, i, 6);
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
     * Gets the piece on a given square's coordinates
     * @param coords the coordinates of the square
     * @return the piece on the square (null if no piece)
     */
    public Piece getPiece(Point coords) {
        if (coords.x < 0 || coords.x >= SIZE || 
                coords.y < 0 || coords.y >= SIZE) {
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
     * Moves a piece by checking the validity of the move, then changing the
     * piece's location on the board
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

        Piece pawn = board.getPiece(new Point(0, 1));
        System.out.println(pawn);
        System.out.println(pawn.getMoves(board));
    }
}
