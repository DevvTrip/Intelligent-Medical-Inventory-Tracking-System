package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import businesslogic.Inventory;
import dataprocessing.SampleData;

public class InventoryServer {
    private static final int PORT = 5555;

    public static void main(String[] args) {
        Inventory inventory = new Inventory();
        try {
            inventory.initialize(SampleData.getMedicines());
            System.out.println("Data loaded, medicines: " + inventory.getMedicines().size());
        } catch (IOException e) {
            System.out.println("Could not load data: " + e.getMessage());
            return;
        }
        try (ServerSocket server = new ServerSocket(PORT, 50, InetAddress.getByName("localhost"))) {
            System.out.println("Central Inventory Server started on port " + PORT);
            while (true) {
                handle(server.accept(), inventory);
            }
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }

    private static void handle(Socket socket, Inventory inventory) {
        try (Socket connection = socket;
                BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                PrintWriter output = new PrintWriter(connection.getOutputStream(), true)) {
            connection.setSoTimeout(5000);
            String request = input.readLine();
            output.println(process(request, inventory));
        } catch (IOException e) {
            System.out.println("Connection problem: " + e.getMessage());
        }
    }

    private static String process(String request, Inventory inventory) {
        try {
            if (request == null) {
                return "ERROR empty request";
            }
            String[] parts = request.trim().split("\\s+");
            if (parts.length != 3 || !parts[0].equalsIgnoreCase("SELL")) {
                return "ERROR use SELL <id> <qty>";
            }
            int remaining = inventory.getStockService().sellMedicine(parts[1], Integer.parseInt(parts[2]));
            return "OK " + remaining;
        } catch (Exception e) {
            return "ERROR " + e.getMessage();
        }
    }
}
