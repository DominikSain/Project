package model;

public class Position {
    public int row;
    public int col;

    public Position(int row, int col){ // this is a constructor
        this.row = row;
        this.col = col;
    }

    public Position createPosition(int row, int col){
        return new Position(row, col); // returns a new constructed position
    }
    // get methods for row and col
    public int getRow(){
        return row;
    }

    public int getCol(){
        return col;
    }

}
