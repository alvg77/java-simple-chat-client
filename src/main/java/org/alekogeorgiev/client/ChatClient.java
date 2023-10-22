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

        while (true) {
            try {
                Runtime.getRuntime().exec("clear");
            } catch (IOException ioe) {
                System.out.println("Failed to clear the screen");
            }

            System.out.println("Connect to the server with /connect <hostname> <port>");
            System.out.println("Type /quit to exit the program.");
            String command = scanner.nextLine();
            String[] cmdArr = command.split(" ");

            if (cmdArr[0].equals("/connect") || cmdArr.length == 3) {
                try {
                    socket.connect(new InetSocketAddress(cmdArr[1], Integer.parseInt(cmdArr[2])));
                    chat();
                } catch (IOException ioe) {
                    if (ioe instanceof ConnectException) {
                        System.out.println("Failed to connect to the server! Press any key to continue...");
                        scanner.nextLine();
                    } else if (ioe instanceof UnknownHostException) {
                        System.out.println("Unknown host! Press any key to continue...");
                        scanner.nextLine();
                    } else {
                        throw new RuntimeException(ioe);
                    }
                } catch (NumberFormatException nfe) {
                    System.out.println("Invalid port number! Press any key to continue...");
                    scanner.nextLine();
                } catch (IllegalArgumentException iae) {
                    System.out.println("Wrong connection data! Press any key to continue...");
                    scanner.nextLine();
                }
            } else if (cmdArr[0].equals("/quit")) {
                System.exit(0);
            } else {
                System.out.println("Invalid command! Press any key to continue...");
                scanner.nextLine();
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
                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                            while (socket.isConnected() && !socket.isClosed()) {
                                String msg = reader.readLine();
                                if (msg == null) {
                                    System.out.println("Connection to the server has been terminated! Press any key to continue...");
                                    scanner.nextLine();
                                    break;
                                }
                                System.out.println(msg);
                            }
                        } catch (SocketException se) {
                            if(socket.isClosed() || !socket.isConnected()) {
                                System.out.println("Connection to the server has been terminated! Press any key to continue...");
                                scanner.nextLine();
                            } else {
                                throw new RuntimeException(se);
                            }
                        } catch (IOException ioe) {
                            throw new RuntimeException(ioe);
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
            outputStream.writeBytes(data + '\n');
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }
}
