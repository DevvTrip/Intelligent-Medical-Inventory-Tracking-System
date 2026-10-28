package utils;

import java.util.ArrayList;
import java.util.List;
import model.Medicine;

public class SortUtil {

    public static void sortByExpiry(ArrayList<Medicine> list) {
        list.sort((m1, m2) -> m1.getExpiryDate().compareTo(m2.getExpiryDate()));
    }

    public static ArrayList<Medicine> filterLowStock(ArrayList<Medicine> list) {
        ArrayList<Medicine> low = new ArrayList<Medicine>();
        for (Medicine m : list) {
            if (m.isLowStock()) {
                low.add(m);
            }
        }
        return low;
    }

    public static <T> void printAll(List<T> list) {
        for (T item : list) {
            System.out.println(item);
        }
    }

    public static void printReportLines(ArrayList<Medicine> list) {
        for (Medicine m : list) {
            System.out.println(m.getReportLine());
        }
    }
}
