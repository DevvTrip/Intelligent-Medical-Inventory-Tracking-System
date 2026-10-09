package com.mits.app;

import com.mits.service.InventoryService;
import com.mits.network.InventoryServer;
import com.mits.util.Constants;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

/**
 * Entry point for the TCP Inventory Server.
 * Parses optional port argument; default is 5000.
 */
public class ServerApp {
    public static void main(String[] args) {
        int port = Constants.DEFAULT_SERVER_PORT;

        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port provided, using default: " + port);
            }
        }

        InventoryService inventoryService = new InventoryService();
        InventoryServer server = new InventoryServer(inventoryService, port);

        try {
            server.start();
            System.out.println("MITS Server running on port " + port + ". Press Enter to stop.");

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            reader.readLine();
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        } finally {
            server.stop();
            System.out.println("Server stopped.");
        }
    }
}
