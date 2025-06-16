package Model;


import algorithms.mazeGenerators.Maze;
import algorithms.search.AState;

import java.util.List;

public interface IModel {
    void generateMaze(int rows, int cols);
    void solveMaze();
    void moveCharacter(String direction);
    void setCharacterPosition(int row, int col);
    int[][] getMaze();
    int[] getStartPosition();
    int[] getGoalPosition();
    List<AState> getSolutionPath();
    Maze getMazeObject();
    void setMaze(Maze maze);


}