// src/main/java/app/BackgroundMusic.java
package View;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class BackgroundMusic {
    private static MediaPlayer player;

    public static void init() {
        if (player == null) {
            String url = BackgroundMusic.class.getResource("/music/videoplayback.m4a")
                    .toExternalForm();
            Media media = new Media(url);
            player = new MediaPlayer(media);
            player.setCycleCount(MediaPlayer.INDEFINITE);
            player.play();
        }
    }

    public static void stop() {
        if (player != null) {
            player.stop();
        }
    }
    public static void play() {
        init();
        if (player != null) {
            player.play();
        }
    }

}
