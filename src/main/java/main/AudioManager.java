/**
 * @Author: RogerDTZ
 * @FileName: AudioManager.java
 */

package main;

import javafx.application.Application;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import static main.ResourceManager.RESOURCES_PROTOCOL;

public class AudioManager extends Application {

    private static Map<String, Media> map = new HashMap<>();
    private static ReentrantLock lock = new ReentrantLock();
    private static MediaPlayer bgmPlayer;


    public static void load(String audioName, String audioPath) {
        if (map.containsKey(audioName))
            return;

        Media sound;

        if (audioPath.startsWith(RESOURCES_PROTOCOL)) {
            audioPath = audioPath.substring(RESOURCES_PROTOCOL.length());
            URL resource = ResourceManager.class.getResource(audioPath);
            if (resource == null) {
                throw new RuntimeException(audioPath + " Not Found");
            }
            URI uri;
            try {
                uri = resource.toURI();
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
            sound = new Media(uri.toString());

        } else {
            sound = new Media(new File(audioPath).toURI().toString());
        }

        map.put(audioName, sound);
    }

    public static void Play(String name, double delay) {
        PlayWithVolume(name, 1.0, delay);
    }

    public static void PlayWithVolume(String name, double volume, double delay) {
        if (map.containsKey(name)) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        Thread.sleep(Math.round(delay * 1000), 0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (!lock.isLocked()) {
                        lock.lock();
                        MediaPlayer mp = new MediaPlayer(map.get(name));
                        mp.setVolume(volume);
                        mp.play();
                        lock.unlock();
                    }
                }
            };
            thread.start();
        }
    }

    public static void Play(String name) {
        Play(name, 0);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

    }

    public static void initBGM() {
        bgmPlayer = new MediaPlayer(map.get("bgm"));
        bgmPlayer.setVolume(0.1);
        bgmPlayer.setOnEndOfMedia(() -> bgmPlayer.seek(Duration.ZERO));
    }

    public static void playBGM() {
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(9000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            bgmPlayer.play();
        });
        thread.start();
    }

    public static void stopBGM() {
        bgmPlayer.stop();
    }

}
