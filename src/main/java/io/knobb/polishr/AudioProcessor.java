package io.knobb.polishr;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.ode.nonstiff.AdamsIntegrator;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

public class AudioProcessor extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage window) {
        final VBox layout = new VBox();
        final LineChart<Number,Number> timeData = new LineChart<>(new NumberAxis(), new NumberAxis());
        final LineChart<Number,Number> fftData = new LineChart<>(new NumberAxis(), new NumberAxis());

        timeData.setTitle("Time Data");
        timeData.setCreateSymbols(false);
        timeData.setLegendVisible(false);
        fftData.setTitle("Fourier Transform");
        fftData.setCreateSymbols(false);
        fftData.setLegendVisible(false);

        layout.getChildren().add(timeData);
        layout.getChildren().add(fftData);

        double[] waveData = generateWaveData(1024);

        timeData.getData().add(convertToLineData(waveData));
        fftData.getData().add(createFFTSpectrum(waveData));

        Scene scene = new Scene(layout, 900, 600);
        window.setScene(scene);
        window.setTitle("Fast Fourier Transform");
        window.show();
    }

    private double[] generateWaveData(int fftSize) {
        double[] data = new double[fftSize];
        double radians;
        double frequencyFactor = 1;
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
        FastFourierTransformer transformer = new FastFourierTransformer(DftNormalization.STANDARD);
        Complex[] frequencyData = transformer.transform(data, TransformType.FORWARD);
        double[] fftData = buildFFTPlot(frequencyData);
        return convertToLineData(fftData);
    }

    private double[] buildFFTPlot(Complex[] fftData) {
        double[] fftOut = new double[fftData.length];
        double angle;
        double scale = 2;
        double real;
        double imaginary;

        for (int i=0;i<fftData.length;i++) {
            real = fftData[i].getReal();
            imaginary = fftData[i].getImaginary();
            angle = ( 2.0 * Math.PI * i / fftData.length );
            fftOut[i] += real * Math.cos( angle ) / scale;
            System.out.println( fftOut[i]);
        }

        AdamsIntegrator integrator = new AdamsIntegrator();

        return fftOut;
    }
}
