package com.ook.bot;

import com.ook.ai.ChessAI;
import com.ook.game.Game;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.awt.*;
import java.util.List;
import java.util.Scanner;

/**
 * Chess bot class
 */
public class ChessBot extends ListenerAdapter {
    private static final String PLAY_USAGE = "Usage: `!play <@player1> <@player2>`";
    private static final String MOVE_USAGE = "Usage: `!move <move>`";

    // Instance variables
    private String id;
    private final String prefix = "!";
    private ChessBotIO io;
    private Game game;
    private MessageChannel gameChannel;
    private Member player1;
    private String player1ID;
    private Member player2;
    private String player2ID;
    private ChessAI ai;

    /**
     * Stores the bot user's ID
     * @param id the bot user's ID
     */
    private void setID(String id) {
        this.id = id;
    }

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

        if (content.equals(prefix + "ping")) {
            channel = event.getChannel();
            channel.sendMessage("Pong!").queue();
        }

        // Play game command
        if (content.startsWith(prefix + "play")) {
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

            // Other bots can't play
            for (User user : mentionedUsers) {
                if (user.isBot()) {
                    if (!user.getId().equals(id)) {
                        channel.sendMessage("I can play you. Other bots can't play.").queue();
                        return;
                    }
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

            // Check if player is playing bot
            boolean aiIsPlayingWhite = false;
            String playerName = null;

            if (player1ID.equals(id)) {
                ai = new ChessAI("ChessBot");
                aiIsPlayingWhite = true;
                playerName = player2.getEffectiveName();
            } else if (player2ID.equals(id)) {
                ai = new ChessAI("ChessBot");
                playerName = player1.getEffectiveName();
            }

            // Set up IO and game
            io = new ChessBotIO(channel, player1, player2);
            if (ai != null) {
                game = new Game(io, playerName, !aiIsPlayingWhite, ai);
            } else {
                game = new Game(io, player1.getEffectiveName(), player2.getEffectiveName());
            }

            // Start game
            if (ai != null && ai.isPlayingWhite()) {
                ai.move();
            } else {
                game.takeNextMove(null);
            }
        }

        // Move command
        if (content.startsWith(prefix + "move")) {
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

            // Do AI move if it's AI turn
            if (ai != null && !game.ended() && game.whiteToPlay() == ai.isPlayingWhite()) {
                ai.move();
            }

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
                ai = null;
            }
        }

        // Help command
        if (content.equals(prefix + "help")) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("ChessBot Commands");
            embed.setColor(Color.RED);

            // Add commands and descriptions as fields
            embed.addField(new MessageEmbed.Field(
                    String.format("`%splay @player1 @player2`", prefix),
                    "Starts a game with `player1` as White and `player2` as Black.",
                    false
            ));
            embed.addField(new MessageEmbed.Field(
                    String.format("`%smove <move>`", prefix),
                    "Plays a move. The move should be in standard chess notation.",
                    false
            ));
            embed.addField(new MessageEmbed.Field(
                    String.format("`%shelp`", prefix),
                    "Displays all the commands that ChessBot responds to.",
                    false
            ));

            channel.sendMessageEmbeds(embed.build()).queue();
        }
    }

    /**
     * Runs the discord bot
     * @param args command line arguments
     */
    public static void main(String[] args) {
        // Get bot token from dotenv
        String token = null;
        try {
            Dotenv dotenv = Dotenv.load();
            token = dotenv.get("TOKEN");
        } catch (Exception ignored) {}
        // If token is null, ask for token in console
        if (token == null) {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter bot token: ");
            token = scanner.nextLine();
        }

        ChessBot botObject = new ChessBot();
        JDA discordBot = JDABuilder.createDefault(token) // Create bot with bot token
                .addEventListeners(botObject) // Add new ChessBot event listener
                .enableIntents(GatewayIntent.MESSAGE_CONTENT) // Enable message content intent
                .build(); // Connect to discord

        // Set activity to "Playing Chess"
        discordBot.getPresence().setActivity(Activity.playing("Chess"));

        botObject.setID(discordBot.getSelfUser().getId()); // Store bot ID
    }
}
