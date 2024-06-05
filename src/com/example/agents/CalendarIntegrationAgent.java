package com.example.agents;

import jade.core.Agent;

public class CalendarIntegrationAgent extends Agent {
    @Override
    protected void setup() {
        System.out.println("CalendarIntegrationAgent " + getAID().getName() + " is ready.");
        // Initialization code for CalendarIntegrationAgent
    }

    @Override
    protected void takeDown() {
        System.out.println("CalendarIntegrationAgent " + getAID().getName() + " terminating.");
    }
}
