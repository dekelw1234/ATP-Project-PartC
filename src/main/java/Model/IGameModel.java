package Model;

import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;

public interface IGameModel {
    void generateMaze(int rows, int cols);
    void solveMaze();
    void movePlayer(String direction);
    Maze getMaze();
    Position getPlayerPosition();
    Solution getSolution();
    void loadMaze(byte[] data);
}