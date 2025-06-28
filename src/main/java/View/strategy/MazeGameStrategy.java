package View.strategy;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

public class MazeGameStrategy implements GameStrategy {

    @Override
    public Parent createGamePane() {
        // כאן נטען את ה-FXML של המשחק מבוך
        try {
            return FXMLLoader.load(getClass().getResource("/fxml/MazeGame.fxml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}