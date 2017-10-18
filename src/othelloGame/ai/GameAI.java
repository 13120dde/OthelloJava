package othelloGame.ai;

import othelloGame.gameLogic.Actions;
import othelloGame.gameLogic.GameEngine;
import othelloGame.gameLogic.GameState;

import static othelloGame.gameLogic.GameState.Player.AI;

/**Game AI following minmax algorithm with alpha beta pruning.
 *
 * Created by: Patrik Lind, 13120dde@gmail.com
 */
public class GameAI implements Runnable{


    private GameEngine gameEngine;
    private GameState.Player aiPlayer = AI;
    private GameState gameState;
    private Thread thread;

    private long startTime;

    public void setController(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }

    /**Builds a game-tree and traverses the tree with min-max with alpha-beta pruning. Travarses the whole tree that is
     * being built.
     *
     * @param gameState
     */
    public void choseMove(GameState gameState) {
        this.gameState=gameState;
        System.out.println(">>AI IS PICKING - Game state<<\n"+gameState.toString());
        startAiThread();

    }



    private void startAiThread(){
        if(thread==null){
            thread = new Thread(this);
            thread.start();
        }
    }

    @Override
    public void run() {

        gameEngine.setAiHasPicked(false);
        // Some overhead needed for creating leaves
        GameNode state = new GameNode(gameEngine,gameState.getClone(),4900);
        System.out.println("Depth of tree: "+state.getDepth());
        Actions actionToReturn = AlphaBetaSearch(state);



        //For solution w/o prebuilt tree, (not working atm)
        //startTime = System.currentTimeMillis();
        //depthOfSearch=0;
        //Actions actionToReturn = AlphaBetaSearchWOTree(gameState.getClone());


        gameEngine.setAiHasPicked(true);
        gameEngine.placeMove(gameState, aiPlayer,actionToReturn);
        System.out.println(gameState.toString());
        gameEngine.switchPlayer(gameState,AI);
        thread = null;
    }


    private Actions AlphaBetaSearch(GameNode state){

        Actions action=null;
        float resultValue = Float.NEGATIVE_INFINITY;

        for(int i =0; i<state.getChildrenSize();i++){
            depthOfSearch=0;

            float value = maxValue(state.getChild(i),Float.NEGATIVE_INFINITY,Float.POSITIVE_INFINITY);

            int compare = Float.compare(value,resultValue);
            if(compare>0){
                action=state.getChild(i).getAction();
                resultValue=value;
            }


        }

        float end=System.currentTimeMillis()-startTime;
        System.out.println("Minmax time: "+end+"ms");

        return action;
    }

    private float maxValue(GameNode state, float alpha, float beta) {
        if(terminalTest(state)){
            return utility(state);
        }
        float resultValue = Float.NEGATIVE_INFINITY;
        for(int i = 0; i<state.getChildrenSize();i++){
            resultValue= Math.max(resultValue,minValue(state.getChild(i),alpha,beta));
            int compare = Float.compare(resultValue,beta);
            if(compare>= 0){
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
            int compare = Float.compare(resultValue,alpha);
            if(compare<=0){
                return resultValue;
            }
            beta= Math.min(beta,resultValue);
        }
        return resultValue;
    }

    private boolean terminalTest(GameNode state){
        return state.isLeaf();
    }

    private float utility(GameNode state){
        int aiScore = state.getState().getPlayerAIScore();
        int humanScore = state.getState().getPlayerHUScore();
        return aiScore-humanScore;
    }


    /**################################################################
     * Alternate solution: Dont prebuild the whole tree breath first, let MinMax build on tree breath first with respect
     * to the alpha/beta values (ie dont build the nodes that will be pruned)
     * TODO: childnodes of rootnode dont get updated with the utility found at the leaves, need to fix it!
     *
     */

    private int depthOfSearch=0, depthLimit=3;

    private Actions AlphaBetaSearchWOTree(GameState gameState){
        GameNode rootState = new GameNode(gameEngine,gameState, GameState.Player.AI,null);
        float v = maxV(rootState,Float.NEGATIVE_INFINITY,Float.POSITIVE_INFINITY);

        Actions toReturn = null;
        for(int i =0; i<rootState.getChildrenSize();i++){
            float actionValue = rootState.getChild(i).getUtility();
            if(Float.compare(actionValue,v)==0){
                toReturn = rootState.getChild(i).getAction();
            }
        }

        return toReturn;
    }

    private float maxV(GameNode state, float alpha, float beta) {
        if(terminalTestWOTree(state)){
            float u = utilityWOTree(state);
            //  depthOfSearch--;
            return u;
        }
        depthOfSearch++;
        float v = Float.NEGATIVE_INFINITY;
        for(int i =0;i<state.getChildrenSize();i++){
            GameNode nextState= result(state.getChild(i),state);
            v = Math.max(v, minV(nextState,alpha,beta));
            int compare = Float.compare(v,beta);
            if (compare>=0){
                depthOfSearch--;

                return v;
            }
            alpha = Math.max(alpha,v);
        }
        depthOfSearch--;

        return v;
    }

    private float minV(GameNode state, float alpha, float beta) {
        if(terminalTestWOTree(state)){
            float u = utilityWOTree(state);
            // depthOfSearch--;

            return u;
        }
        depthOfSearch++;
        float v = Float.POSITIVE_INFINITY;
        for(int i =0;i<state.getChildrenSize();i++){
            GameNode nextState= result(state.getChild(i),state);
            v = Math.min(v, maxV(nextState,alpha,beta));
            int compare = Float.compare(v,alpha);
            if (compare<=0){
                depthOfSearch--;//?

                return v;
            }
            beta = Math.min(beta,v);
        }

        depthOfSearch--;

        return v;
    }

    private GameNode result(GameNode state, GameNode parent) {
        return new GameNode(gameEngine,state.getState().getClone(), state.getFakePlayer(),parent);
    }

    private boolean terminalTestWOTree(GameNode state) {
        long elapsedTime = System.currentTimeMillis()-startTime;
        System.out.println("Elapsed time: "+elapsedTime);

        //true leaf
        if(state.getState().getRemainingTurns()==0){
            return true;
        }
        if(depthOfSearch>=depthLimit){
            return true;
        }
        return false;
    }

    private float utilityWOTree(GameNode state) {

        int aiScore = state.getState().getPlayerAIScore();
        int humanScore = state.getState().getPlayerHUScore();
        float utility = aiScore-humanScore;

        GameNode parent=state.getParent();
        while(parent!=null){
            float parentUtility = parent.getUtility();
            if(Float.compare(utility,parentUtility)<0){
                parent.setUtility(utility);
            }
            parent=parent.getParent();
        }

        return utility;
    }



}
