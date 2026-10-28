package gui;

import java.io.IOException;
import javax.swing.SwingUtilities;
import businesslogic.Inventory;
import dataprocessing.SampleData;
import model.Hospital;

public class Main {
    public static void main(String[] args) {
        Hospital hospital = new Hospital("NIET Hospital");
        final Inventory inventory = hospital.getInventory();
        try {
            inventory.initialize(SampleData.getMedicines());
            System.out.println(hospital.getName() + " loaded medicines: "
                    + inventory.getMedicines().size());
        } catch (IOException e) {
            System.out.println("Data file problem: " + e.getMessage());
            return;
        }
        if (args.length > 0 && args[0].equals("--console")) {
            ConsoleMenu.start(inventory);
        } else {
            Ui.look();
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    new LoginScreen(inventory).setVisible(true);
                }
            });
        }
    }
}
