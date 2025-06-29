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
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;

public class GameController {

    @FXML
    private MazeDisplayer mazeDisplayer;
    private MediaPlayer bgPlayer;
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

        //מוזיקת ניצחון
        View.BackgroundMusic.stop(); //עצירת מוזיקת רקע
        try {

            URL mediaUrl = getClass().getResource("/music/victory.m4a");
            if (mediaUrl == null) {
                System.err.println("ERROR: cannot find /music/victory.m4a on classpath");
                return;
            }
            System.out.println(">> Found music at: " + mediaUrl);
            Media bg = new Media(mediaUrl.toExternalForm());
            bgPlayer = new MediaPlayer(bg);
            bgPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            bgPlayer.play();
            System.out.println("victory music started");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Victory!");
        alert.setHeaderText("🎉 You destroyed the nuclear reactor in Iran!!");
        alert.setContentText("You saved the State of Israel, thank you! \n What do you want to do now?");

        ButtonType saveBtn = new ButtonType("Sava this maze");
        ButtonType exitBtn = new ButtonType("Back");
        ButtonType cancelBtn = new ButtonType("Cancel");

        alert.getButtonTypes().setAll(saveBtn, exitBtn, cancelBtn);

        alert.showAndWait().ifPresent(response -> {
            // בכל מקרה – ברגע שהדיאלוג נסגר, נעצור את מוזיקת הניצחון:
            if (bgPlayer != null) {
                bgPlayer.stop();
            }
            // ונפעיל מחדש את מוזיקת הרקע:
            BackgroundMusic.play();

            // עכשיו בהתאם לכפתור:
            if (response == saveBtn && menuListener != null) {
                menuListener.onSave();
            } else if (response == exitBtn && menuListener != null) {
                menuListener.onExit();
            }
            // אם response == cancelBtn – לא עושים פעולה נוספת
        });
    }

    public MazeDisplayer getMazeDisplayer() {
        return mazeDisplayer;
    }
}
