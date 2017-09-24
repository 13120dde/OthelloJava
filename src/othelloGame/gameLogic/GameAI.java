package othelloGame.gameLogic;

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

    private class GameNode {

        //Node specific variables
        private boolean isLeaf, isRoot;
        private int posX, posY;
        private float utilityValue;
        private ArrayList<GameNode> children;

        //all possible actions at given state
        private ArrayList<GameEngine.Placements> actions;

        //Objects needed to create a tree from gamee-engine's rules
        private GameEngine gameEngine;
        private GameState clonedGameState;
        private GameState.BoardState fakePlayer;



        public GameNode() {

            fakePlayer = AI;
            utilityValue = 0;
            isRoot = true;
            isLeaf = false;

            actions = new ArrayList<>();
            children = new ArrayList<>();

        }

        public void setDependency(GameEngine gameEngine,GameState gameState){
            this.gameEngine=gameEngine;
            this.clonedGameState=gameState;
        }

        private GameNode buildTree(GameNode root) {

            /**At each state do:
             * check all empty positions in gameState.
             *      for each empty position in gameState do:
             *          get all possible placements from gameEngine - checkValidMove().x.isEmpty()
             *          create a child-leaf for each possiblePlacement
             *          add posXY to each child, placeMove and calculate utility for each child
             *          switch to other player and for every  child do recursion on this method.
             *
             */

            root.fakePlayer = gameEngine.getPlayerInTurn();
            System.err.println(">>Current depth of tree: "+depthOfTree++);
            System.out.println(">>Current state of gameBoard<<\n"+gameState.getClone().toString());
            //Check if there are more empty cells, if none leaf is reached
            if(root.clonedGameState.getRemainingTurns()<=0){
                root.isLeaf=true;
                root.clonedGameState.calculateScores();
                root.utilityValue = root.clonedGameState.playerAIScore-root.clonedGameState.playerHUScore;
                System.err.println(">>>>>>ROOT reached\n"+root.fakePlayer+"\nPlayer score:"+root.clonedGameState.playerHUScore+
                "\nAi score:"+root.clonedGameState.playerAIScore);
                return root;
            }


            //TODO optimize searching to cut off some overhead, can search outwards instead and cancel if checkValidMove return null

             //Get all possible placements at this depth, add them to the list
            for(int x =0; x<gameEngine.getRowSize(); x++){
                for(int y =0; y<gameEngine.getColSize(); y++){
                    GameEngine.Placements action = gameEngine.checkValidPlacement(x,y,clonedGameState, fakePlayer);
                    if(action!=null){
                        actions.add(action);
                    }
                }
            }

            //Place each possible move, create a child-node and pass in the new state of game to the child.
            for(int i =0; i<actions.size();i++){
                gameEngine.placeMove(clonedGameState,root.fakePlayer,actions.get(i));
                //get default state of board at current depth
                root.clonedGameState=gameState.getClone();
                GameNode child = new GameNode();
                child.clonedGameState=root.clonedGameState;
                child.posX = actions.get(i).posX;
                child.posY = actions.get(i).posY;
                children.add(child);
            }

            gameEngine.switchPlayer(root.fakePlayer);
            for(int i =0; i<children.size();i++){
                buildTree(children.get(i));

            }


            return root;
        }
    }


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
        System.err.println("########################### AI IS PICKING ##########################\n" +
                "\n>>>>>>>>>>>>>Generating tree...");
        gameEngine.setTreeCreated(false);
        GameNode state = new GameNode();
        state.setDependency(gameEngine, gameState.getClone());
        long startTime = System.nanoTime();
        state.buildTree(state);
        long stopTime = System.nanoTime();
        gameEngine.setTreeCreated(true);
        System.err.println("TREE CREATED\n" +
                ">>>>>>>>>>>>>>>>Duration: "+(stopTime-startTime)/1000000+"ms");

        //Change gameEngines mode of placements to end turn after placing move after generating tree
        //Placement action =  alphaBetaSearch(state);

        //return current action with highest utility //Placement.xy
        //gameEngine.placeMove(AI,gameState,action);
    }




}
