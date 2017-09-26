package othelloGame.gameLogic;

import othelloGame.GameAI;
import othelloGame.OthelloBoard;

import java.util.LinkedList;

import static othelloGame.gameLogic.GameState.Player.EM;
import static othelloGame.gameLogic.GameState.Player.HU;
import static othelloGame.gameLogic.GameState.Player.AI;

/**Game engine implementing the rules of the game.
 *
 * Rule 1: Can only place a marker in a empty cell.
 * Rule 2: Only possible to place a marker adjacent to opponent's marker(s).
 * Rule 3: Only possible to place a marker if there is a enclosing player's marker.
 * The rules a realised by the two methods: checkValidPlacement(...) & checkIfTurnable(...).
 *
 * Created by Patrik Lind ,13120dde@gmail.com
 */
public class GameEngine {


    public GameState.Player getPlayerInTurn() {
        return playerInTurn;
    }

    /**Object holding placements related to the game-board array. Is used to store adjacent opponents on first-pass rule
     * check and stores positions of all flippable markers on second pass rule check.
     * Holds also the current position of marker to be placed.
     *
     */


    //Game engine variables
    private OthelloBoard ui;
    private GameState gameBoard;
    private GameState.Player playerInTurn;
    private GameAI ai;
    private boolean isRecursive = false;
    private boolean treeCreated = true;

    /**Instantiate the game engine with GameState object passed in as argument.
     * @param gameState
     */
    public GameEngine(GameState gameState, GameAI ai){

        playerInTurn = HU;
        gameBoard = gameState;
        this.ai=ai;
        ai.setController(this);

    }

    public GameEngine(){

    }

    /**Returns number of rows of the game-board.
     *
     * @return rows: int
     */
    public int getRowSize(GameState gameBoard){
        return gameBoard.getBoardRowSize();
    }

    /**Returns number of columns of the game-board.
     *
     * @return columns : int
     */
    public int getColSize(GameState gameBoard){
        return gameBoard.getBoardColSize();
    }

    /**Called by ai to let it generate a gametree.
     *
     * @param treeCreated : boolean
     */
    public void setTreeCreated(boolean treeCreated) {
        this.treeCreated = treeCreated;
    }


    /**Add ui-controller dependency.
     *
     * @return
     */
    public void setUi(OthelloBoard ui) {
        this.ui = ui;
    }


    /**Returns the state of the cell at given position
     *
     * @param row : int
     * @param col : int
     * @return Player : ENUM Player {AI, HU, EM}
     */
    public GameState.Player checkGameBoard(GameState gameBoard, int row, int col) {
        return gameBoard.getStateInCell(row,col);
    }

    public void switchPlayer(GameState gameBoard,GameState.Player state){
        System.out.print(playerInTurn+" ended his turn. ");

        //Switch turn to other player
        if(state==AI){
            playerInTurn = HU;
        }else if(state==HU){
            playerInTurn =AI;
            System.out.println(playerInTurn+" begins his turn.");

        }

        //Check if next player in turn can place a move, otherwise switch back to last player
        if(!checkIfPlayerCanPlaceAMove(gameBoard,state)){
            switchPlayer(gameBoard,state);
        }

        //Tree need to swithch amongs player in turn to be abel to build itself
        if(treeCreated && state==HU){
            ai.choseMove();
        }

    }

    private boolean checkIfPlayerCanPlaceAMove(GameState gameBoard, GameState.Player playerInTurn){
        LinkedList<Actions> placements = new LinkedList();
        Actions action = new Actions();
        for(int x =0; x<gameBoard.getBoardRowSize();x++){
            for(int y = 0; y<gameBoard.getBoardColSize();y++){
                action = checkValidPlacement(x,y,gameBoard,playerInTurn);
                if(action!=null){
                    placements.add(action);
                }
            }
        }
        if(placements.isEmpty()){
            return false;
        }
        return true;
    }

