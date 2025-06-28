package ViewModel;

import View.menu.MyViewListener;

public class MyViewModel implements MyViewListener {

    @Override
    public void onRefresh() {
        // טיפול בלחיצה על רענון
    }
    @Override
    public void onSave() {
        // טיפול בשמירה
    }

    @Override public void onLoad() {}
    @Override public void onSettings() {}
    @Override public void onExit() {}
    @Override public void onHelp() {}
    @Override public void onAbout() {}
    @Override public void onShowSolution() {}
}
