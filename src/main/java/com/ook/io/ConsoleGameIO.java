package com.ook.io;

import java.util.Scanner;

/**
 * Plays the game in the console/terminal
 */
public class ConsoleGameIO implements ChessGameIO {
    private Scanner scanner;

    /**
     * ConsoleGame constructor
     */
    public ConsoleGameIO() {
        scanner = new Scanner(System.in);
    }

    /**
     * Sends a prompt to the console and returns the given input
     * @param message the message to prompt
     * @return the given input
     */
    public String prompt(String message) {
        System.out.print(message);
        return scanner.nextLine();
    }

    /**
     * Prints a message to the console
     * @param message the message to print
     */
    public void print(String message) {
        System.out.println(message);
    }

    /**
     * Closes the scanner
     */
    public void closeInputStream() {
        scanner.close();
    }

    /**
     * Update function not needed for console game
     */
    public void update() {}
}
