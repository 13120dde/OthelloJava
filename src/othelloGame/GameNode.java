package othelloGame;

import othelloGame.gameLogic.GameEngine;
import othelloGame.gameLogic.GameState;
import othelloGame.gameLogic.Placements;

import java.util.ArrayList;
import java.util.LinkedList;

import static othelloGame.gameLogic.GameState.BoardState.AI;
import static othelloGame.gameLogic.GameState.BoardState.HU;

public class GameNode {

    private ArrayList<GameNode> children;
    private LinkedList<GameNode> childrenTemp = new LinkedList<>();
    private GameNode parent;
    private int utiliy, posX, posY;
    private int depth=0;
    private GameState.BoardState fakePlayer;
    private GameState state;

    private GameEngine gameEngine;
    private ArrayList<Placements> actions;
    private boolean isLeaf = false;

    public GameNode(GameEngine gameEngine){

        this.gameEngine=gameEngine;
    }

    public GameNode(GameEngine gameEngine, GameState gameState){
        this.state=gameState;
        this.gameEngine=gameEngine;
        parent = null;
        children = new ArrayList<>();

        utiliy = 0;
        posY=0;
        posX=0;
        fakePlayer=AI;
        state=gameState;
        actions = new ArrayList<>();
    }

    public GameNode() {
        parent = null;
        children = new ArrayList<>();
        childrenTemp = new LinkedList<>();
        utiliy = 0;
        posY=0;
        posX=0;
        fakePlayer=AI;
        state=null;
        actions = new ArrayList<>();


    }

    public static void main(String[] args) {
        GameState state = new GameState(8,8);
        GameEngine engine = new GameEngine();
        GameNode node = new GameNode(engine,state);
        node = node.buildTree(node);
    }

    public GameNode buildTree(GameNode node) {

        GameNode currentNode = node;
        GameState stateInDepth= node.getState().getClone();
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>IN BUILDING TREE<<<<<<<<<<<<<<<<<<<<<<<<<<<\nDepth:"+node.getDepthOfNode());
        System.out.println("Generating moves for: "+currentNode.getFakePlayer()+" - name: "+currentNode.getPosX()+" - "+currentNode.getPosY()+"\nState of gameboard\n"+stateInDepth.toString());

        depth=currentNode.getDepthOfNode();
        //Terminal check.
        if(currentNode.getState().getRemainingTurns()<=0){
            currentNode.setLeaf(true);
            currentNode.setUtiliy(calculateUtility(state));
        }


        if(currentNode.getDepthOfNode()>10){
            return null;
        }

        //Check all possible moves - place them in actionsarray.
        for(int i = 0; i<gameEngine.getRowSize(state);i++){
            for(int j=0; j<gameEngine.getColSize(state);j++){
                Placements action = gameEngine.checkValidPlacement(i,j,currentNode.getState(),currentNode.getFakePlayer());
                if(action!=null){
                    currentNode.setAction(action);
                }
            }
        }

        //For each action in array, place a move, reclone gamestate and create a childnode with the state which has the
        //appropiate gamestate after placing a move.
        if(currentNode.getAction(0)!=null){
            depth++;

            for(int i=0;i<currentNode.getAcionsSize();i++){
                Placements a = currentNode.getAction(i);

                gameEngine.placeMove(currentNode.getState(),currentNode.getFakePlayer(),a);
                GameState childState = currentNode.getState().getClone();
                GameNode child = new GameNode();
                child.setState(childState);
                child.setParent(currentNode);
                child.setPosX(a.getPosX());
                child.setPosY(a.getPosY());
                child.setDepthOfNode(depth);
                child.setFakePlayer(currentNode.getFakePlayer());
                currentNode.setChild(child);
                childrenTemp.addLast(child);


                currentNode.setState(stateInDepth.getClone());
            }
        }

        //ni viable moves to make, need to pass
        if(!childrenTemp.isEmpty()){
            buildTree(childrenTemp.removeFirst());
        }


        return this;
    }

    private int getDepthOfNode() {
        return this.depth;
    }

    private void setDepthOfNode(int depth) {
        this.depth=depth;
    }

    public void setState(GameState childState) {
        this.state=childState;
    }
    public GameState getState() {
        return state;
    }

    private int getAcionsSize() {
        return actions.size();
    }

    private Placements getAction(int i) {
        Placements action = null;
        try {
            action = actions.get(i);
        }catch (IndexOutOfBoundsException e){
            return null;
        }
        return action;
    }

    private void setAction(Placements action) {
        actions.add(action);
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

    public ArrayList<Placements> getActions() {
        return actions;
    }

    public void setActions(ArrayList<Placements> actions) {
        this.actions = actions;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public void setLeaf(boolean leaf) {
        isLeaf = leaf;
    }

    public int getUtiliy() {
        return utiliy;
    }

    public void setUtiliy(int utiliy) {
        this.utiliy = utiliy;
    }

    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public GameState.BoardState getFakePlayer() {
        return fakePlayer;
    }

    public void setFakePlayer(GameState.BoardState fakePlayer) {
        if(fakePlayer==AI){
            this.fakePlayer = HU;
        }else if(fakePlayer==HU){
            this.fakePlayer=AI;
        }
    }




    private int calculateUtility(GameState clonedState) {
        return clonedState.playerAIScore-clonedState.playerHUScore;
    }
}
