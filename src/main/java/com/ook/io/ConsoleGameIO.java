package com.ook.io;

import com.ook.game.FileHandler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Scanner;

/**
 * Plays the game in the console/terminal
 */
public class ConsoleGameIO implements ChessGameIO {
    private Scanner scanner;
    private String whiteName;
    private String blackName;

    /**
     * ConsoleGame constructor
     */
    public ConsoleGameIO(String whiteName, String blackName) {
        scanner = new Scanner(System.in);
        this.whiteName = whiteName;
        this.blackName = blackName;
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

    /**
     * Sends PGN as text to the console
     */
    public void sendPGN() {
        // Print lines of PGN file
        System.out.println("\nPGN:");
        try (BufferedReader br = new BufferedReader(
                new FileReader(FileHandler.getPGNFile(whiteName, blackName)))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Clean up files in bin
     */
    public void cleanup() {
        FileHandler.deleteDirectory(whiteName, blackName);
    }

    /**
     * Gets the next input entered
     * @return the next input from the console
     */
    public String getNextInput() {
        return scanner.nextLine();
    }
}
