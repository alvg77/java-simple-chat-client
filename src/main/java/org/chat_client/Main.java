package org.chat_client;

import org.chat_client.client.Client;
import org.chat_client.client.ConnectionInitiator;

public class Main {
    public static void main(String[] args) {
        ConnectionInitiator connectionInitiator = new ConnectionInitiator();
        connectionInitiator.initiateConnection();
    }
}