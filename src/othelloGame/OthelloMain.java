package othelloGame;

import javax.swing.*;

public class OthelloMain {

    private GameEngine controller;

    public static void main(String[] args) {
        new OthelloMain().initGame();
    }

    private void initGame() {
        controller = new GameEngine(8,8);
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                createAndShowUI();
            }
        });

    }

    private void createAndShowUI() {
        JFrame frame = new JFrame("Othello");
        frame.add(new OthelloBoard(controller));
        frame.setVisible(true);

        //TODO change starting location
        frame.setLocation(2600,100);
        //frame.setResizable(false);
        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    }

}
