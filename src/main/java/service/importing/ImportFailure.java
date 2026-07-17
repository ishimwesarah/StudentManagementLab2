package service.importing;

/**
 * A single row that failed to import, with the reason why.
 */
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
}