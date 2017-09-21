package othelloGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class OthelloBoard extends JPanel{

    private BoardCell[][] cells;
    private Player player;
    private Controller controller;

    public OthelloBoard(Controller controller){
        this.controller=controller;
        cells = new BoardCell[controller.getRowSize()][controller.getColSize()];
        controller.setUi(this);
        initUi();
    }

    private void initUi() {
        this.setLayout(new GridLayout(cells.length,cells[0].length, 2,2));
        paintBoard();

    }

    public void paintBoard() {

        this.removeAll();

        for(int row = 0; row<cells.length; row++) {
            for (int col = 0; col<cells[row].length; col++) {
                    cells[row][col]=null;
                if (controller.checkGameBoard(row, col) == player.HUMAN) {
                    cells[row][col] = new BoardCell(player.HUMAN, row, col);
                } else if (controller.checkGameBoard(row, col) == player.AI) {
                    cells[row][col] = new BoardCell(player.AI, row, col);
                } else if (controller.checkGameBoard(row, col) == player.EMPTY) {
                    cells[row][col] = new BoardCell(player.EMPTY, row, col);
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
                @Override
                public void mouseClicked(MouseEvent e) {
                    controller.placeMove(getPlayerInCell(), row, col);
                }

                @Override
                public void mousePressed(MouseEvent e) {

                }

                @Override
                public void mouseReleased(MouseEvent e) {

                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    if(controller.checkValidPlacement(row,col, Player.HUMAN)){
                        setBorder(BorderFactory.createLineBorder(Color.BLUE));
                    }else{
                        setBorder(BorderFactory.createLineBorder(Color.RED));

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
                case HUMAN:
                    playerColor = Color.WHITE;
                    break;
                case AI:
                    playerColor = Color.BLACK;
                    break;
                case EMPTY:
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
