package View;

import Server.Server;
import Server.ServerStrategyGenerateMaze;
import Server.ServerStrategySolveSearchProblem;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GuiMain extends Application {

    private Server mazeGeneratingServer;
    private Server solveSearchProblemServer;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // ⬅️ הפעלת השרתים
        mazeGeneratingServer = new Server(5400, 1000, new ServerStrategyGenerateMaze());
        solveSearchProblemServer = new Server(5401, 1000, new ServerStrategySolveSearchProblem());
        mazeGeneratingServer.start();
        solveSearchProblemServer.start();

        // ⬅️ טעינת מסך פתיחה
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Welcome.fxml"));
        Parent root = loader.load();
        Scene welcomeScene = new Scene(root, 400, 250);

        primaryStage.setTitle("ברוך הבא למשימת השמדת הכור!");
        primaryStage.setScene(welcomeScene);
        primaryStage.show();

        // הפסקת השרתים כשסוגרים את התוכנית
        primaryStage.setOnCloseRequest(e -> {
            mazeGeneratingServer.stop();
            solveSearchProblemServer.stop();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
