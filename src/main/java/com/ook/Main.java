package com.ook;

import java.io.IOException;

import com.ook.game.Game;
import com.ook.io.*;

/**
 * Main class
 */
public class Main {
    // Command usage
    private static final String USAGE = 
        "Usage: java -jar <jarfile> <console|gui> <player1> <player2>";
    
    /**
     * Main method
     * @param args command line arguments
     */
    public static void main(String[] args) throws IOException {
        Game game;
        ChessGameIO io;

        // Determine which type of game to start
        if (args.length == 3) {
            if (args[0].equals("console")) {
                io = new ConsoleGameIO();
            } else if (args[0].equals("gui")) {
                io = new GUIGameIO(args[1], args[2]);
            } else {
                System.out.println(USAGE);
                return;
            }

            game = new Game(io, args[1], args[2]);
        } else {
            System.out.println(USAGE);
            return;
        }

        game.start();
    }
}
