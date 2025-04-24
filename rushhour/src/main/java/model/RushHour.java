package model;

import java.util.*;

public class RushHour {
    private static final int SIZE = 6;
    private final List<Vehicle> vehicles = new ArrayList<>();
    private final List<RushHourObserver> observers = new ArrayList<>();
    private int moveCount = 0;

    public RushHour() {

    }

    public RushHour(RushHour other) {
        this.moveCount = other.moveCount;
        for (Vehicle original : other.vehicles) {
            Position backCopy = new Position(
                original.getBack().getRow(),
                original.getBack().getCol()
            );
            Position frontCopy = new Position(
                original.getFront().getRow(),
                original.getFront().getCol()
            );
            
            Vehicle vehicleCopy = new Vehicle(
                original.getId(),
                backCopy,
                frontCopy
            );
            this.vehicles.add(vehicleCopy);
        }

    }

    public void addObserver(RushHourObserver observer) {
        observers.add(observer);
    }

    private void notifyObservers() {
        for (RushHourObserver observer : observers) {
            observer.boardChanged();
        }
    }

    public void addVehicle(Vehicle vehicle) throws RushHourException {
        for (Vehicle v : vehicles) {
            if (v.getId() == vehicle.getId()) {
                throw new RushHourException("Vehicle with this ID already exists.");
            }
        }
        vehicles.add(vehicle);
        notifyObservers();
    }

    public List<Vehicle> getVehicles() {
        return Collections.unmodifiableList(vehicles);
    }

    public Vehicle getVehicle(char id) throws RushHourException {
        return vehicles.stream()
            .filter(v -> v.getId() == id)
            .findFirst()
            .orElseThrow(() -> new RushHourException("Vehicle not found."));
    }

    public Vehicle getRedCar() {
        return vehicles.stream()
            .filter(v -> v.getId() == 'X') // Assuming 'X' is the red car's ID
            .findFirst()
            .orElse(null);
    }

    public int getMoveCount() {
        return moveCount;
    }

    public void reset() {
        vehicles.clear();
        moveCount = 0;
        notifyObservers();
    }

    public int getSize() {
        return SIZE;
    }

    public boolean isGameOver() {
        for (Vehicle v : vehicles) {
            if (v.getId() == 'X' && v.getFront().getCol() == SIZE - 1) {
                return true;
            }
        }
        return false;
    }

    public boolean canMove(char vehicleId, Direction direction) {
        try {
            Vehicle vehicle = getVehicle(vehicleId);
    
            // Calculate the new positions for the vehicle's front and back
            Position newBack = calculateNewPosition(vehicle.getBack(), direction);
            Position newFront = calculateNewPosition(vehicle.getFront(), direction);
    
            // Check if the new positions are within bounds
            if (!isWithinBounds(newBack) || !isWithinBounds(newFront)) {
                return false;
            }
    
            // Check if the new positions collide with other vehicles
            return !collidesWithOtherVehicles(vehicle, newBack, newFront);
        } catch (RushHourException e) {
            return false; // If the vehicle is not found, it cannot move
        }
    }
    public void moveVehicle(char vehicleId, Direction direction) throws RushHourException {
        // Find the vehicle by its ID
        Vehicle vehicle = getVehicle(vehicleId);
    
        // Check if the move is valid
        if (!canMove(vehicleId, direction)) {
            throw new RushHourException("Invalid move for vehicle " + vehicleId + " in direction " + direction);
        }
    
        // Move the vehicle
        vehicle.move(direction);
    
        // Increment the move count
        moveCount++;
    
        // Notify observers about the board change
        notifyObservers();
    }
    
    private Position calculateNewPosition(Position position, Direction direction) {
        switch (direction) {
            case UP:
                return new Position(position.getRow() - 1, position.getCol());
            case DOWN:
                return new Position(position.getRow() + 1, position.getCol());
            case LEFT:
                return new Position(position.getRow(), position.getCol() - 1);
            case RIGHT:
                return new Position(position.getRow(), position.getCol() + 1);
            default:
                return position;
        }
    }
    
    private boolean isWithinBounds(Position position) {
        return position.getRow() >= 0 && position.getRow() < SIZE && position.getCol() >= 0 && position.getCol() < SIZE;
    }
    
    private boolean collidesWithOtherVehicles(Vehicle movingVehicle, Position newBack, Position newFront) {
        for (Vehicle vehicle : vehicles) {
            if (vehicle != movingVehicle) {
                // Check if any part of the moving vehicle overlaps with another vehicle
                if (overlaps(vehicle, newBack) || overlaps(vehicle, newFront)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean overlaps(Vehicle vehicle, Position position) {
        Position front = vehicle.getFront();
        Position back = vehicle.getBack();
    
        for (int row = back.getRow(); row <= front.getRow(); row++) {
            for (int col = back.getCol(); col <= front.getCol(); col++) {
                if (row == position.getRow() && col == position.getCol()) {
                    return true;
                }
            }
        }
        return false;
    }
}

