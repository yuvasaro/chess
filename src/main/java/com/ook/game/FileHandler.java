package com.ook.game;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.awt.Point;

public class FileHandler {
    // Locations and formats
    private static final String PATH = "/assets/";
    private static final String IMAGE = PATH + "board.png";
    private static final String HIGHLIGHT_INITIAL = 
        PATH + "highlight_initial.png";
    private static final String HIGHLIGHT_DESTINATION = 
        PATH + "highlight_destination.png";
    private static final String PGN_FORMAT = 
        "[Event \"1v1 Chess\"]\n" +
        "[Date \"%d.%d.%d\"]\n" +
        "[White \"%4$s\"]\n" +
        "[Black \"%5$s\"]\n" +
        "[Result \"%6$s\"]\n" +
        "[WhiteTitle \"GM\"]\n" +
        "[BlackTitle \"GM\"]\n\n";
    private static final String DIRECTORY = "bin/%1$s_vs_%2$s/";
    private static final String PGN_FILE = DIRECTORY + "%1$s_vs_%2$s.pgn";
    private static final String SAVE_IMAGE_FILE = DIRECTORY + "board.png";

    /**
     * Creates a directory for a game between two players
     * @param whiteName the white player's name
     * @param blackName the black player's name
     */
    public static void makeDirectory(String whiteName, String blackName) {
        new File(String.format(DIRECTORY, whiteName, blackName)).mkdirs();
    }

    /**
     * Deletes a directory for a game between two players
     * @param whiteName the white player's name
     * @param blackName the black player's name
     */
    public static void deleteDirectory(String whiteName, String blackName) {
        File dir = new File(String.format(DIRECTORY, whiteName, blackName));

        // Delete the files inside the directory
        String[] entries = dir.list();
        for(String filename: entries){
            File file = new File(dir.getPath(), filename);
            file.delete();
        }

        dir.delete();
    }

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
        BufferedImage itemImage = ImageIO.read(
            FileHandler.class.getResource(imagePath));
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
     * @param board the chessboard
     * @param whiteToPlay whether it is white's turn
     * @param lastMovedInitialCoords the initial coordinates of the last moved 
     *                               piece
     * @param lastMoved the last moved piece
     * @throws IOException if an image file is not found
     */
    public static void saveAsImage(Board board, boolean whiteToPlay, 
            Point lastMovedInitialCoords, Piece lastMoved, String whiteName, 
            String blackName, boolean flipBoard) throws IOException {
        Piece[][] theBoard = board.getBoard();
        if (flipBoard) { // Flip board according to whose turn it is
            theBoard = whiteToPlay ? board.getBoard() : board.flipBoard();
        }

        // RGB byte shifts
        int redByteShift = 16;
        int greenByteShift = 8;
        int blueByteShift = 0;

        // Open board image and get pixel array
        BufferedImage boardImage = ImageIO.read(
            FileHandler.class.getResource(IMAGE));
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
                            (whiteToPlay || !flipBoard) ? lastMovedInitialCoords :
                            new Point(
                                Board.SIZE - 1 - lastMovedInitialCoords.x, 
                                Board.SIZE - 1 - lastMovedInitialCoords.y), 
                        transparentRGB);
                    drawItemOnBoard(boardPixelArray, HIGHLIGHT_DESTINATION, 
                        pointOnBoard, transparentRGB);
                }

                // Draw piece
                drawItemOnBoard(boardPixelArray, PATH + imagePath, 
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
        File outputFile = new File(String.format(SAVE_IMAGE_FILE, 
            whiteName, blackName));
        ImageIO.write(updatedBoard, "png", outputFile);
    }

    /**
     * Saves the game to a PGN file
     * @param date the date of the game
     * @param whiteName white's name
     * @param blackName black's name
     * @param result the result of the game
     * @param pgn the PGN string of the game
     */
    public static void savePGN(LocalDate date, String whiteName, 
            String blackName, String result, String pgn) {
        try {
            // Get today's date
            int year = date.getYear();
            int month = date.getMonthValue();
            int day = date.getDayOfMonth();

            // Organize PGN data
            String gamePGN = String.format(PGN_FORMAT, year, month, day, 
                whiteName, blackName, result) + pgn;

            // Write to PGN file
            File pgnFile = new File(String.format(
                PGN_FILE, whiteName, blackName));
            pgnFile.getParentFile().mkdirs();
            FileWriter myWriter = new FileWriter(String.format(
                PGN_FILE, whiteName, blackName));
            myWriter.write(gamePGN);
            myWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the file of a game board
     * @param whiteName the white player's name
     * @param blackName the black player's name
     * @return the board file
     */
    public static File getBoardFile(String whiteName, String blackName) {
        return new File(String.format(SAVE_IMAGE_FILE, whiteName, blackName));
    }

    /**
     * Gets the PGN file of a game
     * @param whiteName the white player's name
     * @param blackName the black player's name
     * @return the PGN file of the game between the two players
     */
    public static File getPGNFile(String whiteName, String blackName) {
        return new File(String.format(PGN_FILE, whiteName, blackName));
    }
}
