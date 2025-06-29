package View;

import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.AState;
import algorithms.search.Solution;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.InputStream;

public class MazeDisplayer extends Canvas {
    private Maze maze;
    private Position playerPosition;
    private Position goalPosition;
    private Solution solution;

    public MazeDisplayer() {
        this.setFocusTraversable(true);
        this.setOnMouseClicked(e -> this.requestFocus());
    }

    public void setMaze(Maze maze) {
        this.maze = maze;
        this.goalPosition = maze.getGoalPosition();
        redraw();
    }

    public void setPlayerPosition(Position position) {
        this.playerPosition = position;
        redraw();
    }

    public void setSolution(Solution solution) {
        System.out.println(">>> setSolution called. Solution is " +
                (solution == null ? "null" : "size: " + solution.getSolutionPath().size()));
        this.solution = solution;
        redraw();
    }


    public void redraw() {
        if (maze == null || playerPosition == null) return;

        int[][] grid = maze.getMaze();
        double cellHeight = getHeight() / grid.length;
        double cellWidth = getWidth() / grid[0].length;

        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());

        // רקע
        try (InputStream bgStream = getClass().getResourceAsStream("/images/maze_background.jpg")) {
            if (bgStream != null) {
                Image bg = new Image(bgStream);
                gc.drawImage(bg, 0, 0, getWidth(), getHeight());
            } else {
                gc.setFill(Color.DARKSLATEGRAY);
                gc.fillRect(0, 0, getWidth(), getHeight());
            }
        } catch (Exception e) {
            gc.setFill(Color.DARKGRAY);
            gc.fillRect(0, 0, getWidth(), getHeight());
        }

        // קירות – אפור שקוף
        gc.setFill(Color.rgb(60, 60, 60, 0.8));
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == 1)
                    gc.fillRect(j * cellWidth, i * cellHeight, cellWidth, cellHeight);
            }
        }

        // פתרון
        if (solution != null) {
            gc.setFill(Color.rgb(144, 238, 144, 0.6)); // ירוק בהיר שקוף
            for (AState state : solution.getSolutionPath()) {
                String[] parts = state.getStateView().split(",");
                int row = Integer.parseInt(parts[0].replaceAll("[^\\d]", ""));
                int col = Integer.parseInt(parts[1].replaceAll("[^\\d]", ""));
                gc.fillRect(col * cellWidth, row * cellHeight, cellWidth, cellHeight);
            }
        }

        // שחקן
        try {
            InputStream playerStream = getClass().getResourceAsStream("/images/plane.png");
            if (playerStream != null) {
                Image playerImg = new Image(playerStream);
                gc.drawImage(playerImg,
                        playerPosition.getColumnIndex() * cellWidth,
                        playerPosition.getRowIndex() * cellHeight,
                        cellWidth, cellHeight);
            } else {
                gc.setFill(Color.BLUE);
                gc.fillOval(playerPosition.getColumnIndex() * cellWidth,
                        playerPosition.getRowIndex() * cellHeight,
                        cellWidth, cellHeight);
            }
        } catch (Exception e) {
            gc.setFill(Color.BLUE);
            gc.fillOval(playerPosition.getColumnIndex() * cellWidth,
                    playerPosition.getRowIndex() * cellHeight,
                    cellWidth, cellHeight);
        }

        // יעד
        if (goalPosition != null) {
            try {
                InputStream goalStream = getClass().getResourceAsStream("/images/core.png");
                if (goalStream != null) {
                    Image goalImg = new Image(goalStream);
                    gc.drawImage(goalImg,
                            goalPosition.getColumnIndex() * cellWidth,
                            goalPosition.getRowIndex() * cellHeight,
                            cellWidth, cellHeight);
                } else {
                    gc.setFill(Color.RED);
                    gc.fillOval(goalPosition.getColumnIndex() * cellWidth,
                            goalPosition.getRowIndex() * cellHeight,
                            cellWidth, cellHeight);
                }
            } catch (Exception e) {
                gc.setFill(Color.RED);
                gc.fillOval(goalPosition.getColumnIndex() * cellWidth,
                        goalPosition.getRowIndex() * cellHeight,
                        cellWidth, cellHeight);
            }
        }
    }
}
