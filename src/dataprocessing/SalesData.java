package dataprocessing;

public class SalesData {

    private int[][] weeklySales = new int[4][];

    public SalesData() {
        weeklySales[0] = new int[] {12, 8, 15};
        weeklySales[1] = new int[] {6, 4};
        weeklySales[2] = new int[] {3, 5, 2, 4};
        weeklySales[3] = new int[] {1};
    }

    public int getCategoryTotal(int category) {
        int total = 0;
        for (int i = 0; i < weeklySales[category].length; i++) {
            total = total + weeklySales[category][i];
        }
        return total;
    }

    public int getGrandTotal() {
        int total = 0;
        for (int i = 0; i < weeklySales.length; i++) {
            total = total + getCategoryTotal(i);
        }
        return total;
    }

    public String getReport() {
        String[] names = {"Tablet", "Syrup", "Injection", "Other"};
        StringBuilder sb = new StringBuilder();
        sb.append("WEEKLY SALES SAMPLE (jagged array)\n\n");
        for (int i = 0; i < weeklySales.length; i++) {
            sb.append(names[i]);
            sb.append(" | days ").append(weeklySales[i].length);
            sb.append(" | total ").append(getCategoryTotal(i)).append("\n");
        }
        sb.append("\nGrand total: ").append(getGrandTotal());
        return sb.toString();
    }
}
