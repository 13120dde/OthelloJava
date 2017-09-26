package othelloGame.ai;

import othelloGame.gameLogic.Actions;
import othelloGame.gameLogic.GameEngine;
import othelloGame.gameLogic.GameState;

import static othelloGame.gameLogic.GameState.Player.AI;

/**Game AI following minmax algorithm with alpha beta pruning.
 *
 * Created by: Patrik Lind, 13120dde@gmail.com
 */
public class GameAI {


    private GameEngine gameEngine;
    private GameState.Player aiPlayer = AI;

    float startTime;

    public void setController(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }

    public void choseMove(GameState gameState) {
        gameEngine.setTreeCreated(false);
        GameNode state = new GameNode(gameEngine,gameState.getClone(),4600);
        Actions actionToReturn = AlphaBetaSearch(state);


        gameEngine.setTreeCreated(true);
        gameEngine.placeMove(gameState, aiPlayer,actionToReturn);
        gameEngine.switchPlayer(gameState,AI);

    }

    private Actions AlphaBetaSearch(GameNode state){

        Actions result=null;
        float resultValue = Float.NEGATIVE_INFINITY;

        for(int i =0; i<state.getChildrenSize();i++){
            float value = maxValue(state.getChild(i),Float.NEGATIVE_INFINITY,Float.POSITIVE_INFINITY);
            if(value>resultValue){
                result=state.getChild(i).getAction();
                resultValue=value;
            }


        }

        float end=System.currentTimeMillis()-startTime;
        System.out.println("Minmax time: "+end);

        return result;
    }

    private float maxValue(GameNode state, float alpha, float beta) {
        if(terminalTest(state)){
            return utility(state);
        }
        float resultValue = Float.NEGATIVE_INFINITY;
        for(int i = 0; i<state.getChildrenSize();i++){
            resultValue= Math.max(resultValue,minValue(state.getChild(i),alpha,beta));
            if(resultValue>= beta){
                return resultValue;
            }
            alpha = Math.max(alpha,resultValue);
        }

        return resultValue;
    }

    private float minValue(GameNode state, float alpha, float beta) {
        if(terminalTest(state)){
            return utility(state);
        }
        float resultValue = Float.POSITIVE_INFINITY;
        for(int i =0; i<state.getChildrenSize();i++){
            resultValue= Math.min(resultValue,maxValue(state.getChild(i),alpha,beta));
            if(resultValue<=alpha){
                return resultValue;
            }
            beta= Math.min(beta,resultValue);
        }
        return resultValue;
    }

    private float utility(GameNode state) {
        int aiScore = state.getState().getPlayerAIScore();
        int humanScore = state.getState().getPlayerHUScore();
        return aiScore-humanScore;
    }

    //probably should add some timingrestricions too well see.
    private boolean terminalTest(GameNode state) {
        return state.isLeaf();
    }


}
