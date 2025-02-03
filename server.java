import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class server {
    private static final int PORT = 12345; // Port number for the chat server
    private static Set<PrintWriter> clientWriters = new HashSet<>(); // A set to keep track of client output streams

    public static void main(String[] args) {
        System.out.println("Server is running...");
        ExecutorService pool = Executors.newFixedThreadPool(10); // Thread pool to handle client connections

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                new ClientHandler(serverSocket.accept(), pool).start(); // Accept new clients
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Broadcast a message to all clients
    public static synchronized void broadcastMessage(String message) {
        for (PrintWriter writer : clientWriters) {
            writer.println(message);
        }
    }

    // Handle individual client communication
    private static class ClientHandler extends Thread {
        private Socket clientSocket;
        private ExecutorService pool;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket, ExecutorService pool) {
            this.clientSocket = socket;
            this.pool = pool;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                synchronized (clientWriters) {
                    clientWriters.add(out); // Add this client to the writer set
                }

                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println("Received: " + message);
                    server.broadcastMessage(message); // Broadcast the message to all clients
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                synchronized (clientWriters) {
                    clientWriters.remove(out); // Remove this client from the writer set
                }
            }
        }
    }
}
