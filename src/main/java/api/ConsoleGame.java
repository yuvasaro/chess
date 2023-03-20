package api;

import java.util.Scanner;

/**
 * Plays the game in the console/terminal
 */
public class ConsoleGame implements ChessGameIO {
    private Scanner scanner;

    /**
     * ConsoleGame constructor
     */
    public ConsoleGame() {
        scanner = new Scanner(System.in);
    }

    /**
     * Sends a prompt to the console and returns the given input
     * @param message the message to prompt
     * @return the given input
     */
    @Override
    public String prompt(String message) {
        System.out.print(message);
        return scanner.nextLine();
    }

    /**
     * Prints a message to the console
     * @param message the message to print
     */
    @Override
    public void print(String message) {
        System.out.println(message);
    }

    /**
     * Closes the scanner
     */
    @Override
    public void closeInputStream() {
        scanner.close();
    }
}
