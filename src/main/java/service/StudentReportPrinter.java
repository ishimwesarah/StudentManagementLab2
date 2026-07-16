package service;

import model.HonorsStudent;
import model.Student;

import java.util.List;

/**
 * Responsible only for formatting and printing the student roster to the console.
 *
 * Pulls the student list from StudentManager and computed statistics from
 * StudentAverageCalculator rather than owning either concern itself - this
 * class only knows how to lay text out on screen.
 */
public class StudentReportPrinter {

    private final StudentManager studentManager;
    private final StudentAverageCalculator calculator;

    public StudentReportPrinter(StudentManager studentManager, StudentAverageCalculator calculator) {
        this.studentManager = studentManager;
        this.calculator = calculator;
    }

    public void printAllStudents() {
        List<Student> students = studentManager.getAllStudents();

        if (students.isEmpty()) {
            System.out.println("No students registered yet.");
            return;
        }

        System.out.println();
        System.out.println("STUDENT LISTING");
        System.out.println("---------------------------------------------------------------------------");
        System.out.println("STU ID   | NAME              | TYPE         | AVG GRADE | STATUS");
        System.out.println("---------------------------------------------------------------------------");

        // polymorphism: this list can contain both Regular and Honors students.
        for (Student s : students) {
            double average = calculator.round(s.calculateAverageGrade());

            String status;
            if (s.isPassing()) {
                status = "Passing";
            } else {
                status = "Failing";
            }

            System.out.println(s.getStudentId() + " | " + s.getName() + " | "
                    + s.getStudentType() + " | " + average + "% | " + status);

            System.out.println("Enrolled Subjects: " + s.getNumberOfGrades()
                    + " | Passing Grade: " + (int) s.getPassingGrade() + "%");

            if (s instanceof HonorsStudent) {
                HonorsStudent honors = (HonorsStudent) s;
                honors.checkHonorsEligibility();
                if (honors.isHonorsEligible()) {
                    System.out.println("Honors Eligible: Yes");
                } else {
                    System.out.println("Honors Eligible: No");
                }
            }

            System.out.println("---------------------------------------------------------------------------");
        }

        System.out.println("Total Students: " + students.size());
        System.out.println("Average Class Grade: " + calculator.round(calculator.calculateClassAverage(students)) + "%");
    }
}