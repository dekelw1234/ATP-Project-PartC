package Model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Properties;

public interface IModel {

    void refresh();
    void save(File file) throws FileNotFoundException;
    void load(File file) throws FileNotFoundException;
    String[] settings();
    void exit();
    void help();
    void about();
    void showSolution();
}
