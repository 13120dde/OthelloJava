package othelloGame;


public class GameState {


    public enum BoardState{
        AI, HU, EM;
    }

    private BoardState[][] gameBoard ;
    private int playerHUScore, playerAIScore;

    public GameState(int row, int col){
        gameBoard = new BoardState[row][col];
        stateZero();

    }

    /**Setups a new board.
     *
     */
    private void stateZero() {
        for (int row =0; row<gameBoard.length; row++){
            for(int col=0; col<gameBoard[row].length; col++){
                if((row==3 && col==3)||(row==4 &&col==4)){

                    gameBoard[row][col]=BoardState.AI;

                }
                else if((row==3 && col==4)||(row==4 &&col==3)){
                    gameBoard[row][col]= BoardState.HU;

                }else{
                    gameBoard[row][col]= BoardState.EM;
                }

            }
        }
    }

    /**Just for testing the game-tree.
     *
     */
    public void printBoard(){
        for (int row =0; row<gameBoard.length; row++){
            for(int col=0; col<gameBoard[row].length; col++){
                System.out.print(gameBoard[row][col]+"\t");
            }
            System.out.println("\n");
        }
    }

    public int getPlayerHUScore() {
        calculateScores();
        return playerHUScore;
    }

    public int getPlayerAIScore() {
        calculateScores();
        return playerAIScore;
    }

    public int getBoardRowSize(){
        return gameBoard.length;
    }

    public int getBoardColSize(){
        return gameBoard[0].length;
    }

    public BoardState getStateInCell(int row, int col) {
        return gameBoard[row][col];
    }

    public void setBoardStateInCell(int posX, int posY, BoardState state) {
        gameBoard[posX][posY] = state;
    }

    private void calculateScores() {

        playerAIScore=0;
        playerHUScore=0;

        for(int i=0; i<gameBoard.length;i++){
            for(int j = 0; j<gameBoard[0].length;j++){
                if(gameBoard[i][j]==BoardState.AI){
                    playerAIScore++;
                }
                if(gameBoard[i][j]==BoardState.HU){
                    playerHUScore++;
                }
            }
        }
    }

}
