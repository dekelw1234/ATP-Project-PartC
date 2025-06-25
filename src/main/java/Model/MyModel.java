// === Model/MyModel.java ===
package Model;
import Client.Client;
import Client.IClientStrategy;

import IO.MyDecompressorInputStream;
import View.*;
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
    private Position currentPosition;



    /**
     * Generates a new maze of the specified size by requesting it from the maze generation server.
     * Also starts solving the maze in a background thread after generation.
     */
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

        // מבקש פתרון מיד אחרי יצירת המבוך
        new Thread(() -> solveMazeSilently()).start();

    }

    /**
     * Solves the current maze by sending it to the maze-solving server and retrieving the solution path.
     */
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

                        /*
                        if (view != null) {
                            view.onMazeSolved(); // אפשרות לדווח ל-GUI
                        }

                         */



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

    /**
     * Sets a custom starting position for the maze character (optional usage).
     */
    @Override
    public void setCharacterPosition(int row, int col) {
        // Optional: set custom starting point
    }

    /**
     * Returns the current maze layout as a 2D integer array.
     */
    @Override
    public int[][] getMaze() {
        return currentMaze != null ? currentMaze.getMaze() : new int[0][0];
    }


    /**
     * Retrieves the start position coordinates of the current maze.
     */
    @Override
    public int[] getStartPosition() {
        return new int[]{startPosition.getRowIndex(), startPosition.getColumnIndex()};
    }

    /**
     * Retrieves the goal position coordinates of the current maze.
     */
    @Override
    public int[] getGoalPosition() {
        return new int[]{goalPosition.getRowIndex(), goalPosition.getColumnIndex()};
    }

    /**
     * Returns the list of states representing the solution path for the maze.
     */
    @Override
    public List<AState> getSolutionPath() {
        return solutionPath;
    }

    /**
     * Returns the Maze object of the current maze.
     */
    @Override
    public Maze getMazeObject() {
        return currentMaze;
    }

    /**
     * Sets the current maze and updates start and goal positions accordingly.
     */
    @Override
    public void setMaze(Maze maze) {
        this.currentMaze = maze;
        this.startPosition = maze.getStartPosition();
        this.goalPosition = maze.getGoalPosition();
    }

    /**
     * Assigns the view component that will receive updates from this model.
     */
    public void setView(IView view) {
        //this.view = view;
    }

    /**
     * Moves the character in the specified direction if the target cell is valid, then updates the view display.
     */
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
            //if (view != null) view.displayMaze(currentMaze.getMaze());
        }
    }

    /**
     * Checks whether the specified row and column are within bounds and not a wall in the maze.
     */
    private boolean isValidPosition(int row, int col) {
        return row >= 0 && col >= 0 &&
                row < currentMaze.getMaze().length &&
                col < currentMaze.getMaze()[0].length &&
                currentMaze.getMaze()[row][col] == 0;
    }

    /**
     *
     * @return current position at the maze.
     */
    @Override
    public Position getCurrentPosition() {
        return currentPosition;
    }


    /**
     * Internally solves the maze in a background thread without notifying the view component.
     */
    private void solveMazeSilently() {
        if (currentMaze == null) return;

        try {
            Client client = new Client(InetAddress.getLocalHost(), 5401, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);

                        toServer.flush();
                        toServer.writeObject(currentMaze);
                        toServer.flush();

                        Solution solution = (Solution) fromServer.readObject();
                        solutionPath = solution.getSolutionPath();

                        System.out.println("✔ פתרון ברקע נטען: " + solutionPath.size() + " צעדים");

                    } catch (Exception e) {
                        System.err.println("❌ שגיאה בקבלת פתרון מהשרת (רקע)");
                        e.printStackTrace();
                    }
                }
            });

            client.communicateWithServer();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }








}