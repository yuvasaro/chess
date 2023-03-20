package com.ook;

import com.ook.game.Game;
import com.ook.io.ConsoleGame;

/**
 * Main class
 */
public class Main {
    
    /**
     * Main method
     * @param args
     */
    public static void main(String[] args) {
        Game game;
        if (args.length == 2) {
            game = new Game(new ConsoleGame(), args[0], args[1]);
        } else {
            game = new Game(new ConsoleGame());
        }
        game.start();
    }
}