    /**Pass in a Actions object as argument. Places a marker on platement's posXY, iterates through the x/y lists of
     * the object to turn all markers in the lists. One-step recursive method to chainflip when markers are being flipped.
     *
     * @param state: Player ENUM {AI, HU}
     * @param actions
     */
    public boolean placeMove(GameState gameBoard, GameState.Player state, Actions actions) {

        if((actions !=null)){


            int size = actions.getSize();
            switch (state){

                case HU:

                    //place player-marker at posXY
                    gameBoard.setBoardStateInCell(actions.getPosX(), actions.getPosY(), HU);

                    //turn all markers of opposing color
                    for ( int i =0; i<size; i++) {

                        //every turned marker needs in turn check if it turns other markers
                        int x = actions.getFromListX(i);
                        int y = actions.getFromListY(i);

                        gameBoard.setBoardStateInCell(x,y, HU);

                        //recursion to handle chain-flipping
                        isRecursive=true;
                        Actions p = checkValidPlacement(x,y, gameBoard,HU);
                        placeMove(gameBoard,HU,p);
                    }


                    break;

                case AI:

                    //place player-marker at posXY
                    gameBoard.setBoardStateInCell(actions.getPosX(), actions.getPosY(), AI);

                    //turn all markers of opposing color
                    for ( int i =0; i<size; i++) {

                        //every turned marker needs in turn check if it turns other markers
                        int x = actions.getFromListX(i);
                        int y = actions.getFromListY(i);

                        gameBoard.setBoardStateInCell(x,y, AI);

                        //recursion to handle chain-flipping
                        isRecursive=true;
                        Actions p = checkValidPlacement(x,y, gameBoard,AI);
                        placeMove(gameBoard,AI,p);
                    }
                    break;

            }
            isRecursive=false;

//            System.out.println("##############In gameEngine.placeMove(...)######################");
  //          System.out.println(state+" put marker at row:"+actions.posX+" col:"+actions.posY);

    //        System.out.println(gameBoard.toString());

            //Let the AI to build its tree breath first.
            /*if(treeCreated){
                ui.repaintCell();
            }*/

            return true;
            //TODO remove after testing, test to play a round against yourself to see if recursion works as intended.
           // ui.switchToOtherPlayer(state);
        }

        return false;

    }

    /**First pass checks if there is adjacent opponent, if this method is called non-recursively it checks also if
     * current position is empy. The recursion is only done to chain-flip markers.
     *
     * @param row : int
     * @param col : int
     * @param state : Player
     * @return placements : Actions - object holding all XY positions of gameboard where there are flippable markers
     */
    public Actions checkValidPlacement(int row, int col, GameState gameBoard, GameState.Player state) {
       // System.out.println("#####################IN CHECK VALID PLACEMENT FIRST PASS ####################\n" +
         //       "Player: "+state+"\trow: "+row+"\tcol:"+col);

        //need to allow recursion to place markers inf non-empty positions to be able to chain-flip
        if(!isRecursive){
            if(gameBoard.getStateInCell(row,col)!= EM){
                return null;
            }
        }

        Actions possibleActions = new Actions();
        possibleActions.setPosX(row);
        possibleActions.setPosY(col);

        //Cell must be adjacent to opposing color
        for(int x = row-1; x<=row+1; x++){
            for(int y = col-1;y<=col+1;y++){
                if(x>=0 && x<gameBoard.getBoardRowSize() && y>=0 && y<gameBoard.getBoardColSize()){

                    if(gameBoard.getStateInCell(x,y)!=state && gameBoard.getStateInCell(x,y)!= EM){
                        possibleActions.addToListX(x);
                        possibleActions.addToListY(y);

                    }
                }
            }
        }

    //    System.out.println(possibleActions.toString());
        //No adjacent opposing markers at pos row/col
        if(possibleActions.isEmpty()){
            return null;
        }else{
            //second pass checks if there are end-markers of your color and returns positions of all markers that will
            //be flipped
            Actions markersToTurn  = checkIfTurnable(gameBoard, possibleActions, state);
            if(markersToTurn.getSize()<=0){
                return null;
            }else{
                return markersToTurn;
            }
        }

    }

