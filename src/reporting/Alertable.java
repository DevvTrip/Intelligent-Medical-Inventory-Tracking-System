package reporting;

public interface Alertable {
    boolean isLowStock();
    boolean isExpired();
    boolean isExpiringSoon();
    String getStatus();
}
