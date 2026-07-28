package service.concurrency;

import java.util.ArrayList;
import java.util.List;

public class BatchExportResult {

    private int successCount = 0;
    private final List<String> failures = new ArrayList<>();

    public void recordOutcome(StudentExportOutcome outcome) {
        if (outcome.isSuccess()) {
            successCount++;
        } else {
            failures.add(outcome.getStudentId() + ": " + outcome.getErrorMessage());
        }
    }

    public void recordFailure(String message) {
        failures.add(message);
    }

    public int getSuccessCount() {
        return successCount;
    }

    public List<String> getFailures() {
        return failures;
    }
}