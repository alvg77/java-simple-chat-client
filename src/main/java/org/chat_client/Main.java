package org.chat_client;

import org.chat_client.client.ChatClient;

public class Main {
    public static void main(String[] args) {
        ChatClient client = new ChatClient();
        client.connect();
    }
}