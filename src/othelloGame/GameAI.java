package othelloGame;

import othelloGame.gameLogic.GameEngine;
import othelloGame.gameLogic.GameState;

import java.util.ArrayList;

import static othelloGame.gameLogic.GameState.BoardState.AI;
import static othelloGame.gameLogic.GameState.BoardState.EM;
import static othelloGame.gameLogic.GameState.BoardState.HU;

public class GameAI {



    /**ENCYCLOPEDIA
     *
     * S0 - stateZero
     * s- state
     * p - player
     * a - action
     *
     * - Player(s) player in turn
     * - Actions(s) returns a set of legal moves in a state
     * - Results(s,a) Transition model, defines the result of a move
     * - TerminalTest(s) - true if game is over, otherwise false
     * - Utility(s,p) - the final numeric value for a game that ends in a terminal state. Ratio of black markers vs White
     *
     */




    private GameEngine gameEngine;
    private GameState gameState;
    private int depthOfTree = 0;
    private GameState.BoardState aiPlayer;

    public GameAI(GameState gameState) {
        this.gameState=gameState;
        aiPlayer = AI;

    }

    public void setController(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }

    public void choseMove() {
        System.out.println("########################### AI IS PICKING ##########################\n" +
                "\n>>>>>>>>>>>>>Generating tree...");
        gameEngine.setTreeCreated(false);

        GameNode state = new GameNode(gameEngine,gameState.getClone());

        long startTime = System.nanoTime();
        state = state.buildTree(state);
        long stopTime = System.nanoTime();
        gameEngine.setTreeCreated(true);
        System.out.println("TREE CREATED\n" +
                ">>>>>>>>>>>>>>>>Duration: "+(stopTime-startTime)/1000000+"ms");

        //Change gameEngines mode of placements to end turn after placing move after generating tree
        //Placement action =  alphaBetaSearch(state);

        //return current action with highest utility //Placement.xy
        //gameEngine.placeMove(AI,gameState,action);
    }




}
