package Model;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class GameModel implements IGameModel {
    public GameModel(int rows, int cols) {
    }

    @Override
    public byte[] toByteArray() {
        return new byte[0]; //todo
    }

    @Override
    public void fromByteArray(byte[] data) {
        System.out.println("hiii");

        //todo
    }

    @Override
    public void restartGame() {
        //todo

    }

    @Override
    public String[] settings() {
        Properties props = new Properties();
        try (InputStream in = getClass().getResourceAsStream("/config.properties")) {
            if (in == null) {
                throw new RuntimeException("config.properties not found in resources");
            }
            props.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }

        String threadPoolSize          = props.getProperty("threadPoolSize", "");
        String mazeGeneratingAlgorithm = props.getProperty("mazeGeneratingAlgorithm", "");
        String mazeSearchingAlgorithm  = props.getProperty("mazeSearchingAlgorithm", "");

        return new String[]{
                threadPoolSize,
                mazeGeneratingAlgorithm,
                mazeSearchingAlgorithm
        };
    }
    /**
     * מחזיר מחרוזת עם הוראות השימוש במשחק
     */
    public String getHelpText() {
        return """
    • Goal: From the starting point, find the exit at the edge of the maze.
    • Allowed moves: You may move only up, down, left, or right.
    • Controls:
        – Use the arrow keys (← ↑ ↓ →) to move your character.
    • Menu buttons:
        – Refresh (↺): Generate a new maze with the same dimensions.
        – Save (💾): Save the current maze state to a file.
        – Load (📂): Load a previously saved maze.
        – Settings (⚙): Show the current configuration (threadPoolSize, algorithms).
        – Help (❓): Display this help text.
        – About (ℹ): Show version info and copyright.
        – Exit (⇦): Close the application.
    • Tip:
        – Plan your route ahead of time to minimize unnecessary turns.
    Good luck! 🏹
""";

    }

    @Override
    public void solve() {
        System.out.println("show it");
    }

    @Override
    public void clearSolution() {
        System.out.println("remove it");
    }
}
