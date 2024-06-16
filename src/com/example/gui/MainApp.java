package com.example.gui;

import com.example.Room;
import com.example.RoomLoader;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class MainApp extends Application {
    private TextField roomField;
    private DatePicker datePicker;
    private ComboBox<String> startTimeComboBox;
    private ComboBox<String> endTimeComboBox;
    private ComboBox<String> capacityComboBox;
    private static AgentController userAgentController;
    private List<Room> rooms;

    public static void setUserAgentController(AgentController userAgentController) {
        MainApp.userAgentController = userAgentController;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Meeting Room Booking System");

        // Load rooms from JSON file
        try {
            rooms = RoomLoader.loadRooms("src/com/example/rooms.json");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load rooms.");
            return;
        }

        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(8);

        Label roomLabel = new Label("Room:");
        roomField = new TextField();
        Label dateLabel = new Label("Date:");
        datePicker = new DatePicker();
        Label startTimeLabel = new Label("Start Time:");
        startTimeComboBox = new ComboBox<>();
        Label endTimeLabel = new Label("End Time:");
        endTimeComboBox = new ComboBox<>();
        Label capacityLabel = new Label("Capacity:");
        capacityComboBox = new ComboBox<>();

        // Add time options to ComboBoxes
        populateTimeOptions(startTimeComboBox);
        populateTimeOptions(endTimeComboBox);

        // Add capacity options to ComboBox
        populateCapacityOptions(capacityComboBox);

        Button submitButton = new Button("Submit Booking");
        submitButton.setOnAction(e -> submitBooking());

        vbox.getChildren().addAll(roomLabel, roomField, dateLabel, datePicker, startTimeLabel, startTimeComboBox, endTimeLabel, endTimeComboBox, capacityLabel, capacityComboBox, submitButton);

        Scene scene = new Scene(vbox, 300, 350);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void populateTimeOptions(ComboBox<String> comboBox) {
        List<String> timeOptions = new ArrayList<>();
        for (int hour = 0; hour < 24; hour++) {
            for (int minute = 0; minute < 60; minute += 15) {
                LocalTime time = LocalTime.of(hour, minute);
                timeOptions.add(time.format(DateTimeFormatter.ofPattern("HH:mm")));
            }
        }
        comboBox.getItems().addAll(timeOptions);
    }

    private void populateCapacityOptions(ComboBox<String> comboBox) {
        comboBox.getItems().addAll("1-2", "2-6", "7-15", "16-50");
    }

    private void submitBooking() {
        String room = roomField.getText();
        LocalDate date = datePicker.getValue();
        String startTime = startTimeComboBox.getValue();
        String endTime = endTimeComboBox.getValue();
        String capacity = capacityComboBox.getValue();

        if (!isValidRoom(room)) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Invalid room. Please book a valid room.");
            return;
        }

        if (date == null || !isValidTime(startTime) || !isValidTime(endTime) || !isValidCapacity(capacity)) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Invalid date, time, or capacity.");
            return;
        }

        if (!isFutureDateTime(date, startTime) || !isValidTimeRange(startTime, endTime)) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Date and time must be in the future and valid.");
            return;
        }

        String content = "Room: " + room + ", Date: " + date.toString() + ", Start Time: " + startTime + ", End Time: " + endTime + ", Capacity: " + capacity;
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

    private boolean isValidRoom(String room) {
        for (Room r : rooms) {
            if (r.getName().equalsIgnoreCase(room)) {
                return true;
            }
        }
        return false;
    }

    private boolean isValidTime(String timeStr) {
        try {
            LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm"));
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private boolean isValidCapacity(String capacity) {
        return capacity != null && (capacity.equals("1-2") || capacity.equals("2-6") || capacity.equals("7-15") || capacity.equals("16-50"));
    }

    private boolean isFutureDateTime(LocalDate date, String timeStr) {
        try {
            LocalTime time = LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm"));
            LocalDateTime dateTime = LocalDateTime.of(date, time);
            return dateTime.isAfter(LocalDateTime.now());
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private boolean isValidTimeRange(String startTimeStr, String endTimeStr) {
        try {
            LocalTime startTime = LocalTime.parse(startTimeStr, DateTimeFormatter.ofPattern("HH:mm"));
            LocalTime endTime = LocalTime.parse(endTimeStr, DateTimeFormatter.ofPattern("HH:mm"));
            return startTime.isBefore(endTime);
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
