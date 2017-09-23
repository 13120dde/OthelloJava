package othelloGame;

import othelloGame.gameLogic.GameEngine;
import othelloGame.gameLogic.GameState;

import static othelloGame.gameLogic.GameState.BoardState.AI;
import static othelloGame.gameLogic.GameState.BoardState.HU;

public class GameAI {

    private GameEngine engine;
    private GameState realGameState;
    private GameState clonedGameState;
    private GameState.BoardState aiPlayer;
    private GameState.BoardState playerToGenerateTree;


    public GameAI(GameEngine gameEngine, GameState gameState) {
        engine=gameEngine;
        realGameState = gameState;
        aiPlayer=AI;
        playerToGenerateTree=HU;
        System.out.println(realGameState.toString());
    }
}
