package othelloGame.ai;

import othelloGame.gameLogic.Actions;
import othelloGame.gameLogic.GameEngine;
import othelloGame.gameLogic.GameState;

import java.util.ArrayList;
import java.util.LinkedList;

import static othelloGame.gameLogic.GameState.Player.AI;
import static othelloGame.gameLogic.GameState.Player.HU;

/**This class is responsible for building the game-tree which holds data for all possible states of the game.
 * The tree can get pretty large for a game with 8x8 size so time restrictions of 2s is set for tree-building. If
 * the time limit is reached we must abort building and set leaves at a depth above the current depth being processed.
 *
 * The tree is being built breath first, meaning that we build all possible states at current depth before moving on
 * to the next depth.
 *
 */
class GameNode {


    /*>>>>>>Node specific variables<<<<<<*/

    private GameNode parent;
    private GameState.Player fakePlayer;
    //The state of the game-board
    private GameState state;
    //Heuristic vals

    private int nbrOfActions=0,childrenSize=0 , amntMarkersFlipped=0, ifLast=0, amntMFlippedOpponent=0, depth;

    //Action for the specific Node holding a specific state
    private Actions action;
    private ArrayList<GameNode> children;
    private int depthOfNode =0;
    private float utility =Float.MIN_VALUE;
    private boolean isLeaf = true;


    /*>>>>>>>Variables for tree building<<<<<<<<<*/

    private GameEngine gameEngine;
    private static LinkedList<GameNode> childrenTemp = new LinkedList<>();
    private static long timeLimit;

    /**Constructor for creating the tree, instantiates and builds the tree for a limited time. For a  8x8 gameboard it
     * can process all nodes down to depth 6 at 2000ms time-limit. Of course the depth is  also determined by the current
     * state of the game meaning that we can get to higher depth the longer the game proceeds.
     *
     * @param gameEngine : GameEngine
     * @param gameState : GameState
     * @param timeLimit : int - milliseconds
     */
    public GameNode(GameEngine gameEngine, GameState gameState, int timeLimit){
        this.timeLimit = timeLimit;
        this.state=gameState;
        this.gameEngine=gameEngine;
        parent = null;
        children = new ArrayList<>();
        action = new Actions();
        fakePlayer=AI;
        state=gameState;
        buildTree(this);

        GameNode node = null;

        //Build the tree for limited time.
        long startTime=System.currentTimeMillis();
        long elapsedTime = 0;


        while(!childrenTemp.isEmpty()){
            elapsedTime = System.currentTimeMillis()-startTime;
            if(elapsedTime>=timeLimit){
                break;
            }
            node = childrenTemp.removeFirst();
            buildTree(node);

        }

        System.out.println("Tree built in:" +(System.currentTimeMillis()-startTime)+"ms");
    }

    public GameNode(GameEngine gameEngine, GameState gameState, GameState.Player player, GameNode parent){
        this.state=gameState;
        this.gameEngine=gameEngine;
        this.parent = parent;
        children = new ArrayList<>();
        action = new Actions();
        fakePlayer=player;
        buildTree(this);

    }


    /**Constructor for child-nodes, should only be accessed by this class iteslf.
     *
     */
    private GameNode() {
        parent = null;
        action = null;
        children = new ArrayList<>();
        fakePlayer=AI;
        state=null;
    }


    /**The bread and butter of tree-building process. This method creates a child node for each valid move according to
     * the game-rules.
     *
     * @param currentNode : GameNode
     * @return
     */
    private GameNode buildTree(GameNode currentNode) {

        ArrayList<Actions> actions = new ArrayList<>();
        GameState stateInDepth= currentNode.getState().getClone();


        depthOfNode =currentNode.getDepthOfNode();

        /*TODO: inefficient to check naively, should probably refactor to a outward search in the gameboard[][]*/
        //Check all possible moves - place them in actionsarray.
        for(int i = 0; i<gameEngine.getRowSize(state);i++){
            for(int j=0; j<gameEngine.getColSize(state);j++){
                Actions action = gameEngine.checkValidPlacement(i,j,currentNode.getState(),currentNode.getFakePlayer());
                if(action!=null){
                   // currentNode.setAction(action);
                    actions.add(action);

                }
            }
        }

        //For each action in array, place a move, reclone gamestate and create a childnode with the state which has the
        //appropiate gamestate after placing a move.
        if(!actions.isEmpty()){
            depthOfNode++;
            currentNode.setLeaf(false);
            for(int i=0;i<actions.size();i++){
                Actions a = actions.get(i);

                gameEngine.placeMove(currentNode.getState(),currentNode.getFakePlayer(),a);
                GameState childState = currentNode.getState().getClone();
                GameNode child = new GameNode();
                child.setState(childState);
                child.setParent(currentNode);
                child.setAction(a);
                child.setDepthOfNode(depthOfNode);
                child.setFakePlayer(currentNode.getFakePlayer());
                currentNode.setChild(child);
                childrenTemp.addLast(child);
                currentNode.setState(stateInDepth.getClone());
            }
        }
        //Current node cant place a move, change to leaf although the game has not ended and there are more moves to make
        else{
            currentNode.setLeaf(true);
        }

        return currentNode;
    }

    /*>>>>>>>>>>> Getters and setters<<<<<<<<<<<<<<<*/

    private void setAction(Actions a) {
        this.action=a;
    }

    private int getDepthOfNode() {
        return this.depthOfNode;
    }

    private void setDepthOfNode(int depth) {
        this.depthOfNode =depth;
    }

    private void setState(GameState childState) {
        this.state=childState;
    }

    private void setChild(GameNode child) {
        children.add(child);
    }

    private void setParent(GameNode parent) {
        this.parent = parent;
    }

    private void setLeaf(boolean leaf) {
        isLeaf = leaf;
    }

    /**Changes player to !player
     *
     * @param fakePlayer : Player
     */
    private void setFakePlayer(GameState.Player fakePlayer) {
        if(fakePlayer==AI){
            this.fakePlayer = HU;
        }else if(fakePlayer==HU){
            this.fakePlayer=AI;
        }
    }

    protected Actions getAction(){

        return action;
    }

    protected float getUtility() {
        return utility;
    }

    protected void setUtility(float utility) {
        this.utility = utility;
    }



    protected GameState getState() {
        return state;
    }

    protected GameNode getChild(int i) {
        return children.get(i);
    }

    protected int getChildrenSize(){
        return children.size();
    }

    protected GameNode getParent() {
        return parent;
    }

    protected boolean isLeaf() {
        return isLeaf;
    }

    protected GameState.Player getFakePlayer() {
        return fakePlayer;
    }



    protected int getDepth() {
        return depthOfNode;
    }

}
