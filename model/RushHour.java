package model;

import java.util.*;

public class RushHour {
    private static final int SIZE = 6;
    private final List<Vehicle> vehicles = new ArrayList<>();
    private final List<RushHourObserver> observers = new ArrayList<>();
    private int moveCount = 0;

    public RushHour() {}

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
}

