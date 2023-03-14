import java.util.Scanner;

/**
 * The chess game engine
 */
public class Game {
    private boolean whiteToPlay;
    private Board board;

    /**
     * Creates a new game with white as the first player
     */
    public Game() {
        whiteToPlay = true;
        board = new Board();
    }

    /**
     * Starts the game, handles game logic
     */
    public void start() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Start game.\n");

        // Game loop
        do {
            String whoPlays = whiteToPlay ? "White" : "Black";
            boolean validMove = false;
            String input = null;
            String[] move = null;

            System.out.println();
            System.out.println(board);
            System.out.println();

            do {
                System.out.print(String.format("%s to play: ", whoPlays));
                input = scanner.next();
                move = MoveHandler.parseMove(input);
                validMove = isValidMove(move);
            } while (!validMove);

            // TODO: Make the move

            // Toggle whiteToPlay to change to next player's turn
            whiteToPlay = !whiteToPlay;

        } while (!gameEnd());

        System.out.println("\nEnd game.");

        scanner.close();
    }

    /**
     * Checks whether a given move is valid
     * @param move a string array of the move's components
     * @return whether the move is valid
     */
    public boolean isValidMove(String[] move) {
        // Check if the move notation represents a valid move
        if (!MoveHandler.validMoveComponents(move)) {
            return false;
        }

        // Translate move components to array of booleans (not null = true)
        boolean[] notNull = new boolean[move.length];
        for (int i = 0; i < move.length; i++) {
            notNull[i] = (move[i] != null);
        }

        // Castle
        if (notNull[MoveHandler.CASTLE]) {

        }

        // Promotion
        else if (notNull[MoveHandler.PROMOTION]) {

        }

        // All other moves
        else {
            String piece = move[MoveHandler.PIECE];
            if (piece == null) { // Pawn
                piece = "P";
            }

            // Check white pieces array and get legal moves of piece
            if (whiteToPlay) {

            } 
            
            // Check black pieces array and get legal moves of piece
            else {

            }
        }

        return true;
    }

    /**
     * Returns whether the current player is in check
     * @return whether the current player is in check
     */
    public boolean isInCheck() {
        // TODO
        return false;
    }

    /**
     * Returns whether the game has ended or not
     * @return whether the game has ended or not
     */
    public boolean gameEnd() {
        // TODO
        return false;
    }
}
