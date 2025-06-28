package View;

import ViewModel.GameViewModel;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;

public class GameController {

    @FXML private MazeDisplayer mazeDisplayer;
    @FXML private Label statusLabel;

    private GameViewModel viewModel;

    public void setViewModel(GameViewModel vm, int rows, int cols, boolean generateNew) {
        this.viewModel = vm;
        mazeDisplayer.setFocusTraversable(true);
        mazeDisplayer.setOnKeyPressed(this::handleKeyPressed);
        viewModel.setCanvas(mazeDisplayer);

        if (generateNew) {
            viewModel.generateMaze(rows, cols);
        } else {
            viewModel.draw(); // אם טען קובץ
        }
    }


    private void handleKeyPressed(KeyEvent event) {
        switch (event.getCode()) {
            case UP, NUMPAD8 -> viewModel.movePlayer("UP");
            case DOWN, NUMPAD2 -> viewModel.movePlayer("DOWN");
            case LEFT, NUMPAD4 -> viewModel.movePlayer("LEFT");
            case RIGHT, NUMPAD6 -> viewModel.movePlayer("RIGHT");
        }
        updateDisplay();
    }

    private void updateDisplay() {
        Maze maze = viewModel.getMaze();
        Position pos = viewModel.getPlayerPosition();
        Solution sol = viewModel.getSolution();

        mazeDisplayer.setMaze(maze);
        mazeDisplayer.setPlayerPosition(pos);
        mazeDisplayer.setSolution(sol);
    }

    // כפתורים עתידיים
    @FXML
    public void onSaveMaze() {
        viewModel.saveMaze();
    }

    @FXML
    public void onLoadMaze() {
        viewModel.loadMaze();
        updateDisplay();
    }
}
