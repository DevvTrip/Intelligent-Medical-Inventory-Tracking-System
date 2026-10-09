package com.mits.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * TCP Socket Client for connecting to the MITS InventoryServer.
 * Demonstrates:
 * - Unit 4: TCP Socket Programming (Socket, InputStream, OutputStream)
 * - Usable from ConsoleApp and from the GUI Remote Inventory tab.
 */
public class InventoryClient implements AutoCloseable {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private boolean connected = false;

    /**
     * Connects to the InventoryServer.
     */
    public synchronized String connect(String host, int port) throws IOException {
        if (connected) {
            disconnect();
        }
        this.socket = new Socket(host, port);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.connected = true;

        // Read server banner
        return in.readLine();
    }

    /**
     * Sends a raw command and reads response lines until an OK, ERROR, or completion token is reached.
     */
    public synchronized List<String> sendCommand(String command) throws IOException {
        if (!connected || socket == null || socket.isClosed()) {
            throw new IOException("Client is not connected to server.");
        }

        out.println(command);
        List<String> responseLines = new ArrayList<>();

        String firstLine = in.readLine();
        if (firstLine == null) {
            disconnect();
            throw new IOException("Server closed connection unexpectedly.");
        }
        responseLines.add(firstLine);

        // Multi-line responses like SEARCH and ALERTS return COUNT first and end with OK END_*
        if (firstLine.startsWith("COUNT ")) {
            String line;
            while ((line = in.readLine()) != null) {
                responseLines.add(line);
                if (line.startsWith("OK END_") || line.startsWith("ERROR")) {
                    break;
                }
            }
        }

        return responseLines;
    }

    public synchronized String login(String user, String pass) throws IOException {
        List<String> res = sendCommand("LOGIN " + user + " " + pass);
        return String.join("\n", res);
    }

    public synchronized List<String> search(String query) throws IOException {
        return sendCommand("SEARCH " + (query != null ? query : ""));
    }

    public synchronized String checkStock(String code) throws IOException {
        List<String> res = sendCommand("STOCK " + code);
        return String.join("\n", res);
    }

    public synchronized String issueStock(String code, int quantity) throws IOException {
        List<String> res = sendCommand("ISSUE " + code + " " + quantity);
        return String.join("\n", res);
    }

    public synchronized List<String> getAlerts() throws IOException {
        return sendCommand("ALERTS");
    }

    public synchronized void disconnect() {
        if (!connected) return;
        try {
            if (out != null) {
                out.println("QUIT");
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException ignored) {
        } finally {
            connected = false;
            socket = null;
            in = null;
            out = null;
        }
    }

    public boolean isConnected() {
        return connected && socket != null && !socket.isClosed();
    }

    @Override
    public void close() {
        disconnect();
    }
}
