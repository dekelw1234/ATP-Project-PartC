import Server.Server;
import View.menu.MyViewController;
import ViewModel.MyViewModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GuiMain extends Application {

    private Server mazeGeneratingServer;
    private Server solveSearchProblemServer;

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/menu.fxml"));
        Parent root = loader.load();

        // מחזיק את ה־Controller
        MyViewController controller = loader.getController();

        // יוצרים את ה־ViewModel (שעושה בפועל את הלוגיקה) ורושמים אותו
        MyViewModel viewModel = new MyViewModel(stage);
        controller.addListener(viewModel);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/myStyle.css").toExternalForm());

        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args) {

        launch(args);
    }
}
