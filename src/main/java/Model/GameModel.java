package Model;

import Client.Client;
import IO.MyDecompressorInputStream;
import Server.Server;
import Server.ServerStrategyGenerateMaze;
import Server.ServerStrategySolveSearchProblem;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
import javafx.application.Platform;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Properties;

public class GameModel implements IGameModel {

    private Maze currentMaze;
    private Position playerPosition;
    private Solution solution;

    private int rows;
    private int cols;
    private int mazeServerPort;
    private int solverServerPort;

    private static boolean serversStarted = false;

    public GameModel(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        loadPortsFromConfig();
        startServersIfAvailable();
    }

    private void loadPortsFromConfig() {
        Properties props = new Properties();
        try (InputStream in = GameModel.class.getResourceAsStream("/config.properties")) {
            if (in == null) {
                throw new RuntimeException("config.properties not found in resources");
            }
            props.load(in);
            mazeServerPort = Integer.parseInt(props.getProperty("mazeServerPort", "5400"));
            solverServerPort = Integer.parseInt(props.getProperty("solverServerPort", "5401"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    private void startServersIfAvailable() {
        if (serversStarted) return;

        try {
            new ServerSocket(mazeServerPort).close();
            new ServerSocket(solverServerPort).close();
        } catch (IOException e) {
            serversStarted = true;
            return;
        }

        Server mazeServer = new Server(mazeServerPort, 1000, new ServerStrategyGenerateMaze());
        Server solverServer = new Server(solverServerPort, 1000, new ServerStrategySolveSearchProblem());

        mazeServer.start();
        solverServer.start();
        serversStarted = true;
    }

    @Override
    public void generateMaze(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        try {
            Client client = new Client(InetAddress.getLocalHost(), mazeServerPort, (in, out) -> {
                try {
                    ObjectOutputStream toServer = new ObjectOutputStream(out);
                    ObjectInputStream fromServer = new ObjectInputStream(in);
                    toServer.flush();

                    toServer.writeObject(new int[]{rows, cols});
                    toServer.flush();

                    byte[] compressedMaze = (byte[]) fromServer.readObject();
                    InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                    byte[] decompressed = new byte[rows * cols + 100];
                    is.read(decompressed);
                    currentMaze = new Maze(decompressed);
                    playerPosition = currentMaze.getStartPosition();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            client.communicateWithServer();
        } catch (Exception e) {
            e.printStackTrace();
        }

        new Thread(() -> {
            solveMaze();
            if (solution != null) {
                Platform.runLater(() -> {});
            }
        }).start();
    }

    @Override
    public void solveMaze() {
        if (currentMaze == null) return;

        try {
            Client client = new Client(InetAddress.getLocalHost(), solverServerPort, (in, out) -> {
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
            case "UP_LEFT" -> { row--; col--; }
            case "UP_RIGHT" -> { row--; col++; }
            case "DOWN_LEFT" -> { row++; col--; }
            case "DOWN_RIGHT" -> { row++; col++; }
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

    @Override
    public byte[] toByteArray() {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(currentMaze);
            return bos.toByteArray();
        } catch (IOException e) {
            return new byte[0];
        }
    }

    @Override
    public void fromByteArray(byte[] data) {
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(data))) {
            currentMaze = (Maze) in.readObject();
            playerPosition = currentMaze.getStartPosition();
            solveMaze();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void restartGame() {
        playerPosition = currentMaze != null ? currentMaze.getStartPosition() : null;
        solution = null;
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

        return new String[]{
                props.getProperty("threadPoolSize", ""),
                props.getProperty("mazeGeneratingAlgorithm", ""),
                props.getProperty("mazeSearchingAlgorithm", "")
        };
    }

    @Override
    public void clearSolution() {
        // intentionally left blank
    }

    public String getHelpText() {
        return """
                • Goal: From the starting point, find the exit at the edge of the maze.
                • Allowed moves: You may move only up, down, left, or right.
                • Controls:
                    – Use the arrow keys (← ↑ ↓ →) to move your character.
                • Menu buttons:
                    – Refresh: Generate a new maze with the same dimensions.
                    – Save: Save the current maze state to a file.
                    – Load: Load a previously saved maze.
                    – Settings: Show the current configuration.
                    – Help: Display this help text.
                    – About: Show version info.
                    – Exit: Close the application.
                """;
    }
}
