package com.example;

import com.example.gui.MainApp;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import javafx.application.Application;

public class Main {
    public static void main(String[] args) {
        // Launch JADE runtime
        new Thread(() -> {
            Runtime rt = Runtime.instance();
            Profile p = new ProfileImpl();
            p.setParameter(Profile.MAIN_HOST, "localhost");
            p.setParameter(Profile.MAIN_PORT, "8000");
            AgentContainer mainContainer = rt.createMainContainer(p);

            try {
                AgentController userAgent = mainContainer.createNewAgent("UserAgent", "com.example.agents.UserAgent", null);
                userAgent.start();

                AgentController resourceManagementAgent = mainContainer.createNewAgent("ResourceManagementAgent", "com.example.agents.ResourceManagementAgent", null);
                resourceManagementAgent.start();

                AgentController roomManagementAgent = mainContainer.createNewAgent("RoomManagementAgent", "com.example.agents.RoomManagementAgent", null);
                roomManagementAgent.start();

                AgentController notificationAgent = mainContainer.createNewAgent("NotificationAgent", "com.example.agents.NotificationAgent", null);
                notificationAgent.start();

                MainApp.setUserAgentController(userAgent);
            } catch (StaleProxyException e) {
                e.printStackTrace();
            }
        }).start();

        // Launch JavaFX application
        Application.launch(MainApp.class, args);
    }
}
