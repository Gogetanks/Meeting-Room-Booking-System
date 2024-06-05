package com.example.agents;

import jade.core.Agent;

public class NotificationAgent extends Agent {
    @Override
    protected void setup() {
        System.out.println("NotificationAgent " + getAID().getName() + " is ready.");
        // Initialization code for NotificationAgent
    }

    @Override
    protected void takeDown() {
        System.out.println("NotificationAgent " + getAID().getName() + " terminating.");
    }
}
