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
import javafx.scene.control.*;
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
    private DatePicker datePicker;
    private ComboBox<String> startTimeComboBox;
    private ComboBox<String> endTimeComboBox;
    private ComboBox<String> capacityComboBox;
    private List<CheckBox> equipmentCheckBoxes;
    private Label recommendedRoomLabel;
    private ComboBox<String> availableRoomsComboBox;
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

        Label dateLabel = new Label("Date:");
        datePicker = new DatePicker();
        Label startTimeLabel = new Label("Start Time:");
        startTimeComboBox = new ComboBox<>();
        Label endTimeLabel = new Label("End Time:");
        endTimeComboBox = new ComboBox<>();
        Label capacityLabel = new Label("Capacity:");
        capacityComboBox = new ComboBox<>();
        Label equipmentLabel = new Label("Equipment:");
        equipmentCheckBoxes = new ArrayList<>();
        equipmentCheckBoxes.add(new CheckBox("Projector"));
        equipmentCheckBoxes.add(new CheckBox("Whiteboard"));

        // Add time options to ComboBoxes
        populateTimeOptions(startTimeComboBox);
        populateTimeOptions(endTimeComboBox);

        // Add capacity options to ComboBox
        populateCapacityOptions(capacityComboBox);

        Button autoAssignButton = new Button("Auto Recommend Room");
        autoAssignButton.setOnAction(e -> autoAssignRoom());

        recommendedRoomLabel = new Label("Recommended Room: None");

        Label availableRoomsLabel = new Label("Choose Room:");
        availableRoomsComboBox = new ComboBox<>();
        populateAvailableRooms();

        Button submitButton = new Button("Submit Booking");
        submitButton.setOnAction(e -> submitBooking());

        vbox.getChildren().addAll(dateLabel, datePicker, startTimeLabel, startTimeComboBox, endTimeLabel, endTimeComboBox, capacityLabel, capacityComboBox, equipmentLabel);
        equipmentCheckBoxes.forEach(vbox.getChildren()::add);
        vbox.getChildren().addAll(autoAssignButton, recommendedRoomLabel, availableRoomsLabel, availableRoomsComboBox, submitButton);

        Scene scene = new Scene(vbox, 400, 500);
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

    private void populateAvailableRooms() {
        availableRoomsComboBox.getItems().clear();
        for (Room room : rooms) {
            availableRoomsComboBox.getItems().add(room.getName());
        }
    }

    private void autoAssignRoom() {
        LocalDate date = datePicker.getValue();
        String startTime = startTimeComboBox.getValue();
        String endTime = endTimeComboBox.getValue();
        String capacityStr = capacityComboBox.getValue();

        if (date == null) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "No date selected.");
            return;
        }

        if (startTime == null || startTime.isEmpty() || endTime == null || endTime.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Start time and end time must be selected.");
            return;
        }

        if (!isValidTime(startTime) || !isValidTime(endTime) || !isValidCapacity(capacityStr)) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Invalid time or capacity.");
            return;
        }

        int capacity = parseCapacity(capacityStr);

        if (!isFutureDateTime(date, startTime) || !isValidTimeRange(startTime, endTime)) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Date and time must be in the future and valid.");
            return;
        }

        // Auto-assign room based on user inputs
        Room recommendedRoom = findRecommendedRoom(date, startTime, endTime, capacity);
        if (recommendedRoom != null) {
            recommendedRoomLabel.setText("Recommended Room: " + recommendedRoom.getName());
        } else {
            showAlert(Alert.AlertType.WARNING, "No Room Available", "No available room matches the criteria.");
        }
    }

    private Room findRecommendedRoom(LocalDate date, String startTime, String endTime, int capacity) {
        LocalTime start = LocalTime.parse(startTime, DateTimeFormatter.ofPattern("HH:mm"));
        LocalTime end = LocalTime.parse(endTime, DateTimeFormatter.ofPattern("HH:mm"));

        for (Room room : rooms) {
            if (room.isAvailable(date, start, end) && room.getCapacity() >= capacity) {
                // Check if the room capacity matches the selected capacity range
                switch (capacity) {
                    case 2:
                        if (room.getName().equals("Lublin") || room.getName().equals("Katowice")) {
                            return room;
                        }
                        break;
                    case 6:
                        if (room.getName().equals("Lodz") || room.getName().equals("Wroclaw") || room.getName().equals("Poznan") || room.getName().equals("Gdansk") || room.getName().equals("Szczecin") || room.getName().equals("Bydgoszcz")) {
                            return room;
                        }
                        break;
                    case 15:
                        if (room.getName().equals("Krakow")) {
                            return room;
                        }
                        break;
                    case 50:
                        if (room.getName().equals("Warsaw")) {
                            return room;
                        }
                        break;
                }
            }
        }
        return null;
    }

    private void submitBooking() {
        String roomName = availableRoomsComboBox.getValue();
        if (roomName == null || roomName.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "No room selected.");
            return;
        }

        LocalDate date = datePicker.getValue();
        String startTime = startTimeComboBox.getValue();
        String endTime = endTimeComboBox.getValue();
        String capacityStr = capacityComboBox.getValue();

        if (date == null) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "No date selected.");
            return;
        }

        if (startTime == null || startTime.isEmpty() || endTime == null || endTime.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Start time and end time must be selected.");
            return;
        }

        if (!isValidTime(startTime) || !isValidTime(endTime) || !isValidCapacity(capacityStr)) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Invalid date, time, or capacity.");
            return;
        }

        int capacity = parseCapacity(capacityStr);

        Room selectedRoom = rooms.stream().filter(r -> r.getName().equals(roomName)).findFirst().orElse(null);
        if (selectedRoom != null && (selectedRoom.getCapacity() < capacity || selectedRoom.getCapacity() > capacity)) {
            showAlert(Alert.AlertType.ERROR, "Capacity Mismatch", "Selected room does not match the required capacity.");
            return;
        }

        LocalTime start = LocalTime.parse(startTime, DateTimeFormatter.ofPattern("HH:mm"));
        LocalTime end = LocalTime.parse(endTime, DateTimeFormatter.ofPattern("HH:mm"));

        if (!selectedRoom.isAvailable(date, start, end)) {
            showAlert(Alert.AlertType.ERROR, "Availability Error", "Selected room is not available during the chosen time.");
            return;
        }

        if (!isFutureDateTime(date, startTime) || !isValidTimeRange(startTime, endTime)) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Date and time must be in the future and valid.");
            return;
        }

        selectedRoom.addBooking(date, start, end);

        String content = "Room: " + roomName + ", Date: " + date.toString() + ", Start Time: " + startTime + ", End Time: " + endTime + ", Capacity: " + capacityStr;
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

    private int parseCapacity(String capacityStr) {
        switch (capacityStr) {
            case "1-2":
                return 2;
            case "2-6":
                return 6;
            case "7-15":
                return 15;
            case "16-50":
                return 50;
            default:
                return 0;
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
