package com.example.agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class ResourceManagementAgent extends Agent {
    @Override
    protected void setup() {
        System.out.println("RoomManagementAgent " + getAID().getName() + " is ready.");
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    System.out.println("ResourceManagementAgent received: " + msg.getContent());

                } else {
                    block();
                }
            }
        });
    }

}
