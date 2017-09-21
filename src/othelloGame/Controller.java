package othelloGame;

public class Controller {

    Player[][] board;
    OthelloBoard ui;


    public Controller(int row, int col){
        board = new Player[row][col];
        stateZero();
    }

    private void stateZero() {
        for (int row =0; row<board.length; row++){
            for(int col=0; col<board[row].length; col++){
                if((row==3 && col==3)||(row==4 &&col==4)){

                    board[row][col]=Player.AI;

                }
                else if((row==3 && col==4)||(row==4 &&col==3)){
                    board[row][col]=Player.HUMAN;

                }else{
                    board[row][col]=Player.EMPTY;
                }
                System.out.println(board[row][col]);

            }
        }
    }

    public int getRowSize(){
        return board.length;
    }

    public int getColSize(){
        return board[0].length;
    }

    public Player checkGameBoard(int i, int j) {
        return board[i][j];
    }

    public void placeMove(Player playerInCell, int row, int col) {
        System.out.println("In controller.placeMove()\n"+playerInCell+" row: "+row+" col: "+col);

        if(playerInCell==Player.EMPTY){
            //check if legal move
            board[row][col] = Player.HUMAN;
            System.out.println("NEW VALUE "+board[row][col]);
            //ui.repaintCell(row, col, board[row][col]);
            ui.paintBoard();


        }
    }

    public void setUi(OthelloBoard ui) {
        this.ui = ui;
    }

    public boolean checkValidPlacement(int row, int col, Player player) {

        if(board[row][col]==Player.EMPTY){

            for(int x =row-1; x<=row+1; x++){
                for(int y =col-1; y<=col+1;y++){

                    if(x>=0 && y >=0 && x<=board.length-1 && y <= board[x].length-1){
                        switch (player){
                            case HUMAN:
                                break;
                            case AI:
                                break;
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }
}
