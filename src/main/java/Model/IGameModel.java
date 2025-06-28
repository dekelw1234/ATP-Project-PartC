package Model;

public interface IGameModel {

    byte[] toByteArray();

    void fromByteArray(byte[] data);

    void restartGame();

    String[] settings();

    String getHelpText();

    void solve();

    void clearSolution();
}
