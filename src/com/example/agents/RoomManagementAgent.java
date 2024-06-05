package com.example.agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class RoomManagementAgent extends Agent {
    @Override
    protected void setup() {
        System.out.println("RoomManagementAgent " + getAID().getName() + " is ready.");

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {

                ACLMessage msg = receive();
                if (msg != null) {
                    System.out.println("RoomManagementAgent processing booking request: " + msg.getContent());
                    // Handle booking logic and send confirmation message
                    ACLMessage reply = msg.createReply();
                    reply.setPerformative(ACLMessage.INFORM);
                    reply.setContent("Booking confirmed: " + msg.getContent());
                    send(reply);
                    System.out.println("RoomManagementAgent sent confirmation: " + reply.getContent());
                } else {
                    block();
                }
            }
        });
    }

    @Override
    protected void takeDown() {
        System.out.println("RoomManagementAgent " + getAID().getName() + " terminating.");
    }
}
