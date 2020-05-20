package io.knobb.polishr;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

public class AudioProcessor extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage window) throws Exception {
        final NumberAxis axisX = new NumberAxis();
        final NumberAxis axisY = new NumberAxis();
        final LineChart<Number,Number> lineChart = new LineChart<>(axisX, axisY);
        lineChart.setTitle("Sine Wave Example");
        lineChart.setCreateSymbols(false);
        lineChart.getData().add(createSineWave());
        Scene scene = new Scene(lineChart, 700, 400);
        window.setScene(scene);
        window.setTitle("Fast Fourier Transform");
        window.show();
    }

    private XYChart.Series createSineWave() {
        double[][] data = new double[512][2];
        double radian;
        double frequencyFactor = 20;
        double amplitudeFactor = 0.5;
        XYChart.Series series = new XYChart.Series();
        series.setName("Sine Wave");
        for (int i=0;i<data.length;i++) {
            data[i][0] = i;
            // radian = (2*i) / (2*Math.PI);
            radian = Math.toRadians(frequencyFactor*i);
            data[i][1] = (amplitudeFactor*(Math.sin(radian)));
            radian++;
            series.getData().add(new XYChart.Data((int)data[i][0], data[i][1]));
        }
//        Complex[] dataVector = TransformUtils.createComplexArray(data);
//        FastFourierTransformer transformer = new FastFourierTransformer(DftNormalization.STANDARD);
//        Complex[] frequencyData = transformer.transform(dataVector, TransformType.FORWARD);
        return series;
    }
}
