package model;
/** @author Laura Babic */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RushHour {
    public static final int BOARD_DIM = 6;
    public static final char RED_SYMBOL = 'R';
    public static final char EMPTY_SYMBOL = '.';
    public static final Position EXIT_POS = new Position(2, 5); // exit is always at row 2, column 5

    private Map<Character, Vehicle> vehicles;
    private char[][] board;
    private int moveCount;
    private static final String DEFAULT_FILENAME = "data/03_01.csv";

    public RushHour() {
        this(DEFAULT_FILENAME);
    }

    public RushHour(String filename) {
        this.vehicles = new HashMap<>();
        this.board = new char[BOARD_DIM][BOARD_DIM];
        this.moveCount = 0;
        
        // initialize an empty board
        initializeEmptyBoard();
        
        // load initial configuration from file
        loadInitialConfiguration(filename);
    }

    private void initializeEmptyBoard() {
        for (int i = 0; i < BOARD_DIM; i++) {
            for (int j = 0; j < BOARD_DIM; j++) {
                board[i][j] = EMPTY_SYMBOL;
            }
        }
    }

    private void loadInitialConfiguration(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 5) continue;
                
                char symbol = parts[0].charAt(0);
                int backRow = Integer.parseInt(parts[1]);
                int backCol = Integer.parseInt(parts[2]);
                int frontRow = Integer.parseInt(parts[3]);
                int frontCol = Integer.parseInt(parts[4]);
                
                Position back = new Position(backRow, backCol);
                Position front = new Position(frontRow, frontCol);
                
                Vehicle vehicle = new Vehicle(symbol, back, front);
                vehicles.put(symbol, vehicle);
            }
            updateBoard();
        } catch (IOException e) {
            System.err.println("Error loading initial configuration: " + e.getMessage());
            // Fallback to default configuration if file can't be read
            initializeDefaultVehicles();
        }
    }

    private void initializeDefaultVehicles() {
        // This matches the 03_01.csv configuration as a fallback
        Vehicle redCar = new Vehicle(RED_SYMBOL, new Position(2, 0), new Position(2, 1));
        Vehicle truck1 = new Vehicle('A', new Position(0, 2), new Position(2, 2));
        Vehicle truck2 = new Vehicle('B', new Position(1, 3), new Position(2, 3));
        
        vehicles.put(RED_SYMBOL, redCar);
        vehicles.put('A', truck1);
        vehicles.put('B', truck2);
        
        updateBoard();
    }


    private void updateBoard() {
        // Clear the board
        for (int i = 0; i < BOARD_DIM; i++) {
            for (int j = 0; j < BOARD_DIM; j++) {
                board[i][j] = EMPTY_SYMBOL;
            }
        }
        
        // place all vehicles on the board
        for (Vehicle vehicle : vehicles.values()) {
            Position back = vehicle.getBack();
            Position front = vehicle.getFront();
            
            if (vehicle.isHorizontal()) {
                for (int col = back.getCol(); col <= front.getCol(); col++) {
                    board[back.getRow()][col] = vehicle.getId();
                }
            } else {
                for (int row = back.getRow(); row <= front.getRow(); row++) {
                    board[row][back.getCol()] = vehicle.getId();
                }
            }
        }
    }

    public void moveVehicle(Move move) throws RushHourException {
        if (isGameOver()) {
            throw new RushHourException("No moves allowed on a completed game. Please reset the game.");
        }
        
        char vehicleId = move.getVehicleId();
        Direction direction = move.getDirection();
        
        // check if vehicle exists
        if (!vehicles.containsKey(vehicleId)) {
            throw new RushHourException("Invalid vehicle symbol: " + vehicleId);
        }
        
        Vehicle vehicle = vehicles.get(vehicleId);
        
        // check if direction is valid for the vehicle orientation
        if (vehicle.isHorizontal() && (direction == Direction.UP || direction == Direction.DOWN)) {
            throw new RushHourException("Cannot move horizontal vehicle " + vehicleId + " vertically");
        }
        if (!vehicle.isHorizontal() && (direction == Direction.LEFT || direction == Direction.RIGHT)) {
            throw new RushHourException("Cannot move vertical vehicle " + vehicleId + " horizontally");
        }
        
        // check if move would go off the board
        Position newBack = calculateNewPosition(vehicle.getBack(), direction);
        Position newFront = calculateNewPosition(vehicle.getFront(), direction);
        
        if (!isPositionValid(newBack) || !isPositionValid(newFront)) {
            throw new RushHourException("Cannot move vehicle " + vehicleId + " off the board");
        }
        
        // check if new positions are occupied
        if (direction == Direction.LEFT || direction == Direction.UP) {
            // Moving left or up - check the new back position
            if (board[newBack.getRow()][newBack.getCol()] != EMPTY_SYMBOL) {
                throw new RushHourException("Cannot move vehicle " + vehicleId + " - space is occupied");
            }
        } else {
            if (board[newFront.getRow()][newFront.getCol()] != EMPTY_SYMBOL) {
                throw new RushHourException("Cannot move vehicle " + vehicleId + " - space is occupied");
            }
        }

        vehicle.move(direction);
        moveCount++;
        updateBoard();
    }

    private Position calculateNewPosition(Position position, Direction direction) {
        return switch (direction) {
            case UP -> new Position(position.getRow() - 1, position.getCol());
            case DOWN -> new Position(position.getRow() + 1, position.getCol());
            case LEFT -> new Position(position.getRow(), position.getCol() - 1);
            case RIGHT -> new Position(position.getRow(), position.getCol() + 1);
            default -> position;
        };
    }

    private boolean isPositionValid(Position position) {
        return position.getRow() >= 0 && position.getRow() < BOARD_DIM &&
               position.getCol() >= 0 && position.getCol() < BOARD_DIM;
    }

    public boolean isGameOver() {
        Vehicle redCar = vehicles.get(RED_SYMBOL);
        return redCar.getFront().equals(EXIT_POS);
    }

    public Collection<Move> getPossibleMoves() {
        List<Move> possibleMoves = new ArrayList<>();
        
        for (Vehicle vehicle : vehicles.values()) {
            char vehicleId = vehicle.getId();
            
            for (Direction direction : Direction.values()) {
                try {
                    // temporary copy of the vehicle to test the move
                    Vehicle testVehicle = new Vehicle(vehicleId, 
                                                    new Position(vehicle.getBack().getRow(), vehicle.getBack().getCol()),
                                                    new Position(vehicle.getFront().getRow(), vehicle.getFront().getCol()));
                    
                    // test if the move is valid
                    testVehicle.move(direction);
                    
                    Position newBack = testVehicle.getBack();
                    Position newFront = testVehicle.getFront();
                    
                    if (isPositionValid(newBack) && isPositionValid(newFront)) {
                        // check if the new position is empty
                        if (direction == Direction.LEFT || direction == Direction.UP) {
                            if (board[newBack.getRow()][newBack.getCol()] == EMPTY_SYMBOL) {
                                possibleMoves.add(new Move(vehicleId, direction));
                            }
                        } else {
                            if (board[newFront.getRow()][newFront.getCol()] == EMPTY_SYMBOL) {
                                possibleMoves.add(new Move(vehicleId, direction));
                            }
                        }
                    }
                } catch (Exception e) {
                }
            }
        }
        
        return possibleMoves;
    }

    public int getMoveCount() {
        return moveCount;
    }

    public void reset() {
        loadInitialConfiguration(DEFAULT_FILENAME);
        moveCount = 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < BOARD_DIM; i++) {
            for (int j = 0; j < BOARD_DIM; j++) {
                sb.append(board[i][j]).append(" ");
            }
            sb.append("\n");
        }
        
        return sb.toString();
    }

    public static void main(String[] args) {
        //creating a new game
        RushHour game = new RushHour();
        
        // initial board
        System.out.println("Initial Board:");
        System.out.println(game);
        System.out.println("Move count: " + game.getMoveCount());
        System.out.println("Game over? " + game.isGameOver());
  
        System.out.println("\npossible moves:");
        Collection<Move> moves = game.getPossibleMoves();
        for (Move move : moves) {
            System.out.println("Move vehicle " + move.getVehicleId() + " " + move.getDirection());
        }
        
        try {
            // try moving vehicle 'A' down --this should fail
            System.out.println("\nAttempting to move A down:");
            game.moveVehicle(new Move('A', Direction.DOWN));
        } catch (RushHourException e) {
            System.out.println("Error: " + e.getMessage());
        }
        
        try {
            // move vehicle 'B' down
            System.out.println("\nMoving B down:");
            game.moveVehicle(new Move('B', Direction.DOWN));
            System.out.println(game);
            System.out.println("Move count: " + game.getMoveCount());
        } catch (RushHourException e) {
            System.out.println("Error: " + e.getMessage());
        }
        
        try {
            // move red vehicle 'R' right (toward exit)
            System.out.println("\nMoving R right:");
            game.moveVehicle(new Move('R', Direction.RIGHT));
            System.out.println(game);
            System.out.println("Move count: " + game.getMoveCount());
            System.out.println("Game over? " + game.isGameOver());
        } catch (RushHourException e) {
            System.out.println("Error: " + e.getMessage());
        }
        System.out.println("\nResetting game...");
        game.reset();
        System.out.println(game);
        System.out.println("Move count after reset: " + game.getMoveCount());
        
        try {
            System.out.println("\nplaying to win:");
            game.moveVehicle(new Move('R', Direction.RIGHT));
            game.moveVehicle(new Move('R', Direction.RIGHT));
            game.moveVehicle(new Move('R', Direction.RIGHT));
            game.moveVehicle(new Move('R', Direction.RIGHT));
            System.out.println(game);
            System.out.println("Game over? " + game.isGameOver());
            System.out.println("Final move count: " + game.getMoveCount());
            
            System.out.println("\nAttempting to move after game over:");
            game.moveVehicle(new Move('A', Direction.DOWN));
        } catch (RushHourException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
