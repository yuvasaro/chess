package com.ook.bot;

import com.ook.game.FileHandler;
import com.ook.io.ChessGameIO;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.utils.FileUpload;

import java.awt.*;
import java.io.File;

/**
 * Chess bot input/output
 */
public class ChessBotIO implements ChessGameIO {
    private static final String BOARD_PATH = "/%1$s_vs_%2$s/board.png";

    private MessageChannel channel;
    private Member white;
    private String whiteName;
    private Member black;
    private String blackName;

    /**
     * ChessBotIO constructor
     * @param channel the text channel the game is being played in
     * @param player1 the user playing white
     * @param player2 the user playing black
     */
    public ChessBotIO(MessageChannel channel, Member player1, Member player2) {
        this.channel = channel;
        white = player1;
        whiteName = player1.getEffectiveName();
        black = player2;
        blackName = player2.getEffectiveName();
    }

    /**
     * Sends a message to the current text channel
     * @param message the message to print
     */
    public void print(String message) {
        channel.sendMessage(message).queue();
    }

    /**
     * Close input stream not needed for bot
     */
    public void closeInputStream() {}

    /**
     * Sends the board to the text channel the game is being played in
     */
    public void update() {
        File gameBoard = FileHandler.getBoardFile(whiteName, blackName);
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(String.format("%1$s vs %2$s", whiteName, blackName));
        embed.setColor(Color.BLUE);
        embed.setImage("attachment://" + gameBoard);
        channel.sendMessageEmbeds(embed.build())
                .addFiles(FileUpload.fromData(gameBoard)).queue();
    }

    /**
     * Sends the game PGN file to the text channel
     */
    public void sendPGN() {
        File pgn = FileHandler.getPGNFile(whiteName, blackName);
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Download this PGN and upload it to an analysis board at " +
                "https://www.chess.com/analysis?tab=analysis");
        embed.setColor(Color.GREEN);
        channel.sendMessageEmbeds(embed.build()).addFiles(FileUpload.fromData(pgn)).queue();
    }

    /**
     * Clean up files in bin
     */
    public void cleanup() {
        FileHandler.deleteDirectory(whiteName, blackName);
    }
}
