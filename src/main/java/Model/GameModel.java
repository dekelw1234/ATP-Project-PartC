package Model;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class GameModel implements IGameModel {
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
}
