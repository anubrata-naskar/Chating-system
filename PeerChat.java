import java.io.*;
import java.net.*;
import java.util.Scanner;

public class PeerChat {
    public static void main(String[] args) {
        final int port = 8888;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Peer Chat is running and waiting for connections on port " + port);

            // Accept incoming connections in a separate thread
            new Thread(() -> {
                while (true) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        new Thread(() -> handleClient(clientSocket)).start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            // Allow the user to send messages
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print("Enter message (or 'exit' to quit): ");
                String message = scanner.nextLine();

                if ("exit".equalsIgnoreCase(message)) {
                    break;
                }

                System.out.print("Enter peer's IP address: ");
                String peerIP = scanner.nextLine();

                // Send the message to the specified peer
                sendMessage(peerIP, port, message);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        ) {
            String peerMessage;
            while ((peerMessage = reader.readLine()) != null) {
                System.out.println("Received from " + clientSocket.getInetAddress() + ": " + peerMessage);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendMessage(String peerIP, int peerPort, String message) {
        try (
            Socket socket = new Socket(peerIP, peerPort);
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)
        ) {
            // Send the message to the specified peer
            writer.println(message);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
