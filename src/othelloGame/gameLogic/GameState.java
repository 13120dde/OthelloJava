package othelloGame.gameLogic;

import static othelloGame.gameLogic.GameState.Player.AI;
import static othelloGame.gameLogic.GameState.Player.EM;

/**This class is responsible for the state of the game. It's a 2d array holding Player enumerations which can be:
 * HU - human
 * AI - Ai
 * EM - Empty
 */
public class GameState {



    public enum Player {
        AI, HU, EM;
    }

    private Player[][] gameBoard ;
    private int playerHUScore, playerAIScore,row, col;


    public GameState(int row, int col){
        this.row=row;
        this.col=col;

        gameBoard = new Player[row][col];
        stateZero();

    }

    /**Setups a new board by instantiating all the cells to empty, except for the for cells in the middle, which are
     * occupied by 2 human and 2 ai markers.
     */
    private void stateZero() {

        for (int row =0; row<gameBoard.length; row++){
            for(int col=0; col<gameBoard[row].length; col++){

                if((row==(gameBoard.length/2)-1 && col==(gameBoard.length/2)-1)||(row==gameBoard.length/2 &&col==gameBoard.length/2)){
                    gameBoard[row][col]= AI;
                }

                else if((row==(gameBoard.length/2)-1 && col==gameBoard.length/2)||(row==gameBoard.length/2 &&col==(gameBoard.length/2)-1)){
                    gameBoard[row][col]= Player.HU;

                }else{
                    gameBoard[row][col]= EM;
                }

            }
        }

    }

    public int getRemainingTurns() {
       int remainingTurns=0;
       for(int i=0;i<gameBoard.length;i++){
           for(int j=0;j<gameBoard.length;j++){
               if(gameBoard[i][j]==EM){
                   remainingTurns++;
               }
           }
       }
        return remainingTurns;
    }

    /**Prints out the board in it's current state.
     *
     * @return board : String
     */
    private String printBoard(){
        String board ="";
        int x=0, y=0;

        while(y<8){
            board+=y+"\t";
            y++;
        }
        board+="\n";

        for (int row =0; row<gameBoard.length; row++){

            for(int col=0; col<gameBoard[row].length; col++){
                board+=gameBoard[row][col]+"\t";

            }
            board+=x+"\n";
            x++;
        }

        return board;
    }

    /**Returns the number of rows of the board.
     *
     * @return rows : int
     */
    public int getBoardRowSize(){
        return gameBoard.length;
    }

    /**Returns the number of columns of the board.
     *
     * @return cols: int
     */
    public int getBoardColSize(){
        return gameBoard[0].length;
    }

    /**Returns the current state of a cell at a given position.
     *
     * @param row : int
     * @param col : int
     * @return BoarState : {HU, AI, EM} -Human, AI, Empty
     */
    public Player getStateInCell(int row, int col) {
        return gameBoard[row][col];
    }


    /**Changest the player of a cell at given position.
     *
     * @param posX : int
     * @param posY : int
     * @param player : Boardstate {HU, AI} - !EM, cant remove player's markers.
     */
    protected void setBoardStateInCell(int posX, int posY, Player player) {
        gameBoard[posX][posY] = player;
    }


    /**Traverses the game-board array and calculates scores for each player.
     *
     */
    public void calculateScores() {

        playerAIScore=0;
        playerHUScore=0;

        for(int i=0; i<gameBoard.length;i++){
            for(int j = 0; j<gameBoard[0].length;j++){
                if(gameBoard[i][j]== AI){
                    playerAIScore++;
                }
                if(gameBoard[i][j]== Player.HU){
                    playerHUScore++;
                }
            }
        }
    }

    /**Creates a new GameState object and copies all the values from current GameState object.
     *
     * @return clone : GameState
     */
    public GameState getClone() {
        GameState clone = new GameState(row,col);
        clone.playerHUScore=this.playerHUScore;
        clone.playerAIScore=this.playerAIScore;
        for(int i=0; i<row; i++){
            for (int j =0; j<col;j++){
                clone.gameBoard[i][j]=this.gameBoard[i][j];
            }
        }
        return clone;
    }

    public int getPlayerHUScore(){
        calculateScores();
        return playerHUScore;
    }

    public int getPlayerAIScore(){
        calculateScores();
        return playerAIScore;
    }


    @Override
    public String toString(){
        calculateScores();
        return  printBoard()+"\n"+"Human score: "+playerHUScore+"\nAI score:"+playerAIScore+"\nMoves left: "+getRemainingTurns();
    }

}
