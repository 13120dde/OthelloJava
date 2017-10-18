package othelloGame;

import othelloGame.ai.GameAI;
import othelloGame.gameLogic.GameEngine;
import othelloGame.gameLogic.GameState;

import javax.swing.*;

/**Driver of the program, instantiates all neccessary components and runs the application.
 *
 * Created by: Patrik Lind, 13120dde@gmail.com
 */
public class OthelloMain {

    private GameEngine gameEngine;
    private GameState gameState;
    private GameAI gameAI;
    private OthelloBoard gameUI;

    public static void main(String[] args) {
        new OthelloMain().initGame();
    }

    private void initGame() {
        gameState = new GameState(8,8);
        gameAI = new GameAI();
        gameUI = new OthelloBoard(gameState);
        gameEngine = new GameEngine(gameState, gameAI, gameUI);
        createAndShowUI(gameUI);

    }

    private void createAndShowUI(OthelloBoard ui) {
        JFrame frame = new JFrame("Othello - by Patrik Lind");
        frame.add(ui);
        frame.setVisible(true);


       // frame.setLocation(2600,100);
        frame.setResizable(false);
        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    }

}
