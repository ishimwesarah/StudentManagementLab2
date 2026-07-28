package service.concurrency;

public class StudentExportOutcome {

    private final String studentId;
    private final boolean success;
    private final String errorMessage;

    public StudentExportOutcome(String studentId, boolean success, String errorMessage) {
        this.studentId = studentId;
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public String getStudentId() {
        return studentId;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}