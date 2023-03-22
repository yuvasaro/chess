package com.ook;

import com.ook.game.Game;
import com.ook.io.ConsoleGameIO;

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
            game = new Game(new ConsoleGameIO(), args[0], args[1]);
        } else {
            game = new Game(new ConsoleGameIO());
        }
        game.start();
    }
}
