import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class Worker {
    private static final int PORT = Integer.parseInt(System.getProperty("worker.port")); // Get port dynamically
    private static final Map<String, String> cache = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Worker listening on port: " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getRemoteSocketAddress());
                
                // Handle each client in a separate thread
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String request;
            while ((request = in.readLine()) != null) { // Keep listening for multiple commands
                System.out.println("Received: " + request);
                
                String response = processCommand(request);
                out.println(response);
            }

            System.out.println("Client disconnected: " + clientSocket.getRemoteSocketAddress());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String processCommand(String request) {
        String[] parts = request.split(" ", 3);

        if (parts.length < 2) {
            return "ERROR: Invalid command";
        }

        String command = parts[0].toUpperCase();
        String key = parts[1];

        switch (command) {
            case "PUT":
                if (parts.length < 3) return "ERROR: PUT requires key and value";
                cache.put(key, parts[2]);
                return "OK";

            case "GET":
                return cache.getOrDefault(key, "NULL");

            case "DEL":
                cache.remove(key);
                return "OK";

            default:
                return "ERROR: Invalid command";
        }
    }
}
