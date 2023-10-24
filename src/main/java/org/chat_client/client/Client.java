package org.chat_client.client;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.logging.Logger;

public class Client {
    private Socket socket;
    private String username;
    private BufferedReader input;
    private DataOutputStream output;
    private Thread receiveThread = new Thread(() -> {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String data;
            while (!socket.isClosed() && (data = bufferedReader.readLine()) != null) {
                System.out.println(data);
            }
        } catch (IOException ioe) {
            System.out.println("You have been disconnected from the server.");
            close();
        }
    });

    public Client(Socket socket) {
        if (socket == null) {
            throw new IllegalArgumentException("Socket cannot be null");
        }
        this.socket = socket;
        this.username = "";
        try {
            this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.output = new DataOutputStream(socket.getOutputStream());
        } catch (IOException ioe) {
            close();
            throw new RuntimeException("Error while initiating client io: " + ioe.getMessage());
        }
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter your username with /nick");
        System.out.println("Enter your message with /msg");
        System.out.println("Exit with /quit");

        receiveThread.start();

        while (!socket.isClosed()) {
            String msg = scanner.nextLine();
            String[] msgArr = msg.split(" ", 2);
            parseMsgType(msgArr);
            if (!receiveThread.isAlive()) {
                System.out.println("You have been disconnected from the server.");
                close();
            }
        }
    }

    private void parseMsgType(String[] msgArr) {
        switch (msgArr[0]) {
            case "/nick":
                username = msgArr[1];
                break;
            case "/msg":
                if (username.isEmpty()) {
                    System.out.println("-----------------!!Specify username first!!-----------------");
                    break;
                }
                sendMessage(username + ": " + msgArr[1]);
                break;
            case "/quit":
                close();
                return;
            default:
                System.out.println("Invalid command!");
                break;
        }
    }

    private void sendMessage(String data) {
        try {
            output.writeBytes(data + '\n');
        } catch (IOException ioe) {
            close();
            throw new RuntimeException("Error while sending message: " + ioe.getMessage());
        }
    }

    private void close() {
        try {
            receiveThread.interrupt();
            socket.close();
            if (input != null) {
                input.close();
            }
            if (output != null) {
                output.close();
            }
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }
}
