package othelloGame;

import java.util.ArrayList;
import static othelloGame.Player.AI;
import static othelloGame.Player.EM;
import static othelloGame.Player.HU;

/**Game engine implementing the rules of the game.
 * Rule 1: only possible to place a marker adjacent to opponent's marker(s).
 * Rule 2: only possible to place a marker if there is a enclosing player's marker.
 * The rules a realised the two methods: checkValidPlacement(...) & checkIfTurnable(...).
 *
 * This class is also responsible for the state of the board and sets up the beginning state.
 * Created by Patrik Lind
 *
 */
public class Controller {


    /**Object holding placements related to the game-board array. Is used to store adjacent opponents on first-pass rule
     * check and stores positions of all flippable markers on second pass rule check.
     * Holds also the current position of marker to be placed.
     *
     */
    public class Placements {
       private ArrayList<Integer> x = new ArrayList<>();
       private ArrayList<Integer> y = new ArrayList<>();
       private int posX;
       private int posY;

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder. append("currentPosition: posX:"+posX+" - posY:"+posY+"\n");
            builder.append("list x:");
            for (Integer x : x){
                builder.append(x+",\t");
            }
            builder.append("\nlist y:");
            for (Integer y: y) {
                builder.append(y+",\t");
            }
            return builder.toString()+"\n";
        }
    }

    Player[][] board;
    OthelloBoard ui;
    Player currentPlater;
    boolean isRecursive = false;


    /**Instantiate the game engine with and sets the size of game-board.
     *
     * @param row : int
     * @param col : int
     */
    public Controller(int row, int col){

        board = new Player[row][col];
        currentPlater = HU;
        stateZero();
        printBoard();
    }

    /**Setups a new board.
     *
     */
    private void stateZero() {
        for (int row =0; row<board.length; row++){
            for(int col=0; col<board[row].length; col++){
                if((row==3 && col==3)||(row==4 &&col==4)){

                    board[row][col]=Player.AI;

                }
                else if((row==3 && col==4)||(row==4 &&col==3)){
                    board[row][col]= HU;

                }else{
                    board[row][col]= EM;
                }

            }
        }
    }

    /**Just for testing the game-tree.
     *
     */
    public void printBoard(){
        for (int row =0; row<board.length; row++){
            for(int col=0; col<board[row].length; col++){
                System.out.print(board[row][col]+"\t");
            }
            System.out.println("\n");
        }
    }

    /**Returns number of rows of the game-board.
     *
     * @return rows: int
     */
    public int getRowSize(){
        return board.length;
    }

    /**Returns number of columns of the game-board.
     *
     * @return columns : int
     */
    public int getColSize(){
        return board[0].length;
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
    public Player checkGameBoard(int row, int col) {
        return board[row][col];
    }

    /**Pass in a Placements object as argument. Places a marker on platement's posXY, iterates through the x/y lists of
     * the object to turn all markers in the lists. One-step recursive method to chainflip when markers are being flipped.
     *
     * @param player : Player ENUM {AI, HU}
     * @param placements
     */
    public void placeMove(Player player, Placements placements) {
        System.err.println("##############In controller.placeMove(...)######################\n");

        if(placements!=null){


            int size = placements.x.size();
            switch (player){

                case HU:
                    //place player-marker at posXY
                    board[placements.posX][placements.posY] = HU;
                    System.out.println(player+"put marker at row:"+placements.posX+" col:"+placements.posY);


                    //turn all markers of opposing color
                    for ( int i =0; i<size; i++) {

                        //every turned marker needs in turn check if it turns other markers
                        int x = placements.x.get(i);
                        int y = placements.y.get(i);

                        board[x][y]=HU;
                        printBoard();
                        isRecursive=true;
                        Placements p = checkValidPlacement(x,y,HU);
                        placeMove(HU,p);
                        ui.repaintCell();
                    }


                    break;
                case AI:

                    //place player-marker at posXY
                    board[placements.posX][placements.posY] = AI;
                    System.out.println(player+"put marker at row:"+placements.posX+" col:"+placements.posY);


                    //turn all markers of opposing color
                    for ( int i =0; i<size; i++) {

                        //every turned marker needs in turn check if it turns other markers
                        int x = placements.x.get(i);
                        int y = placements.y.get(i);

                        board[x][y]=AI;
                        printBoard();
                        isRecursive=true;
                        Placements p = checkValidPlacement(x,y,AI);
                        placeMove(AI,p);
                        ui.repaintCell();
                    }
                    break;
            }
            isRecursive=false;

            //TODO remove after testing, test to play a round against yourself to see if recursion works as intended.
            ui.switchToOtherPlayer(player);
        }

    }

    /**First pass checks if there is adjacent opponent, if this method is called non-recursively it checks also if
     * current position is empy. The recursion is only done to chain-flip markers.
     *
     * @param row : int
     * @param col : int
     * @param player : Player
     * @return placements : Placements - object holding all XY positions of gameboard where there are flippable markers
     */
    public Placements checkValidPlacement(int row, int col, Player player) {
        System.out.println("#####################IN CHECK VALID PLACEMENT FIRST PASS ####################\n" +
                "row: "+row+"col:"+col);

        //need to allow recursion to place markers inf non-empty positions to be able to chain-flip
        if(!isRecursive){
            if(board[row][col]!=EM){
                return null;
            }
        }

        Placements possiblePlacements = new Placements();
        possiblePlacements.posX=row;
        possiblePlacements.posY=col;

        //Cell must be adjacent to opposing color
        for(int x = row-1; x<=row+1; x++){
            for(int y = col-1;y<=col+1;y++){
                if(x>=0 && x<board.length && y>=0 && y<board[0].length){
                    if(board[x][y]!=player && board[x][y]!=EM){
                        possiblePlacements.x.add(x);
                        possiblePlacements.y.add(y);

                    }
                }
            }
        }

        System.out.println(possiblePlacements.toString());
        //No adjacent opposing markers at pos row/col
        if(possiblePlacements.x.isEmpty()){
            return null;
        }else{
            //second pass checks if there are end-markers of your color and returns positions of all markers that will
            //be flipped
            Placements markersToTurn  = checkIfTurnable(possiblePlacements, player);
            if(markersToTurn.x.isEmpty()){
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
     * @param player : Player
     * @return : Placements - filled with all turnable opponent's markers
     */
    private Placements  checkIfTurnable(Placements possiblePlacements, Player player) {
        System.out.println("#####################IN CHECK IF TURNABLE SECOND PASS####################");

        int posX = possiblePlacements.posX;
        int posY = possiblePlacements.posY;
        int numberOfPossiblePlacements=possiblePlacements.x.size();

        //Probably redundant now since this method doesn't return endmarkers anymore, still good to have to error check
        Placements posXYOfEndMarkers = new Placements();
        posXYOfEndMarkers.posX=posX;
        posXYOfEndMarkers.posY=posY;

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
            int x = possiblePlacements.x.get(i);
            int y = possiblePlacements.y.get(i);

            //Checking in all directions of posXY in one single large method, TODO split into seperate methods

            //up-left
            if(x<posX && y<posY){

                boolean foundEndMarker = false;
                while(board[x][y]!=player && board[x][y]!=EM){

                    if(board[x][y]!=player && board[x][y]!=EM){
                        upLeft.x.add(x);
                        upLeft.y.add(y);
                    }
                    x--;
                    y--;
                    if((x<0 || y<0)){
                        break;
                    }


                    if(board[x][y]==player){
                        posXYOfEndMarkers.x.add(x);
                        posXYOfEndMarkers.y.add(y);
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

                while(board[x][y]!=player && board[x][y]!=EM){

                    if(board[x][y]!=player && board[x][y]!=EM){
                        up.x.add(x);
                        up.y.add(y);
                    }
                    x--;
                    if(x<0){
                        break;
                    }

                    if(board[x][y]==player){
                        posXYOfEndMarkers.x.add(x);
                        posXYOfEndMarkers.y.add(y);
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

                while(board[x][y]!=player && board[x][y]!=EM){

                    if(board[x][y]!=player && board[x][y]!=EM){
                        upRight.x.add(x);
                        upRight.y.add(y);
                    }

                    x--;
                    y++;
                    if(x<0||y>=board.length){
                        break;
                    }

                    if(board[x][y]==player){
                        posXYOfEndMarkers.x.add(x);
                        posXYOfEndMarkers.y.add(y);
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

                while(board[x][y]!=player && board[x][y]!=EM){

                    if(board[x][y]!=player && board[x][y]!=EM){
                        left.x.add(x);
                        left.y.add(y);
                    }

                    y--;
                    if(y<0){
                        break;
                    }

                    if(board[x][y]==player){
                        posXYOfEndMarkers.x.add(x);
                        posXYOfEndMarkers.y.add(y);
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

                while(board[x][y]!=player && board[x][y]!=EM){

                    if(board[x][y]!=player && board[x][y]!=EM){
                        right.x.add(x);
                        right.y.add(y);
                    }

                    y++;
                    if(y>=board.length){
                        break;
                    }

                    if(board[x][y]==player){
                        posXYOfEndMarkers.x.add(x);
                        posXYOfEndMarkers.y.add(y);
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

                while(board[x][y]!=player && board[x][y]!=EM){

                    if(board[x][y]!=player && board[x][y]!=EM){
                        downLeft.x.add(x);
                        downLeft.y.add(y);
                    }

                    x++;
                    y--;
                    if(x>=board.length || y<0) {
                        break;
                    }

                    if(board[x][y]==player){
                        posXYOfEndMarkers.x.add(x);
                        posXYOfEndMarkers.y.add(y);
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

                while(board[x][y]!=player && board[x][y]!=EM){

                    if(board[x][y]!=player && board[x][y]!=EM){
                        down.x.add(x);
                        down.y.add(y);
                    }

                    x++;
                    if(x>=board.length){
                        break;
                    }

                    if(board[x][y]==player){
                        posXYOfEndMarkers.x.add(x);
                        posXYOfEndMarkers.y.add(y);
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

                while(board[x][y]!=player && board[x][y]!=EM){

                    if(board[x][y]!=player && board[x][y]!=EM){
                        downRight.x.add(x);
                        downRight.y.add(y);
                    }

                    x++;
                    y++;
                    if(x>=board.length || y>=board.length) {
                        break;
                    }

                    if(board[x][y]==player){
                        posXYOfEndMarkers.x.add(x);
                        posXYOfEndMarkers.y.add(y);
                        foundEndMarker = true;

                        break;
                    }
                }
                if(!foundEndMarker){
                    downRight = new Placements();
                }

            }

        }

        Placements markersToTurn = concatAllPlacments(upLeft,up,upRight,left,right,downLeft,down,downRight,posX,posY);
        System.out.println("\nEnclosing markers position:\n"+posXYOfEndMarkers.toString());
        System.out.println("\nMarkers to turn position:\n"+markersToTurn.toString());
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
    private Placements concatAllPlacments(Placements upLeft, Placements upRight, Placements up, Placements left, Placements right, Placements downLeft, Placements down, Placements downRight, int posX, int posY) {


        Placements allMarkersToReturn = new Placements();
        allMarkersToReturn.posX=posX;
        allMarkersToReturn.posY=posY;

        if(!upLeft.x.isEmpty()){
            allMarkersToReturn.x.addAll(upLeft.x);
            allMarkersToReturn.y.addAll(upLeft.y);
        }

        if(!up.x.isEmpty()){
            allMarkersToReturn.x.addAll(up.x);
            allMarkersToReturn.y.addAll(up.y);
        }

        if(!upRight.x.isEmpty()){
            allMarkersToReturn.x.addAll(upRight.x);
            allMarkersToReturn.y.addAll(upRight.y);
        }

        if(!left.x.isEmpty()){
            allMarkersToReturn.x.addAll(left.x);
            allMarkersToReturn.y.addAll(left.y);
        }

        if(!right.x.isEmpty()){
            allMarkersToReturn.x.addAll(right.x);
            allMarkersToReturn.y.addAll(right.y);
        }

        if(!downLeft.x.isEmpty()){
            allMarkersToReturn.x.addAll(downLeft.x);
            allMarkersToReturn.y.addAll(downLeft.y);
        }

        if(!down.x.isEmpty()){
            allMarkersToReturn.y.addAll(down.y);
            allMarkersToReturn.x.addAll(down.x);
        }

        if(!downRight.x.isEmpty()){
            allMarkersToReturn.x.addAll(downRight.x);
            allMarkersToReturn.y.addAll(downRight.y);
        }

        return allMarkersToReturn;
    }
}
