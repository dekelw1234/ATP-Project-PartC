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

    public void fromByteArray(byte[] data) {
        System.out.println("hiii");

        //todo
    }

    public void restartGame() {
        //todo

    }

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
}
