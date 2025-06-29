import View.WelcomeController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GuiMain extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // הפעלת מוזיקת רקע
        View.BackgroundMusic.init();

        // טענת מסך פתיחה
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/welcomePage.fxml"));
        Parent root = loader.load();

        // חיבור הקונטרולר לבמה
        WelcomeController welcomeCtrl = loader.getController();
        welcomeCtrl.setPrimaryStage(stage);

        // בניית סצנה והצגת חלון
        Scene scene = new Scene(root, 700, 500);
        scene.getStylesheets().add(getClass().getResource("/myStyle.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Air Force Maze");
        stage.setResizable(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
