package io.knobb.polishr;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Polishr extends Application {

    Button button;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage window) {
        window.setTitle("Polishr By Knobb");
        button = new Button();
        button.setText("Test");
        button.setOnAction(e -> {
            System.out.println("Bro");
        });
        StackPane layout = new StackPane();
        layout.getChildren().add(button);
        Scene scene = new Scene(layout, 300, 400);
        scene.getStylesheets().add("file:style.css");
        window.setScene(scene);
        window.setOnCloseRequest(e -> {
            System.out.println("Closing window");
        });
        window.show();
    }
}
