package View.menu;

import ViewModel.MyViewModel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyViewController {

    @FXML
    private Button exitBtn;

    // רשימת המאזינים
    private final List<MyViewListener> listeners = new ArrayList<>();

    // מאפשר ל־ViewModel (או כל מאזין אחר) להירשם
    public void addListener(MyViewListener listener) {
        listeners.add(listener);
        // ברגע שמוסיפים את ה־ViewModel, גם מגדירים לו איך "לצאת חזרה"
        ((MyViewModel)listener).setReturnToWelcomeCallback(() -> {
            // הקפצת ה-UI thread על מנת לעשות swap של הסצנה
            Platform.runLater(this::showWelcome);
        });
    }

    private void showWelcome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/welcomePage.fxml"));
            Parent welcomeRoot = loader.load();
            // מצמידים קונטרולר + viewmodel חדש (שיתן לו שוב את ה־Stage)
            View.WelcomeController wc = loader.getController();
            wc.setPrimaryStage((Stage) exitBtn.getScene().getWindow());
            Scene scene = new Scene(welcomeRoot);
            scene.getStylesheets().add(getClass().getResource("/myStyle.css").toExternalForm());
            Stage stage = (Stage) exitBtn.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void removeListener(MyViewListener listener) {
        listeners.remove(listener);
    }


    @FXML
    public void onRefresh(ActionEvent event) {
        listeners.forEach(MyViewListener::onRefresh);
    }

    @FXML
    public void onSave(ActionEvent event) {
        listeners.forEach(MyViewListener::onSave);
    }

    @FXML
    public void onLoad(ActionEvent event) {
        listeners.forEach(MyViewListener::onLoad);
    }

    @FXML
    public void onSettings(ActionEvent event) {

        listeners.forEach(MyViewListener::onSettings);
    }

    @FXML
    public void onExit(ActionEvent event) {
        listeners.forEach(MyViewListener::onExit);
    }

    @FXML
    public void onHelp(ActionEvent event) {
        listeners.forEach(MyViewListener::onHelp);
    }

    @FXML
    public void onAbout(ActionEvent event) {
        listeners.forEach(MyViewListener::onAbout);
    }

    @FXML
    public void onShowSolution(ActionEvent event) {
        listeners.forEach(MyViewListener::onShowSolution);
    }
}

