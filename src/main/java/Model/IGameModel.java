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
    byte[] toByteArray();

    void fromByteArray(byte[] data);

    void restartGame();

    String[] settings();

    String getHelpText();

    void solve();

    void clearSolution();
}
