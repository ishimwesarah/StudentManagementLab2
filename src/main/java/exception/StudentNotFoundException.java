package exception;


/**
 * Thrown when a lookup is attempted for a student ID that does not exist
 * in the system.
 */
public class StudentNotFoundException extends GradeSystemException {

    public StudentNotFoundException(String message) {
        super(message);
    }
}