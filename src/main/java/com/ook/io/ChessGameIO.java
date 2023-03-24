package com.ook.io;

/*
 * Chess game input output interface
 */
public interface ChessGameIO {
    /**
     * Prints a message to the output destination
     * @param message the message to print
     */
    void print(String message);

    /**
     * Closes the input stream (ex. scanner for System.in)
     */
    void closeInputStream();

    /**
     * Update function
     */
    void update();

    /**
     * Send PGN
     */
    void sendPGN();

    /**
     * Cleanup
     */
    void cleanup();
}
