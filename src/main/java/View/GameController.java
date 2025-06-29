package View;

import ViewModel.GameViewModel;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.input.KeyEvent;



    public class GameController {

        @FXML
        private MazeDisplayer mazeDisplayer;

        private GameViewModel viewModel;

        @FXML
        public void initialize() {
            Platform.runLater(() -> mazeDisplayer.requestFocus());
        }

        public void setViewModel(GameViewModel vm, int rows, int cols, boolean generateNew) {
            this.viewModel = vm;
            if (generateNew) {
                viewModel.generateMaze(rows, cols);
            }
            updateDisplay();
        }

        private void updateDisplay() {
            mazeDisplayer.setMaze(viewModel.getMaze());
            mazeDisplayer.setPlayerPosition(viewModel.getPlayerPosition());
            mazeDisplayer.setSolution(viewModel.isSolutionVisible() ? viewModel.getSolution() : null);
            mazeDisplayer.redraw();
        }

        @FXML
        public void handleKeyPressed(KeyEvent event) {
            switch (event.getCode()) {
                case UP, NUMPAD8 -> viewModel.movePlayer("UP");
                case DOWN, NUMPAD2 -> viewModel.movePlayer("DOWN");
                case LEFT, NUMPAD4 -> viewModel.movePlayer("LEFT");
                case RIGHT, NUMPAD6 -> viewModel.movePlayer("RIGHT");
                default -> { return; }
            }
            updateDisplay();
        }


}
