package Model;

public interface IGameModel {

    byte[] toByteArray();

    void fromByteArray(byte[] data);

    void restartGame();
}