    /**Given the possibleActions argument, checks in all directions of adjacent opponents markers if there are
     * any playerowned markers that enclose opponents markers.
     *
     * @param possibleActions : Actions - all adjacent markers of opposing color
     * @param state : Player
     * @return : Actions - filled with all turnable opponent's markers
     */
    private Actions checkIfTurnable(GameState gameBoard, Actions possibleActions,
                                    GameState.Player state) {
     //   System.out.println("#####################IN CHECK IF TURNABLE SECOND PASS####################\n");

        int posX = possibleActions.getPosX();
        int posY = possibleActions.getPosY();

        int numberOfPossiblePlacements= possibleActions.getSize();

        //Probably redundant now since this method doesn't return endmarkers anymore, still good to have to error check
        Actions posXYOfEndMarkers = new Actions();

        posXYOfEndMarkers.setPosX(posX);
        posXYOfEndMarkers.setPosY(posY);

        //Was too much hassle adding and removing in one single list, appends all result at the end
        Actions upLeft = new Actions();
        Actions up= new Actions();
        Actions upRight= new Actions();
        Actions left= new Actions();
        Actions right= new Actions();
        Actions downLeft= new Actions();
        Actions down= new Actions();
        Actions downRight= new Actions();

        /**traverse the array from posXY in all directions where there is opposing color adjacent to posXY, if
         * you meet a marker of your color along the trajectory put the in the collection
         */
        for(int i = 0; i<numberOfPossiblePlacements;i++){
            int x = possibleActions.getFromListX(i);
            int y = possibleActions.getFromListY(i);

            //Checking in all directions of posXY in one single large method, TODO split into seperate methods

            //up-left
            if(x<posX && y<posY){

                boolean foundEndMarker = false;

                while(gameBoard.getStateInCell(x,y)!=state && gameBoard.getStateInCell(x,y)!=EM){

                    if(gameBoard.getStateInCell(x,y)!=state && gameBoard.getStateInCell(x,y)!=EM){
                        upLeft.addToListX(x);
                        upLeft.addToListY(y);
                    }
                    x--;
                    y--;
                    if((x<0 || y<0)){
                        break;
                    }


                    if(gameBoard.getStateInCell(x,y)==state){
                        posXYOfEndMarkers.addToListX(x);
                        posXYOfEndMarkers.addToListY(y);
                        foundEndMarker = true;
                        break;
                    }
                }
                if(!foundEndMarker){
                    upLeft = new Actions();
                }
            }

            //up
            if(x<posX && y==posY){
                boolean foundEndMarker = false;

                while(gameBoard.getStateInCell(x,y)!=state && gameBoard.getStateInCell(x,y)!=EM){

                    if(gameBoard.getStateInCell(x,y)!=state && gameBoard.getStateInCell(x,y)!=EM){
                        up.addToListX(x);
                        up.addToListY(y);
                    }
                    x--;
                    if(x<0){
                        break;
                    }

                    if(gameBoard.getStateInCell(x,y)==state){
                        posXYOfEndMarkers.addToListX(x);
                        posXYOfEndMarkers.addToListY(y);
                        foundEndMarker = true;

                        break;
                    }
                }
                if(!foundEndMarker){
                    up = new Actions();
                }

            }
            //up-right
            if(x<posX && y>posY){
                boolean foundEndMarker = false;

                while(gameBoard.getStateInCell(x,y)!=state && gameBoard.getStateInCell(x,y)!=EM){

                    if(gameBoard.getStateInCell(x,y)!=state && gameBoard.getStateInCell(x,y)!=EM){
                        upRight.addToListX(x);
                        upRight.addToListY(y);
                    }

                    x--;
                    y++;
                    if(x<0||y>=gameBoard.getBoardColSize()){
                        break;
                    }

                    if(gameBoard.getStateInCell(x,y)==state){
                        posXYOfEndMarkers.addToListX(x);
                        posXYOfEndMarkers.addToListY(y);
                        foundEndMarker = true;

                        break;
                    }
                }
                if(!foundEndMarker){
                    upRight = new Actions();
                }

            }
            //left
            if(x==posX && y<posY){
                boolean foundEndMarker = false;

                while(gameBoard.getStateInCell(x,y)!=state && gameBoard.getStateInCell(x,y)!=EM){

                    if(gameBoard.getStateInCell(x,y)!=state && gameBoard.getStateInCell(x,y)!=EM){
                        left.addToListX(x);
                        left.addToListY(y);
                    }

                    y--;
                    if(y<0){
                        break;
                    }

                    if(gameBoard.getStateInCell(x,y)==state){
                        posXYOfEndMarkers.addToListX(x);
                        posXYOfEndMarkers.addToListY(y);
                        foundEndMarker = true;

                        break;
                    }
                }
                if(!foundEndMarker){
                    left = new Actions();
                }
            }

            //right
            if(x==posX && y>posY){
                boolean foundEndMarker = false;

                while(gameBoard.getStateInCell(x,y)!=state && gameBoard.getStateInCell(x,y)!=EM){

                    if(gameBoard.getStateInCell(x,y)!=state && gameBoard.getStateInCell(x,y)!=EM){
                        right.addToListX(x);
                        right.addToListY(y);
                    }

                    y++;
                    if(y>=getColSize(gameBoard)){
                        break;
                    }

                    if(gameBoard.getStateInCell(x,y)==state){
                        posXYOfEndMarkers.addToListX(x);
                        posXYOfEndMarkers.addToListY(y);
                        foundEndMarker = true;

                        break;

                    }
                }
                if(!foundEndMarker){
                    right = new Actions();
                }
            }
            //down-left
            if(x>posX && y<posY){
                boolean foundEndMarker = false;

                while(gameBoard.getStateInCell(x,y)!=state && gameBoard.getStateInCell(x,y)!=EM){

                    if(gameBoard.getStateInCell(x,y)!=state && gameBoard.getStateInCell(x,y)!=EM){
                        downLeft.addToListX(x);
                        downLeft.addToListY(y);
                    }

                    x++;
                    y--;
                    if(x>=gameBoard.getBoardRowSize() || y<0) {
                        break;
                    }

                    if(gameBoard.getStateInCell(x,y)==state){
                        posXYOfEndMarkers.addToListX(x);
                        posXYOfEndMarkers.addToListY(y);
                        foundEndMarker = true;

                        break;
                    }
                }
                if(!foundEndMarker){
                    downLeft = new Actions();
                }
            }
            //down
            if(x>posX && y==posY){
                boolean foundEndMarker = false;

                while(gameBoard.getStateInCell(x,y)!=state && gameBoard.getStateInCell(x,y)!=EM){

                    if(gameBoard.getStateInCell(x,y)!=state && gameBoard.getStateInCell(x,y)!=EM){
                        down.addToListX(x);
                        down.addToListY(y);
                    }

                    x++;
                    if(x>=gameBoard.getBoardRowSize()){
                        break;
                    }

                    if(gameBoard.getStateInCell(x,y)==state){
                        posXYOfEndMarkers.addToListX(x);
                        posXYOfEndMarkers.addToListY(y);
                        foundEndMarker = true;

                        break;
                    }
                }
                if(!foundEndMarker){
                    down = new Actions();
                }
            }
            //down-right
            if(x>posX && y>posY){
                boolean foundEndMarker = false;

                while(gameBoard.getStateInCell(x,y)!=state && gameBoard.getStateInCell(x,y)!=EM){

                    if(gameBoard.getStateInCell(x,y)!=state && gameBoard.getStateInCell(x,y)!=EM){
                        downRight.addToListX(x);
                        downRight.addToListY(y);
                    }

                    x++;
                    y++;
                    if(x>=gameBoard.getBoardRowSize() || y>=gameBoard.getBoardColSize()) {
                        break;
                    }

                    if(gameBoard.getStateInCell(x,y)==state){
                        posXYOfEndMarkers.addToListX(x);
                        posXYOfEndMarkers.addToListY(y);
                        foundEndMarker = true;

                        break;
                    }
                }
                if(!foundEndMarker){
                    downRight = new Actions();
                }

            }

        }

        Actions markersToTurn = appendPlacements(upLeft,up,upRight,left,right,downLeft,down,downRight,posX,posY);
//        System.out.println("\nEnclosing markers position:\n"+posXYOfEndMarkers.toString());
 //       System.out.println("\nMarkers to turn position:\n"+markersToTurn.toString());
        return markersToTurn;
    }

