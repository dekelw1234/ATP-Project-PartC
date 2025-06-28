package View;

import View.menu.MyViewController;
import ViewModel.GameViewModel;
import ViewModel.MyViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

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

        // טען את ה־menu.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/menu.fxml"));
        BorderPane menuRoot = loader.load();

        // טען את MazeCreationView.fxml והכנס אותו למרכז התצוגה
        FXMLLoader gameLoader = new FXMLLoader(getClass().getResource("/MazeCreationView.fxml"));
        Parent gameView = gameLoader.load();
        menuRoot.setCenter(gameView);

        // הצמדת סצנה חדשה ל־Stage
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene menuScene = new Scene(menuRoot);
        menuScene.getStylesheets().add(getClass().getResource("/myStyle.css").toExternalForm());
        stage.setScene(menuScene);

        // יצירת ViewModel מרכזי
        MyViewModel vm = new MyViewModel(stage, rows, cols);
        MyViewController menuController = loader.getController();
        menuController.addListener(vm);

        // חיבור בין GameController ל־GameViewModel
        GameController gameController = gameLoader.getController();
        GameViewModel gameViewModel = new GameViewModel(vm.getModel(), stage);
        gameController.setViewModel(gameViewModel, rows, cols, true);
    }
}
