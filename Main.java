import api.ConsoleGame;
import game.Game;

/**
 * Main class
 */
public class Main {
    
    /**
     * Main method
     * @param args
     */
    public static void main(String[] args) {
        Game game = new Game(new ConsoleGame());
        game.start();
    }
}
