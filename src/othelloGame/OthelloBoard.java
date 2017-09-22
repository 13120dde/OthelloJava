package othelloGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import static othelloGame.Player.AI;
import static othelloGame.Player.HU;

public class OthelloBoard extends JPanel{

    private BoardCell[][] cells;
    private Player player = AI;
    private Controller controller;

    public OthelloBoard(Controller controller){
        this.controller=controller;
        cells = new BoardCell[controller.getRowSize()][controller.getColSize()];
        controller.setUi(this);
        initUi();
    }

    private void initUi() {
        this.setLayout(new GridLayout(cells.length+1,cells[0].length+1, 2,2));
        paintBoard();

    }

    public void paintBoard() {

        this.removeAll();

        for(int row = 0; row<cells.length; row++) {
            for (int col = 0; col<cells[row].length; col++) {
                    cells[row][col]=null;
                if (controller.checkGameBoard(row, col) == HU) {
                    cells[row][col] = new BoardCell(HU, row, col);
                } else if (controller.checkGameBoard(row, col) == AI) {
                    cells[row][col] = new BoardCell(AI, row, col);
                } else if (controller.checkGameBoard(row, col) == player.EM) {
                    cells[row][col] = new BoardCell(player.EM, row, col);
                }
                this.add(cells[row][col]);
            }
        }

        validate();

    }

    public void repaintCell(int row, int col, Player player) {

        this.removeAll();
        paintBoard();
        validate();

    }


    private class BoardCell extends JPanel{


        private Player playerInCell;
        private Color playerColor;
        private int row, col;

        public BoardCell(Player player, int row, int col){

            this.playerInCell=player;
            this.row=row;
            this.col=col;

            setPlayerColor(playerInCell);
            setCellLayout();
            addListener();

        }

        private void addListener() {


            addMouseListener(new MouseListener() {
                boolean okToPlace = false;

                @Override
                public void mouseClicked(MouseEvent e) {
                    if(okToPlace){
                        controller.placeMove(getPlayerInCell(), row, col);

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

                            if(controller.checkValidPlacement(row,col, AI)){
                                okToPlace=true;
                                setBorder(BorderFactory.createLineBorder(Color.BLUE,2));
                            }else{
                                okToPlace=false;
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

        private void setPlayerColor(Player playerInCell) {
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

        public Player getPlayerInCell() {
            return playerInCell;
        }

        public void setPlayerInCell(Player player){
            playerInCell=player;
            repaint();
            validate();
        }


    }

}