package ViewModel;

import Model.IGameModel;
import Model.GameModel;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class GameViewModel {
    private final IGameModel model;
    private final Stage owner;
    private Canvas canvas;

    public GameViewModel(IGameModel model, Stage owner) {
        this.model = model;
        this.owner = owner;
    }

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }

    public void generateMaze(int rows, int cols) {
        model.generateMaze(rows, cols);
        draw();
    }

    public void solveMaze() {
        model.solveMaze();
        draw();
    }

    public void movePlayer(String direction) {
        model.movePlayer(direction);
        draw();
    }

    public void draw() {
        if (canvas == null || model.getMaze() == null) return;

        GraphicsContext gc = canvas.getGraphicsContext2D();
        Maze maze = model.getMaze();
        int[][] grid = maze.getMaze();
        double cellHeight = canvas.getHeight() / grid.length;
        double cellWidth = canvas.getWidth() / grid[0].length;

        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // ציור קירות
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == 1) {
                    gc.setFill(Color.BLACK);
                    gc.fillRect(j * cellWidth, i * cellHeight, cellWidth, cellHeight);
                }
            }
        }

        // ציור פתרון
        Solution sol = model.getSolution();
        if (sol != null) {
            gc.setFill(Color.LIGHTGREEN);
            sol.getSolutionPath().forEach(state -> {
                String[] coords = state.getStateView().split(",");
                int row = Integer.parseInt(coords[0].replaceAll("[^\\d]", ""));
                int col = Integer.parseInt(coords[1].replaceAll("[^\\d]", ""));
                gc.fillRect(col * cellWidth, row * cellHeight, cellWidth, cellHeight);
            });
        }

        // ציור שחקן
        Position player = model.getPlayerPosition();
        if (player != null) {
            try {
                Image planeImage = new Image(getClass().getResourceAsStream("/images/plane.png"));
                gc.drawImage(planeImage,
                        player.getColumnIndex() * cellWidth,
                        player.getRowIndex() * cellHeight,
                        cellWidth, cellHeight);
            } catch (Exception e) {
                gc.setFill(Color.BLUE);
                gc.fillOval(
                        player.getColumnIndex() * cellWidth,
                        player.getRowIndex() * cellHeight,
                        cellWidth, cellHeight);
            }
        }
    }

    public void loadMaze() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Load Maze");
        File file = fc.showOpenDialog(owner);
        if (file == null) return;

        try (FileInputStream in = new FileInputStream(file)) {
            byte[] data = in.readAllBytes();
            model.loadMaze(data);
            draw();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveMaze() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Save Maze");
        File file = fc.showSaveDialog(owner);
        if (file == null) return;

        try (FileOutputStream out = new FileOutputStream(file)) {
            Maze maze = model.getMaze();
            if (maze != null) {
                out.write(maze.toByteArray());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Maze getMaze() {
        return model.getMaze();
    }

    public Position getPlayerPosition() {
        return model.getPlayerPosition();
    }

    public Solution getSolution() {
        return model.getSolution();
    }
}
