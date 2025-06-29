package View;

import ViewModel.GameViewModel;
import View.menu.MyViewListener;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyEvent;

public class GameController {

    @FXML
    private MazeDisplayer mazeDisplayer;

    private GameViewModel viewModel;
    private MyViewListener menuListener; // נוספה תמיכה בלחצני הטולבר

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

    public void setMenuListener(MyViewListener listener) {
        this.menuListener = listener;
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

        event.consume();
        updateDisplay();
        mazeDisplayer.requestFocus();

        // בדיקת ניצחון
        Position player = viewModel.getPlayerPosition();
        Position goal = viewModel.getMaze().getGoalPosition();
        if (player.equals(goal)) {
            showVictoryMessage();
        }
    }

    private void showVictoryMessage() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("ניצחון!");
        alert.setHeaderText("🎉 השמדת את הכור באיראן!");
        alert.setContentText("ועזרת לשמור על מדינת ישראל \nמה ברצונך לעשות?");

        ButtonType saveBtn = new ButtonType(" שמור מבוך");
        ButtonType exitBtn = new ButtonType(" חזור");
        ButtonType cancelBtn = new ButtonType(" בטל");

        alert.getButtonTypes().setAll(saveBtn, exitBtn, cancelBtn);

        alert.showAndWait().ifPresent(response -> {
            if (response == saveBtn && menuListener != null) {
                menuListener.onSave();
            } else if (response == exitBtn && menuListener != null) {
                menuListener.onExit();
            }
        });
    }

    public MazeDisplayer getMazeDisplayer() {
        return mazeDisplayer;
    }
}
