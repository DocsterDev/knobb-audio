package io.knobb.polishr.contoller;

import io.knobb.polishr.filter.ExponentialMovingAverageFilter;
import io.knobb.polishr.io.MP3DecoderModded;
import io.knobb.polishr.player.AudioPlayer;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

import java.net.URL;
import java.util.*;

public class MainController implements Initializable {

    public StackPane playButton;

    @FXML
    public HBox visualizer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Initializing...");
    }

    public void handleButtonClick () {
        System.out.println("Button Clicked");
        Task<Void> audio = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                process();
                return null;
            }
        };
        Thread thread1 = new Thread(audio);
        thread1.start();
    }

    private void process() {
        int numBars = 60;
        List<String> colors = new ArrayList<>();
        colors.add("7ae2ff");
        colors.add("4bd8ff");
        colors.add("2eccf4");
        colors.add("1bd1ff");
        Map<Integer, ExponentialMovingAverageFilter> maFilter = new HashMap<>();
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                for (int i = 0; i < numBars; i++) {
                    Collections.shuffle(colors);
                    maFilter.put(i, new ExponentialMovingAverageFilter(10));
                    Rectangle rectangle = new Rectangle();
                    rectangle.setId("bar" + i);
                    rectangle.setFill(Paint.valueOf(colors.get(0)));
                    rectangle.setWidth(10);
                    rectangle.setHeight(1);
                    rectangle.setStrokeType(StrokeType.INSIDE);
                    rectangle.setLayoutX(135);
                    rectangle.setLayoutY(10);
                    rectangle.setSmooth(false);
                    visualizer.getChildren().add(rectangle);
                }
            }
        });
        try {
            //WaveDecoder waveDecoder = new WaveDecoder(new FileInputStream("C:/Users/jeffr/Documents/Code/audio-poc/src/main/resources/stay.wav"));
            //MP3DecoderModded mp3Decoder = new MP3DecoderModded(new FileInputStream("C:/Users/jeffr/Documents/Code/audio-poc/src/main/resources/song1.mp3"));
            MP3DecoderModded mp3Decoder = new MP3DecoderModded(new URL("http://localhost:8084/stream/videos/eLGiTV2oP10").openStream());
            // 2D Sample array for each stereo channel
            float[][] samples = new float[1024][2];
            FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);

            int increment = 100 / numBars;
            while (mp3Decoder.readSamples( samples ) > 0) {

                double[] samples_left = new double[samples.length];
                double[] samples_right = new double[samples.length];

                for (int i = 0; i < samples.length; i++) {
                    samples_left[i] = samples[i][0];
                    samples_right[i] = samples[i][1];
                }
                Complex[] fft_left = fft.transform(samples_left, TransformType.FORWARD);
                Complex[] fft_right = fft.transform(samples_right, TransformType.FORWARD);

                int index = 0;
                double val;
                for (Node rect: visualizer.getChildren()) {
                    val = 0;
                    for (int i = (index * increment); i < ((index + 1) * increment); i++) {
                        //val += fft_left[i].abs() / (float)(numBars / (index + 1));
                        val += fft_left[i].abs() * (4 * (float)(index + 1) * 0.125);
                    }
                    val = val / increment;
                    rect.setScaleY(maFilter.get(index).addAndCalculate(val));
                    index++;
                }

//                double factor = 10;
//                for (int i = 1000; i < 1024; i++) {
//                    fft_left[i] = fft_left[i].divide(factor);
//                    fft_right[i] = fft_right[i].divide(factor);
//                }


//                double factorLo = Math.pow(10, 0.5);
//                for (int i = 0; i < 100; i++) {
//                    fft_left[i] = fft_left[i].divide(factorLo);
//                    fft_right[i] = fft_right[i].divide(factorLo);
//                }

                Complex[] inverse_left = fft.transform(fft_left, TransformType.INVERSE);
                Complex[] inverse_right = fft.transform(fft_right, TransformType.INVERSE);

                for (int i = 0; i < fft_right.length; i++) {
                    AudioPlayer.play(inverse_left[i].getReal(), inverse_right[i].getReal());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
