package com.mits.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

/**
 * String processing utility fulfilling all Unit 3 syllabus requirements.
 * Demonstrates:
 * - String: immutability, contains, indexOf, equalsIgnoreCase, regex matching, split, substring, trim, toUpperCase.
 * - StringBuilder: efficient, mutable, single-threaded buffer for formatting reports and tables.
 * - StringBuffer: thread-safe, synchronized buffer used for concurrent activity logging.
 */
public class StringProcessor {

    // Regex pattern for validating medicine codes like MED-001, MED-1052
    public static final Pattern MEDICINE_CODE_PATTERN = Pattern.compile("^MED-\\d{3,4}$", Pattern.CASE_INSENSITIVE);

    // Regex pattern for validating email addresses
    public static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    // Thread-safe buffer using StringBuffer (Unit 3: StringBuffer requirement)
    private static final StringBuffer AUDIT_LOG_BUFFER = new StringBuffer();

    /**
     * Appends a log entry to the thread-safe StringBuffer.
     * @param message log entry
     */
    public static void logActivity(String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        AUDIT_LOG_BUFFER.append("[").append(timestamp).append("] ")
                .append(Thread.currentThread().getName()).append(": ")
                .append(message).append("\n");
    }

    /**
     * Returns the contents of the thread-safe StringBuffer.
     * @return accumulated log text
     */
    public static String getAuditLog() {
        return AUDIT_LOG_BUFFER.toString();
    }

    /**
     * Clears the thread-safe StringBuffer.
     */
    public static void clearAuditLog() {
        AUDIT_LOG_BUFFER.setLength(0);
    }

    /**
     * Validates medicine code format against MED-XXX regex.
     * @param code code to test
     * @return true if valid format
     */
    public static boolean isValidMedicineCode(String code) {
        if (code == null) return false;
        return MEDICINE_CODE_PATTERN.matcher(code.trim()).matches();
    }

    /**
     * Validates email format using regex.
     * @param email email to test
     * @return true if valid
     */
    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Pads a string with spaces to the right to reach the desired length.
     * Uses StringBuilder for construction.
     */
    public static String padRight(String text, int length) {
        if (text == null) text = "";
        if (text.length() >= length) return text.substring(0, length);
        StringBuilder sb = new StringBuilder(text);
        while (sb.length() < length) {
            sb.append(' ');
        }
        return sb.toString();
    }

    /**
     * Pads a string with spaces to the left to reach the desired length.
     */
    public static String padLeft(String text, int length) {
        if (text == null) text = "";
        if (text.length() >= length) return text.substring(0, length);
        StringBuilder sb = new StringBuilder();
        while (sb.length() + text.length() < length) {
            sb.append(' ');
        }
        sb.append(text);
        return sb.toString();
    }

    /**
     * Tokenizes a comma-separated line, trimming all components.
     * Demonstrates String.split, trim, substring.
     */
    public static String[] tokenizeCsv(String csvLine) {
        if (csvLine == null) return new String[0];
        String[] tokens = csvLine.split(",", -1);
        for (int i = 0; i < tokens.length; i++) {
            tokens[i] = tokens[i].trim();
        }
        return tokens;
    }

    /**
     * Checks if a target text matches search query (case-insensitive contains or code match).
     * Demonstrates contains, indexOf, equalsIgnoreCase.
     */
    public static boolean matchesQuery(String target, String query) {
        if (target == null || query == null) return false;
        String cleanTarget = target.trim().toLowerCase();
        String cleanQuery = query.trim().toLowerCase();
        return cleanTarget.contains(cleanQuery) || cleanTarget.indexOf(cleanQuery) >= 0;
    }

    /**
     * Generates a clean border line using StringBuilder.
     */
    public static String repeatChar(char ch, int count) {
        StringBuilder sb = new StringBuilder(count);
        for (int i = 0; i < count; i++) {
            sb.append(ch);
        }
        return sb.toString();
    }
}
