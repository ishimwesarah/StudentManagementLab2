package service.importing;

public class ImportFailure {

    private final int rowNumber;
    private final String reason;

    public ImportFailure(int rowNumber, String reason) {
        this.rowNumber = rowNumber;
        this.reason = reason;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public String getReason() {
        return reason;
    }

    /**
     * A rough severity ranking, higher meaning worse - used to prioritize
     * which failures get reviewed first. An unknown student ID means the
     * whole row is meaningless (we don't even know who it's for), while
     * an out-of-range grade or type mismatch is a smaller, more isolated
     * problem with an otherwise identifiable row.
     */
    public int getSeverity() {
        if (reason.contains("Invalid student ID")) {
            return 3;
        }
        if (reason.contains("Unknown subject")) {
            return 2;
        }
        if (reason.contains("mismatch")) {
            return 1;
        }
        return 0; // grade out of range, not a number, etc.
    }
}