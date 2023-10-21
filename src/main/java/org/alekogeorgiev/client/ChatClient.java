package org.alekogeorgiev.client;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatClient {
    private Scanner scanner;
    private Socket socket;
    private String userUsername;
    public ChatClient() {
        this.scanner = new Scanner(System.in);
        this.socket = new Socket();
        this.userUsername = "";
    }
    public void connect() {
        System.out.println("Connect to the server with /connect <hostname> <port>");

        while (true) {
            String command = scanner.nextLine();
            String[] cmdArr = command.split(" ");

            if (!cmdArr[0].equals("/connect") || cmdArr.length != 3) {
                System.out.println("Invalid command!");
                continue;
            }

            try {
                socket.connect(new InetSocketAddress(cmdArr[1], Integer.parseInt(cmdArr[2])));
                chat();
            } catch (IOException ioe) {
                throw new RuntimeException(ioe);
            } catch (NumberFormatException nfe) {
                System.out.println("Invalid port number!");
            }
        }
    }
    public void chat() {
        try {
            System.out.println("Enter your username with /username");
            System.out.println("Enter your message with /msg");
            System.out.println("Exit with /exit");

            Thread receiveThread = new Thread(
                    () -> {
                        try (var buff = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                            String line;
                            while ((line = buff.readLine()) != null) {
                                System.out.println(line);
                            }
                        } catch (IOException ioe) {
                            if(ioe instanceof SocketException && (socket.isClosed() || !socket.isConnected())) {
                                System.out.println("Connection to the server has been terminated!");
                            } else {
                                throw new RuntimeException(ioe);
                            }
                        }
                    }
            );
            receiveThread.start();

            while (true) {
                String msg = scanner.nextLine();

                String[] msgArr = msg.split(" ", 2);
                switch (msgArr[0]) {
                    case "/username":
                        userUsername = msgArr[1];
                        break;
                    case "/msg":
                        if (userUsername.isEmpty()) {
                            System.out.println("-----------------!!Specify username first!!-----------------");
                            break;
                        }
                        sendMessage(userUsername + ": " + msgArr[1]);
                        break;
                    case "/exit":
                        socket.close();
                        receiveThread.interrupt();
                        return;
                    default:
                        System.out.println("Invalid command!");
                        break;
                }
            }
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    private void sendMessage(String data) {
        try {
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeBytes(data);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }
}
