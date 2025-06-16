package View;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GuiMain extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Welcome.fxml"));
        Parent root = loader.load();

        Scene welcomeScene = new Scene(root, 400, 250);
        primaryStage.setTitle("ברוך הבא למשימת השמדת הכור!");
        primaryStage.setScene(welcomeScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
