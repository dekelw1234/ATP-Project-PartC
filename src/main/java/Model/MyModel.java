// === Model/MyModel.java ===
package Model;
import Client.Client;
import Client.IClientStrategy;

import IO.MyDecompressorInputStream;
import View.IView;
import algorithms.mazeGenerators.*;
import algorithms.search.*;

import java.io.*;
import java.net.InetAddress;
import java.util.List;

public class MyModel implements IModel {
    private Maze currentMaze;
    private Position startPosition;
    private Position goalPosition;
    private List<AState> solutionPath;
    private IView view;
    private Position currentPosition;




    @Override
    public void generateMaze(int rows, int cols) {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5400, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);

                        // שליחת ממדי המבוך לשרת
                        toServer.flush();
                        int[] mazeDimensions = new int[]{rows, cols};
                        toServer.writeObject(mazeDimensions);
                        toServer.flush();

                        // קבלת המבוך הדחוס
                        byte[] compressedMaze = (byte[]) fromServer.readObject();

                        // פענוח המבוך
                        InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                        byte[] decompressed = new byte[rows * cols + 100]; // חשוב: מספיק גודל

                        is.read(decompressed);
                        currentMaze = new Maze(decompressed);
                        System.out.println("Maze content:");
                        currentMaze.print();


                        // עדכון מצב
                        startPosition = currentMaze.getStartPosition();
                        goalPosition = currentMaze.getGoalPosition();
                        currentPosition = currentMaze.getStartPosition();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            client.communicateWithServer();
            System.out.println("Maze generated from server: " + rows + "x" + cols);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void solveMaze() {
        if (currentMaze == null)
            return;

        try {
            Client client = new Client(InetAddress.getLocalHost(), 5401, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);

                        // שליחת המבוך לשרת
                        toServer.flush();
                        toServer.writeObject(currentMaze);
                        toServer.flush();

                        // קבלת פתרון
                        Solution solution = (Solution) fromServer.readObject();
                        solutionPath = solution.getSolutionPath();

                        if (view != null) {
                            view.onMazeSolved(); // אפשרות לדווח ל-GUI
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            client.communicateWithServer();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    public void setCharacterPosition(int row, int col) {
        // Optional: set custom starting point
    }

    @Override
    public int[][] getMaze() {
        return currentMaze != null ? currentMaze.getMaze() : new int[0][0];
    }

    @Override
    public int[] getStartPosition() {
        return new int[]{startPosition.getRowIndex(), startPosition.getColumnIndex()};
    }

    @Override
    public int[] getGoalPosition() {
        return new int[]{goalPosition.getRowIndex(), goalPosition.getColumnIndex()};
    }

    @Override
    public List<AState> getSolutionPath() {
        return solutionPath;
    }
    @Override
    public Maze getMazeObject() {
        return currentMaze;
    }

    @Override
    public void setMaze(Maze maze) {
        this.currentMaze = maze;
        this.startPosition = maze.getStartPosition();
        this.goalPosition = maze.getGoalPosition();
    }

    public void setView(IView view) {
        this.view = view;
    }

    public void moveCharacter(String direction) {
        if (currentPosition == null || currentMaze == null) return;

        int row = currentPosition.getRowIndex();
        int col = currentPosition.getColumnIndex();

        switch (direction) {
            case "UP": row--; break;
            case "DOWN": row++; break;
            case "LEFT": col--; break;
            case "RIGHT": col++; break;
            case "UP_LEFT": row--; col--; break;
            case "UP_RIGHT": row--; col++; break;
            case "DOWN_LEFT": row++; col--; break;
            case "DOWN_RIGHT": row++; col++; break;
        }


        if (isValidPosition(row, col)) {
            currentPosition = new Position(row, col);
            if (view != null) view.displayMaze(currentMaze.getMaze());
        }
    }
    private boolean isValidPosition(int row, int col) {
        return row >= 0 && col >= 0 &&
                row < currentMaze.getMaze().length &&
                col < currentMaze.getMaze()[0].length &&
                currentMaze.getMaze()[row][col] == 0;
    }

    @Override
    public Position getCurrentPosition() {
        return currentPosition;
    }






}