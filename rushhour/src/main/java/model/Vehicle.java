package model;

/** @author Dominik Sain */
public class Vehicle {
    private char id;
    private Position back;
    private Position front;
    private boolean isHorizontal;

    public Vehicle(char id, Position back, Position front) {
        this.id = id;
        this.back = back;
        this.front = front;
        this.isHorizontal = (back.row == front.row); // True if the vehicle is horizontal
    }

    public char getId() {
        return id;
    }

    public Position getBack() {
        return back;
    }

    public Position getFront() {
        return front;
    }

    public boolean isHorizontal() {
        return isHorizontal;
    }

    public void move(Direction direction) {
        if (isHorizontal) { //If vehicle is horizontal we only move columns left or right
            if (direction == Direction.LEFT) {
                back = new Position(back.row, back.col - 1);
                front = new Position(front.row, front.col - 1);
            } else if (direction == Direction.RIGHT) {
                back = new Position(back.row, back.col + 1);
                front = new Position(front.row, front.col + 1);
            }
        } else { // if it is vertical we move it by changing rows up or down
            if (direction == Direction.UP) {
                back = new Position(back.row - 1, back.col);
                front = new Position(front.row - 1, front.col);
            } else if (direction == Direction.DOWN) {
                back = new Position(back.row + 1, back.col);
                front = new Position(front.row + 1, front.col);
            }
        }
    }
}
