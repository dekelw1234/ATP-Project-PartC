package ViewModel;

import Model.GameModel;
import Model.IModel;
import Model.MyModel;
import View.menu.MyViewListener;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class MyViewModel implements MyViewListener {

    private final Stage owner;
    private final IModel model;

    public MyViewModel(Stage owner) {
        this.model = new MyModel(new GameModel());
        this.owner=owner;
    }

    @Override
    public void onRefresh() {
        model.refresh();
    }

    @Override
    public void onSave() {

        //זה כאן כי זה מתעסק בUI של שמירה לקובץ
        FileChooser fc = new FileChooser(); //האפשרות לפתוח את בחירת הקבצים
        fc.setTitle("Save Game"); //הכותרת של החלוןש יפתח
        File file = fc.showSaveDialog(owner); //מחזיר את הנתיב שנבחר אם נבחר
        if (file == null) return;

        try {
            model.save(file); //כתיבת המשחק לקובץ - לוגיקה
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
        model.settings();
    }

    @Override
    public void onExit() {
        model.exit();
    }

    @Override
    public void onHelp() {
        model.help();
    }

    @Override
    public void onAbout() {
        model.about();
    }

    @Override
    public void onShowSolution() {
        model.showSolution();
    }
}
