package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class Grade implements Serializable {
    private static final long serialVersionUID = 1L;


    private static int gradeCounter = 0;

    private String gradeId;
    private String studentId;
    private Subject subject;
    private double grade;
    private String date;


    public Grade(String studentId, Subject subject, double grade) {
        gradeId = "GRD" + String.format("%03d", gradeCounter++);

        this.studentId = studentId;
        this.subject = subject;
        this.grade = grade;

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

    /**
     * Converts the numeric score into a letter grade using a standard
     * scale: A (90+), B (80-89), C (70-79), D (60-69), F (below 60).
     *
     * @return the letter grade corresponding to this grade's score
     */
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