    /**Append all possible results to one Placement object.
     *
     * @param upLeft
     * @param upRight
     * @param up
     * @param left
     * @param right
     * @param downLeft
     * @param down
     * @param downRight
     * @param posX
     * @param posY
     * @return : Placement - filled with all opponent's markers that will be flipped.
     */
    private Actions appendPlacements(Actions upLeft, Actions upRight, Actions up, Actions left, Actions right, Actions downLeft, Actions down, Actions downRight, int posX, int posY) {


        Actions allMarkersToReturn = new Actions();
        allMarkersToReturn.setPosX(posX);
        allMarkersToReturn.setPosY(posY);

        if(!upLeft.isEmpty()){
            allMarkersToReturn.addAll(upLeft.getListX(), upLeft.getListY());
        }

        if(!up.isEmpty()){
            allMarkersToReturn.addAll(up.getListX(), up.getListY());
        }

        if(!upRight.isEmpty()){
            allMarkersToReturn.addAll(upRight.getListX(), upRight.getListY());
        }

        if(!left.isEmpty()){
            allMarkersToReturn.addAll(left.getListX(), left.getListY());
        }

        if(!right.isEmpty()){
            allMarkersToReturn.addAll(right.getListX(), right.getListY());

        }

        if(!downLeft.isEmpty()){
            allMarkersToReturn.addAll(downLeft.getListX(), downLeft.getListY());
        }

        if(!down.isEmpty()){
            allMarkersToReturn.addAll(down.getListX(), down.getListY());
        }

        if(!downRight.isEmpty()){
            allMarkersToReturn.addAll(downRight.getListX(), downRight.getListY());
        }

        return allMarkersToReturn;
    }
}
