import Server.Server;
import View.WelcomeController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class GuiMain extends Application {

    private Server mazeGeneratingServer;
    private Server solveSearchProblemServer;

    @Override
    public void start(Stage stage) throws Exception {

        //טוענים את ה־welcomePage.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/welcomePage.fxml") );
        Parent root = loader.load();

        // מאתחלים את ה־Controller (אם צריך להעביר Stage)
        WelcomeController welcomeCtrl = loader.getController();
        welcomeCtrl.setPrimaryStage(stage);  // אם כתבת שיטה כזו

        // מצמידים את ה־CSS
        Scene scene = new Scene(root, 700, 500);
        scene.getStylesheets().add(getClass().getResource("/myStyle.css").toExternalForm());

        // מציגים
        stage.setScene(scene);
        stage.setTitle("Air Force Maze");
        stage.show();
    }

    public static void main(String[] args) {

        launch(args);
    }
}
