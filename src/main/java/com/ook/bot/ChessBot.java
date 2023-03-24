package com.ook.bot;

import com.ook.game.Game;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.List;

/**
 * Chess bot class
 */
public class ChessBot extends ListenerAdapter {
    private static final String PLAY_USAGE = "Usage: `!play <@player1> <@player2>`";
    private static final String MOVE_USAGE = "Usage: `!move <move>`";

    // Instance variables
    private ChessBotIO io;
    private Game game;
    private MessageChannel gameChannel;
    private Member player1;
    private String player1ID;
    private Member player2;
    private String player2ID;

    /**
     * Performs actions when messages are sent
     * @param event the message received event
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return; // Ignore bot messages
        if (!event.isFromGuild()) return; // Only respond to messages in server

        MessageChannel channel = event.getChannel();
        Member sender = event.getMember();
        Message message = event.getMessage();
        String content = message.getContentRaw();

        if (content.equals("!ping")) {
            channel = event.getChannel();
            channel.sendMessage("Pong!").queue();
        }

        // Play game command
        if (content.startsWith("!play")) {
            String[] args = content.split(" ");

            if (args.length != 3) { // Wrong command syntax
                channel.sendMessage(PLAY_USAGE).queue();
                return;
            }

            if (game != null) {
                channel.sendMessage("Another game is in progress.");
                return;
            }

            // Players must be mentioned
            List<User> mentionedUsers = message.getMentions().getUsers();
            List<Member> mentionedMembers = message.getMentions().getMembers();

            if (mentionedMembers.size() != 2) {
                channel.sendMessage(PLAY_USAGE).queue();
                return;
            }

            // Bots can't play
            for (User user : mentionedUsers) {
                if (user.isBot()) {
                    channel.sendMessage("Bots can't play.").queue();
                    return;
                }
            }

            // Check if someone else is trying to make two people play
            if (!mentionedMembers.contains(sender)) {
                channel.sendMessage("Are you being serious right now? Play the game yourself dumbass.").queue();
                return;
            }

            // Set game channel
            gameChannel = channel;

            // Get players
            player1 = mentionedMembers.get(0);
            player1ID = player1.getId();
            player2 = mentionedMembers.get(1);
            player2ID = player2.getId();

            // Start game
            io = new ChessBotIO(channel, player1, player2);
            game = new Game(io, player1.getEffectiveName(), player2.getEffectiveName());
            game.takeNextMove(null);
        }

        // Move command
        if (content.startsWith("!move")) {
            String[] args = content.split(" ");

            // Check command syntax
            if (args.length != 2) {
                channel.sendMessage(MOVE_USAGE).queue();
                return;
            }

            // Check if game has not been started
            if (game == null) {
                channel.sendMessage("Game ain't even start yet.").queue();
                return;
            }

            // Check if not on correct text channel
            if (channel != gameChannel) {
                channel.sendMessage(
                        "What are you doing here? The game is going on in " + gameChannel.getAsMention()).queue();
                return;
            }

            // Check if sender is not in the game
            if (!sender.getId().equals(player1ID) && !sender.getId().equals(player2ID)) {
                channel.sendMessage("Bro you're not even in the game.").queue();
                return;
            }

            // Check if wrong player is sending move
            if (game.whiteToPlay() && sender.equals(player2) ||
                    !game.whiteToPlay() && sender.equals(player1)) {
                channel.sendMessage("It's not your turn.").queue();
                return;
            }

            // Take the next move in the game
            game.takeNextMove(args[1]);

            // Send PGN and reset game variables if game has ended
            if (game.ended()) {
                io.sendPGN();
                io.cleanup();

                io = null;
                game = null;
                gameChannel = null;
                player1 = null;
                player2 = null;
                player1ID = null;
                player2ID = null;
            }
        }
    }

    /**
     * Runs the discord bot
     * @param args command line arguments
     * @throws Exception JDA exceptions
     */
    public static void main(String[] args) throws Exception {
        // Get bot token from config json
        JSONParser parser = new JSONParser();
        JSONObject obj = (JSONObject) parser.parse(new FileReader("config.json"));
        String token = (String) obj.get("token");

        JDABuilder.createDefault(token) // Create bot with bot token
                .addEventListeners(new ChessBot()) // Add new ChessBot event listener
                .enableIntents(GatewayIntent.MESSAGE_CONTENT) // Enable message content intent
                .build() // Connect to discord
                .getPresence().setActivity(Activity.playing("Chess")); // Set activity to "Playing Chess"
    }
}
