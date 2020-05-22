package io.knobb.polishr;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AudioPlayerFFT extends Application {

    //private static final int INTEGER_MAX = 65536‬;

    public static void main(String[] args)
    {
        AudioPlayerFFT.playSound();
        //launch(args);
    }

    @Override
    public void start(Stage window) {
        final StackPane layout = new StackPane();

        Button play = new Button();
        play.setText("Play");
        play.setOnAction((e) -> {
            playSound();
        });

        layout.getChildren().add(play);
        Scene scene = new Scene(layout, 300, 400);
        scene.getStylesheets().add("file:fftStyle.css");
        window.setScene(scene);
        window.setTitle("KNOBB - Audio Player Tester");
        window.show();
    }

    public static void playSound() {
        File file = new File("C:\\Users\\jeffr\\Downloads\\dancing_through_sunday.wav");
        try {
            WavFile wav = new WavFile(file);
            double[] audioData = new double[(int)wav.getFramesCount()];
            for (int i=0;i<wav.getFramesCount();i++) {
                int amplitude = wav.getSampleInt(i);
                // 65536‬ <- Max integer combinations (2^4)
                double amplitudeVal;
                if (amplitude == 0) {
                    amplitudeVal = 0;
                } else {
                    amplitudeVal = amplitude / 65536.0;
                }
                audioData[i] = amplitudeVal;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            System.out.println("DONE");
        }


    }
}
