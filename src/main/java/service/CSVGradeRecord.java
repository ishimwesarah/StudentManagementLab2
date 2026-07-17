package service;

/**
 * One parsed row from a bulk-import CSV file: StudentID,SubjectName,SubjectType,Grade.
 * Just data - no validation logic beyond what CSVParser already checked
 * (correct column count). Business validation (does the student exist? is
 * the grade in range?) happens later in BulkImportService.
 */
public class CSVGradeRecord {

    private final int rowNumber;
    private final String studentId;
    private final String subjectName;
    private final String subjectType;
    private final String rawGrade;

    public CSVGradeRecord(int rowNumber, String studentId, String subjectName, String subjectType, String rawGrade) {
        this.rowNumber = rowNumber;
        this.studentId = studentId;
        this.subjectName = subjectName;
        this.subjectType = subjectType;
        this.rawGrade = rawGrade;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public String getSubjectType() {
        return subjectType;
    }

    public String getRawGrade() {
        return rawGrade;
    }
}