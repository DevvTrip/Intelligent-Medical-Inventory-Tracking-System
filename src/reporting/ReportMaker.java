package reporting;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import businesslogic.Inventory;
import model.Medicine;
import utils.SortUtil;

public class ReportMaker {

    public static String stockReport(Inventory inventory) {
        StringBuilder sb = new StringBuilder();
        sb.append("STOCK REPORT\n\n");
        double totalValue = 0;
        int count = 0;
        for (Medicine m : inventory.getMedicines()) {
            sb.append(m.getReportLine()).append("\n");
            sb.append("   ").append(m.getStorageInfo()).append("\n");
            totalValue = totalValue + m.getQty() * m.calculatePrice();
            count++;
        }
        sb.append("\nTotal medicines: ").append(count);
        sb.append("\nTotal stock value: Rs ").append(String.format("%.2f", totalValue));
        return sb.toString();
    }

    public static String expiryReport(Inventory inventory) {
        StringBuilder sb = new StringBuilder();
        sb.append("EXPIRY REPORT (nearest date first)\n\n");
        ArrayList<Medicine> list = inventory.getByExpiryOrder();
        for (Medicine m : list) {
            long days = ChronoUnit.DAYS.between(LocalDate.now(), m.getExpiryDate());
            sb.append(m.getReportLine()).append(" | days left ").append(days).append("\n");
        }
        return sb.toString();
    }

    public static String reorderReport(Inventory inventory) {
        StringBuffer sb = new StringBuffer();
        sb.append("REORDER REPORT\n\n");
        ArrayList<Medicine> lowList = SortUtil.filterLowStock(inventory.getMedicines());
        if (lowList.isEmpty()) {
            sb.append("Nothing to reorder");
        }
        for (Medicine m : lowList) {
            // itna order karo ki stock low-stock limit ka double ho jaye
            int qtyToOrder = m.getThreshold() * 2 - m.getQty();
            sb.append(m.getName()).append(" | qty ").append(m.getQty());
            sb.append(" | limit ").append(m.getThreshold());
            sb.append(" | order ").append(qtyToOrder).append("\n");
        }
        return sb.toString();
    }
}
