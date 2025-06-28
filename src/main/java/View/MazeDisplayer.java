package View;

import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.AState;
import algorithms.search.Solution;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class MazeDisplayer extends Canvas {
    private Maze maze;
    private Position playerPosition;
    private Position goalPosition;
    private Solution solution;

    public void setMaze(Maze maze) {
        this.maze = maze;
        this.goalPosition = maze.getGoalPosition(); // היעד מתוך המבוך
        redraw();
    }

    public void setPlayerPosition(Position pos) {
        this.playerPosition = pos;
        redraw();
    }

    public void setSolution(Solution solution) {
        this.solution = solution;
        redraw();
    }

    private void redraw() {
        if (maze == null || playerPosition == null) return;

        int[][] grid = maze.getMaze();
        double cellHeight = getHeight() / grid.length;
        double cellWidth = getWidth() / grid[0].length;

        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());

        // 🎨 רקע כללי
        try {
            Image bg = new Image(getClass().getResourceAsStream("/images/maze_background.jpg"));
            gc.drawImage(bg, 0, 0, getWidth(), getHeight());
        } catch (Exception e) {
            System.err.println("⚠️ לא נמצא maze_background.jpg");
        }

        // 🧱 קירות
        gc.setFill(Color.rgb(0, 0, 0, 0.4)); // שקוף חלקית
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == 1)
                    gc.fillRect(j * cellWidth, i * cellHeight, cellWidth, cellHeight);
            }
        }

        // 🟢 פתרון (אם קיים)
        if (solution != null) {
            gc.setFill(Color.LIGHTGREEN);
            for (AState state : solution.getSolutionPath()) {
                String[] parts = state.getStateView().split(",");
                int row = Integer.parseInt(parts[0].replaceAll("[^\\d]", ""));
                int col = Integer.parseInt(parts[1].replaceAll("[^\\d]", ""));
                gc.fillRect(col * cellWidth, row * cellHeight, cellWidth, cellHeight);
            }
        }

        // 🎯 יעד (כור)
        if (goalPosition != null) {
            try {
                Image goalImage = new Image(getClass().getResourceAsStream("/images/core.png"));
                gc.drawImage(goalImage,
                        goalPosition.getColumnIndex() * cellWidth,
                        goalPosition.getRowIndex() * cellHeight,
                        cellWidth, cellHeight);
            } catch (Exception e) {
                gc.setFill(Color.RED);
                gc.fillOval(goalPosition.getColumnIndex() * cellWidth,
                        goalPosition.getRowIndex() * cellHeight,
                        cellWidth, cellHeight);
            }
        }

        // ✈️ שחקן
        try {
            Image playerImg = new Image(getClass().getResourceAsStream("/images/plane.png"));
            gc.drawImage(playerImg,
                    playerPosition.getColumnIndex() * cellWidth,
                    playerPosition.getRowIndex() * cellHeight,
                    cellWidth, cellHeight);
        } catch (Exception e) {
            gc.setFill(Color.BLUE);
            gc.fillOval(playerPosition.getColumnIndex() * cellWidth,
                    playerPosition.getRowIndex() * cellHeight,
                    cellWidth, cellHeight);
        }
    }
}
