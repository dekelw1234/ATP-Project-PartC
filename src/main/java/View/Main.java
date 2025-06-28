package View;

import Model.GameModel;
import ViewModel.GameViewModel;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MazeCreationView.fxml"));
            Parent root = loader.load();

            GameModel.startServers(); // הפעלת השרתים

            GameModel model = new GameModel();
            GameViewModel viewModel = new GameViewModel(model, primaryStage);
            GameController controller = loader.getController();
            controller.setViewModel(viewModel);

            Scene scene = new Scene(root, 800, 700);
            primaryStage.setScene(scene);
            primaryStage.setTitle("מבוך גרעיני");

            // 🛑 עצירת שרתים עם סגירת החלון
            primaryStage.setOnCloseRequest(event -> {
                System.out.println("🔚 סוגרים את האפליקציה...");
                GameModel.stopServers(); // ודא שקיימת המתודה
                Platform.exit();
                System.exit(0);
            });

            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
