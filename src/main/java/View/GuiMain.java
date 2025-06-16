package View;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;
import Model.MyModel;
import ViewModel.MyViewModel;

public class GuiMain extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MyView.fxml"));
        Parent root = loader.load();

        MyModel model = new MyModel();
        MyViewModel viewModel = new MyViewModel(model);

        MyViewController controller = loader.getController();
        controller.setViewModel(viewModel);

        primaryStage.setTitle("Maze Game");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
