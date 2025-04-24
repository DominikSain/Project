package model;

import java.util.ArrayList;
import java.util.List;

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
    public List<Position> getOccupiedPositions() {
    List<Position> positions = new ArrayList<>();
    Position back = getBack();
    int size = 2;

    if (isHorizontal()) {
        int row = back.getRow();
        int startCol = back.getCol();
        for (int i = 0; i < size; i++) {
            positions.add(new Position(row, startCol + i));
        }
    } else { // vertical
        int col = back.getCol();
        int startRow = back.getRow();
        for (int i = 0; i < size; i++) {
            positions.add(new Position(startRow + i, col));
        }
    }
    return positions;
}
}
