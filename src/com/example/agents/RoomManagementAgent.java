package com.example.agents;

import com.example.Room;
import com.example.RoomLoader;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.io.IOException;
import java.util.List;

public class RoomManagementAgent extends Agent {
    private List<Room> rooms;

    @Override
    protected void setup() {
        System.out.println("RoomManagementAgent " + getAID().getName() + " is ready.");

        // Load rooms from JSON file
        try {
            rooms = RoomLoader.loadRooms("src/com/example/rooms.json");
            System.out.println("Rooms loaded successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to load rooms.");
        }

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    System.out.println("RoomManagementAgent processing booking request: " + msg.getContent());
                    // Handle booking logic and send confirmation message
                    String response = handleBookingRequest(msg.getContent());
                    ACLMessage reply = msg.createReply();
                    reply.setPerformative(ACLMessage.INFORM);
                    reply.setContent(response);
                    send(reply);
                    System.out.println("RoomManagementAgent sent confirmation: " + reply.getContent());
                } else {
                    block();
                }
            }
        });
    }

    private String handleBookingRequest(String content) {
        // Simplified booking logic for demonstration purposes
        for (Room room : rooms) {
            if (content.contains(room.getName())) {
                return "Booking confirmed for " + room.getName();
            }
        }
        return "Booking failed: Room not found";
    }

    @Override
    protected void takeDown() {
        System.out.println("RoomManagementAgent " + getAID().getName() + " terminating.");
    }
}
