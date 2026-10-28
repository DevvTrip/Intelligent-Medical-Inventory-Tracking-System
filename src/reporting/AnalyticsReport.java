package reporting;

import java.util.ArrayList;
import java.util.HashMap;
import businesslogic.Inventory;
import model.Medicine;
import model.Supplier;

public class AnalyticsReport {

    public static String fastMovingReport(Inventory inventory) {
        StringBuffer sb = new StringBuffer();
        sb.append("FAST MOVING MEDICINES\n\n");
        HashMap<String, Integer> sold = inventory.getStockService().getSoldCount();
        ArrayList<String> ids = new ArrayList<String>(sold.keySet());
        ids.sort((id1, id2) -> sold.get(id2) - sold.get(id1));
        if (ids.isEmpty()) {
            sb.append("No sales yet");
        }
        for (String id : ids) {
            Medicine m = inventory.getById(id);
            String name = "removed medicine";
            if (m != null) {
                name = m.getName();
            }
            sb.append(name).append(" | sold ").append(sold.get(id)).append("\n");
        }
        return sb.toString();
    }

    public static String supplierReport(Inventory inventory) {
        StringBuffer sb = new StringBuffer();
        sb.append("SUPPLIER LIST\n\n");
        for (Supplier s : inventory.getSupplierList()) {
            sb.append(s.toString()).append("\n");
        }
        return sb.toString();
    }

    public static String activityReport(Inventory inventory) {
        StringBuffer sb = new StringBuffer();
        sb.append("RECENT ACTIVITY\n\n");
        for (String line : inventory.getActivityLog()) {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }
}
