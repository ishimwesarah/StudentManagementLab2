package com.schoolapp.gradesystem;

import com.schoolapp.gradesystem.model.*;
import com.schoolapp.gradesystem.service.*;

import java.util.Scanner;

public class Main {

    private static Scanner scanner = new Scanner(System.in);
    private static StudentManager studentManager = new StudentManager();
    private static GradeManager gradeManager = new GradeManager();

    public static void main(String[] args) {
        loadSampleData();

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readMenuChoice();

            switch (choice) {
                case 1:
                    addStudent();
                    break;
                case 2:
                    studentManager.viewAllStudents(gradeManager);
                    break;
                case 3:
                    recordGrade();
                    break;
                case 4:
                    viewGradeReport();
                    break;
                case 5:
                    deleteStudent();
                    break;
                case 6:
                    System.out.println();
                    System.out.println("Thank you for using Student Grade Management System!");
                    System.out.println("Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 6.");
                    break;
            }

            if (running) {
                System.out.println();
                System.out.println("Press Enter to continue...");
                scanner.nextLine();
            }
        }
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("=============================================");
        System.out.println("   STUDENT GRADE MANAGEMENT - MAIN MENU");
        System.out.println("=============================================");
        System.out.println("1. Add Student");
        System.out.println("2. View Students");
        System.out.println("3. Record Grade");
        System.out.println("4. View Grade Report");
        System.out.println("5. Delete Student");
        System.out.println("6. Exit");
        System.out.print("Enter choice: ");
    }

    // Reads one line of text and tries to turn it into a number.
    // If the person typed something that is not a number, we just
    // return -1, which the switch above will treat as invalid.
    private static int readMenuChoice() {
        String input = scanner.nextLine();
        try {
            return Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // ---------------- Feature: Add Student ----------------
    private static void addStudent() {
        System.out.println();
        System.out.println("ADD STUDENT");
        System.out.println("---------------------------------------------");

        System.out.print("Enter student name: ");
        String name = scanner.nextLine();

        int age = readNumberBetween("Enter student age: ", 3, 120);

        System.out.print("Enter student email: ");
        String email = scanner.nextLine();

        System.out.print("Enter student phone: ");
        String phone = scanner.nextLine();

        System.out.println();
        System.out.println("Student type:");
        System.out.println("1. Regular Student (Passing grade: 50%)");
        System.out.println("2. Honors Student (Passing grade: 60%, honors recognition)");
        int type = readNumberBetween("Select type (1-2): ", 1, 2);

        Student newStudent;
        if (type == 1) {
            newStudent = new RegularStudent(name, age, email, phone);
        } else {
            newStudent = new HonorsStudent(name, age, email, phone);
        }

        studentManager.addStudent(newStudent);

        System.out.println();
        System.out.println("Student added successfully!");
        newStudent.displayStudentDetails();
    }

    // ---------------- Feature: Record Grade ----------------
    private static void recordGrade() {
        System.out.println();
        System.out.println("RECORD GRADE");
        System.out.println("---------------------------------------------");

        System.out.print("Enter Student ID: ");
        String studentId = scanner.nextLine();
        Student student = studentManager.findStudent(studentId);

        if (student == null) {
            System.out.println("No student found with ID: " + studentId);
            return;
        }

        System.out.println();
        System.out.println("Student: " + student.getName());
        System.out.println("Type: " + student.getStudentType());
        if (gradeManager.hasAllCoreSubjects(student.getStudentId())) {
            System.out.println("Current Average: " + Math.round(student.calculateAverageGrade() * 10) / 10.0 + "%");
        } else {
            System.out.println("Current Average: Incomplete (missing core subject grades)");
        }

        System.out.println();
        System.out.println("Subject type:");
        System.out.println("1. Core Subject (Mathematics, English, Science)");
        System.out.println("2. Elective Subject (Music, Art, Physical Education)");
        int subjectType = readNumberBetween("Select type (1-2): ", 1, 2);

        Subject subject = null;

        if (subjectType == 1) {
            System.out.println();
            System.out.println("Available Core Subjects:");
            System.out.println("1. Mathematics");
            System.out.println("2. English");
            System.out.println("3. Science");
            int pick = readNumberBetween("Select subject (1-3): ", 1, 3);

            if (pick == 1) {
                subject = new CoreSubject("Mathematics", "MATH101");
            } else if (pick == 2) {
                subject = new CoreSubject("English", "ENG101");
            } else {
                subject = new CoreSubject("Science", "SCI101");
            }
        } else {
            System.out.println();
            System.out.println("Available Elective Subjects:");
            System.out.println("1. Music");
            System.out.println("2. Art");
            System.out.println("3. Physical Education");
            int pick = readNumberBetween("Select subject (1-3): ", 1, 3);

            if (pick == 1) {
                subject = new ElectiveSubject("Music", "MUS101");
            } else if (pick == 2) {
                subject = new ElectiveSubject("Art", "ART101");
            } else {
                subject = new ElectiveSubject("Physical Education", "PE101");
            }
        }

        double grade = readGrade();

        System.out.println();
        System.out.println("GRADE CONFIRMATION");
        System.out.println("---------------------------------------------");
        System.out.println("Student: " + student.getStudentId() + " - " + student.getName());
        subject.displaySubjectDetails();
        System.out.println("Grade: " + grade + "%");
        System.out.println("---------------------------------------------");

        System.out.print("Confirm grade? (Y/N): ");
        String confirm = scanner.nextLine();

        if (confirm.equalsIgnoreCase("Y")) {
            Grade newGrade = new Grade(student.getStudentId(), subject, grade);
            student.recordGrade(grade);
            gradeManager.addGrade(newGrade);
            System.out.println();
            System.out.println("Grade recorded successfully!");
            newGrade.displayGradeDetails();
        } else {
            System.out.println("Grade entry cancelled.");
        }
    }

    // ---------------- Feature: View Grade Report ----------------
    private static void viewGradeReport() {
        System.out.println();
        System.out.println("VIEW GRADE REPORT");
        System.out.println("---------------------------------------------");

        System.out.print("Enter Student ID: ");
        String studentId = scanner.nextLine();
        Student student = studentManager.findStudent(studentId);

        if (student == null) {
            System.out.println("No student found with ID: " + studentId);
            return;
        }

        gradeManager.viewGradesByStudent(student);
    }

    // ---------------- Feature: Delete Student ----------------
    private static void deleteStudent() {
        System.out.println();
        System.out.println("DELETE STUDENT");
        System.out.println("---------------------------------------------");

        System.out.print("Enter Student ID: ");
        String studentId = scanner.nextLine();
        Student student = studentManager.findStudent(studentId);

        if (student == null) {
            System.out.println("No student found with ID: " + studentId);
            return;
        }

        int gradeCountForStudent = gradeManager.countGradesForStudent(student.getStudentId());

        System.out.println();
        System.out.println("Student: " + student.getStudentId() + " - " + student.getName());
        System.out.println("Type: " + student.getStudentType());
        System.out.println("This student has " + gradeCountForStudent + " grade(s) recorded.");
        System.out.println("Deleting this student will also permanently delete all of their recorded grades.");

        System.out.print("Are you sure you want to delete this student? (Y/N): ");
        String confirm = scanner.nextLine();

        if (confirm.equalsIgnoreCase("Y")) {
            gradeManager.deleteGradesForStudent(student.getStudentId());
            studentManager.deleteStudent(student.getStudentId());
            System.out.println();
            System.out.println("Student " + studentId + " and all their grades have been deleted.");
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    // ---------------- Input validation helpers ----------------

    // Keeps asking the user until they type a whole number
    // that falls between min and max (inclusive).
    private static int readNumberBetween(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            try {
                int value = Integer.parseInt(input.trim());
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("Please enter a number between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    // Keeps asking the user until they type a valid grade (0-100).
    private static double readGrade() {
        while (true) {
            System.out.print("Enter grade (0-100): ");
            String input = scanner.nextLine();
            try {
                double value = Double.parseDouble(input.trim());
                if (value >= 0 && value <= 100) {
                    return value;
                }
                System.out.println("Grade must be between 0 and 100.");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    // ---------------- Sample data so the app has content right away ----------------
    private static void loadSampleData() {
        RegularStudent alice = new RegularStudent("Alice Johnson", 16, "alice.johnson@school.edu", "+1-555-0001");
        HonorsStudent bob = new HonorsStudent("Bob Smith", 17, "bob.smith@school.edu", "+1-555-0002");
        RegularStudent carol = new RegularStudent("Carol Martinez", 15, "carol.martinez@school.edu", "+1-555-0003");
        HonorsStudent david = new HonorsStudent("David Chen", 17, "david.chen@school.edu", "+1-555-0004");
        RegularStudent emma = new RegularStudent("Emma Wilson", 16, "emma.wilson@school.edu", "+1-555-0005");

        studentManager.addStudent(alice);
        studentManager.addStudent(bob);
        studentManager.addStudent(carol);
        studentManager.addStudent(david);
        studentManager.addStudent(emma);

        CoreSubject math = new CoreSubject("Mathematics", "MATH101");
        CoreSubject english = new CoreSubject("English", "ENG101");
        CoreSubject science = new CoreSubject("Science", "SCI101");
        ElectiveSubject music = new ElectiveSubject("Music", "MUS101");
        ElectiveSubject art = new ElectiveSubject("Art", "ART101");
        ElectiveSubject pe = new ElectiveSubject("Physical Education", "PE101");

        // Alice's grades
        addSampleGrade(alice, math, 75);
        addSampleGrade(alice, english, 80);
        addSampleGrade(alice, science, 78);
        addSampleGrade(alice, music, 85);
        addSampleGrade(alice, art, 74);

        // Bob's grades
        addSampleGrade(bob, math, 88);
        addSampleGrade(bob, english, 82);
        addSampleGrade(bob, science, 90);
        addSampleGrade(bob, music, 84);
        addSampleGrade(bob, art, 86);
        addSampleGrade(bob, pe, 81);

        // Carol's grades
        addSampleGrade(carol, math, 40);
        addSampleGrade(carol, english, 48);
        addSampleGrade(carol, science, 42);
        addSampleGrade(carol, art, 50);

        // David's grades
        addSampleGrade(david, math, 95);
        addSampleGrade(david, english, 91);
        addSampleGrade(david, science, 94);
        addSampleGrade(david, music, 90);
        addSampleGrade(david, art, 92);
        addSampleGrade(david, pe, 95);

        // Emma's grades
        addSampleGrade(emma, math, 70);
        addSampleGrade(emma, english, 65);
        addSampleGrade(emma, science, 68);
        addSampleGrade(emma, music, 66);
        addSampleGrade(emma, pe, 67);
    }

    // Records one grade both on the Student object (for its own
    // average) and in the GradeManager (for detailed reports).
    private static void addSampleGrade(Student student, Subject subject, double score) {
        Grade g = new Grade(student.getStudentId(), subject, score);
        student.recordGrade(score);
        gradeManager.addGrade(g);
    }
}
