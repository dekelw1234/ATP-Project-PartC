package ViewModel;

import Model.GameModel;
import Model.IGameModel;
import Model.IModel;
import Model.MyModel;
import View.menu.MyViewListener;
import algorithms.search.Solution;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyViewModel implements MyViewListener {

    private final Stage owner;
    private final IModel model;
    private Runnable returnToWelcomeCallback;
    private boolean solutionVisible = false;
    private final List<MyViewListener> listeners = new ArrayList<>();

    public MyViewModel(Stage owner, int rows, int cols) {
        this.owner = owner;
        this.model = new MyModel(new GameModel(rows, cols));
    }

    @Override
    public void onRefresh() {
        model.refresh();
    }

    @Override
    public void onSave() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Save Game");
        File file = fc.showSaveDialog(owner);
        if (file == null) return;

        try {
            model.save(file);
        } catch (IOException e) {
            System.out.println("Couldn't save the game");
        }
    }

    @Override
    public void onLoad() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Load Game");
        File file = fc.showOpenDialog(owner);
        if (file == null) return;

        try {
            model.load(file);
        } catch (IOException e) {
            System.out.println("Couldn't load the game");
        }
    }

    @Override
    public void onSettings() {
        String[] cfg = model.settings();
        String content = String.join("\n", cfg);

        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, content, ButtonType.OK);
            alert.setTitle("Application Settings");
            alert.setHeaderText("Current configuration");
            alert.showAndWait();
        });
    }

    @Override
    public void onExit() {
        if (returnToWelcomeCallback != null) {
            returnToWelcomeCallback.run();
        }
    }

    public void setReturnToWelcomeCallback(Runnable r) {
        this.returnToWelcomeCallback = r;
    }

    @Override
    public void onHelp() {
        String helpText = model.help();

        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, helpText, ButtonType.OK);
            alert.setTitle("Help");
            alert.setHeaderText("How to Play");
            alert.getDialogPane().setMinWidth(600);
            alert.showAndWait();
        });
    }

    @Override
    public void onAbout() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION,
                    "All rights reserved to Shay Avraham and Dekel Winkler.",
                    ButtonType.OK
            );
            alert.setTitle("About");
            alert.setHeaderText("About this application");
            alert.showAndWait();
        });
    }

    @Override
    public void onShowSolution() {
        solutionVisible = !solutionVisible;

        IGameModel gameModel = getModel();
        Solution solution = solutionVisible ? gameModel.getSolution() : null;

        listeners.forEach(l -> l.onSolutionToggled(solution, solutionVisible));
    }

    public void addListener(MyViewListener listener) {
        listeners.add(listener);
    }


    public IGameModel getModel() {
        if (model instanceof MyModel) {
            MyModel myModel = (MyModel) model;
            return myModel.getGameModel();
        }
        throw new IllegalStateException("Model is not of type MyModel");
    }
    @Override
    public void onSolutionToggled(Solution solution, boolean visible) {
        // אפשר להשאיר ריק אם MyViewModel לא צריך להגיב לפעולה הזו
    }


}
