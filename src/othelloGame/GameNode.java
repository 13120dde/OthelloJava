package othelloGame;

import othelloGame.gameLogic.Actions;
import othelloGame.gameLogic.GameEngine;
import othelloGame.gameLogic.GameState;

import java.util.ArrayList;
import java.util.LinkedList;

import static othelloGame.gameLogic.GameState.Player.AI;
import static othelloGame.gameLogic.GameState.Player.HU;

public class GameNode {


    private GameNode parent;
    private GameState.Player fakePlayer;
    private GameState state;
    private Actions action;
    private ArrayList<GameNode> children;
    private boolean isLeaf = false;


    //Variables for tree building
    private GameEngine gameEngine;
    private LinkedList<GameNode> childrenTemp = new LinkedList<>();
    private int depth=0;

    public GameNode(GameEngine gameEngine, GameState gameState){
        this.state=gameState;
        this.gameEngine=gameEngine;
        parent = null;
        action = new Actions();
        children = new ArrayList<>();

        fakePlayer=AI;
        state=gameState;
    }

    public GameNode() {
        parent = null;
        children = new ArrayList<>();
        childrenTemp = new LinkedList<>();
        fakePlayer=AI;
        state=null;
        action = null;

    }

    private void build() {

        //Add timingrestricions here probably
        while(!childrenTemp.isEmpty()){
            buildTree(childrenTemp.removeFirst());
        }

    }


    public GameNode buildTree(GameNode currentNode) {

        ArrayList<Actions> actions = new ArrayList<>();

      //  GameNode currentNode = node;
        GameState stateInDepth= currentNode.getState().getClone();
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>IN BUILDING TREE<<<<<<<<<<<<<<<<<<<<<<<<<<<\nDepth:"+currentNode.getDepthOfNode());
        System.out.println("Generating moves for: "+currentNode.getFakePlayer()+" - name: "+currentNode.getAction().getPosX()+" - "+currentNode.getAction().getPosY()+"\nState of gameboard\n"+stateInDepth.toString());

        depth=currentNode.getDepthOfNode();
        //Terminal check.
        if(currentNode.getState().getRemainingTurns()<=0 ){
            currentNode.setLeaf(true);
        }


        if(currentNode.getDepthOfNode()>10){
            return null;
        }

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
            depth++;

            for(int i=0;i<actions.size();i++){
                Actions a = actions.get(i);

                gameEngine.placeMove(currentNode.getState(),currentNode.getFakePlayer(),a);
                GameState childState = currentNode.getState().getClone();
                GameNode child = new GameNode();
                child.setState(childState);
                child.setParent(currentNode);
                child.setDepthOfNode(depth);
                child.setAction(a);
                child.setFakePlayer(currentNode.getFakePlayer());
                currentNode.setChild(child);
                childrenTemp.addLast(child);
                currentNode.setState(stateInDepth.getClone());
            }
        }

        return currentNode;
    }

    private void setAction(Actions action) {
        this.action=action;
    }

    private int getDepthOfNode() {
        return this.depth;
    }

    private void setDepthOfNode(int depth) {
        this.depth=depth;
    }

    public Actions getAction(){
        return action;
    }

    public void setState(GameState childState) {
        this.state=childState;
    }
    public GameState getState() {
        return state;
    }

    public void setChild(GameNode child) {
        children.add(child);
    }

    public GameNode getParent() {
        return parent;
    }

    public void setParent(GameNode parent) {
        this.parent = parent;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public void setLeaf(boolean leaf) {
        isLeaf = leaf;
    }

    public GameState.Player getFakePlayer() {
        return fakePlayer;
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

    private int calculateUtility(GameState clonedState) {
        return clonedState.playerAIScore-clonedState.playerHUScore;
    }


    /**Just for testing
     *
     *
     */
    public static void main(String[] args) {
        GameState state = new GameState(4,4);
        GameEngine engine = new GameEngine();
        GameNode node = new GameNode(engine,state);
        node = node.buildTree(node);
        node.build();
    }

}
