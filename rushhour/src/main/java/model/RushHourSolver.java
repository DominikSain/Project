package model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RushHourSolver {
    public RushHour rushHour;
    private List<Move> moves; // Use List to preserve move order

    public RushHourSolver(RushHour rsh) {
        this(rsh, new ArrayList<>());
    }

    // Private constructor for successors
    private RushHourSolver(RushHour rsh, List<Move> moves) {
        this.rushHour = new RushHour(rsh); // Use deep copy constructor
        this.moves = new ArrayList<>(moves); // Copy existing moves
    }

    public Collection<RushHourSolver> getSuccessors() throws RushHourException {
        Set<RushHourSolver> successors = new HashSet<>();
        List<Vehicle> vehicles = rushHour.getVehicles();

        for (Vehicle vehicle : vehicles) {
            // Check all possible directions for this vehicle
            for (Direction dir : Direction.values()) {
                if (rushHour.canMove(vehicle.getId(), dir)) {
                    // Create a new RushHour state with the move applied
                    RushHour newState = new RushHour(this.rushHour);
                    newState.moveVehicle(vehicle.getId(), dir);

                    // Create a new move list
                    List<Move> newMoves = new ArrayList<>(this.moves);
                    newMoves.add(new Move(vehicle.getId(), dir));

                    // Add the new configuration
                    successors.add(new RushHourSolver(newState, newMoves));
                }
            }
        }
        return successors;
    }

    public boolean isValid() {
        Set<Position> occupied = new HashSet<>();
        int boardSize = rushHour.getSize();

        for (Vehicle v : rushHour.getVehicles()) {
            // Check all positions occupied by the vehicle
            for (Position p : v.getOccupiedPositions()) {
                // Boundary check
                if (p.getRow() < 0 || p.getRow() >= boardSize || 
                    p.getCol() < 0 || p.getCol() >= boardSize) {
                    return false;
                }
                // Overlap check
                if (!occupied.add(p)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isGoal() {
        Vehicle redCar = rushHour.getRedCar();
        if (!redCar.isHorizontal()) return false;

        // Check if front of red car is at the exit (2,5)
        Position front = redCar.getFront();
        return front.getRow() == 2 && front.getCol() == 5;
    }

    public List<Move> getMoves() {
        return moves;
    }

    public static RushHourSolver solve(RushHour game) throws RushHourException {
        RushHourSolver initialConfig = new RushHourSolver(game);
        Backtracker backtracker = new Backtracker(false);
        return backtracker.solve(initialConfig);
    }
    public RushHour RushHour(){
        return rushHour;
    }
}