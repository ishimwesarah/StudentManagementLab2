package service.importing;

import exception.InvalidFileFormatException;
import exception.StudentNotFoundException;
import model.CoreSubject;
import model.ElectiveSubject;
import model.Grade;
import model.Student;
import model.Subject;
import service.GradeManager;
import service.StudentManager;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Validates and applies parsed CSV rows against the real StudentManager and
 * GradeManager. Depends on CSVParser for the parsing step - this class
 * only handles business validation (does the student exist? is the grade
 * in range?) and applying valid rows.
 */
public class BulkImportService {

    private final CSVParser csvParser;
    private final StudentManager studentManager;
    private final GradeManager gradeManager;

    /**
     * Subjects get looked up by name so a CSV row like "Mathematics,Core"
     * maps to the same Subject object the rest of the app already uses,
     * rather than creating a duplicate. Passed in from ConsoleApp, which
     * owns the canonical subject instances.
     */
    private final Map<String, Subject> subjectsByName = new HashMap<>();

    public BulkImportService(CSVParser csvParser, StudentManager studentManager, GradeManager gradeManager,
                             List<Subject> knownSubjects) {
        this.csvParser = csvParser;
        this.studentManager = studentManager;
        this.gradeManager = gradeManager;
        for (Subject subject : knownSubjects) {
            subjectsByName.put(subject.getSubjectName().toLowerCase(), subject);
        }
    }

    public BulkImportResult importFromFile(Path filePath) throws InvalidFileFormatException {
        List<CSVGradeRecord> records = csvParser.parse(filePath);

        BulkImportResult result = new BulkImportResult();
        for (CSVGradeRecord record : records) {
            String failureReason = validate(record);
            if (failureReason != null) {
                result.recordFailure(record.getRowNumber(), failureReason);
                continue;
            }

            Student student;
            try {
                student = studentManager.findStudent(record.getStudentId());
            } catch (StudentNotFoundException e) {
                result.recordFailure(record.getRowNumber(), "Invalid student ID (" + record.getStudentId() + ")");
                continue;
            }

            Subject subject = subjectsByName.get(record.getSubjectName().toLowerCase());
            double grade = Double.parseDouble(record.getRawGrade());

            Grade newGrade = new Grade(student.getStudentId(), subject, grade);
            student.recordGrade(grade);
            gradeManager.addGrade(newGrade);

            result.recordSuccess();
        }

        return result;
    }

    /**
     * Returns a human-readable failure reason, or null if the row is valid.
     * Checked before touching StudentManager/GradeManager so a single bad
     * row can't partially apply.
     */
    private String validate(CSVGradeRecord record) {
        if (!subjectsByName.containsKey(record.getSubjectName().toLowerCase())) {
            return "Unknown subject (" + record.getSubjectName() + ")";
        }

        Subject subject = subjectsByName.get(record.getSubjectName().toLowerCase());
        String expectedType = subject instanceof CoreSubject ? "Core"
                : subject instanceof ElectiveSubject ? "Elective" : null;
        if (expectedType != null && !expectedType.equalsIgnoreCase(record.getSubjectType())) {
            return "Subject type mismatch for " + record.getSubjectName()
                    + " (expected " + expectedType + ", got " + record.getSubjectType() + ")";
        }

        double grade;
        try {
            grade = Double.parseDouble(record.getRawGrade());
        } catch (NumberFormatException e) {
            return "Grade is not a valid number (" + record.getRawGrade() + ")";
        }

        if (grade < 0 || grade > 100) {
            return "Grade out of range (" + record.getRawGrade() + ")";
        }

        return null;
    }
}