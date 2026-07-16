package model;

// An "interface" is just a checklist. It has NO code inside it,
// only a list of methods that a class PROMISES to write itself
// if it says "implements Gradable".
public interface Gradable {

    // Any class that implements Gradable must have a method
    // that tries to record a grade, and returns true/false
    // depending on whether it worked.
    boolean recordGrade(double grade);

    // Any class that implements Gradable must also have a method
    // that checks if a grade number makes sense (between 0 and 100).
    boolean validateGrade(double grade);
}
