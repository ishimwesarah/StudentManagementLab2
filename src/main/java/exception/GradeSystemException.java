package exception;

/**
 * Base class for every custom exception in this application.
 *
 * This is a checked exception (extends Exception, not RuntimeException) on
 * purpose: these represent expected, recoverable business errors - a student
 * that doesn't exist, a grade out of range - not programming bugs. Making
 * them checked forces every call site to explicitly handle or declare them,
 * instead of letting them silently propagate or crash the app.
 */
public abstract class GradeSystemException extends Exception {

    public GradeSystemException(String message) {
        super(message);
    }
}