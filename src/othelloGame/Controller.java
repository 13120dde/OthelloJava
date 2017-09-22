package othelloGame;

import java.util.LinkedList;

import static othelloGame.Player.AI;
import static othelloGame.Player.EM;
import static othelloGame.Player.HU;

/**
 * Created by Patrik Lind
 *
 */
public class Controller {

    private class Placements {
        LinkedList<Integer> x =new LinkedList();
        LinkedList<Integer> y =new LinkedList();
        int posX;
        int posY;

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
            return builder.toString();
        }
    }

    Player[][] board;
    OthelloBoard ui;
    Player currentPlater;



    public Controller(int row, int col){
        board = new Player[row][col];
        currentPlater = HU;
        stateZero();
        printBoard();
    }

    /**Setups a new board
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
        // TODO remove these after tessting board[3][5]=AI;

        board[0][0]=AI;
        board[0][7]=AI;
        board[7][7]=AI;
        board[7][0]=AI;
        board[7][3]=AI;
        board[0][3]=AI;
        board[0][3]=AI;
        board[0][3]=AI;
        board[3][7]=AI;
        board[3][0]=AI;
        board[3][1]=HU;
        board[5][4]=AI;
        board[2][5]=HU;
        board[1][6]=HU;
        board[2][4]=HU;
        board[2][6]=HU;
        board[3][6]=HU;
        board[4][6]=HU;
        board[4][5]=HU;






    }

    public void printBoard(){
        for (int row =0; row<board.length; row++){
            for(int col=0; col<board[row].length; col++){
                System.out.print(board[row][col]+"\t");
            }
            System.out.println("\n");
        }
    }

    public int getRowSize(){
        return board.length;
    }

    public int getColSize(){
        return board[0].length;
    }

    public void setUi(OthelloBoard ui) {
        this.ui = ui;
    }


    public Player checkGameBoard(int i, int j) {
        return board[i][j];
    }

    public void placeMove(Player playerInCell, int row, int col) {
        System.out.println("In controller.placeMove()\n"+playerInCell+" row: "+row+" col: "+col);

        if(playerInCell== EM){
            //check if legal move
            board[row][col] = Player.HU;
            System.out.println("NEW VALUE "+board[row][col]);
            //ui.repaintCell(row, col, board[row][col]);
            ui.paintBoard();


        }
    }

    /**First pass checks if the cell is empty and there is adjacent opponent
     *
     * @param row
     * @param col
     * @param player
     * @return
     */
    public boolean checkValidPlacement(int row, int col, Player player) {
        System.out.println("#####################IN CHECK VALID PLACEMENT FIRST PASS ####################");

        boolean ok= false;
        Placements possiblePlacements = new Placements();
        possiblePlacements.posX=row;
        possiblePlacements.posY=col;
        //Cell must be empty
        if(board[row][col]== EM){


            //Cell must be adjacent to opposing color
            for(int x = row-1; x<=row+1; x++){
                for(int y = col-1;y<=col+1;y++){
                    if(x>=0 && x<board.length && y>=0 && y<board[0].length){
                        if(board[x][y]!=player && board[x][y]!=EM){
                            ok=true;
                            possiblePlacements.x.add(x);
                            possiblePlacements.y.add(y);

                        }
                    }
                }
            }
            System.out.println(possiblePlacements.toString());
            //No adjacent opposing markers at pos row/col
            if(possiblePlacements.x.isEmpty()){
                return false; //
            }else{
                //second pass checks if there are endmarkers of your color
                Placements endMarkersFound  = checkIfTurnable(possiblePlacements, player);
                if(endMarkersFound.x.isEmpty()){
                    return false;
                }else{
                    ok=true;
                }

               //thirdPass
            }

        }

        return ok;

    }

    private Placements  checkIfTurnable(Placements possiblePlacements, Player player) {
        System.out.println("#####################IN CHECK IF TURNABLE SECOND PASS####################");

        int posX = possiblePlacements.posX;
        int posY = possiblePlacements.posY;
        int numberOfPossiblePlacements=possiblePlacements.x.size();
        Placements posXYOfEndMarkers = new Placements();
        posXYOfEndMarkers.posX=posX;
        posXYOfEndMarkers.posY=posY;

        /**traverse the array from posXY in all directions where there is opposing color adjacent to posXY, if
         * you meet a marker of your color along the trajectory put the in the collection
         */
        for(int i = 0; i<numberOfPossiblePlacements;i++){
            int x = possiblePlacements.x.pop();
            int y = possiblePlacements.y.pop();

            //check in all directions of posXY

            //up-left
            if(x<posX && y<posY){

                while(board[x][y]!=player && board[x][y]!=EM){
                    x--;
                    y--;
                    if((x<0 || y<0)){
                        break;
                    }

                    if(board[x][y]==player){
                        posXYOfEndMarkers.x.add(x);
                        posXYOfEndMarkers.y.add(y);
                        break;
                    }
                }
            }
            //up
            if(x<posX && y==posY){

                while(board[x][y]!=player && board[x][y]!=EM){
                    x--;
                    if(x<0){
                        break;
                    }

                    if(board[x][y]==player){
                        posXYOfEndMarkers.x.add(x);
                        posXYOfEndMarkers.y.add(y);
                        break;
                    }
                }

            }
            //up-right
            if(x<posX && y>posY){
                while(board[x][y]!=player && board[x][y]!=EM){
                    x--;
                    y++;
                    if(x<0||y>=board.length){
                        break;
                    }

                    if(board[x][y]==player){
                        posXYOfEndMarkers.x.add(x);
                        posXYOfEndMarkers.y.add(y);
                        break;
                    }
                }

            }
            //left
            if(x==posX && y<posY){
                while(board[x][y]!=player && board[x][y]!=EM){
                    y--;
                    if(y<0){
                        break;
                    }

                    if(board[x][y]==player){
                        posXYOfEndMarkers.x.add(x);
                        posXYOfEndMarkers.y.add(y);
                        break;
                    }
                }
            }

            //right
            if(x==posX && y>posY){
                while(board[x][y]!=player && board[x][y]!=EM){
                    y++;
                    if(y>=board.length){
                        break;
                    }

                    if(board[x][y]==player){
                        posXYOfEndMarkers.x.add(x);
                        posXYOfEndMarkers.y.add(y);
                        break;

                    }
                }
            }
            //down-left
            if(x>posX && y<posY){
                while(board[x][y]!=player && board[x][y]!=EM){
                    x++;
                    y--;
                    if(x>=board.length || y<0) {
                        break;
                    }
                    if(board[x][y]==player){
                        posXYOfEndMarkers.x.add(x);
                        posXYOfEndMarkers.y.add(y);
                        break;
                    }
                }
            }
            //down
            if(x>posX && y==posY){
                while(board[x][y]!=player && board[x][y]!=EM){
                    x++;
                    if(x>=board.length){
                        break;
                    }

                    if(board[x][y]==player){
                        posXYOfEndMarkers.x.add(x);
                        posXYOfEndMarkers.y.add(y);
                        break;
                    }
                }
            }
            //down-right
            if(x>posX && y>posY){
                while(board[x][y]!=player && board[x][y]!=EM){
                    x++;
                    y++;
                    if(x>=board.length || y>=board.length) {
                        break;
                    }

                    if(board[x][y]==player){
                        posXYOfEndMarkers.x.add(x);
                        posXYOfEndMarkers.y.add(y);
                        break;
                    }
                }

            }

        }

        System.out.println(posXYOfEndMarkers.toString());
        return posXYOfEndMarkers;
    }

}
