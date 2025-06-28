package View;

import View.menu.MyViewController;
import ViewModel.GameViewModel;
import ViewModel.MyViewModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.Parent;

public class GameAndMenuController {

    @FXML
    private BorderPane root;

    public void init(GameViewModel gameVM, MyViewModel menuVM, int rows, int cols, byte[] mazeData) {
        try {
            // טען את התפריט
            FXMLLoader menuLoader = new FXMLLoader(getClass().getResource("/menu.fxml"));
            Parent menu = menuLoader.load();
            MyViewController menuController = menuLoader.getController();
            menuController.addListener(menuVM);

            // טען את המשחק
            FXMLLoader gameLoader = new FXMLLoader(getClass().getResource("/MazeCreationView.fxml"));
            Parent game = gameLoader.load();
            GameController gameController = gameLoader.getController();
            gameController.setViewModel(gameVM, rows, cols, mazeData == null);

            if (mazeData != null) {
                gameVM.loadMazeFromBytes(mazeData);
            } else {
                gameVM.generateMaze(rows, cols);
            }

            // הוסף למסך
            root.setTop(menu);
            root.setCenter(game);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
