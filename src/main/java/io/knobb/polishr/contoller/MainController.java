package io.knobb.polishr.contoller;

import io.knobb.polishr.filter.ExponentialMovingAverageFilter;
import io.knobb.polishr.filter.SuperSmootherFilter;
import io.knobb.polishr.io.MP3DecoderModded;
import io.knobb.polishr.player.AudioPlayer;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

import java.io.FileInputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    public StackPane playButton;
    public Circle visualizerLow;
    public Circle visualizerMid;
    public Circle visualizerHigh;

    @FXML
    public Rectangle bar0;
    @FXML
    public Rectangle bar1;
    @FXML
    public Rectangle bar2;
    @FXML
    public Rectangle bar3;
    @FXML
    public Rectangle bar4;

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
        System.out.println("Playing audio");
        try {
            //WaveDecoder waveDecoder = new WaveDecoder(new FileInputStream("C:/Users/jeffr/Documents/Code/audio-poc/src/main/resources/stay.wav"));
            //MP3DecoderModded mp3Decoder = new MP3DecoderModded(new FileInputStream("C:/Users/jeffr/Documents/Code/audio-poc/src/main/resources/song1.mp3"));
            MP3DecoderModded mp3Decoder = new MP3DecoderModded(new URL("http://localhost:8084/stream/videos/2Lb2BiUC898").openStream());
            // 2D Sample array for each stereo channel
            float[][] samples = new float[1024][2];
            FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);

            ExponentialMovingAverageFilter ema0 = new ExponentialMovingAverageFilter(10);
            ExponentialMovingAverageFilter ema1 = new ExponentialMovingAverageFilter(10);
            ExponentialMovingAverageFilter ema2 = new ExponentialMovingAverageFilter(10);
            ExponentialMovingAverageFilter ema3 = new ExponentialMovingAverageFilter(10);
            ExponentialMovingAverageFilter ema4 = new ExponentialMovingAverageFilter(10);

            int increment = 256 / 5;
            while (mp3Decoder.readSamples( samples ) > 0) {

                double[] samples_left = new double[samples.length];
                double[] samples_right = new double[samples.length];

                for (int i = 0; i < samples.length; i++) {
                    samples_left[i] = samples[i][0];
                    samples_right[i] = samples[i][1];
                }
                Complex[] fft_left = fft.transform(samples_left, TransformType.FORWARD);
                Complex[] fft_right = fft.transform(samples_right, TransformType.FORWARD);


                double val0 = 0;
                for (int i = 0; i < increment; i++) {
                    val0 += fft_left[i].abs() / 20;
                }
                val0 = val0 / increment;
                double val1 = 0;
                for (int i = increment; i < increment * 2; i++) {
                    val1 += fft_left[i].abs() / 10;
                }
                val1 = val1 / increment;
                double val2 = 0;
                for (int i = increment * 2; i < increment * 3; i++) {
                    val2 += fft_left[i].abs() / 5;
                }
                val2 = val2 / increment;
                double val3 = 0;
                for (int i = increment * 3; i < increment * 4; i++) {
                    val3 += fft_left[i].abs() / 2.5;
                }
                val3 = val3 / increment;
                double val4 = 0;
                for (int i = increment * 4; i < 256; i++) {
                    val4 += fft_left[i].abs() / 1.25;
                }
                val4 = val4 / increment;

//                double factor = 10;
//                for (int i = 1000; i < 1024; i++) {
//                    fft_left[i] = fft_left[i].divide(factor);
//                    fft_right[i] = fft_right[i].divide(factor);
//                }

                bar0.setScaleY(ema0.addAndCalculate(val0));
                bar1.setScaleY(ema1.addAndCalculate(val1));
                bar2.setScaleY(ema2.addAndCalculate(val2));
                bar3.setScaleY(ema3.addAndCalculate(val3));
                bar4.setScaleY(ema4.addAndCalculate(val4));


//                lo = emaLo.addAndCalculate(fft_left[50].abs()/10);
//                mid = emaMid.addAndCalculate(fft_left[500].abs()/10);
//                hi = emaHi.addAndCalculate(fft_left[1000].abs()/10);
//
//                visualizerLow.setScaleX(lo);
//                visualizerLow.setScaleY(lo);
//                visualizerMid.setScaleX(mid);
//                visualizerMid.setScaleY(mid);
//                visualizerHigh.setScaleX(hi);
//                visualizerHigh.setScaleY(hi);

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
