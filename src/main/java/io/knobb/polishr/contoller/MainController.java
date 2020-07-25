package io.knobb.polishr.contoller;

import javafx.concurrent.Task;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Circle;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    public StackPane playButton;
    public Circle visualizerLow;
    public Circle visualizerMid;
    public Circle visualizerHigh;

    public MediaPlayer mediaPlayer;
    private Media mediaFile;

    private boolean isRunning = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Initializing...");
    }

    public void handleButtonClick () {
        System.out.println("Button Clicked");

        // Add javafx concurrency
        Task task = new Task<Void>() {
            @Override public Void call() {
                pulse();
                return null;
            }
        };
        if (!isRunning) {
            Thread thread = new Thread(task);
            thread.start();
        }
        isRunning = !isRunning;
    }

    private void pulse() {
        try {
            double value = 0;
            while (isRunning) {
                for (int i=100;i>=0;i--) {
                    value = (i/100.0);
                    visualizerLow.setScaleX(value);
                    visualizerLow.setScaleY(value);
                    visualizerMid.setScaleX(value);
                    visualizerMid.setScaleY(value);
                    visualizerHigh.setScaleX(value);
                    visualizerHigh.setScaleY(value);
                    Thread.sleep(20);
                }
                for (double i=0.01;i<100;i++) {
                    value = (i/100.0);
                    visualizerLow.setScaleX(value);
                    visualizerLow.setScaleY(value);
                    visualizerMid.setScaleX(value);
                    visualizerMid.setScaleY(value);
                    visualizerHigh.setScaleX(value);
                    visualizerHigh.setScaleY(value);
                    Thread.sleep(20);
                }
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
