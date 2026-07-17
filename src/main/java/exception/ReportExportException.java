package exception;

/**
 * Thrown when writing a report to disk fails - e.g. the reports directory
 * can't be created, or the file can't be written.
 */
public class ReportExportException extends GradeSystemException {

    public ReportExportException(String message) {
        super(message);
    }
}