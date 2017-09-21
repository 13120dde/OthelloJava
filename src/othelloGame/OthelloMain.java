package othelloGame;

import javax.swing.*;
import java.awt.*;

public class OthelloMain {

    private Controller controller;

    public static void main(String[] args) {
        new OthelloMain().initGame();
    }

    private void initGame() {
        controller = new Controller(8,8);
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
        frame.setLocationRelativeTo(null);
        //frame.setResizable(false);
        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    }

}
