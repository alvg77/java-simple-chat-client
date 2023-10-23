package org.chat_client.client;

import java.io.IOException;
import java.net.*;
import java.util.Optional;
import java.util.Scanner;

public class ConnectionInitiator {
    private Scanner scanner;
    public ConnectionInitiator() {
        scanner = new Scanner(System.in);
    }

    public void initiateConnection() {
        while (true) {
            try {
                Runtime.getRuntime().exec("clear");
            } catch (IOException ioe) {
                System.out.println("Failed to clear the screen");
            }

            System.out.println("Connect to the server with /connect <hostname>:<port>");
            System.out.println("Type /quit to exit the program.");
            String command = scanner.nextLine();
            String[] cmdArr = command.split("[ :]");

            if (cmdArr[0].equals("/connect") && cmdArr.length == 3) {
                Optional<Socket> socket = initSocket(cmdArr);
                if (socket.isPresent()) {
                    try {
                        Client client = new Client(socket.get());
                        client.start();
                    } catch (RuntimeException re) {
                        System.out.println(re.getMessage());
                    }
                }
            } else if (cmdArr[0].equals("/quit")) {
                System.exit(0);
            } else {
                System.out.println("Invalid command! Press any key to continue...");
                scanner.nextLine();
            }
        }
    }

    private Optional<Socket> initSocket(String[] cmdArr) {
        Socket socket = null;
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(cmdArr[1], Integer.parseInt(cmdArr[2])));
        }catch (ConnectException ce){
            System.out.println("Failed to connect to the server! Press any key to continue...");
            scanner.nextLine();
        } catch (UnknownHostException uhe) {
            System.out.println("Unknown host! Press any key to continue...");
            scanner.nextLine();
        } catch (SocketTimeoutException ste) {
            System.out.println("Connection timed out! Press any key to continue...");
            scanner.nextLine();
        } catch (SocketException se) {
            System.out.println(se.getMessage() + "! Press any key to continue...");
            scanner.nextLine();
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid port number! Press any key to continue...");
            scanner.nextLine();
        } catch (IllegalArgumentException iae) {
            System.out.println("Wrong connection data! Press any key to continue...");
            scanner.nextLine();
        }
        return Optional.ofNullable(socket);
    }
}
