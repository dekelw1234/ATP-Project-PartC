package View;

import View.menu.MyViewController;
import ViewModel.MyViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;

public class WelcomeController {

    @FXML private TextField rowsField;
    @FXML private TextField colsField;
    private Stage primaryStage;

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    @FXML
    public void onStart(ActionEvent event) throws Exception {

        // קבלת ערכי שורות ועמודות מהמשתמש
        int rows = Integer.parseInt(rowsField.getText());
        int cols = Integer.parseInt(colsField.getText());

        //  טען את ה־menu.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/menu.fxml"));
        BorderPane menuRoot = loader.load();

        // טוענים את ה־MazeCreationView ומכניסים למרכז:
        FXMLLoader gameLoader = new FXMLLoader(
                getClass().getResource("/MazeCreationView.fxml")
        );
        Parent gameView = gameLoader.load();
        menuRoot.setCenter(gameView);

        // ייבוא ה־Stage ממנו נגיע
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();

        // קבע לו את הסצנה החדשה
        Scene menuScene = new Scene(menuRoot);
        menuScene.getStylesheets().add(getClass().getResource("/myStyle.css").toExternalForm());
        stage.setScene(menuScene);

        // התחברות בין Controller ל־ViewModel עם הגדרות המשחק
        MyViewController menuController = loader.getController();

        // תוכל להעביר rows/cols ל־GameModel בתוך ה־ViewModel
        MyViewModel vm = new MyViewModel(stage, rows, cols);
        menuController.addListener(vm);
    }
}
