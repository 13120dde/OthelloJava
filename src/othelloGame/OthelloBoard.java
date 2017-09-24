package othelloGame;

import othelloGame.gameLogic.GameEngine;
import othelloGame.gameLogic.GameState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import static othelloGame.gameLogic.GameState.BoardState.EM;
import static othelloGame.gameLogic.GameState.BoardState.HU;
import static othelloGame.gameLogic.GameState.BoardState.AI;

/**Simple yet functional game-board ui.
 * TODO add some ui elements ie scoreboard
 * Created by Patrik Lind
 */
public class OthelloBoard extends JPanel{

    private BoardCell[][] cells;
    private GameState.BoardState player = HU;
    private GameEngine controller;
    private GameState gameBoard;

    public OthelloBoard(GameEngine controller, GameState gameBoard){
        this.controller=controller;
        this.gameBoard=gameBoard;

        cells = new BoardCell[controller.getRowSize(gameBoard)][controller.getColSize(gameBoard)];
        controller.setUi(this);
        initUi();
    }


    private void initUi() {
        this.setLayout(new GridLayout(cells.length+1,cells[0].length+1, 2,2));
        paintBoard();

    }


    /**Clumsy way to repaint the ui, does it from scratch instead of repainting just the cells that altered states.
     *
     */
    public void paintBoard() {


        this.removeAll();

        for(int row = 0; row<cells.length; row++) {
            for (int col = 0; col<cells[row].length; col++) {


                cells[row][col]=null;

                if (controller.checkGameBoard(gameBoard,row, col) == HU) {
                    cells[row][col] = new BoardCell(HU, row, col);
                }
                else if (controller.checkGameBoard(gameBoard,row, col) == AI) {
                    cells[row][col] = new BoardCell(AI, row, col);
                }
                else if (controller.checkGameBoard(gameBoard,row, col) == EM) {
                    cells[row][col] = new BoardCell(EM, row, col);
                }
                this.add(cells[row][col]);
            }
        }

        validate();

    }

    /**Enforces the ui to repaint it depending on the current state of gameboard
     *
     */
    public void repaintCell() {

        this.removeAll();
        paintBoard();
        validate();

    }

    /**Just to be able to play against oneself to try the gamerules.
     *
     * @param player : Player
     */
    public void switchToOtherPlayer(GameState.BoardState player) {
        if(player==AI){
            this.player=HU;
        }
        if(player==HU){
            this.player=AI;
        }
    }


    /**A single cell of gameboard
     *
     */
    private class BoardCell extends JPanel{

        private GameState.BoardState playerInCell;
        private Color playerColor;
        private int row, col;

        public BoardCell(GameState.BoardState player, int row, int col){

            this.playerInCell=player;
            this.row=row;
            this.col=col;

            setPlayerColor(playerInCell);
            setCellLayout();
            addListener();

        }

        private void addListener() {


            addMouseListener(new MouseListener() {

                GameEngine.Placements placements;

                @Override
                public void mouseClicked(MouseEvent e) {
                    System.err.println("PLAYER CLICK");
                       boolean ok = controller.placeMove(gameBoard,player,placements);
                       if(ok){
                           controller.switchPlayer(gameBoard,player);
                       }
                }

                @Override
                public void mousePressed(MouseEvent e) {

                }

                @Override
                public void mouseReleased(MouseEvent e) {

                }

                @Override
                public void mouseEntered(MouseEvent e) {
                   // System.out.println("mouseOver: x:"+row+" y:"+col);
                            placements = controller.checkValidPlacement(row,col,gameBoard,player);
                            if(placements!=null){
                                setBorder(BorderFactory.createLineBorder(Color.BLUE,2));
                            }else{
                                setBorder(BorderFactory.createLineBorder(Color.RED,2));

                            }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    setBorder(null);

                }
            });
        }

        private void setCellLayout() {
            this.setLayout(null);
            this.setPreferredSize(new Dimension(110,110));
            this.setBackground(Color.GREEN);
            this.setOpaque(true);
        }

        private void setPlayerColor(GameState.BoardState playerInCell) {
            switch (playerInCell){
                case HU:
                    playerColor = Color.WHITE;
                    break;
                case AI:
                    playerColor = Color.BLACK;
                    break;
                case EM:
                    playerColor = Color.GREEN;
                    break;
            }
        }

        @Override
        protected void paintComponent(Graphics grphcs){
            super.paintComponent(grphcs);

            Graphics2D g2d = (Graphics2D) grphcs;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(playerColor);
            g2d.fillOval(5,5,100,100);

        }
    }

}
