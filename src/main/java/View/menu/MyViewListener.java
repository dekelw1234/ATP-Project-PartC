package View.menu;

import algorithms.search.Solution;

public interface MyViewListener {
    void onRefresh();
    void onSave();
    void onLoad();
    void onSettings();
    void onExit();
    void onHelp();
    void onAbout();
    void onShowSolution();
    void onSolutionToggled(Solution solution, boolean visible);

}

