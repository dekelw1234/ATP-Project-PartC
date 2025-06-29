package Model;

import java.io.*;

public class MyModel implements IModel {
    private final GameModel gameModel;

    public MyModel(GameModel gameModel) {
        this.gameModel = gameModel;
        System.out.println(" MyModel received GameModel: " + gameModel.hashCode());
    }

    @Override
    public void refresh() {
        gameModel.restartGame();
    }

    @Override
    public void save(File file) throws FileNotFoundException {
        byte[] game = gameModel.toByteArray();
        try (FileOutputStream out = new FileOutputStream(file)) {
            out.write(game);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save game", e);
        }
    }

    @Override
    public void load(File file) throws FileNotFoundException {
        byte[] game = new byte[(int) file.length()];
        try (FileInputStream in = new FileInputStream(file)) {
            if (in.read(game) != game.length) {
                throw new IOException("Incomplete game file read");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading game from file", e);
        }

        gameModel.fromByteArray(game);
    }

    @Override
    public String[] settings() {
        return gameModel.settings();
    }

    @Override
    public void exit() {
    }

    @Override
    public String help() {
        return gameModel.getHelpText();
    }

    @Override
    public void about() {
    }

    @Override
    public void showSolution() {
    }

    @Override
    public void hideSolution() {
        gameModel.clearSolution();
    }

    public GameModel getGameModel() {
        System.out.println(" MyModel.getGameModel() returns: " + gameModel.hashCode());
        return gameModel;
    }
}
