package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class CounterClient {

    private static final String SERVER = "localhost";
    private static final int PORT = 5555;

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java -cp out network.CounterClient <id> <qty>");
            return;
        }
        try {
            int qty = Integer.parseInt(args[1]);
            System.out.println(sendSell(args[0], qty));
        } catch (NumberFormatException e) {
            System.out.println("ERROR quantity must be a number");
        }
    }

    public static String sendSell(String id, int qty) {
        if (id.trim().isEmpty() || id.contains(" ") || id.contains("\n")
                || id.contains("\r") || id.contains("\t") || qty <= 0) {
            return "ERROR enter a valid id and positive quantity";
        }
        Socket socket = null;
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            socket = new Socket(SERVER, PORT);
            socket.setSoTimeout(5000);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println("SELL " + id + " " + qty);
            return in.readLine();
        } catch (IOException e) {
            return "ERROR server not available";
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                System.out.println("Close problem");
            }
        }
    }
}
