package com.example.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ResourceRequestApp extends Application {
    private static boolean isStarted = false;
    private static Stage primaryResourceStage;

    @Override
    public void start(Stage primaryStage) {
        isStarted = true;
        primaryResourceStage = primaryStage;
        primaryResourceStage.setTitle("Resource Details");
        primaryResourceStage.show();
    }

    public static void launchResourceGUI(String data) {
        if (!Platform.isFxApplicationThread()) {
            if (!isStarted) {
                new Thread(() -> Application.launch(ResourceRequestApp.class)).start();
            }
        } else {
            updateGUI(data);
        }
    }

    private static void updateGUI(String data) {
        Platform.runLater(() -> {
            if (primaryResourceStage == null) {
                primaryResourceStage = new Stage();
                primaryResourceStage.setTitle("Resource Details");
            }

            VBox vbox = new VBox(10);
            vbox.setSpacing(8);

            Label infoLabel = new Label("Meeting details: " + data);
            TextField peopleField = new TextField();
            TextField itemsField = new TextField();
            Button submitButton = new Button("Submit Resources");

            submitButton.setOnAction(e -> {
                System.out.println("Resources needed: People - " + peopleField.getText() + ", Items - " + itemsField.getText());
                primaryResourceStage.close();  // Close the stage after submitting resources
            });

            vbox.getChildren().addAll(infoLabel, new Label("Number of People:"), peopleField, new Label("Items Quantity:"), itemsField, submitButton);

            Scene scene = new Scene(vbox, 300, 250);
            primaryResourceStage.setScene(scene);
            primaryResourceStage.show();
        });
    }
}
