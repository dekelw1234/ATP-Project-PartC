package View;

import ViewModel.GameViewModel;
import ViewModel.MyViewModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import Model.GameModel;

public class Main extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/StartScreen.fxml"));
            Parent root = loader.load();

            // קישור לבקר
            StartScreenController controller = loader.getController();
            controller.setPrimaryStage(primaryStage);
            controller.setMainApp(this); // 👈 חשוב מאוד!

            // יצירת סצנה
            Scene scene = new Scene(root, 400, 300);
            scene.getStylesheets().add(getClass().getResource("/myStyle.css").toExternalForm());

            primaryStage.setTitle("🌀 התחלת משחק מבוך");
            primaryStage.setScene(scene);
            primaryStage.show();

            // 📌 סגירת שרתים ויציאה
            primaryStage.setOnCloseRequest(e -> {
                System.out.println("📴 סוגר את השרתים והתהליכים...");
                GameModel.stopServers();
                System.exit(0);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * קריאה מתוך StartScreenController לצורך מעבר למסך המשחק
     */
    public void loadGameView(int rows, int cols, byte[] mazeData) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GameAndMenu.fxml"));
            Parent root = loader.load();

            GameAndMenuController controller = loader.getController();

            GameViewModel gameVM = new GameViewModel(new GameModel(), primaryStage);
            MyViewModel menuVM = new MyViewModel(primaryStage);

            controller.init(gameVM, menuVM, rows, cols, mazeData);

            Scene gameScene = new Scene(root, 1000, 700);
            gameScene.getStylesheets().add(getClass().getResource("/myStyle.css").toExternalForm());

            primaryStage.setScene(gameScene);
            primaryStage.setTitle("🧩 משחק מבוך גרעיני");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
