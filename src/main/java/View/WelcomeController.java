package View;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import Model.MyModel;
import ViewModel.MyViewModel;

public class WelcomeController {

    @FXML private TextField rowsInput;
    @FXML private TextField colsInput;
    @FXML private Label errorLabel;

    @FXML
    public void onStartClicked(ActionEvent event) {
        try {
            int rows = Integer.parseInt(rowsInput.getText());
            int cols = Integer.parseInt(colsInput.getText());

            if (rows < 10 || cols < 10) {
                errorLabel.setText("שגיאה: יש להזין לפחות 10 שורות ועמודות.");
                return;
            }

            // טען את המסך הראשי של המשחק
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MyView.fxml"));
            Pane root = loader.load();

            // צור את המודל והקשרים
            MyModel model = new MyModel();
            MyViewModel viewModel = new MyViewModel(model);
            MyViewController controller = loader.getController();

            model.setView(controller);
            controller.setViewModel(viewModel);

            // צור את המבוך
            viewModel.generateMaze(rows, cols);
            controller.displayMaze(viewModel.getMaze());
            controller.requestFocusOnCanvas();

            // הצג את הסצנה
            Stage stage = (Stage)((Button)event.getSource()).getScene().getWindow();
            Scene gameScene = new Scene(root, 800, 600);

            // קיצורי מקשים לתנועה
            gameScene.setOnKeyPressed(e -> {
                switch (e.getCode()) {
                    case UP, NUMPAD8 -> viewModel.moveCharacter("UP");
                    case DOWN, NUMPAD2 -> viewModel.moveCharacter("DOWN");
                    case LEFT, NUMPAD4 -> viewModel.moveCharacter("LEFT");
                    case RIGHT, NUMPAD6 -> viewModel.moveCharacter("RIGHT");
                    case NUMPAD7 -> viewModel.moveCharacter("UP_LEFT");
                    case NUMPAD9 -> viewModel.moveCharacter("UP_RIGHT");
                    case NUMPAD1 -> viewModel.moveCharacter("DOWN_LEFT");
                    case NUMPAD3 -> viewModel.moveCharacter("DOWN_RIGHT");
                }
            });

            stage.setScene(gameScene);

        } catch (NumberFormatException e) {
            errorLabel.setText("שגיאה: יש להזין מספרים תקינים בלבד.");
        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("שגיאה פנימית בטעינת המשחק.");
        }
    }
}
