package View;

import ViewModel.MyViewModel;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.AState;
import algorithms.search.MazeState;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Node;
import java.io.*;
import java.util.List;
import javafx.scene.media.AudioClip;


public class MyViewController implements IView {
    private MyViewModel viewModel;

    @FXML private TextField rowsInput;
    @FXML private TextField colsInput;

    @FXML
    private Pane mazeDisplay;


    private AudioClip backgroundMusic;
    @FXML
    private Label statusLabel;

    private Canvas canvas;
    private boolean showingSolution = false;
    private boolean isSolutionVisible = false;


    @FXML
    public void initialize() {
        this.canvas = new Canvas();
        mazeDisplay.getChildren().add(canvas);
        canvas.widthProperty().bind(mazeDisplay.widthProperty());
        canvas.heightProperty().bind(mazeDisplay.heightProperty());

        // הפיכת הקנבס לפוקוסבילי
        canvas.setFocusTraversable(true);
        canvas.requestFocus();
        startBackgroundMusic();
        // האזנה ללחיצות מקשים
        canvas.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case NUMPAD8 -> viewModel.moveCharacter("UP");
                case NUMPAD2 -> viewModel.moveCharacter("DOWN");
                case NUMPAD4 -> viewModel.moveCharacter("LEFT");
                case NUMPAD6 -> viewModel.moveCharacter("RIGHT");
                case NUMPAD7 -> viewModel.moveCharacter("UP_LEFT");
                case NUMPAD9 -> viewModel.moveCharacter("UP_RIGHT");
                case NUMPAD1 -> viewModel.moveCharacter("DOWN_LEFT");
                case NUMPAD3 -> viewModel.moveCharacter("DOWN_RIGHT");
            }

