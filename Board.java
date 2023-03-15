import java.awt.Point;
import java.util.Map;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Board class
 */
public class Board {
    private static final int SIZE = 8;
    private static final String IMAGE = "assets/board.png";
    private static final String SAVE_IMAGE = "current_board.png";
    
    private Piece[][] board;
    private ArrayList<Piece> whitePieces;
    private ArrayList<Piece> blackPieces;

    // Piece values
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
        whitePieces = new ArrayList<>();
        blackPieces = new ArrayList<>();

        // Piece order: Rook Knight Bishop Queen King Bishop Knight Rook

        // Set up White pieces
        setUpPiece(new Rook(this, Team.WHITE, 0, 0));
        setUpPiece(new Knight(this, Team.WHITE, 1, 0));
        setUpPiece(new Bishop(this, Team.WHITE, 2, 0));
        setUpPiece(new Queen(this, Team.WHITE, 3, 0));
        setUpPiece(new King(this, Team.WHITE, 4, 0));
        setUpPiece(new Bishop(this, Team.WHITE, 5, 0));
        setUpPiece(new Knight(this, Team.WHITE, 6, 0));
        setUpPiece(new Rook(this, Team.WHITE, 7, 0));

        for (int i = 0; i < SIZE; i++) {
            setUpPiece(new Pawn(this, Team.WHITE, i, 1));
        }

        // Set up Black pieces
        setUpPiece(new Rook(this, Team.BLACK, 0, 7));
        setUpPiece(new Knight(this, Team.BLACK, 1, 7));
        setUpPiece(new Bishop(this, Team.BLACK, 2, 7));
        setUpPiece(new Queen(this, Team.BLACK, 3, 7));
        setUpPiece(new King(this, Team.BLACK, 4, 7));
        setUpPiece(new Bishop(this, Team.BLACK, 5, 7));
        setUpPiece(new Knight(this, Team.BLACK, 6, 7));
        setUpPiece(new Rook(this, Team.BLACK, 7, 7));

        for (int i = 0; i < SIZE; i++) {
            setUpPiece(new Pawn(this, Team.BLACK, i, 6));
        }
    }

    /**
     * Adds a piece to the board and the piece array for its team
     * @param piece the piece to add
     */
    public void setUpPiece(Piece piece) {
        Point coords = piece.getCoords();
        Team team = piece.getTeam();

        setPiece(piece, coords);
        if (team == Team.WHITE) {
            whitePieces.add(piece);
        } else {
            blackPieces.add(piece);
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
     * Getter for whitePieces
     * @return ArrayList of white pieces
     */
    public ArrayList<Piece> getWhitePieces() {
        return whitePieces;
    }

    /**
     * Getter for blackPieces
     * @return ArrayList of black pieces
     */
    public ArrayList<Piece> getBlackPieces() {
        return blackPieces;
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
     * Sets the piece on a given square
     * @param piece the piece to set
     * @param coords the coordinates to put the piece on
     */
    public void setPiece(Piece piece, Point coords) {
        if (!isInBounds(coords)) {
            return;
        }
        board[coords.x][coords.y] = piece;
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
    public Piece movePiece(Piece piece, Point newCoords) {
        Point currentCoords = piece.getCoords();
        Piece otherPiece = getPiece(newCoords);

        // Move given piece and return the original piece on that square
        setPiece(piece, newCoords);
        setPiece(null, currentCoords);
        piece.move(newCoords); // Changes coords of Piece object
        return otherPiece;
    }

    /**
     * Opens an image and returns its pixel array
     * @param imagePath the path to the image
     * @return the pixel array
     * @throws IOException if the image file is not found
     */
    private int[][] open(BufferedImage bufferedImage) throws IOException {
        // Get height and width
        int height = bufferedImage.getHeight();
        int width = bufferedImage.getWidth();

        // Image is stored in column-major order; traverse that way
        int[][] pixelArray = new int[width][height];
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                // Get each pixel's RGB
                pixelArray[i][j] = bufferedImage.getRGB(i, j);
            }
        }

        return pixelArray;
    }

    /**
     * Saves the current board position as a PNG image
     * @throws IOException if an image file is not found
     */
    public void saveAsImage() throws IOException {
        // RGB byte shifts
        int redByteShift = 16;
        int greenByteShift = 8;
        int blueByteShift = 0;

        // Open board image and get pixel array
        BufferedImage boardImage = ImageIO.read(new File(IMAGE));
        int[][] boardPixelArray = open(boardImage);

        // Transparent RGB color as pixel int
        int transparentRGB = (255 << redByteShift) + (255 << greenByteShift) + 
            (255 << blueByteShift);

        // Loop through all pieces on the board and add draw them
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                Piece piece = getPiece(new Point(i, j));
                if (piece == null) { // No piece on square
                    continue;
                }

                // Open piece image and get pixel array
                String imagePath = piece.getImagePath();
                BufferedImage pieceImage = ImageIO.read(new File(imagePath));
                int[][] piecePixelArray = open(pieceImage);
                int pieceSize = piecePixelArray.length;
                
                // Calculate start and end coords of piece image on board image
                int startRow = i * pieceSize;
                int startCol = (SIZE - 1 - j) * pieceSize;
                int endRow = startRow + pieceSize;
                int endCol = startCol + pieceSize;

                // Draw the piece on the board using the pixel arrays
                for (int ii = startRow; ii < endRow; ii++) {
                    for (int jj = startCol; jj < endCol; jj++) {
                        int patchRow = ii - startRow;
                        int patchCol = jj - startCol;

                        // Draw the part of the image that's not transparent
                        if (piecePixelArray[patchRow][patchCol] != 
                                transparentRGB) {
                            boardPixelArray[ii][jj] = 
                            piecePixelArray[patchRow][patchCol];
                        }
                    }
                }
            }
        }
        
        // Set the RGB values of the board image using the pixel array
        for (int i = 0; i < boardImage.getWidth(); i++) {
            for (int j = 0; j < boardImage.getHeight(); j++) {
                // Turn pixel value into packed RGB int
                int pixel = boardPixelArray[i][j] << redByteShift | 
                    boardPixelArray[i][j] << greenByteShift | 
                    boardPixelArray[i][j] << blueByteShift;
                boardImage.setRGB(i, j, pixel);
            }
        }

        // Save to output file
        File outputFile = new File(SAVE_IMAGE);
        ImageIO.write(boardImage, "png", outputFile);
    }

    /**
     * Main method for testing
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        Board board = new Board();
        Piece pawn = board.getPiece(new Point(4, 1));
        board.movePiece(pawn, new Point(4, 3));
        System.out.println(pawn.getCoords());
        board.saveAsImage();
    }
}
