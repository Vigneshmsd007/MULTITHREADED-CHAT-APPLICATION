import java.io.*;
import java.net.*;

public class client {
    private static final String SERVER_ADDRESS = "127.0.0.1"; // Localhost for testing
    private static final int PORT = 12345; // Port number for the chat server

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, PORT);
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Create a thread for receiving messages from the server
            Thread receiveMessages = new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = in.readLine()) != null) {
                        System.out.println(serverMessage);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            receiveMessages.start();

            // Allow the user to send messages to the server
            String userMessage;
            while ((userMessage = userInput.readLine()) != null) {
                out.println(userMessage); // Send message to server
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
