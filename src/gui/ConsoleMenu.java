package gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import businesslogic.ExpiredMedicineException;
import businesslogic.Inventory;
import businesslogic.InvalidInputException;
import businesslogic.OutOfStockException;
import model.Medicine;
import utils.SortUtil;

public class ConsoleMenu {

    public static void start(Inventory inventory) {
        Scanner sc = new Scanner(System.in);
        boolean running = true;
        while (running) {
            System.out.println("\n1. List medicines");
            System.out.println("2. Search by name");
            System.out.println("3. Sell medicine");
            System.out.println("4. Low stock list");
            System.out.println("5. Exit");
            System.out.print("Choice: ");
            if (!sc.hasNextLine()) {
                break;
            }
            String choice = sc.nextLine();
            if (!choice.equals("5")) {
                try {
                    inventory.refresh();
                } catch (IOException e) {
                    System.out.println("Could not read stock: " + e.getMessage());
                    continue;
                }
            }
            if (choice.equals("1")) {
                SortUtil.printAll(inventory.getMedicines());
            } else if (choice.equals("2")) {
                search(sc, inventory);
            } else if (choice.equals("3")) {
                sell(sc, inventory);
            } else if (choice.equals("4")) {
                lowStock(inventory);
            } else if (choice.equals("5")) {
                running = false;
            } else {
                System.out.println("Wrong choice");
            }
        }
        System.out.println("Bye");
    }

    private static void search(Scanner sc, Inventory inventory) {
        System.out.print("Enter name: ");
        String word = sc.nextLine();
        ArrayList<Medicine> found = inventory.searchMedicine(word);
        if (found.isEmpty()) {
            System.out.println("No match");
        }
        SortUtil.printReportLines(found);
    }

    private static void sell(Scanner sc, Inventory inventory) {
        try {
            System.out.print("Medicine id: ");
            String id = sc.nextLine();
            System.out.print("Quantity: ");
            int qty = Integer.parseInt(sc.nextLine());
            inventory.getStockService().sellMedicine(id, qty);
            System.out.println("Stock updated");
        } catch (NumberFormatException e) {
            System.out.println("Quantity must be a number");
        } catch (OutOfStockException e) {
            System.out.println(e.getMessage());
        } catch (ExpiredMedicineException e) {
            System.out.println(e.getMessage());
        } catch (InvalidInputException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println("Could not save stock: " + e.getMessage());
        }
    }

    private static void lowStock(Inventory inventory) {
        ArrayList<Medicine> low = SortUtil.filterLowStock(inventory.getMedicines());
        System.out.println("Low stock medicines: " + low.size());
        for (Medicine m : low) {
            System.out.println(m.getReportLine());
        }
    }
}
