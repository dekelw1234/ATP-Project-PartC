package Model;

import IO.MyDecompressorInputStream;
import Server.Server;
import Server.ServerStrategyGenerateMaze;
import Server.ServerStrategySolveSearchProblem;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
import Client.Client;
import Client.IClientStrategy;

import java.io.*;
import java.net.InetAddress;
import java.util.Objects;
import java.util.Properties;

public class GameModel implements IGameModel {

    private Maze currentMaze;
    private Position playerPosition;
    private Solution solution;

    private static final Server mazeGeneratingServer = new Server(5400, 1000, new ServerStrategyGenerateMaze());
    private static final Server solveSearchProblemServer = new Server(5401, 1000, new ServerStrategySolveSearchProblem());
    public GameModel() {
        startServers();
    }

    public static void startServers() {
        mazeGeneratingServer.start();
        solveSearchProblemServer.start();
    }


    @Override
    public void generateMaze(int rows, int cols) {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5400, (in, out) -> {
                try {
                    ObjectOutputStream toServer = new ObjectOutputStream(out);
                    ObjectInputStream fromServer = new ObjectInputStream(in);
                    toServer.flush();

                    int[] dimensions = new int[]{rows, cols};
                    toServer.writeObject(dimensions);
                    toServer.flush();

                    byte[] compressedMaze = (byte[]) fromServer.readObject();
                    InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                    byte[] decompressed = new byte[rows * cols + 100]; // לגודל בטוח
                    is.read(decompressed);
                    currentMaze = new Maze(decompressed);
                    playerPosition = currentMaze.getStartPosition();
                    solution = null; // איפוס הפתרון הקודם
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            client.communicateWithServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void solveMaze() {
        if (currentMaze == null) return;

        try {
            Client client = new Client(InetAddress.getLocalHost(), 5401, (in, out) -> {
                try {
                    ObjectOutputStream toServer = new ObjectOutputStream(out);
                    ObjectInputStream fromServer = new ObjectInputStream(in);
                    toServer.flush();

                    toServer.writeObject(currentMaze);
                    toServer.flush();

                    solution = (Solution) fromServer.readObject();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            client.communicateWithServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void movePlayer(String direction) {
        if (playerPosition == null || currentMaze == null) return;

        int row = playerPosition.getRowIndex();
        int col = playerPosition.getColumnIndex();

        switch (direction.toUpperCase()) {
            case "UP" -> row--;
            case "DOWN" -> row++;
            case "LEFT" -> col--;
            case "RIGHT" -> col++;
        }

        if (isValidMove(row, col)) {
            playerPosition = new Position(row, col);
        }
    }

    private boolean isValidMove(int row, int col) {
        return row >= 0 && col >= 0 &&
                row < currentMaze.getMaze().length &&
                col < currentMaze.getMaze()[0].length &&
                currentMaze.getMaze()[row][col] == 0;
    }

    @Override
    public Maze getMaze() {
        return currentMaze;
    }

    @Override
    public Position getPlayerPosition() {
        return playerPosition;
    }

    @Override
    public Solution getSolution() {
        return solution;
    }

    public void loadMaze(byte[] data) {
        currentMaze = new Maze(data);
        playerPosition = currentMaze.getStartPosition();
    }
    public static void stopServers() {
     mazeGeneratingServer.stop();
     solveSearchProblemServer.stop();
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
