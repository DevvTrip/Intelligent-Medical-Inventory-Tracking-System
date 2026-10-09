package com.mits.network;

import com.mits.exception.AuthenticationException;
import com.mits.exception.ExpiredMedicineException;
import com.mits.exception.InsufficientStockException;
import com.mits.exception.MedicineNotFoundException;
import com.mits.model.Alert;
import com.mits.model.Medicine;
import com.mits.model.User;
import com.mits.service.InventoryService;
import com.mits.util.Constants;
import com.mits.util.StringProcessor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Multi-client TCP Server providing remote inventory access over sockets.
 * Demonstrates:
 * - Unit 4: TCP Socket Programming (ServerSocket, Socket)
 * - Unit 4: Multi-threaded client handling (Thread pool / Runnable)
 * - Thread-safe synchronization on shared InventoryService
 */
public class InventoryServer {
    private final InventoryService inventoryService;
    private final int port;
    private ServerSocket serverSocket;
    private final ExecutorService clientPool;
    private final AtomicBoolean running = new AtomicBoolean(false);

    public InventoryServer(InventoryService inventoryService, int port) {
        this.inventoryService = inventoryService;
        this.port = port > 0 ? port : Constants.DEFAULT_SERVER_PORT;
        this.clientPool = Executors.newCachedThreadPool();
    }

    public InventoryServer(InventoryService inventoryService) {
        this(inventoryService, Constants.DEFAULT_SERVER_PORT);
    }

    /**
     * Starts the TCP Server in a background listener thread.
     */
    public synchronized void start() throws IOException {
        if (running.get()) return;
        serverSocket = new ServerSocket(port);
        running.set(true);
        StringProcessor.logActivity("InventoryServer started on port: " + port);

        Thread listenerThread = new Thread(() -> {
            while (running.get() && !serverSocket.isClosed()) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    StringProcessor.logActivity("Accepted TCP client from: " + clientSocket.getRemoteSocketAddress());
                    clientPool.execute(new ClientHandler(clientSocket));
                } catch (IOException e) {
                    if (!running.get()) {
                        break; // Expected when server is stopped
                    }
                    System.err.println("Server accept error: " + e.getMessage());
                }
            }
        }, "Server-Listener-Thread");
        listenerThread.start();
    }

    /**
     * Stops the TCP Server and shuts down worker threads.
     */
    public synchronized void stop() {
        if (!running.get()) return;
        running.set(false);
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing server socket: " + e.getMessage());
        }
        clientPool.shutdownNow();
        StringProcessor.logActivity("InventoryServer stopped.");
    }

    public boolean isRunning() {
        return running.get();
    }

    public int getPort() {
        return port;
    }

    /**
     * Handles an individual client connection session.
     */
    private class ClientHandler implements Runnable {
        private final Socket socket;
        private User authenticatedUser = null;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                out.println("200 MITS Inventory Server Ready. Available commands: LOGIN, SEARCH, STOCK, ISSUE, ALERTS, QUIT");

                String line;
                while ((line = in.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty()) continue;
                    StringProcessor.logActivity("TCP Command from " + socket.getRemoteSocketAddress() + ": " + line);

                    String[] parts = line.split("\\s+", 3);
                    String command = parts[0].toUpperCase();

                    if ("QUIT".equals(command)) {
                        out.println("200 Bye. Connection closing.");
                        break;
                    } else if ("LOGIN".equals(command)) {
                        handleLogin(parts, out);
                    } else if ("SEARCH".equals(command)) {
                        handleSearch(parts, out);
                    } else if ("STOCK".equals(command)) {
                        handleStock(parts, out);
                    } else if ("ISSUE".equals(command)) {
                        handleIssue(parts, out);
                    } else if ("ALERTS".equals(command)) {
                        handleAlerts(out);
                    } else {
                        out.println("ERROR Unknown command: " + command + ". Use LOGIN, SEARCH, STOCK, ISSUE, ALERTS, QUIT");
                    }
                }
            } catch (IOException e) {
                // Client disconnected
            } finally {
                try {
                    socket.close();
                } catch (IOException ignored) {}
                StringProcessor.logActivity("Client session ended: " + socket.getRemoteSocketAddress());
            }
        }

        private void handleLogin(String[] parts, PrintWriter out) {
            if (parts.length < 3) {
                out.println("ERROR Syntax: LOGIN <username> <password>");
                return;
            }
            try {
                authenticatedUser = inventoryService.authenticate(parts[1], parts[2]);
                out.println("OK Welcome " + authenticatedUser.getFullName() + " (" + authenticatedUser.getRole() + ")");
            } catch (AuthenticationException e) {
                out.println("ERROR " + e.getMessage());
            }
        }

        private void handleSearch(String[] parts, PrintWriter out) {
            String query = (parts.length >= 2) ? parts[1] : "";
            List<Medicine> results = inventoryService.search(query);
            out.println("COUNT " + results.size());
            for (Medicine m : results) {
                out.printf("DATA %s | %s | %s | Stock: %d | ₹%.2f%n",
                        m.getCode(), m.getName(), m.getCategory(), m.getTotalStock(), m.getUnitPrice());
            }
            out.println("OK END_SEARCH");
        }

        private void handleStock(String[] parts, PrintWriter out) {
            if (parts.length < 2) {
                out.println("ERROR Syntax: STOCK <medicine_code>");
                return;
            }
            try {
                Medicine m = inventoryService.getMedicine(parts[1]);
                out.printf("OK CODE: %s | NAME: %s | CATEGORY: %s | STOCK: %d | PRICE: ₹%.2f | EXPIRY: %s | REORDER: %d%n",
                        m.getCode(), m.getName(), m.getCategory(), m.getTotalStock(), m.getUnitPrice(),
                        m.getExpiryDate(), m.getReorderLevel());
            } catch (MedicineNotFoundException e) {
                out.println("ERROR " + e.getMessage());
            }
        }

        private void handleIssue(String[] parts, PrintWriter out) {
            if (authenticatedUser == null) {
                out.println("ERROR Please LOGIN first before issuing stock.");
                return;
            }
            if (!authenticatedUser.canManageStock()) {
                out.println("ERROR Access Denied. Role " + authenticatedUser.getRole() + " is not authorized to issue stock.");
                return;
            }
            if (parts.length < 3) {
                out.println("ERROR Syntax: ISSUE <medicine_code> <quantity>");
                return;
            }
            try {
                String code = parts[1];
                int qty = Integer.parseInt(parts[2]);
                inventoryService.issueStock(code, qty, authenticatedUser.getUsername(), "TCP Network Issue");
                Medicine m = inventoryService.getMedicine(code);
                out.printf("OK Successfully issued %d units of %s (%s). Remaining stock: %d%n",
                        qty, m.getName(), code, m.getTotalStock());
            } catch (NumberFormatException e) {
                out.println("ERROR Invalid quantity integer: " + parts[2]);
            } catch (MedicineNotFoundException | InsufficientStockException | ExpiredMedicineException e) {
                out.println("ERROR " + e.getMessage());
            }
        }

        private void handleAlerts(PrintWriter out) {
            List<Alert> alerts = inventoryService.getActiveAlerts();
            out.println("COUNT " + alerts.size());
            for (Alert a : alerts) {
                out.printf("DATA [%s] [%s] %s: %s (%s)%n",
                        a.getTimestamp(), a.getSeverity(), a.getType(), a.getMedicineName(), a.getMessage());
            }
            out.println("OK END_ALERTS");
        }
    }
}
