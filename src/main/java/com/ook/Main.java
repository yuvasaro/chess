package com.ook;

import com.ook.ai.ChessAI;
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

            // Check if player is playing AI
            ChessAI ai = null;
            boolean aiIsPlayingWhite = false;
            String playerName = null;

            // Set AI team based on order of arguments
            if (args[1].equalsIgnoreCase("ai")) {
                ai = new ChessAI("AI");
                playerName = args[2];
                aiIsPlayingWhite = true;
            } else if (args[2].equalsIgnoreCase("ai")) {
                ai = new ChessAI("AI");
                playerName = args[1];
            }

            // Create Game object based on whether the player is playing the AI
            ConsoleGameIO io = new ConsoleGameIO(args[1], args[2]);;
            Game game = null;
            if (ai != null) {
                game = new Game(io, playerName, !aiIsPlayingWhite, ai);
            } else {
                game = new Game(io, args[1], args[2]);
            }

            // Play until game ends
            if (ai != null && ai.isPlayingWhite()) {
                ai.move();
            } else {
                game.takeNextMove(null);
            }
            while (!game.ended()) {
                if (ai != null && game.whiteToPlay() == ai.isPlayingWhite()) {
                    ai.move();
                } else {
                    game.takeNextMove(io.getNextInput());
                }
            }
            io.sendPGN();
            io.cleanup();
        } else {
            System.out.println(USAGE);
        }
    }
}
