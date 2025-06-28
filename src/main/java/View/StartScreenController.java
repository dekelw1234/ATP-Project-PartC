package View;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import View.Main;

public class StartScreenController {

    @FXML private TextField rowsField;
    @FXML private TextField colsField;
    @FXML private Label errorLabel;
    private Main mainApp;

    private Stage primaryStage;

    private byte[] loadedMazeData = null;

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    @FXML
    public void onStartGame() {
        try {
            int rows = Integer.parseInt(rowsField.getText());
            int cols = Integer.parseInt(colsField.getText());

            if (rows < 5 || cols < 5) {
                errorLabel.setText("⚠️ המידות צריכות להיות לפחות 5x5");
                return;
            }

            mainApp.loadGameView(rows, cols, loadedMazeData);
        } catch (NumberFormatException e) {
            errorLabel.setText("⚠️ יש להזין מספרים חוקיים");
        } catch (Exception e) {
            errorLabel.setText("שגיאה בטעינה: " + e.getMessage());
        }
    }

    @FXML
    public void onLoadMaze() {
        FileChooser fc = new FileChooser();
        fc.setTitle("בחר קובץ מבוך");
        File file = fc.showOpenDialog(primaryStage);
        if (file != null) {
            try (FileInputStream in = new FileInputStream(file)) {
                loadedMazeData = in.readAllBytes();
                errorLabel.setText("✅ מבוך נטען בהצלחה");
            } catch (IOException e) {
                errorLabel.setText("⚠️ שגיאה בקריאת הקובץ");
            }
        }
    }

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }
}
