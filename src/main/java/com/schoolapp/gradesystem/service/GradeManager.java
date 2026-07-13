package com.schoolapp.gradesystem.service;

import com.schoolapp.gradesystem.model.*;

// GradeManager "has" a bunch of Grade objects, the same way
// StudentManager "has" a bunch of Student objects.
public class GradeManager {

    private Grade[] grades = new Grade[200];
    private int gradeCount = 0;

    public boolean addGrade(Grade grade) {
        if (gradeCount >= grades.length) {
            System.out.println("Cannot add grade, storage is full.");
            return false;
        }

        grades[gradeCount] = grade;
        gradeCount = gradeCount + 1;
        return true;
    }

    public int getGradeCount() {
        return gradeCount;
    }

    // Counts how many grades a student has, without printing anything.
    // Used to warn the user before deleting a student.
    public int countGradesForStudent(String studentId) {
        int count = 0;
        for (int i = 0; i < gradeCount; i++) {
            if (grades[i].getStudentId().equalsIgnoreCase(studentId)) {
                count = count + 1;
            }
        }
        return count;
    }

    // Removes EVERY grade belonging to one student. Since there can be
    // several matching grades scattered through the array (not just
    // one, like with deleteStudent), we rebuild the array by copying
    // over only the grades we want to KEEP, in order, then shrink the
    // count to match. This is sometimes called "compacting" an array.
    public void deleteGradesForStudent(String studentId) {
        int newIndex = 0;

        for (int i = 0; i < gradeCount; i++) {
            if (!grades[i].getStudentId().equalsIgnoreCase(studentId)) {
                grades[newIndex] = grades[i];
                newIndex = newIndex + 1;
            }
        }

        // Clear out the now-unused leftover slots at the end.
        for (int i = newIndex; i < gradeCount; i++) {
            grades[i] = null;
        }

        gradeCount = newIndex;
    }

    // Same rounding trick as StudentManager: keeps percentages
    // readable, e.g. 85.2% instead of 85.16666666666667%.
    private double round(double value) {
        return Math.round(value * 10) / 10.0;
    }

    // Checks whether this student has at least one grade in EACH of
    // the 3 mandatory core subjects (Mathematics, English, Science).
    // A student can have plenty of elective grades and still fail
    // this check if even one core subject is still missing.
    public boolean hasAllCoreSubjects(String studentId) {
        boolean hasMath = false;
        boolean hasEnglish = false;
        boolean hasScience = false;

        for (int i = 0; i < gradeCount; i++) {
            Grade g = grades[i];
            if (g.getStudentId().equalsIgnoreCase(studentId) && g.getSubject().getSubjectType().equals("Core")) {
                String subjectName = g.getSubject().getSubjectName();
                if (subjectName.equalsIgnoreCase("Mathematics")) {
                    hasMath = true;
                } else if (subjectName.equalsIgnoreCase("English")) {
                    hasEnglish = true;
                } else if (subjectName.equalsIgnoreCase("Science")) {
                    hasScience = true;
                }
            }
        }

        return hasMath && hasEnglish && hasScience;
    }

    // Note: calculateCoreAverage and calculateElectiveAverage below
    // look almost identical. In a bigger program you would combine
    // them into one helper method, but writing them out separately
    // is easier to follow when you are first learning.

    public double calculateCoreAverage(String studentId) {
        double total = 0.0;
        int count = 0;

        for (int i = 0; i < gradeCount; i++) {
            Grade g = grades[i];
            if (g.getStudentId().equalsIgnoreCase(studentId) && g.getSubject().getSubjectType().equals("Core")) {
                total = total + g.getGrade();
                count = count + 1;
            }
        }

        if (count == 0) {
            return 0.0;
        }
        return total / count;
    }

    public double calculateElectiveAverage(String studentId) {
        double total = 0.0;
        int count = 0;

        for (int i = 0; i < gradeCount; i++) {
            Grade g = grades[i];
            if (g.getStudentId().equalsIgnoreCase(studentId) && g.getSubject().getSubjectType().equals("Elective")) {
                total = total + g.getGrade();
                count = count + 1;
            }
        }

        if (count == 0) {
            return 0.0;
        }
        return total / count;
    }

    public double calculateOverallAverage(String studentId) {
        double total = 0.0;
        int count = 0;

        for (int i = 0; i < gradeCount; i++) {
            Grade g = grades[i];
            if (g.getStudentId().equalsIgnoreCase(studentId)) {
                total = total + g.getGrade();
                count = count + 1;
            }
        }

        if (count == 0) {
            return 0.0;
        }
        return total / count;
    }

    public void viewGradesByStudent(Student student) {
        String studentId = student.getStudentId();

        System.out.println();
        System.out.println("Student: " + studentId + " - " + student.getName());
        System.out.println("Type: " + student.getStudentType() + " Student");

        // First, just count how many grades this student has.
        int matchCount = 0;
        for (int i = 0; i < gradeCount; i++) {
            if (grades[i].getStudentId().equalsIgnoreCase(studentId)) {
                matchCount = matchCount + 1;
            }
        }

        if (matchCount == 0) {
            System.out.println("Passing Grade: " + (int) student.getPassingGrade() + "%");
            System.out.println("---------------------------------------------");
            System.out.println("No grades recorded for this student.");
            System.out.println("---------------------------------------------");
            return;
        }

        boolean complete = hasAllCoreSubjects(studentId);

        if (complete) {
            double overall = round(calculateOverallAverage(studentId));
            System.out.println("Current Average: " + overall + "%");

            if (student.isPassing()) {
                System.out.println("Status: PASSING");
            } else {
                System.out.println("Status: FAILING");
            }
        } else {
            System.out.println("Current Average: Incomplete (missing core subject grades)");
            System.out.println("Status: INCOMPLETE");
        }

        System.out.println();
        System.out.println("GRADE HISTORY");
        System.out.println("-------------------------------------------------------------------");
        System.out.println("GRD ID  | DATE       | SUBJECT          | TYPE      | GRADE");
        System.out.println("-------------------------------------------------------------------");

        // We loop from the LAST grade added back to the FIRST one,
        // so the newest grade prints at the top, like the assignment asks.
        for (int i = gradeCount - 1; i >= 0; i--) {
            Grade g = grades[i];
            if (g.getStudentId().equalsIgnoreCase(studentId)) {
                System.out.println(g.getGradeId() + " | " + g.getDate() + " | "
                        + g.getSubject().getSubjectName() + " | " + g.getSubject().getSubjectType()
                        + " | " + g.getGrade() + "%");
            }
        }
        System.out.println("-------------------------------------------------------------------");

        System.out.println("Total Grades: " + matchCount);
        System.out.println("Core Subjects Average: " + round(calculateCoreAverage(studentId)) + "%");
        System.out.println("Elective Subjects Average: " + round(calculateElectiveAverage(studentId)) + "%");

        if (complete) {
            System.out.println("Overall Average: " + round(calculateOverallAverage(studentId)) + "%");
        } else {
            System.out.println("Overall Average: Incomplete (missing core subject grades)");
        }
    }
}
