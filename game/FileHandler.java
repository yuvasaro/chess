package game;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.Point;

public class FileHandler {
     // Image locations
     private static final String IMAGE = "assets/board.png";
     private static final String HIGHLIGHT_INITIAL = 
         "assets/highlight_initial.png";
     private static final String HIGHLIGHT_DESTINATION = 
         "assets/highlight_destination.png";

    /**
     * Opens an image and returns its pixel array
     * @param imagePath the path to the image
     * @return the pixel array
     * @throws IOException if the image file is not found
     */
    private static int[][] open(BufferedImage bufferedImage) 
            throws IOException {
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
     * Draws an item on the board image
     * @param boardPixelArray the board's pixel array
     * @param imagePath the path to the image to be drawn
     * @param drawCoords the coordinates on the board the image will be drawn on
     * @param transparentRGB the transparent RGB color as an integer
     * @throws IOException if the image file is not found
     */
    private static void drawItemOnBoard(int[][] boardPixelArray, 
            String imagePath, Point drawCoords, int transparentRGB) 
            throws IOException {
        // Open item image and get pixel array
        BufferedImage itemImage = ImageIO.read(new File(imagePath));
        int[][] itemPixelArray = open(itemImage);
        int itemSize = itemPixelArray.length;
        
        // Calculate start and end coords of item image on board image
        int startRow = drawCoords.x * itemSize;
        int startCol = (Board.SIZE - 1 - drawCoords.y) * itemSize;
        int endRow = startRow + itemSize;
        int endCol = startCol + itemSize;

        // Draw the item on the board using the pixel arrays
        for (int i = startRow; i < endRow; i++) {
            for (int j = startCol; j < endCol; j++) {
                int patchRow = i - startRow;
                int patchCol = j - startCol;

                // Draw the part of the image that's not transparent
                if (itemPixelArray[patchRow][patchCol] != 
                        transparentRGB) {
                    boardPixelArray[i][j] = 
                        itemPixelArray[patchRow][patchCol];
                }
            }
        }
    }

    /**
     * Saves the current board position as a PNG image
     * @param Board the chessboard
     * @param whiteToPlay whether it is white's turn
     * @param lastMovedInitialCoords the initial coordinates of the last moved 
     *                               piece
     * @param lastMoved the last moved piece
     * @param saveImagePath the path to save the image
     * @throws IOException if an image file is not found
     */
    public static void saveAsImage(Board board, boolean whiteToPlay, 
            Point lastMovedInitialCoords, Piece lastMoved, 
            String saveImagePath) throws IOException {
        Piece[][] theBoard = whiteToPlay ? board.getBoard() : board.flipBoard();

        // RGB byte shifts
        int redByteShift = 16;
        int greenByteShift = 8;
        int blueByteShift = 0;

        // Open board image and get pixel array
        BufferedImage boardImage = ImageIO.read(new File(IMAGE));
        int[][] boardPixelArray = open(boardImage);

        // Transparent RGB color as pixel int
        int transparentRGB = (0 << redByteShift) + (0 << greenByteShift) + 
            (0 << blueByteShift);

        // Loop through all pieces on the board and add draw them
        for (int i = 0; i < Board.SIZE; i++) {
            for (int j = 0; j < Board.SIZE; j++) {
                Point pointOnBoard = new Point(i, j);
                Piece piece = theBoard[i][j];
                if (piece == null) { // No piece on square
                    continue;
                }

                // Open piece image
                String imagePath = piece.getImagePath();

                // Draw highlights on initial coords and destination of last 
                // moved piece
                if (piece == lastMoved) {
                    drawItemOnBoard(
                        boardPixelArray, 
                        HIGHLIGHT_INITIAL, 
                        whiteToPlay ? lastMovedInitialCoords : 
                            new Point(
                                Board.SIZE - 1 - lastMovedInitialCoords.x, 
                                Board.SIZE - 1 - lastMovedInitialCoords.y), 
                        transparentRGB);
                    drawItemOnBoard(boardPixelArray, HIGHLIGHT_DESTINATION, 
                        pointOnBoard, transparentRGB);
                }

                // Draw piece
                drawItemOnBoard(boardPixelArray, imagePath, 
                    pointOnBoard, transparentRGB);
            }
        }

        // Put image back together
        BufferedImage updatedBoard = new BufferedImage(boardPixelArray.length, 
            boardPixelArray.length, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < updatedBoard.getHeight(); i++) {
            for (int j = 0; j < updatedBoard.getWidth(); j++) {
                updatedBoard.setRGB(i, j, boardPixelArray[i][j]);
            }
        }

        // Save to output file
        File outputFile = new File(saveImagePath);
        ImageIO.write(updatedBoard, "png", outputFile);
    }
}
