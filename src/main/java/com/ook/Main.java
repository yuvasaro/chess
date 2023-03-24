package com.ook;

import com.ook.bot.ChessBot;
import com.ook.game.Game;
import com.ook.io.*;

/**
 * Main class
 */
public class Main {
    // Command usage
    private static final String USAGE = 
        "Usage: java -jar <jarfile> console <player1> <player2> OR java -jar <jarfile> bot";
    
    /**
     * Main method
     * @param args command line arguments
     */
    public static void main(String[] args) throws Exception {
        // If bot run bot
        if (args.length == 1) {
            if (args[0].equals("bot")) {
                ChessBot.main(new String[]{});
            } else {
                System.out.println(USAGE);
            }
            return;
        }

        // Determine which type of game to start
        if (args.length == 3) {
            if (!args[0].equals("console")) {
                System.out.println(USAGE);
                return;
            }

            // Create console game
            ConsoleGameIO io = new ConsoleGameIO(args[1], args[2]);
            Game game = new Game(io, args[1], args[2]);

            // Play until game ends
            game.takeNextMove(null);
            while (!game.ended()) {
                game.takeNextMove(io.getNextInput());
            }
            io.sendPGN();
            io.cleanup();
        } else {
            System.out.println(USAGE);
        }
    }
}
