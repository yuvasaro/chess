import java.awt.Point;

/**
 * Board class
 */
public class Board {
    public static final int SIZE = 8;

    private Piece[][] board;

    /**
     * Board constructor
     */
    public Board() {
        board = new Piece[SIZE][SIZE];

        // Piece order: Rook Knight Bishop Queen King Bishop Knight Rook

        // Set up White pieces
        board[0][0] = new Rook(Team.WHITE, 0, 0);
        board[0][1] = new Knight(Team.WHITE, 0, 1);
        board[0][2] = new Bishop(Team.WHITE, 0, 2);
        board[0][3] = new Queen(Team.WHITE, 0, 3);
        board[0][4] = new King(Team.WHITE, 0, 4);
        board[0][5] = new Bishop(Team.WHITE, 0, 5);
        board[0][6] = new Knight(Team.WHITE, 0, 6);
        board[0][7] = new Rook(Team.WHITE, 0, 7);

        for (int i = 0; i < SIZE; i++) {
            board[1][i] = new Pawn(Team.WHITE, 1, i);
        }

        // Set up Black pieces
        board[7][0] = new Rook(Team.BLACK, 7, 0);
        board[7][1] = new Knight(Team.BLACK, 7, 1);
        board[7][2] = new Bishop(Team.BLACK, 7, 2);
        board[7][3] = new Queen(Team.BLACK, 7, 3);
        board[7][4] = new King(Team.BLACK, 7, 4);
        board[7][5] = new Bishop(Team.BLACK, 7, 5);
        board[7][6] = new Knight(Team.BLACK, 7, 6);
        board[7][7] = new Rook(Team.BLACK, 7, 7);

        for (int i = 0; i < SIZE; i++) {
            board[6][i] = new Pawn(Team.BLACK, 7, i);
        }
    }

    /**
     * toString() for the board
     * @return a string displaying the chessboard
     */
    public String toString() {
        String display = "";

        for (int i = SIZE - 1; i >= 0; i--) {
            for (int j = 0; j < SIZE; j++) {
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
        return board[coords.x][coords.y];
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
        System.out.println(board.getPiece(new Point(0, 1)).getMoves(board));
    }
}
