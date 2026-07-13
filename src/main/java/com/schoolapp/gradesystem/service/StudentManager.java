package com.schoolapp.gradesystem.service;

import com.schoolapp.gradesystem.model.*;

// StudentManager does NOT extend Student.
// A manager "has" a bunch of students, it is not itself a student.
// This is called "composition" (has-a) instead of "inheritance" (is-a).
public class StudentManager {

    // A simple array with a fixed size, plus a counter that tracks
    // how many of the slots are actually being used.
    private Student[] students = new Student[50];
    private int studentCount = 0;

    public boolean addStudent(Student student) {
        if (studentCount >= students.length) {
            System.out.println("Cannot add student, the list is full.");
            return false;
        }

        students[studentCount] = student;
        studentCount = studentCount + 1;
        return true;
    }

    // Checks every slot one by one until it finds a match.
    // This is called a "linear search".
    public Student findStudent(String studentId) {
        for (int i = 0; i < studentCount; i++) {
            if (students[i].getStudentId().equalsIgnoreCase(studentId)) {
                return students[i];
            }
        }
        return null;
    }

    // Removes a student from the array. Unlike adding (which just
    // drops something into the next empty slot), removing from the
    // MIDDLE of an array means every student who came after the
    // removed one has to slide one slot to the left, so there is no
    // gap left behind.
    public boolean deleteStudent(String studentId) {
        int indexToRemove = -1;

        // Step 1: find WHERE the student is (their index), not just
        // whether they exist.
        for (int i = 0; i < studentCount; i++) {
            if (students[i].getStudentId().equalsIgnoreCase(studentId)) {
                indexToRemove = i;
                break;
            }
        }

        // If we never found a match, there's nothing to delete.
        if (indexToRemove == -1) {
            return false;
        }

        // Step 2: slide everyone AFTER the removed spot one slot to
        // the left. Example: removing index 2 out of [A, B, C, D, E]
        // means D moves into C's old spot, and E moves into D's old spot.
        for (int i = indexToRemove; i < studentCount - 1; i++) {
            students[i] = students[i + 1];
        }

        // Step 3: the very last slot is now a leftover duplicate
        // (its value got copied one step to the left already), so
        // we clear it out and shrink the count by one.
        students[studentCount - 1] = null;
        studentCount = studentCount - 1;

        return true;
    }

    public int getStudentCount() {
        return studentCount;
    }

    public double getAverageClassGrade(GradeManager gradeManager) {
        double total = 0.0;
        int completeCount = 0;

        for (int i = 0; i < studentCount; i++) {
            Student s = students[i];
            if (gradeManager.hasAllCoreSubjects(s.getStudentId())) {
                total = total + s.calculateAverageGrade();
                completeCount = completeCount + 1;
            }
        }

        if (completeCount == 0) {
            return 0.0;
        }
        return total / completeCount;
    }

    // Division often gives long, messy decimals like 85.16666666666667.
    // This trick rounds to ONE decimal place: multiply by 10, round to
    // the nearest whole number, then divide by 10.0 again.
    // Example: 85.1666... * 10 = 851.666..., rounded = 852, / 10.0 = 85.2
    private double round(double value) {
        return Math.round(value * 10) / 10.0;
    }

    public void viewAllStudents(GradeManager gradeManager) {
        if (studentCount == 0) {
            System.out.println("No students registered yet.");
            return;
        }

        System.out.println();
        System.out.println("STUDENT LISTING");
        System.out.println("---------------------------------------------------------------------------");
        System.out.println("STU ID   | NAME              | TYPE         | AVG GRADE | STATUS");
        System.out.println("---------------------------------------------------------------------------");

        int completeCount = 0;

        for (int i = 0; i < studentCount; i++) {
            // Even though "s" is typed as Student, the object underneath
            // is really a RegularStudent or an HonorsStudent. Calling
            // s.getStudentType() below automatically runs the CORRECT
            // version for whichever one it really is. This is polymorphism.
            Student s = students[i];

            boolean complete = gradeManager.hasAllCoreSubjects(s.getStudentId());

            String averageText;
            String status;

            if (complete) {
                completeCount = completeCount + 1;
                double average = round(s.calculateAverageGrade());
                averageText = average + "%";
                if (s.isPassing()) {
                    status = "Passing";
                } else {
                    status = "Failing";
                }
            } else {
                averageText = "N/A";
                status = "Incomplete";
            }

            System.out.println(s.getStudentId() + " | " + s.getName() + " | "
                    + s.getStudentType() + " | " + averageText + " | " + status);

            System.out.println("Enrolled Subjects: " + s.getNumberOfGrades()
                    + " | Passing Grade: " + (int) s.getPassingGrade() + "%");

            if (!complete) {
                System.out.println("Missing one or more core subjects (Mathematics, English, Science)");
            }

            // Only HonorsStudent objects have "honors eligible" info,
            // so here we DO need to check the specific type.
            if (s instanceof HonorsStudent) {
                HonorsStudent honors = (HonorsStudent) s;
                honors.checkHonorsEligibility();
                if (honors.isHonorsEligible()) {
                    System.out.println("Honors Eligible: Yes");
                }
            }

            System.out.println("---------------------------------------------------------------------------");
        }

        System.out.println("Total Students: " + studentCount);

        if (completeCount == 0) {
            System.out.println("Average Class Grade: N/A (no students have completed all core subjects yet)");
        } else {
            System.out.println("Average Class Grade: " + round(getAverageClassGrade(gradeManager)) + "%");
        }
    }
}
