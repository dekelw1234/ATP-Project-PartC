package View.menu;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.util.ArrayList;
import java.util.List;

public class MyViewController {

    // רשימת המאזינים
    private final List<MyViewListener> listeners = new ArrayList<>();

    // מאפשר ל־ViewModel (או כל מאזין אחר) להירשם
    public void addListener(MyViewListener listener) {
        listeners.add(listener);
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

