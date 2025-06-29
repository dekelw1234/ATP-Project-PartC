package ViewModel;

import Model.IGameModel;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class GameViewModel {
    private final IGameModel model;
    private final Stage owner;
    private boolean showSolution = false;

    public GameViewModel(IGameModel model, Stage owner) {
        this.model = model;
        this.owner = owner;
    }

    public void generateMaze(int rows, int cols) {
        showSolution = false;
        model.generateMaze(rows, cols);
    }

    public void solveMaze() {
        model.solveMaze();
        showSolution = true;
    }

    public void movePlayer(String direction) {
        model.movePlayer(direction);
    }

    public void loadMaze() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Load Maze");
        File file = fc.showOpenDialog(owner);
        if (file == null) return;

        try (FileInputStream in = new FileInputStream(file)) {
            byte[] data = in.readAllBytes();
            model.loadMaze(data);
            showSolution = false;
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
        return showSolution ? model.getSolution() : null;
    }

    public void loadMazeFromBytes(byte[] data) {
        model.loadMaze(data);
        showSolution = false;
    }

    public boolean isSolutionVisible() {
        return showSolution;
    }

    public void hideSolution() {
        showSolution = false;
    }
}
