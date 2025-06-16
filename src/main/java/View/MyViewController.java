package View;

import ViewModel.MyViewModel;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.AState;
import algorithms.search.MazeState;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.*;
import java.util.List;

public class MyViewController implements IView {
    private MyViewModel viewModel;

    @FXML private TextField rowsInput;
    @FXML private TextField colsInput;

    @FXML
    private Pane mazeDisplay;

    @FXML
    private Label statusLabel;

    private Canvas canvas;

    @FXML
    public void initialize() {
        this.canvas = new Canvas();
        mazeDisplay.getChildren().add(canvas);
        canvas.widthProperty().bind(mazeDisplay.widthProperty());
        canvas.heightProperty().bind(mazeDisplay.heightProperty());
    }

    public void setViewModel(MyViewModel vm) {
        this.viewModel = vm;
    }

    @FXML
    public void onGenerateMaze(ActionEvent event) {
        viewModel.generateMaze();
        displayMaze(viewModel.getMaze());
        statusLabel.setText("מבוך נוצר בהצלחה!");
    }

    @FXML
    public void onSolveMaze(ActionEvent event) {
        viewModel.solveMaze();
        displayMaze(viewModel.getMaze());
        drawSolution(viewModel.getSolutionPath());
        statusLabel.setText("פתרון מוצג על גבי המבוך");
    }

    @FXML
    public void onSaveMaze(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Maze");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Maze files", "*.maze"));
        File file = fileChooser.showSaveDialog(getStage(event));
        if (file != null) {
            try (FileOutputStream fos = new FileOutputStream(file)) {
                byte[] mazeData = viewModel.getMazeObject().toByteArray();
                fos.write(mazeData);
                statusLabel.setText("מבוך נשמר בהצלחה");
            } catch (IOException e) {
                statusLabel.setText("שגיאה בשמירת המבוך");
            }
        }
    }

    @FXML
    public void onLoadMaze(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Maze");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Maze files", "*.maze"));
        File file = fileChooser.showOpenDialog(getStage(event));
        if (file != null) {
            try (FileInputStream fis = new FileInputStream(file);
                 ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {

                byte[] temp = new byte[1024];
                int nRead;
                while ((nRead = fis.read(temp, 0, temp.length)) != -1) {
                    buffer.write(temp, 0, nRead);
                }

                byte[] data = buffer.toByteArray();
                Maze loadedMaze = new Maze(data);
                viewModel.setMaze(loadedMaze);
                displayMaze(loadedMaze.getMaze());
                statusLabel.setText("מבוך נטען בהצלחה");

            } catch (IOException e) {
                statusLabel.setText("שגיאה בטעינת המבוך");
            }
        }
    }

    @FXML
    public void onExit(ActionEvent event) {
        getStage(event).close();
    }

    private Stage getStage(ActionEvent event) {
        return (Stage) ((Node) event.getSource()).getScene().getWindow();
    }

    @Override
    public void showAlert(String message) {
        // TODO: show alert to user
    }

    @Override
    public void displayMaze(int[][] maze) {
        if (maze == null || maze.length == 0) return;

        GraphicsContext gc = canvas.getGraphicsContext2D();
        double cellHeight = canvas.getHeight() / maze.length;
        double cellWidth = canvas.getWidth() / maze[0].length;

        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // רקע המבוך
        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[i].length; j++) {
                gc.setFill(maze[i][j] == 1 ? Color.BLACK : Color.WHITE);
                gc.fillRect(j * cellWidth, i * cellHeight, cellWidth, cellHeight);
            }
        }

        // ציור הכור
        try {
            Image coreImage = new Image(getClass().getResourceAsStream("/images/core.png"));
            int goalRow = viewModel.getGoalPosition()[0];
            int goalCol = viewModel.getGoalPosition()[1];
            gc.drawImage(coreImage, goalCol * cellWidth, goalRow * cellHeight, cellWidth, cellHeight);
        } catch (Exception e) {
            System.err.println("❌ לא נמצא core.png");
        }

        // ציור המטוס
        try {
            Position pos = viewModel.getCurrentPosition();
            if (pos != null) {
                Image planeImage = new Image(getClass().getResourceAsStream("/images/plane.png"));
                gc.drawImage(planeImage,
                        pos.getColumnIndex() * cellWidth,
                        pos.getRowIndex() * cellHeight,
                        cellWidth, cellHeight);
            }
        } catch (Exception e) {
            System.err.println("❌ לא נמצא plane.png – מצייר עיגול במקום:");
            Position pos = viewModel.getCurrentPosition();
            if (pos != null) {
                gc.setFill(Color.DEEPSKYBLUE);
                gc.fillOval(pos.getColumnIndex() * cellWidth,
                        pos.getRowIndex() * cellHeight,
                        cellWidth, cellHeight);
            }
        }
    }

    private void drawSolution(List<AState> path) {
        if (path == null || path.isEmpty()) return;

        GraphicsContext gc = canvas.getGraphicsContext2D();
        int[][] maze = viewModel.getMaze();
        double cellHeight = canvas.getHeight() / maze.length;
        double cellWidth = canvas.getWidth() / maze[0].length;

        gc.setFill(Color.LIGHTGREEN);
        for (AState state : path) {
            if (state instanceof MazeState) {
                String[] coords = ((MazeState) state).getStateView().split(",");
                int row = Integer.parseInt(coords[0]);
                int col = Integer.parseInt(coords[1]);
                gc.fillRect(col * cellWidth, row * cellHeight, cellWidth, cellHeight);
            }
        }
    }

    @Override
    public void onMazeSolved() {
        displayMaze(viewModel.getMaze());
        drawSolution(viewModel.getSolutionPath());
        statusLabel.setText("הפתרון הוצג בהצלחה");
    }

    @FXML
    public void onGenerateMazeCustom(ActionEvent event) {
        try {
            int rows = Integer.parseInt(rowsInput.getText());
            int cols = Integer.parseInt(colsInput.getText());

            viewModel.setRows(rows);
            viewModel.setCols(cols);
            viewModel.generateMaze();

            displayMaze(viewModel.getMaze());
            statusLabel.setText("מבוך נוצר בהצלחה!");

        } catch (NumberFormatException e) {
            statusLabel.setText("שגיאה: יש להזין מספרים תקינים לשורות ועמודות.");
        }
    }
}
