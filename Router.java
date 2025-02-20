import java.io.*;
import java.net.*;
import java.util.*;

public class Router {
    private static final int[] WORKER_PORTS = {3000, 3001, 3002, 3003}; // Worker ports
    private static final Map<Integer, Socket> workerSockets = new HashMap<>();
    private static final Map<Integer, PrintWriter> workerWriters = new HashMap<>();
    private static final Map<Integer, BufferedReader> workerReaders = new HashMap<>();

    public static void main(String[] args) {
        try {
            // sleep thread for 2 second to allow workers to start
            Thread.sleep(2000);
            // Create persistent connections to all workers
            for (int port : WORKER_PORTS) {
                Socket socket = new Socket("localhost", port);
                workerSockets.put(port, socket);
                workerWriters.put(port, new PrintWriter(socket.getOutputStream(), true));
                workerReaders.put(port, new BufferedReader(new InputStreamReader(socket.getInputStream())));
                System.out.println("Connected to Worker on port: " + port);
            }

            Scanner scanner = new Scanner(System.in);
            System.out.println("Router started. Enter commands (PUT key value | GET key | DEL key):");

            while (scanner.hasNextLine()) {
                String command = scanner.nextLine().trim();
                if (command.isEmpty()) continue;

                String[] parts = command.split(" ", 3);
                if (parts.length < 2) {
                    System.out.println("Invalid command format.");
                    continue;
                }

                // Hashing the key to determine the worker
                int workerIndex = Math.abs(parts[1].hashCode()) % WORKER_PORTS.length;
                int workerPort = WORKER_PORTS[workerIndex];

                // Get writer & reader for the worker
                PrintWriter workerOut = workerWriters.get(workerPort);
                BufferedReader workerIn = workerReaders.get(workerPort);

                // Send request
                workerOut.println(command);

                // Read response
                String response = workerIn.readLine();
                System.out.println("Response from Worker[" + workerIndex + "]: " + response);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
    }

    private static void closeConnections() {
        workerSockets.values().forEach(socket -> {
            try {
                socket.close();
            } catch (IOException ignored) {}
        });
    }
}
