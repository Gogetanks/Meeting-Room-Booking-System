package com.example.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class UserAgent extends Agent {
    @Override
    protected void setup() {
        setEnabledO2ACommunication(true, 0);
        System.out.println("UserAgent is ready.");

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = (ACLMessage) getO2AObject();
                if (msg != null) {
                    String content = msg.getContent();
                    System.out.println("UserAgent received content: " + content);
                    forwardBookingDetails(content);
                    sendNotificationRequest(content);
                } else {
                    block();
                }
            }
        });
    }

    private void forwardBookingDetails(String content) {
        ACLMessage forwardMsg = new ACLMessage(ACLMessage.INFORM);
        forwardMsg.addReceiver(new AID("ResourceManagementAgent", AID.ISLOCALNAME));
        forwardMsg.setContent(content);
        send(forwardMsg);
    }

    private void sendNotificationRequest(String content) {
        System.out.println("Processing content for notification: " + content);

        try {
            // Example content: "Booking confirmed for room Lublin on 2024-06-23 from 19:45 to 22:00 with capacity 1-2."
            String[] parts = content.split(" for room | on | from | to | with capacity ");

            if (parts.length < 6) {
                throw new IllegalArgumentException("Content format is incorrect. Received: " + content);
            }

            String roomName = parts[1]; // "Lublin"
            String dateStr = parts[2]; // "2024-06-23"
            String startTimeStr = parts[3]; // "19:45"
            String endTimeStr = parts[4]; // "22:00"

            LocalDate date = LocalDate.parse(dateStr);
            LocalTime startTime = LocalTime.parse(startTimeStr, DateTimeFormatter.ofPattern("HH:mm"));
            LocalTime notificationTime = startTime.minusHours(1);

            String notificationTimeStr = notificationTime.format(DateTimeFormatter.ofPattern("HH:mm"));

            String notificationContent = "Reminder: Meeting in room " + roomName + " today at " + notificationTimeStr;

            ACLMessage notificationMsg = new ACLMessage(ACLMessage.INFORM);
            notificationMsg.addReceiver(new AID("NotificationAgent", AID.ISLOCALNAME));
            notificationMsg.setContent(notificationContent);
            send(notificationMsg);

            System.out.println("Notification scheduled for: " + notificationContent);
        } catch (DateTimeParseException e) {
            System.err.println("Failed to parse date or time: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error processing booking content: " + e.getMessage());
        }
    }


}
