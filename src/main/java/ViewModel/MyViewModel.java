// === ViewModel/MyViewModel.java ===
package ViewModel;

import Model.IModel;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.AState;

import javafx.beans.property.*;
import java.util.List;

public class MyViewModel {
    private IModel model;

    private IntegerProperty rows = new SimpleIntegerProperty(10);
    private IntegerProperty cols = new SimpleIntegerProperty(10);

    public MyViewModel(IModel model) {
        this.model = model;
    }

    public void generateMaze() {
        model.generateMaze(getRows(), getCols());
    }

    public void generateMaze(int rows, int cols) {
        setRows(rows);
        setCols(cols);
        model.generateMaze(rows, cols);
    }


    public void solveMaze() {
        model.solveMaze();
    }


    public int[][] getMaze() {
        return model.getMaze();
    }

    public List<AState> getSolutionPath() {
        return model.getSolutionPath();
    }

    public Maze getMazeObject() {
        return model.getMazeObject();
    }

    public void setMaze(Maze maze) {
        model.setMaze(maze);
    }

    public int getRows() {
        return rows.get();
    }

    public void setRows(int value) {
        rows.set(value);
    }

    public IntegerProperty rowsProperty() {
        return rows;
    }

    public int getCols() {
        return cols.get();
    }

    public void setCols(int value) {
        cols.set(value);
    }

    public IntegerProperty colsProperty() {
        return cols;
    }

    public void moveCharacter(String direction) {
        model.moveCharacter(direction);
    }

    public Position getCurrentPosition() {
        return model.getCurrentPosition();
    }

    public int[] getGoalPosition() {
        return model.getGoalPosition();
    }





}
