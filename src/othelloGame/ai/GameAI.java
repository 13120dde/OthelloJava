package othelloGame.ai;

import othelloGame.gameLogic.GameEngine;
import othelloGame.gameLogic.GameState;

import static othelloGame.gameLogic.GameState.Player.AI;

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
    private GameState.Player aiPlayer;

    public GameAI(GameState gameState) {
        this.gameState=gameState;
        aiPlayer = AI;

    }

    public void setController(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }

    public void choseMove() {
        gameEngine.setTreeCreated(false);
        GameNode state = new GameNode(gameEngine,gameState.getClone(),2000);



        //Change gameEngines mode of placements to end turn after placing move after generating tree
        //Placement action =  alphaBetaSearch(state);

        //return current action with highest utility //Placement.xy
        //gameEngine.placeMove(AI,gameState,action);
    }




}
