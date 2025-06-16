// === Model/MyModel.java ===
package Model;

import View.IView;
import algorithms.mazeGenerators.*;
import algorithms.search.*;

import java.util.List;

public class MyModel implements IModel {
    private Maze currentMaze;
    private Position startPosition;
    private Position goalPosition;
    private List<AState> solutionPath;
    private IView view;
    private Position currentPosition;




    @Override
    public void generateMaze(int rows, int cols) {
        IMazeGenerator generator = new MyMazeGenerator();
        currentMaze = generator.generate(rows, cols);
        startPosition = currentMaze.getStartPosition();
        goalPosition = currentMaze.getGoalPosition();
        currentPosition = currentMaze.getStartPosition();
        System.out.println("Maze generated with size: " + rows + "x" + cols);
    }

    @Override
    public void solveMaze() {
        if (currentMaze == null) return;
        ISearchingAlgorithm searcher = new BestFirstSearch();
        ISearchable searchableMaze = new SearchableMaze(currentMaze);
        Solution solution = searcher.solve(searchableMaze);
        solutionPath = solution.getSolutionPath();
        if (view != null) {
            view.onMazeSolved();
        }
    }


    @Override
    public void setCharacterPosition(int row, int col) {
        // Optional: set custom starting point
    }

    @Override
    public int[][] getMaze() {
        return currentMaze != null ? currentMaze.getMaze() : new int[0][0];
    }

    @Override
    public int[] getStartPosition() {
        return new int[]{startPosition.getRowIndex(), startPosition.getColumnIndex()};
    }

    @Override
    public int[] getGoalPosition() {
        return new int[]{goalPosition.getRowIndex(), goalPosition.getColumnIndex()};
    }

    @Override
    public List<AState> getSolutionPath() {
        return solutionPath;
    }
    @Override
    public Maze getMazeObject() {
        return currentMaze;
    }

    @Override
    public void setMaze(Maze maze) {
        this.currentMaze = maze;
        this.startPosition = maze.getStartPosition();
        this.goalPosition = maze.getGoalPosition();
    }

    public void setView(IView view) {
        this.view = view;
    }

    public void moveCharacter(String direction) {
        if (currentPosition == null || currentMaze == null) return;

        int row = currentPosition.getRowIndex();
        int col = currentPosition.getColumnIndex();

        switch (direction) {
            case "UP": row--; break;
            case "DOWN": row++; break;
            case "LEFT": col--; break;
            case "RIGHT": col++; break;
            case "UP_LEFT": row--; col--; break;
            case "UP_RIGHT": row--; col++; break;
            case "DOWN_LEFT": row++; col--; break;
            case "DOWN_RIGHT": row++; col++; break;
        }


        if (isValidPosition(row, col)) {
            currentPosition = new Position(row, col);
            if (view != null) view.displayMaze(currentMaze.getMaze());
        }
    }
    private boolean isValidPosition(int row, int col) {
        return row >= 0 && col >= 0 &&
                row < currentMaze.getMaze().length &&
                col < currentMaze.getMaze()[0].length &&
                currentMaze.getMaze()[row][col] == 0;
    }

    @Override
    public Position getCurrentPosition() {
        return currentPosition;
    }






}