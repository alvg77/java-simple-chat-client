package org.alekogeorgiev;

import org.alekogeorgiev.client.ChatClient;

public class Main {
    public static void main(String[] args) {
        ChatClient client = new ChatClient();
        client.connect();
    }
}