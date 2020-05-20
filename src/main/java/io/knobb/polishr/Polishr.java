package io.knobb.polishr;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.concurrent.atomic.AtomicReference;

public class Polishr extends Application {

    StackPane layout;
    Circle circle;
    Label label;
    Rectangle notch;
    StackPane knobGroup;
    boolean init = false;

    AtomicReference<Double> value = new AtomicReference<>(0D);
    AtomicReference<Double> previousY = new AtomicReference<>(0D);

    public static void main(String[] args) {
       launch(args);
    }

    @Override
    public void start(Stage window) {
        window.setTitle("Polishr By Knobb");
        knobGroup = new StackPane();
        // Graphic knob
        circle = new Circle();
        circle.setCenterX(0);
        circle.setCenterY(0);
        circle.setRadius(80);
        circle.setId("knob-control");
        // Graphic knob notch
        notch = new Rectangle();
        notch.setHeight(20);
        notch.setWidth(5);
        notch.setTranslateY(-70);
        notch.setId("knob-control-notch");
        // Graphic knob group object
        knobGroup.getChildren().addAll(circle, notch);
        knobGroup.setRotate(0);
        // Value Label
        label = new Label();
        label.setText(String.valueOf(value.get()));
        label.setId("value-label");

        // User event handlers
        circle.addEventFilter(MouseEvent.MOUSE_PRESSED, (e) -> {
            updatePosition(e);
        });
        circle.addEventFilter(MouseEvent.MOUSE_DRAGGED, (e) -> {
            updatePosition(e);
        });

        // Scene layout
        layout = new StackPane();
        layout.getChildren().addAll(knobGroup, label);
        Scene scene = new Scene(layout, 300, 400);
        scene.getStylesheets().add("file:style.css");
        window.setScene(scene);
        window.setOnCloseRequest(e -> {
            System.out.println("Closing window");
        });
        window.show();
    }

    private void updatePosition(MouseEvent e) {
        double current = e.getSceneY();
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
        previousY.set(current);
        if (val > 360) {
            value.set(360D);
            return;
        }
        if (val < 0) {
            value.set(0D);
            return;
        }
        knobGroup.setRotate(val);
        label.setText(String.valueOf((int)val));
    }
}
