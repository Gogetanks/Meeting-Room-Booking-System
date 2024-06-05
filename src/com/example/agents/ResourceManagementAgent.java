package com.example.agents;

import com.example.gui.ResourceRequestApp;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class ResourceManagementAgent extends Agent {
    @Override
    protected void setup() {
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    System.out.println("ResourceManagementAgent received: " + msg.getContent());
                    launchResourceGUI(msg.getContent());
                } else {
                    block();
                }
            }
        });
    }

    private void launchResourceGUI(String content) {
        ResourceRequestApp.launchResourceGUI(content);
    }
}
