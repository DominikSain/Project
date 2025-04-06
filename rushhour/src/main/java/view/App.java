package view;
//** @author Marko Krznar */
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import model.*;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class App extends Application {
    private Label statusLabel; 
    private Label moveCountLabel; 
    private GridPane gameBoard; // 6x6 board
    private RushHour game;
    private Vehicle selectedVehicle; //  selected vehicle
    private final int EXIT_ROW = 2; // row where the exit is located
    private final int EXIT_COL = 5; // column where the exit is located

    @Override
    public void start(Stage stage) {
        // intializing the game logic
        game = new RushHour();

        // creating the main layout
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        root.setAlignment(Pos.CENTER);

        // status label
        statusLabel = new Label("Welcome to Rush Hour!");
        statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: blue;");

        // move count label
        moveCountLabel = new Label("Moves: 0");
        moveCountLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: green;");

        // game board
        gameBoard = new GridPane();
        gameBoard.setHgap(1);
        gameBoard.setVgap(1);
        gameBoard.setAlignment(Pos.CENTER);

        //  6x6 grid
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                Rectangle cell = new Rectangle(60, 60);
                cell.setFill(Color.LIGHTGRAY);
                cell.setStroke(Color.GRAY);
                gameBoard.add(cell, j, i);
            }
        }

        // control buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        Button upButton = new Button("Up");
        Button downButton = new Button("Down");
        Button leftButton = new Button("Left");
        Button rightButton = new Button("Right");

        // setting up button actions
        upButton.setOnAction(e -> moveVehicle(Direction.UP)); 
        downButton.setOnAction(e -> moveVehicle(Direction.DOWN));
        leftButton.setOnAction(e -> moveVehicle(Direction.LEFT));
        rightButton.setOnAction(e -> moveVehicle(Direction.RIGHT));

        buttonBox.getChildren().addAll(upButton, downButton, leftButton, rightButton);

        // add all components
        root.getChildren().addAll(statusLabel, moveCountLabel, gameBoard, buttonBox);

        // set up the scene
        Scene scene = new Scene(root, 400, 500);
        stage.setTitle("Rush Hour");
        stage.setScene(scene);
        stage.show();

        // display the initial game status
        displayGameStatus("New game started!");

        // intiialize the game and display vehicles
        initializeGame();
    }

    private void initializeGame() {
        // reset
        game.reset();

        // add vehicles 
        try {
            game.addVehicle(new Vehicle('X', new Position(EXIT_ROW, 0), new Position(EXIT_ROW, 1))); // Red car
            addRandomBlueCars(); // Add blue cars with random positions
        } catch (RushHourException e) {
            displayGameStatus("Error: " + e.getMessage());
        }

        // display vehicles and the exit block on the board
        updateBoard();
    }

    private void addRandomBlueCars() throws RushHourException {
        Random random = new Random();
    
        // First blue car (A): Must have at least one block in the same row as the red car and move vertically
        int firstCarCol;
        int firstCarRow;
        do {
            firstCarCol = random.nextInt(6); // Random column (0 to 5)
            firstCarRow = random.nextInt(5); // Random starting row (0 to 4) for vertical movement
        } while (firstCarCol == EXIT_COL || // Avoid the exit column
                 (firstCarRow == EXIT_ROW && (firstCarCol == 0 || firstCarCol == 1)) || // Avoid overlapping with the red car
                 (firstCarRow + 1 == EXIT_ROW && (firstCarCol == 0 || firstCarCol == 1))); // Avoid overlapping with the red car
    
        game.addVehicle(new Vehicle('A', new Position(firstCarRow, firstCarCol), new Position(firstCarRow + 1, firstCarCol)));
    
        // Second blue car (B): Cannot overlap with the red car or the first blue car
        int secondCarCol;
        int secondCarRow;
        do {
            secondCarCol = random.nextInt(6); // Random column (0 to 5)
            secondCarRow = random.nextInt(5); // Random starting row (0 to 4) for vertical movement
        } while (secondCarCol == EXIT_COL || // avoiding
                 secondCarCol == firstCarCol ||
                 (secondCarRow == EXIT_ROW && (secondCarCol == 0 || secondCarCol == 1)) || 
                 (secondCarRow + 1 == EXIT_ROW && (secondCarCol == 0 || secondCarCol == 1)) || 
                 (secondCarRow == firstCarRow && secondCarCol == firstCarCol) || 
                 (secondCarRow + 1 == firstCarRow && secondCarCol == firstCarCol)); 
    
        game.addVehicle(new Vehicle('B', new Position(secondCarRow, secondCarCol), new Position(secondCarRow + 1, secondCarCol)));
    }
    private void updateBoard() {
        // clear the board
        gameBoard.getChildren().clear();

        //  6x6 grid
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                Rectangle cell = new Rectangle(60, 60);
                cell.setFill(Color.LIGHTGRAY);
                cell.setStroke(Color.GRAY);
                gameBoard.add(cell, j, i);
            }
        }

        // Add the exit block
        Rectangle exitBlock = new Rectangle(60, 60);
        exitBlock.setFill(Color.DARKGRAY);
        exitBlock.setStroke(Color.BLACK);
        gameBoard.add(exitBlock, EXIT_COL, EXIT_ROW);

        // Add vehicles to the board
        List<Vehicle> vehicles = game.getVehicles();
        for (Vehicle vehicle : vehicles) {
            Color vehicleColor = vehicle.getId() == 'X' ? Color.RED : Color.BLUE; // Example: Red for 'X', Blue for others
            Position front = vehicle.getFront();
            Position back = vehicle.getBack();

            // Add rectangles for the vehicle's positions
            for (int row = back.getRow(); row <= front.getRow(); row++) {
                for (int col = back.getCol(); col <= front.getCol(); col++) {
                    Rectangle vehicleRect = new Rectangle(60, 60);
                    vehicleRect.setFill(vehicleColor);
                    vehicleRect.setStroke(Color.BLACK);
                    vehicleRect.setOnMouseClicked(e -> selectVehicle(vehicle)); // Select vehicle on click
                    gameBoard.add(vehicleRect, col, row);
                }
            }
        }
    }

    private void selectVehicle(Vehicle vehicle) {
        selectedVehicle = vehicle;
        displayGameStatus("Selected vehicle: " + vehicle.getId());
    }

    private void moveVehicle(Direction direction) {
        if (selectedVehicle == null) {
            displayGameStatus("No vehicle selected!");
            return;
        }

        try {
            game.moveVehicle(selectedVehicle.getId(), direction);
            updateBoard();
            moveCountLabel.setText("Moves: " + game.getMoveCount());

            // Check if the game is won
            if (game.isGameOver()) {
                showWinDialog();
                return;
            }

            displayGameStatus("Moved vehicle " + selectedVehicle.getId() + " " + direction);
        } catch (RushHourException e) {
            displayGameStatus("Invalid move: " + e.getMessage());
        }
    }

    private void showWinDialog() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Congratulations!");
        alert.setHeaderText("You won!");
        alert.setContentText("Would you like to restart or quit?"); // when the game is over, show this text

        ButtonType restartButton = new ButtonType("Restart"); // buttons for restart and quit
        ButtonType quitButton = new ButtonType("Quit");

        alert.getButtonTypes().setAll(restartButton, quitButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == restartButton) {
            initializeGame(); // restart
        } else {
            Platform.exit(); // quit
        }
    }

    private void displayGameStatus(String message) {
        // Update the status label
        statusLabel.setText(message);
    }

    public static void main(String[] args) {
        launch();
    }
}