package exception;

/**
 * Thrown when a grade value fails validation - either it isn't a number
 * at all, or it falls outside the accepted 0-100 range.
 */
public class InvalidGradeException extends GradeSystemException {

    public InvalidGradeException(String message) {
        super(message);
    }
}