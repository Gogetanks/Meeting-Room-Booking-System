package com.example.agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Timer;
import java.util.TimerTask;

public class NotificationAgent extends Agent {
    @Override
    protected void setup() {
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    System.out.println("NotificationAgent received: " + msg.getContent());
                    scheduleNotification(msg.getContent());
                } else {
                    block();
                }
            }
        });
    }

    private void scheduleNotification(String content) {
        try {
            // Assume content is like "Reminder: Meeting in room Lublin today at 18:45"
            String[] parts = content.split(" at ");
            if (parts.length < 2) {
                throw new IllegalArgumentException("Content format is incorrect for scheduling. Received: " + content);
            }
            String timePart = parts[1]; // "18:45"
            LocalDateTime notificationTime = LocalDateTime.now().withHour(LocalTime.parse(timePart, DateTimeFormatter.ofPattern("HH:mm")).getHour())
                    .withMinute(LocalTime.parse(timePart, DateTimeFormatter.ofPattern("HH:mm")).getMinute());

            long delay = java.time.Duration.between(LocalDateTime.now(), notificationTime).toMillis();
            if (delay > 0) {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        showNotification("Meeting Reminder", content);
                    }
                }, delay);
            } else {
                System.out.println("Notification time has already passed.");
            }
        } catch (DateTimeParseException e) {
            System.err.println("Failed to parse time for notification: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error scheduling notification: " + e.getMessage());
        }
    }

    private void showNotification(String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }
}
