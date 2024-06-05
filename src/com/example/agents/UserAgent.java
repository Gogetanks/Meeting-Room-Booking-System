package com.example.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

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
}
