package io.knobb.polishr;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.complex.ComplexUtils;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

public class AudioProcessor extends Application {

    private static final FastFourierTransformer transformer = new FastFourierTransformer(DftNormalization.STANDARD);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage window) {
        final VBox layout = new VBox();
        NumberAxis axisX = new NumberAxis();
        axisX.setAutoRanging(true);
        final LineChart<Number,Number> inputLineChart = new LineChart<>(axisX, new NumberAxis());
        final LineChart<Number,Number> fftLineChart = new LineChart<>(new NumberAxis(), new NumberAxis());
        final LineChart<Number,Number> outputLineChart = new LineChart<>(axisX, new NumberAxis());

        inputLineChart.setTitle("Input Signal");
        inputLineChart.setId("line-chart-input-signal");
        inputLineChart.setCreateSymbols(false);
        inputLineChart.setLegendVisible(false);

        fftLineChart.setTitle("Frequency Spectrum");
        fftLineChart.setId("line-chart-fft");
        fftLineChart.setCreateSymbols(false);
        fftLineChart.setLegendVisible(false);

        outputLineChart.setTitle("Output Signal");
        outputLineChart.setId("line-chart-output-signal");
        outputLineChart.setCreateSymbols(false);
        outputLineChart.setLegendVisible(false);

        layout.getChildren().add(inputLineChart);
        layout.getChildren().add(fftLineChart);
        layout.getChildren().add(outputLineChart);

        // double[] timeSeriesAudioData = generateSineWave(1024);

        System.out.println("Extracting time series data from wav file...");
        double[] timeSeriesAudioData = AudioExtractor.getTimeSeriesFromWavFile("C:\\Users\\jeffr\\Downloads\\dancing_through_sunday.wav");

        //double[] subSeries =  // Get sub list

        System.out.println("Extracting time series complete");

        inputLineChart.getData().add(convertToSeries(timeSeriesAudioData));
//        Complex[] fftFrequencySpectrum = createFFTSpectrum(timeSeriesAudioData);
//        double[] fftFrequencySpectrumAbs = extractAbsoluteValue(fftFrequencySpectrum);
//        fftLineChart.getData().add(convertToSeries(fftFrequencySpectrumAbs));
//        double [] fftInverseOutput = inverseFFT(fftFrequencySpectrum);
//        outputLineChart.getData().add(convertToSeries(fftInverseOutput));

        Scene scene = new Scene(layout, 900, 600);
        scene.getStylesheets().add("file:fftStyle.css");
        window.setScene(scene);
        window.setTitle("KNOBB - Fast Fourier Transform Example");
        window.show();
    }

    private double[] generateSineWave(int fftSize) {
        double[] data = new double[fftSize];
        double radians;
        double frequencyFactor = 16;
        double amplitudeFactor = 1;
        for (int i=0;i<data.length;i++) {
            data[i] = i;
            // Convert frequency selection to radians to generate sine wave
            radians = Math.toRadians( frequencyFactor * i );
            // Apply amplitude value to generated sine wave data
            data[i] = ( amplitudeFactor * ( Math.sin( radians ) ) );
        }
        return data;
    }

    private XYChart.Series convertToSeries(double[] data) {
        XYChart.Series series = new XYChart.Series();
        for (int i=0;i<data.length;i++) {
            series.getData().add(new XYChart.Data( i, data[i]));
        }
        return series;
    }

    private void equalize(Complex[] input) {
        for (int i=0;i<input.length;i++) {
           input[i] = new Complex(0);
        }
        int base = 400;
        Complex eq1 = input[base - 2];
        Complex eq2 = input[base - 1];
        Complex eq3 = input[base];
        Complex eq4 = input[base + 1];
        Complex eq5 = input[base + 2];

        input[base - 2] = eq1.add(25);
        input[base - 1] = eq2.add(50);
        input[base] = eq3.add(100);
        input[base + 1] = eq4.add(50);
        input[base + 2] = eq5.add(25);
    }

    private Complex[] createFFTSpectrum(double[] timeSeriesData) {
        Complex[] fftInput = transformer.transform(timeSeriesData, TransformType.FORWARD);
        // Meat and potatoes
        // equalize(fftInput);
        return fftInput;
    }

    private double[] inverseFFT (Complex[] fftInput) {
        Complex[] fftInverseOutput = transformer.transform(fftInput, TransformType.INVERSE);
        return extractAbsoluteValue(fftInverseOutput);
    }

    private double[] extractAbsoluteValue(Complex[] fftData) {
        double[] fftOut = new double[fftData.length];
        for (int i=0;i<fftData.length;i++) {
            fftOut[i] = fftData[i].abs();
        }
        return fftOut;
    }
}
