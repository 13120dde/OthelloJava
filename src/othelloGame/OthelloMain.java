package othelloGame;

import othelloGame.gameLogic.GameEngine;
import othelloGame.gameLogic.GameState;

import javax.swing.*;

/**
 *
 */
public class OthelloMain {

    private GameEngine controller;
    private GameState gameState;
    private GameAI gameAI;

    public static void main(String[] args) {
        new OthelloMain().initGame();
    }

    private void initGame() {
        gameState = new GameState(8,8);
        controller = new GameEngine(gameState);
        gameAI = new GameAI(controller,gameState);
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                createAndShowUI();
            }
        });

    }

    private void createAndShowUI() {
        JFrame frame = new JFrame("Othello");
        frame.add(new OthelloBoard(controller,gameState));
        frame.setVisible(true);

        //TODO change starting location
        frame.setLocation(2600,100);
        //frame.setResizable(false);
        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    }

}
