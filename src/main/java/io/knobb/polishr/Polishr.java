package io.knobb.polishr;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.util.concurrent.atomic.AtomicReference;

public class Polishr extends Application {

    Circle circle;
    Label label;
    boolean init = false;

    AtomicReference<Double> value = new AtomicReference<>(0D);
    AtomicReference<Double> previousY = new AtomicReference<>(0D);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage window) {
        window.setTitle("Polishr By Knobb");

        StackPane layout = new StackPane();
        circle = new Circle();
        circle.setCenterX(0);
        circle.setCenterY(0);
        circle.setRadius(80);
        circle.setId("knob-control");

        label = new Label();
        label.setText("0");

        circle.addEventFilter(MouseEvent.MOUSE_ENTERED, (e) -> {
            circle.setFill(Color.valueOf("#666666"));
        });
        circle.addEventFilter(MouseEvent.MOUSE_EXITED, (e) -> {
            circle.setFill(Color.valueOf("#999999"));
        });
        circle.addEventFilter(MouseEvent.MOUSE_PRESSED, (e) -> {
            updatePosition(e);
        });
        circle.addEventFilter(MouseEvent.MOUSE_DRAGGED, (e) -> {
            updatePosition(e);
        });
        layout.getChildren().addAll(circle, label);
        Scene scene = new Scene(layout, 300, 400);
        scene.getStylesheets().add("file:style.css");
        window.setScene(scene);
        window.setOnCloseRequest(e -> {
            System.out.println("Closing window");
        });
        window.show();
    }

    private void updatePosition(MouseEvent e) {
        double current = e.getY();
        double prev = previousY.get();
        double val = value.get();
        if (!init) {
            previousY.set(current);
            init = true;
        }
        double difference = current - prev;
        double newValue = (val + (-difference));
        value.set(newValue);
        val = value.get();
        label.setText(String.valueOf((int)val));
        previousY.set(current);
    }
}
