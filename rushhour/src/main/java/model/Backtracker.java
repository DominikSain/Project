package model;

import java.util.Collection;

public class Backtracker {
    private final boolean verbose;

    public Backtracker(boolean verbose) {
        this.verbose = verbose;
    }

    public RushHourSolver solve(RushHourSolver config) throws RushHourException {
        if (config == null || !config.isValid()) {
            return null;
        }

        if (verbose) {
            System.out.println("Current moves: " + config.getMoves());
            System.out.println(config.rushHour);
        }

        if (config.isGoal()) {
            return config;
        }

        Collection<RushHourSolver> successors = config.getSuccessors();
        for (RushHourSolver child : successors) {
            RushHourSolver result = solve(child);
            if (result != null) {
                return result;
            }
        }
        return null;
    }
}