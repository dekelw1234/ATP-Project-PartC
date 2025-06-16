// === Model/MyModel.java ===
package Model;

import algorithms.mazeGenerators.*;
import algorithms.search.*;

import java.util.List;

public class MyModel implements IModel {
    private Maze currentMaze;
    private Position startPosition;
    private Position goalPosition;
    private List<AState> solutionPath;


    @Override
    public void generateMaze(int rows, int cols) {
        IMazeGenerator generator = new MyMazeGenerator();
        currentMaze = generator.generate(rows, cols);
        startPosition = currentMaze.getStartPosition();
        goalPosition = currentMaze.getGoalPosition();
        System.out.println("Maze generated with size: " + rows + "x" + cols);
    }

    @Override
    public void solveMaze() {
        if (currentMaze == null) return;
        ISearchingAlgorithm searcher = new BestFirstSearch();
        ISearchable searchableMaze = new SearchableMaze(currentMaze);
        Solution solution = searcher.solve(searchableMaze);
        solutionPath = solution.getSolutionPath();
    }

    @Override
    public void moveCharacter(String direction) {
        // TODO: update position based on direction
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

}