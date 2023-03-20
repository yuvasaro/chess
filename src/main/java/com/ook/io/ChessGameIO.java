package com.ook.io;

/*
 * Chess game input output interface
 */
public interface ChessGameIO {
    /**
     * Sends a prompt and returns the given input
     * @param message the message to prompt
     * @return the given input
     */
    String prompt(String message);

    /**
     * Prints a message to the output destination
     * @param message the message to print
     */
    void print(String message);

    /**
     * Closes the input stream (ex. scanner for System.in)
     */
    void closeInputStream();
}
