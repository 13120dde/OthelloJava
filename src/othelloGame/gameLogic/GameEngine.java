package othelloGame.gameLogic;

import othelloGame.GameAI;
import othelloGame.OthelloBoard;

import java.util.LinkedList;

import static othelloGame.gameLogic.GameState.BoardState.EM;
import static othelloGame.gameLogic.GameState.BoardState.HU;
import static othelloGame.gameLogic.GameState.BoardState.AI;

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


    public GameState.BoardState getPlayerInTurn() {
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
    private GameState.BoardState playerInTurn;
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
    public GameState.BoardState checkGameBoard(GameState gameBoard,int row, int col) {
        return gameBoard.getStateInCell(row,col);
    }

    public void switchPlayer(GameState gameBoard,GameState.BoardState state){
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

    private boolean checkIfPlayerCanPlaceAMove(GameState gameBoard, GameState.BoardState playerInTurn){
        LinkedList<Placements> placements = new LinkedList();
        Placements action = new Placements();
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

    /**Pass in a Placements object as argument. Places a marker on platement's posXY, iterates through the x/y lists of
     * the object to turn all markers in the lists. One-step recursive method to chainflip when markers are being flipped.
     *
     * @param state: BoardState ENUM {AI, HU}
     * @param placements
     */
    public boolean placeMove(GameState gameBoard,GameState.BoardState state, Placements placements) {

        if((placements!=null)){


            int size = placements.getSize();
            switch (state){

                case HU:

                    //place player-marker at posXY
                    gameBoard.setBoardStateInCell(placements.getPosX(),placements.getPosY(), HU);

                    //turn all markers of opposing color
                    for ( int i =0; i<size; i++) {

                        //every turned marker needs in turn check if it turns other markers
                        int x = placements.getFromListX(i);
                        int y = placements.getFromListY(i);

                        gameBoard.setBoardStateInCell(x,y, HU);

                        //recursion to handle chain-flipping
                        isRecursive=true;
                        Placements p = checkValidPlacement(x,y, gameBoard,HU);
                        placeMove(gameBoard,HU,p);
                    }


                    break;

                case AI:

                    //place player-marker at posXY
                    gameBoard.setBoardStateInCell(placements.getPosX(),placements.getPosY(), AI);

                    //turn all markers of opposing color
                    for ( int i =0; i<size; i++) {

                        //every turned marker needs in turn check if it turns other markers
                        int x = placements.getFromListX(i);
                        int y = placements.getFromListY(i);

                        gameBoard.setBoardStateInCell(x,y, AI);

                        //recursion to handle chain-flipping
                        isRecursive=true;
                        Placements p = checkValidPlacement(x,y, gameBoard,AI);
                        placeMove(gameBoard,AI,p);
                    }
                    break;

            }
            isRecursive=false;

//            System.out.println("##############In gameEngine.placeMove(...)######################");
  //          System.out.println(state+" put marker at row:"+placements.posX+" col:"+placements.posY);

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
     * @return placements : Placements - object holding all XY positions of gameboard where there are flippable markers
     */
    public Placements checkValidPlacement(int row, int col, GameState gameBoard,GameState.BoardState state) {
       // System.out.println("#####################IN CHECK VALID PLACEMENT FIRST PASS ####################\n" +
         //       "Player: "+state+"\trow: "+row+"\tcol:"+col);

        //need to allow recursion to place markers inf non-empty positions to be able to chain-flip
        if(!isRecursive){
            if(gameBoard.getStateInCell(row,col)!= EM){
                return null;
            }
        }

        Placements possiblePlacements = new Placements();
        possiblePlacements.setPosX(row);
        possiblePlacements.setPosY(col);

        //Cell must be adjacent to opposing color
        for(int x = row-1; x<=row+1; x++){
            for(int y = col-1;y<=col+1;y++){
                if(x>=0 && x<gameBoard.getBoardRowSize() && y>=0 && y<gameBoard.getBoardColSize()){

                    if(gameBoard.getStateInCell(x,y)!=state && gameBoard.getStateInCell(x,y)!= EM){
                        possiblePlacements.addToListX(x);
                        possiblePlacements.addToListY(y);

                    }
                }
            }
        }

    //    System.out.println(possiblePlacements.toString());
        //No adjacent opposing markers at pos row/col
        if(possiblePlacements.isEmpty()){
            return null;
        }else{
            //second pass checks if there are end-markers of your color and returns positions of all markers that will
            //be flipped
            Placements markersToTurn  = checkIfTurnable(gameBoard,possiblePlacements, state);
            if(markersToTurn.getSize()<=0){
                return null;
            }else{
                return markersToTurn;
            }
        }

    }

    /**Given the possiblePlacements argument, checks in all directions of adjacent opponents markers if there are
     * any playerowned markers that enclose opponents markers.
     *
     * @param possiblePlacements : Placements - all adjacent markers of opposing color
     * @param state : Player
     * @return : Placements - filled with all turnable opponent's markers
     */
    private Placements  checkIfTurnable(GameState gameBoard,Placements possiblePlacements,
                                        GameState.BoardState state) {
     //   System.out.println("#####################IN CHECK IF TURNABLE SECOND PASS####################\n");

        int posX = possiblePlacements.getPosX();
        int posY = possiblePlacements.getPosY();

        int numberOfPossiblePlacements=possiblePlacements.getSize();

        //Probably redundant now since this method doesn't return endmarkers anymore, still good to have to error check
        Placements posXYOfEndMarkers = new Placements();

        posXYOfEndMarkers.setPosX(posX);
        posXYOfEndMarkers.setPosY(posY);

        //Was too much hassle adding and removing in one single list, appends all result at the end
        Placements upLeft = new Placements();
        Placements up= new Placements();
        Placements upRight= new Placements();
        Placements left= new Placements();
        Placements right= new Placements();
        Placements downLeft= new Placements();
        Placements down= new Placements();
        Placements downRight= new Placements();

        /**traverse the array from posXY in all directions where there is opposing color adjacent to posXY, if
         * you meet a marker of your color along the trajectory put the in the collection
         */
        for(int i = 0; i<numberOfPossiblePlacements;i++){
            int x = possiblePlacements.getFromListX(i);
            int y = possiblePlacements.getFromListY(i);

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
                    upLeft = new Placements();
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
                    up = new Placements();
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
                    upRight = new Placements();
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
                    left = new Placements();
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
                    right = new Placements();
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
                    downLeft = new Placements();
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
                    down = new Placements();
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
                    downRight = new Placements();
                }

            }

        }

        Placements markersToTurn = appendPlacements(upLeft,up,upRight,left,right,downLeft,down,downRight,posX,posY);
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
    private Placements appendPlacements(Placements upLeft, Placements upRight, Placements up, Placements left, Placements right, Placements downLeft, Placements down, Placements downRight, int posX, int posY) {


        Placements allMarkersToReturn = new Placements();
        allMarkersToReturn.setPosY(posX);
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
