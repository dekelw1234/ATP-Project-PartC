package View;

import ViewModel.GameViewModel;
import View.menu.MyViewListener;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;

public class GameController implements MyViewListener {

    @FXML
    private MazeDisplayer mazeDisplayer;

    private MediaPlayer bgPlayer;
    private GameViewModel viewModel;
    private MyViewListener menuListener;

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

        if (viewModel.getPlayerPosition().equals(viewModel.getMaze().getGoalPosition())) {
            showVictoryMessage();
        }
    }

    private void showVictoryMessage() {
        BackgroundMusic.stop();
        try {
            URL mediaUrl = getClass().getResource("/music/victory.m4a");
            if (mediaUrl != null) {
                Media bg = new Media(mediaUrl.toExternalForm());
                bgPlayer = new MediaPlayer(bg);
                bgPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                bgPlayer.play();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Victory!");
        alert.setHeaderText("You destroyed the nuclear bomb of Iran!!");
        alert.setContentText("You saved the State of Israel, thank you! \nWhat do you want to do now?");

        ButtonType saveBtn = new ButtonType("Save this maze");
        ButtonType exitBtn = new ButtonType("Back");
        ButtonType cancelBtn = new ButtonType("Cancel");

        alert.getButtonTypes().setAll(saveBtn, exitBtn, cancelBtn);

        alert.showAndWait().ifPresent(response -> {
            if (bgPlayer != null) {
                bgPlayer.stop();
            }
            BackgroundMusic.play();

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

    @Override
    public void onSolutionToggled(Solution solution, boolean visible) {
        viewModel.setSolutionVisible(visible);
        mazeDisplayer.setSolution(solution);
        mazeDisplayer.redraw();
    }

    @Override public void onRefresh() {}
    @Override public void onSave() {}
    @Override public void onLoad() {}
    @Override public void onSettings() {}
    @Override public void onExit() {}
    @Override public void onHelp() {}
    @Override public void onAbout() {}
    @Override public void onShowSolution() {}
}
