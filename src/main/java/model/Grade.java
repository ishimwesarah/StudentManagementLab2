package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

// Grade is just a simple "record" of one fact:
// this student got this score, in this subject, on this date.
// It does not extend anything and is not part of any hierarchy.
public class Grade {

    private static int gradeCounter = 0;

    private String gradeId;
    private String studentId;
    private Subject subject;
    private double grade;
    private String date;

    public Grade(String studentId, Subject subject, double grade) {
        gradeCounter = gradeCounter + 1;
        gradeId = "GRD" + String.format("%03d", gradeCounter);

        this.studentId = studentId;
        this.subject = subject;
        this.grade = grade;

        // This just gets today's date and writes it as "dd-MM-yyyy",
        // for example "04-07-2026".
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        date = LocalDate.now().format(formatter);
    }

    public String getGradeId() {
        return gradeId;
    }

    public String getStudentId() {
        return studentId;
    }

    public Subject getSubject() {
        return subject;
    }

    public double getGrade() {
        return grade;
    }

    public String getDate() {
        return date;
    }

    // Turns a number score into a letter grade.
    public String getLetterGrade() {
        if (grade >= 90) {
            return "A";
        } else if (grade >= 80) {
            return "B";
        } else if (grade >= 70) {
            return "C";
        } else if (grade >= 60) {
            return "D";
        } else {
            return "F";
        }
    }

    public void displayGradeDetails() {
        System.out.println("Grade ID: " + gradeId);
        System.out.println("Student ID: " + studentId);
        System.out.println("Subject: " + subject.getSubjectName() + " (" + subject.getSubjectType() + ")");
        System.out.println("Grade: " + grade + "% (" + getLetterGrade() + ")");
        System.out.println("Date: " + date);
    }
}
