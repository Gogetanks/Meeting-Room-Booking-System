package com.example.gui;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class MainApp extends Application {
    private TextField roomField;
    private TextField dateField;
    private TextField timeField;
    private static AgentController userAgentController;

    public static void setUserAgentController(AgentController userAgentController) {
        MainApp.userAgentController = userAgentController;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Meeting Room Booking System");

        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(8);

        Label roomLabel = new Label("Room:");
        roomField = new TextField();
        Label dateLabel = new Label("Date (yyyy-MM-dd):");
        dateField = new TextField();
        Label timeLabel = new Label("Time (HH:mm):");
        timeField = new TextField();

        Button submitButton = new Button("Submit Booking");
        submitButton.setOnAction(e -> submitBooking());

        vbox.getChildren().addAll(roomLabel, roomField, dateLabel, dateField, timeLabel, timeField, submitButton);

        Scene scene = new Scene(vbox, 300, 250);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void submitBooking() {
        String room = roomField.getText();
        String date = dateField.getText();
        String time = timeField.getText();

        if (room.isEmpty() || !isValidDate(date) || !isValidTime(time)) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Invalid input.");
            return;
        }

        String content = "Room: " + room + ",` Date: " + date + ", Time: " + time;
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        AID receiver = new AID("UserAgent", AID.ISLOCALNAME);
        msg.addReceiver(receiver);
        msg.setContent(content);

        try {
            if (userAgentController != null) {
                userAgentController.putO2AObject(msg, AgentController.ASYNC);
                System.out.println("Booking request sent to UserAgent with content: " + content);
            } else {
                System.out.println("UserAgentController not initialized.");
            }
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }


    private boolean isValidDate(String dateStr) {
        try {
            LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private boolean isValidTime(String timeStr) {
        try {
            LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm"));
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
