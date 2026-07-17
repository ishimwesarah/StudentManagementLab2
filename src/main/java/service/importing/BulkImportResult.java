package service.importing;

import java.util.ArrayList;
import java.util.List;

/**
 * Summary of a completed bulk-import run: how many rows were attempted,
 * how many succeeded, and the details of any that failed.
 */
public class BulkImportResult {

    private int totalRows = 0;
    private int successCount = 0;
    private final List<ImportFailure> failures = new ArrayList<>();

    public void recordSuccess() {
        totalRows++;
        successCount++;
    }

    public void recordFailure(int rowNumber, String reason) {
        totalRows++;
        failures.add(new ImportFailure(rowNumber, reason));
    }

    public int getTotalRows() {
        return totalRows;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public int getFailureCount() {
        return failures.size();
    }

    public List<ImportFailure> getFailures() {
        return failures;
    }
}