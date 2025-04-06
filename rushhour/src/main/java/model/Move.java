package model;

/** @author Dominik Sain */
public class Move {
    private char vehicleId;
    private Direction direction;

    public Move(char vehicleId, Direction direction) {
        this.vehicleId = vehicleId;
        this.direction = direction;
    }

    public char getVehicleId() {
        return vehicleId;
    }

    public Direction getDirection() {
        return direction;
    }
}

