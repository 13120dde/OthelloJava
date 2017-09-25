package othelloGame.gameLogic;

import java.util.ArrayList;

/**
 * Created by Patrik Lind on 2017-09-25.
 */
public class Placements {
    private ArrayList<Integer> x;
    private ArrayList<Integer> y;
    private int posX;
    private int posY;


    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }


    public Placements() {
        x = new ArrayList<>();
        y = new ArrayList<>();
        posX = -1;
        posY = -1;
    }

    public void addToListX(int value) {
        x.add(value);
    }

    public void addToListY(int value) {
        y.add(value);
    }

    public int getSize(){
        return x.size();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder. append("currentPosition: posX:"+posX+" - posY:"+posY+"\n");
        builder.append("list x:");
        for (Integer val : x){
            builder.append(val+",\t");
        }
        builder.append("\nlist y:");
        for (Integer val: y) {
            builder.append(val+",\t");
        }
        return builder.toString();
    }

    public int getFromListY(int i) {
        return y.get(i);
    }

    public int getFromListX(int i) {
        return x.get(i);
    }

    public boolean isEmpty() {
        return x.isEmpty();
    }

    public void addAll(ArrayList<Integer> listX, ArrayList<Integer> listY){
        x.addAll(listX);
        y.addAll(listY);
    }


    public ArrayList<Integer> getListX() {
        return x;
    }

    public ArrayList<Integer> getListY() {
        return y;
    }
}