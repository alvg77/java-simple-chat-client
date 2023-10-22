package org.chat_client;

import org.chat_client.client.ChatClient;

public class Main {
    public static void main(String[] args) {
        ChatClient client = new ChatClient();
        try {
            client.connect();
        } catch (Exception e) {
            System.err.println("\u001B[31m" + e.getMessage() + "\u001B[0m");
        }
    }
}