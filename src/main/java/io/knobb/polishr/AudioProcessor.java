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
        NumberAxis xAxis1 = new NumberAxis();
        NumberAxis xAxis2 = new NumberAxis();
        xAxis1.setAutoRanging(true);
        xAxis2.setAutoRanging(true);
        final LineChart<Number,Number> timeData = new LineChart<>(xAxis1, new NumberAxis());
        final LineChart<Number,Number> fftData = new LineChart<>(xAxis2, new NumberAxis());

        timeData.setTitle("Time Data");
        timeData.setCreateSymbols(false);
        timeData.setLegendVisible(false);
        timeData.setId("line-chart");
        fftData.setTitle("Fourier Transform");
        fftData.setCreateSymbols(false);
        fftData.setLegendVisible(false);

        layout.getChildren().add(timeData);
        layout.getChildren().add(fftData);

        double[] waveData = generateWaveData(1024);

        timeData.getData().add(convertToLineData(waveData));
        fftData.getData().add(createFFTSpectrum(waveData));

        Scene scene = new Scene(layout, 900, 600);
        scene.getStylesheets().add("file:fftStyle.css");
        window.setScene(scene);
        window.setTitle("Fast Fourier Transform");
        window.show();
    }

    private double[] generateWaveData(int fftSize) {
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

    private XYChart.Series convertToLineData(double[] data) {
        XYChart.Series series = new XYChart.Series();
        for (int i=0;i<data.length;i++) {
            series.getData().add(new XYChart.Data( i, data[i]));
        }
        return series;
    }

    private XYChart.Series createFFTSpectrum(double[] data) {
        Complex[] frequencyData = transformer.transform(data, TransformType.FORWARD);

        Complex eq1 = frequencyData[500];
        Complex eq2 = frequencyData[501];
        Complex eq3 = frequencyData[502];

        frequencyData[500] = eq1.add(50);
        frequencyData[501] = eq2.add(100);
        frequencyData[502] = eq3.add(50);

        Complex[] testInverse = transformer.transform(frequencyData, TransformType.INVERSE);

        double[] fftData = buildFFTPlot(testInverse);

        return convertToLineData(fftData);
    }

    private double[] buildFFTPlot(Complex[] fftData) {
        double[] fftOut = new double[fftData.length];
        for (int i=0;i<fftData.length;i++) {
            fftOut[i] = fftData[i].abs();
        }
        return fftOut;
    }
}