            displayMaze(viewModel.getMaze()); // ציור מחודש של המבוך והדמות
        });
    }


    public void setViewModel(MyViewModel vm) {
        this.viewModel = vm;
    }

    @FXML
    public void onGenerateMaze(ActionEvent event) {
        viewModel.generateMaze();
        displayMaze(viewModel.getMaze());
        statusLabel.setText("מבוך נוצר בהצלחה!");
    }

    @FXML
    public void onSolveMaze(ActionEvent event) {
        viewModel.solveMaze();
        displayMaze(viewModel.getMaze());
        drawSolution(viewModel.getSolutionPath());
        statusLabel.setText("פתרון מוצג על גבי המבוך");
    }

    @FXML
    public void onSaveMaze() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Maze");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Maze files", "*.maze"));
        File file = fileChooser.showSaveDialog(getStage());
        if (file != null) {
            try (FileOutputStream fos = new FileOutputStream(file)) {
                byte[] mazeData = viewModel.getMazeObject().toByteArray();
                fos.write(mazeData);
                statusLabel.setText("מבוך נשמר בהצלחה");
            } catch (IOException e) {
                statusLabel.setText("שגיאה בשמירת המבוך");
            }
        }
    }


    @FXML
    public void onLoadMaze() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Maze");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Maze files", "*.maze"));
        File file = fileChooser.showOpenDialog(getStage());
        if (file != null) {
            try (FileInputStream fis = new FileInputStream(file);
                 ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {

                byte[] temp = new byte[1024];
                int nRead;
                while ((nRead = fis.read(temp, 0, temp.length)) != -1) {
                    buffer.write(temp, 0, nRead);
                }

                byte[] data = buffer.toByteArray();
                Maze loadedMaze = new Maze(data);
                viewModel.setMaze(loadedMaze);
                displayMaze(loadedMaze.getMaze());
                statusLabel.setText("מבוך נטען בהצלחה");

            } catch (IOException e) {
                statusLabel.setText("שגיאה בטעינת המבוך");
            }
        }
    }


    @FXML
    public void onExit() {
        Platform.exit();     // סוגר את JavaFX Application Thread
        System.exit(0);      // סוגר את JVM כולו (כולל Maven process)
    }




    private Stage getStage() {
        return (Stage) mazeDisplay.getScene().getWindow();
    }


    @Override
    public void showAlert(String message) {
        // TODO: show alert to user
    }

    @Override
    public void displayMaze(int[][] maze) {
        if (maze == null || maze.length == 0) return;

        javafx.application.Platform.runLater(() -> {
            System.out.println("displayMaze() called");
            System.out.println("rows = " + maze.length);
            System.out.println("cols = " + (maze.length > 0 ? maze[0].length : 0));

            GraphicsContext gc = canvas.getGraphicsContext2D();
            double cellHeight = canvas.getHeight() / maze.length;
            double cellWidth = canvas.getWidth() / maze[0].length;

            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

            // ציור תאי המבוך עם קירות שקופים
            for (int i = 0; i < maze.length; i++) {
                for (int j = 0; j < maze[i].length; j++) {
                    gc.setFill(maze[i][j] == 1 ? Color.rgb(0, 0, 0, 0.4) : Color.TRANSPARENT);
                    gc.fillRect(j * cellWidth, i * cellHeight, cellWidth, cellHeight);
                }
            }

            // ציור הכור
            try {
                Image coreImage = new Image(getClass().getResourceAsStream("/images/core.png"));
                int goalRow = viewModel.getGoalPosition()[0];
                int goalCol = viewModel.getGoalPosition()[1];
                gc.drawImage(coreImage, goalCol * cellWidth, goalRow * cellHeight, cellWidth, cellHeight);
            } catch (Exception e) {
                System.err.println("❌ לא נמצא core.png");
            }

            // ציור המטוס
            try {
                Position pos = viewModel.getCurrentPosition();
                if (pos != null) {
                    Image planeImage = new Image(getClass().getResourceAsStream("/images/plane.png"));
                    gc.drawImage(planeImage,
                            pos.getColumnIndex() * cellWidth,
                            pos.getRowIndex() * cellHeight,
                            cellWidth, cellHeight);
                }
            } catch (Exception e) {
                System.err.println("❌ לא נמצא plane.png – מצייר עיגול במקום:");
                Position pos = viewModel.getCurrentPosition();
                if (pos != null) {
                    gc.setFill(Color.DEEPSKYBLUE);
                    gc.fillOval(pos.getColumnIndex() * cellWidth,
                            pos.getRowIndex() * cellHeight,
                            cellWidth, cellHeight);
                }
            }

            // פתרון
            if (isSolutionVisible && viewModel.getSolutionPath() != null) {
                drawSolution(viewModel.getSolutionPath());
            }

            // בדיקת ניצחון
            if (viewModel.getCurrentPosition() != null &&
                    viewModel.getGoalPosition() != null) {
                Position current = viewModel.getCurrentPosition();
                int[] goal = viewModel.getGoalPosition();
                if (current.getRowIndex() == goal[0] && current.getColumnIndex() == goal[1]) {
                    showVictoryCelebration(goal[0], goal[1]);
                }
            }

            canvas.requestFocus();
        });
    }




    private void drawSolution(List<AState> path) {
        if (path == null || path.isEmpty()) return;

        GraphicsContext gc = canvas.getGraphicsContext2D();
        int[][] maze = viewModel.getMaze();
        double cellHeight = canvas.getHeight() / maze.length;
        double cellWidth = canvas.getWidth() / maze[0].length;

        gc.setFill(Color.LIGHTGREEN);
        for (AState state : path) {
            if (state instanceof MazeState) {
                String[] coords = ((MazeState) state).getStateView().split(",");
                int row = Integer.parseInt(coords[0].replaceAll("[^\\d]", ""));
                int col = Integer.parseInt(coords[1].replaceAll("[^\\d]", ""));
                gc.fillRect(col * cellWidth, row * cellHeight, cellWidth, cellHeight);
            }
        }
    }

    @Override
    public void onMazeSolved() {
        displayMaze(viewModel.getMaze());
        drawSolution(viewModel.getSolutionPath());
        statusLabel.setText("הפתרון הוצג בהצלחה");
    }

    @FXML
    public void onGenerateMazeCustom(ActionEvent event) {
        try {
            int rows = Integer.parseInt(rowsInput.getText());
            int cols = Integer.parseInt(colsInput.getText());

            viewModel.setRows(rows);
            viewModel.setCols(cols);
            viewModel.generateMaze();

            displayMaze(viewModel.getMaze());
            statusLabel.setText("מבוך נוצר בהצלחה!");

        } catch (NumberFormatException e) {
            statusLabel.setText("שגיאה: יש להזין מספרים תקינים לשורות ועמודות.");
        }
    }

    public void requestFocusOnCanvas() {
        if (canvas != null)
            canvas.requestFocus();
    }

    @FXML
    public void onHelp() {
        String message = """
        🚀 מקשי שליטה:
        NUMPAD8 – למעלה
        NUMPAD2 – למטה
        NUMPAD4 – שמאלה
        NUMPAD6 – ימינה
        NUMPAD7/9/1/3 – תנועה אלכסונית

        🖱️ תפריטים:
        - טען/שמור מבוך
        - פתרון אוטומטי
        - יציאה מהמשחק
        """;

        showPopup("הוראות שימוש", message);
    }

    @FXML
    public void onAbout() {
        String message = """
        אפליקציית מבוך גרעיני
       
        מטרתך: למצוא את הכור האיראני ולהשמיד אותו!
        בהצלחה במבצע 💣✈️
        """;

        showPopup("אודות", message);
    }

    private void showPopup(String title, String content) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }


    private void showVictoryCelebration(int row, int col) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double cellHeight = canvas.getHeight() / viewModel.getMaze().length;
        double cellWidth = canvas.getWidth() / viewModel.getMaze()[0].length;

        try {
            Image boom = new Image(getClass().getResourceAsStream("/images/explosion.png"));
            gc.drawImage(boom, col * cellWidth, row * cellHeight, cellWidth, cellHeight);
        } catch (Exception e) {
            System.err.println("❌ לא נמצא explosion.png");
        }

        try {
            Image flag = new Image(getClass().getResourceAsStream("/images/israel.png"));
            gc.drawImage(flag, canvas.getWidth() / 2 - 50, canvas.getHeight() / 2 - 50, 400, 200);
        } catch (Exception e) {
            System.err.println("❌ לא נמצא israel.png");
        }
        try {
            AudioClip victory = new AudioClip(getClass().getResource("/sounds/victory.mp3").toExternalForm());
            victory.play();
        } catch (Exception e) {
            System.err.println("⚠️ שגיאה בסאונד ניצחון");
        }


        // ✅ תיבת ברכה + "שחק שוב"
        javafx.application.Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("המשימה הושלמה!");
            alert.setHeaderText("כל הכבוד! 🎉");
            alert.setContentText("פתרת את המבוך בהצלחה\nועזרת לישראל להשמיד את הכור הגרעיני של איראן!\nאין עלייך שומר ישראל! ");

            ButtonType replayBtn = new ButtonType("שחק שוב");
            alert.getButtonTypes().setAll(replayBtn);

            alert.showAndWait().ifPresent(response -> {
                if (response == replayBtn) {
                    returnToWelcomeScreen();
                }
            });
        });
    }

    private void returnToWelcomeScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Welcome.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) canvas.getScene().getWindow();
            stage.setScene(new Scene(root, 400, 250));
            stage.setTitle("ברוך הבא למשימת השמדת הכור!");
            stage.show();
        } catch (IOException e) {
            System.err.println("⚠️ שגיאה בטעינת Welcome.fxml");
            e.printStackTrace();
        }
    }

    @FXML
    public void onHoverSolve() {
        if (viewModel.getSolutionPath() == null) {
            statusLabel.setText("⚠️ אין פתרון עדיין");
            return;
        }

        showingSolution = true;

        javafx.application.Platform.runLater(() -> {
            displayMaze(viewModel.getMaze()); // זה כבר יגרום ל־drawSolution אם showingSolution == true
            statusLabel.setText("מציג פתרון זמני...");
        });
    }



    @FXML
    public void onLeaveSolve() {
        showingSolution = false;

        javafx.application.Platform.runLater(() -> {
            displayMaze(viewModel.getMaze());
            statusLabel.setText("חזרה למבוך הרגיל");
        });
    }

    @FXML
    public void onToggleSolve() {
        if (viewModel.getSolutionPath() == null) {
            statusLabel.setText("⚠️ עדיין אין פתרון. לחץ על 'פתור' קודם.");
            return;
        }

        isSolutionVisible = !isSolutionVisible;

        displayMaze(viewModel.getMaze()); // יגרום לציור מחדש

        statusLabel.setText(isSolutionVisible ? "מציג פתרון" : "פתרון הוסר");
    }


    public void startBackgroundMusic() {
        try {
            backgroundMusic = new AudioClip(getClass().getResource("/sounds/bg_music.mp3").toExternalForm());
            backgroundMusic.setCycleCount(AudioClip.INDEFINITE); // לולאה
            backgroundMusic.play();
        } catch (Exception e) {
            System.err.println("⚠️ שגיאה בהשמעת מוזיקת רקע");
            e.printStackTrace();
        }
    }







}
