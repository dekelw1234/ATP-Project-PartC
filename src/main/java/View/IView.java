package View;

public interface IView {
    void showAlert(String message);
    void displayMaze(int[][] maze);
    void onMazeSolved();
}
