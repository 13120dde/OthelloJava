package othelloGame;

import othelloGame.gameLogic.GameEngine;
import othelloGame.gameLogic.GameState;

import java.util.ArrayList;
import java.util.LinkedList;

import static othelloGame.gameLogic.GameState.BoardState.AI;
import static othelloGame.gameLogic.GameState.BoardState.HU;

public class GameNode {

    private ArrayList<GameNode> children = new ArrayList<>();
    private LinkedList<GameNode> childrenTemp = new LinkedList<>();
    private GameNode parent=null;

    private GameState clonedState;
    private GameEngine gameEngine;
    private ArrayList<GameEngine.Placements > actions = new ArrayList<>();
    private boolean isLeaf = false;
    private int utiliy = 0, posX, posY;
    private int depth=0;
    private GameState.BoardState fakePlayer=AI;

    public GameNode(GameState gameState, GameEngine gameEngine){

        this.gameEngine=gameEngine;
        this.clonedState=gameState;
    }

    public GameNode() {

    }

    public GameNode buildTree(GameNode currentRoot) {

        GameState stateInDepth=currentRoot.clonedState.getClone();
        System.out.println(">>>>>IN BUILDING TREE\nDepth:"+depth++);
        System.out.println("Generating moves for: "+currentRoot.fakePlayer+"\nState of gameboard\n"+stateInDepth.toString());

        if(currentRoot.clonedState.getRemainingTurns()<=0){
            currentRoot.isLeaf=true;
            currentRoot.utiliy=calculateUtility(clonedState);
            return this ;
        }

        //Check all possible moves - place them in actionsarray.
        for(int i = 0; i<gameEngine.getRowSize(clonedState);i++){
            for(int j=0; j<gameEngine.getColSize(clonedState);j++){
                GameEngine.Placements action = gameEngine.checkValidPlacement(i,j,clonedState,currentRoot.fakePlayer);
                if(action!=null){
                    currentRoot.actions.add(action);
                }
            }
        }

        //For each action in array, place a move, reclone gamestate and create a childnode with the state which has the
        //appropiate gamestate
        if(!currentRoot.actions.isEmpty()){
            for(int i=0;i<currentRoot.actions.size();i++){
                 GameEngine.Placements a = currentRoot.actions.get(i);

                gameEngine.placeMove(currentRoot.clonedState,currentRoot.fakePlayer,a);
                addChild(currentRoot, a.posX,a.posY);
                clonedState = stateInDepth.getClone();
            }
        }
        if(!childrenTemp.isEmpty()){
            buildTree(childrenTemp.getFirst());

        }
        return this;
    }

    private void addChild(GameNode currentRoot,int posX, int posY) {

        GameNode child = new GameNode();

        if(currentRoot.fakePlayer==AI){
            child.fakePlayer=HU;
        }else if (currentRoot.fakePlayer==HU){
            child.fakePlayer=AI;
        }

        child.clonedState=currentRoot.clonedState.getClone();
        child.posX=posX;
        child.posY=posY;
        child.parent=currentRoot;
        currentRoot.children.add(child);
        childrenTemp.push(child);
    }


    private int calculateUtility(GameState clonedState) {
        return clonedState.playerAIScore-clonedState.playerHUScore;
    }
}
