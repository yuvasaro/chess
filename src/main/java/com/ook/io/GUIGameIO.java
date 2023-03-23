package com.ook.io;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.*;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * Plays the game in a GUI
 */
public class GUIGameIO implements ChessGameIO {
    private static final String DIRECTORY = "/%1$s_vs_%2$s/";
    private static final String BOARD_PATH = 
        DIRECTORY + "board.png";

    // Players
    private String white;
    private String black;

    // GUI components
    private JFrame window;
    private JPanel mainPanel;
    private JPanel leftPanel;
    private JPanel rightPanel;
    private JLabel boardImage;
    private JLabel movePrompt;
    private JTextField moveInput;
    private JButton moveButton;
    private JLabel printMessage;
    private boolean buttonPressed;

    // Input string
    private String input;

    /**
     * GUIGameIO constructor
     * @throws IOException if the board image file is not found
     */
    public GUIGameIO(String player1, String player2) throws IOException {
        white = player1;
        black = player2;
        input = null;

        window = new JFrame();
        window.setTitle("Chess");

        mainPanel = new JPanel();

        // ----- Board image -----
        leftPanel = new JPanel();
        
        boardImage = new JLabel(new ImageIcon(ImageIO.read(
            getClass().getResource(String.format(BOARD_PATH, white, black)))));
        leftPanel.add(boardImage);

        mainPanel.add(leftPanel);

        // ----- Move text and input -----
        rightPanel = new JPanel();
        rightPanel.setLayout(new GridLayout(5, 1));

        movePrompt = new JLabel("Enter your move");
        movePrompt.setPreferredSize(new Dimension(250, 40));
        rightPanel.add(movePrompt);

        moveInput = new JTextField();
        moveInput.setPreferredSize(new Dimension(250, 40));
        rightPanel.add(moveInput);

        moveButton = new JButton("Enter");
        moveButton.setPreferredSize(new Dimension(250, 40));
        moveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                buttonPressed = true;
                input = moveInput.getText();
            }
        });
        rightPanel.add(moveButton);

        printMessage = new JLabel();
        rightPanel.add(printMessage);

        mainPanel.add(rightPanel);

        window.add(mainPanel);

        // ----- Window settings -----
        window.pack();
        window.setResizable(false);
        window.setLocationRelativeTo(null);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
    }

    /**
     * Prompts in the GUI returns the given input
     * @param message the message to prompt
     * @return the given input
     */
    public String prompt(String message) {
        movePrompt.setText(message);

        // Timer that runs until the button is pressed
        while (!buttonPressed) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        buttonPressed = false; // Reset button
        moveInput.setText(""); // Reset input field
        return input;
    }

    /**
     * Prints a message to the GUI
     * @param message the message to print
     */
    public void print(String message) {
        printMessage.setText(message);
    }

    /**
     * Disables the input box and button
     */
    public void closeInputStream() {
        moveInput.setEnabled(false);
        moveButton.setEnabled(false);
    }

    /**
     * Resets the input and print message labels
     */
    public void update() {
        try {
            boardImage.setIcon(new ImageIcon(ImageIO.read(
                getClass().getResource(
                    String.format(BOARD_PATH, white, black)))));
        } catch (Exception e) {
            e.printStackTrace();
        }
        printMessage.setText("");
    }
}
