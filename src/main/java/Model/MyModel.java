package Model;
import java.io.*;


public class MyModel implements IModel {

    private final GameModel gameModel;

    public MyModel(GameModel gameModel) {
        this.gameModel = gameModel;
    }

    @Override
    public void refresh() {
        gameModel.restartGame(); //שיטה שמחזירה את הדמות לנקודת ההתחלה
    }

    @Override
    public void save(File file) throws FileNotFoundException {

        byte[] game = gameModel.toByteArray();
        try (FileOutputStream out = new FileOutputStream(file)) {
            out.write(game);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void load(File file) throws FileNotFoundException {

        byte[] game = new byte[(int)file.length()];
        try (FileInputStream in = new FileInputStream(file)) {
            int read = in.read(game);
            if (read != game.length) {
                throw new IOException("Failed to read");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading game from file", e);
        }

        gameModel.fromByteArray(game); //יציג את זה בפריים של המבוך
    }

    @Override
    public String[] settings() {
        return gameModel.settings(); //מחזיר קובץ קונפיגורציה שרלוונטי למשחק שנטען
    }

    @Override
    public void exit() {
        System.out.println("Model: exit()");
    }

    @Override
    public String help() {
        return gameModel.getHelpText();
    }

    @Override
    public void about() {
        System.out.println("Model: about()");
    }

    @Override
    public void showSolution() {
        //Generate the solution path inside the GameModel
        gameModel.solve();
    }

    @Override
    public void hideSolution() {
        gameModel.clearSolution();  // remove path overlay
    }


    public GameModel getGameModel() {
        return gameModel;
    }

}
