package exception;

/**
 * Thrown when a CSV file being imported doesn't match the expected format -
 * missing header, wrong number of columns, or the file itself can't be read.
 */
public class InvalidFileFormatException extends GradeSystemException {

    public InvalidFileFormatException(String message) {
        super(message);
    }
}