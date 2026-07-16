import model.*;
import service.*;

import java.util.Scanner;

/**
 * Owns the console menu loop and all of the application's wiring.
 *
 * Every dependency (managers, calculators, printers, subjects) is created
 * once in the constructor and held as an instance field - there are no
 * static fields here. That's the point of this class: Main used to hold
 * everything as static state, which made it awkward to reason about and
 * impossible to unit test. Now Main just does `new ConsoleApp().run()`,
 * and each collaborator below can be tested or swapped independently.
 */
public class ConsoleApp {

    private final Scanner scanner;

    private final StudentManager studentManager;
    private final StudentAverageCalculator studentAverageCalculator;
    private final StudentReportPrinter studentReportPrinter;

    private final GradeManager gradeManager;
    private final GradeAverageCalculator gradeAverageCalculator;
    private final GradeReportPrinter gradeReportPrinter;

    private final CoreSubject math;
    private final CoreSubject english;
    private final CoreSubject science;
    private final ElectiveSubject music;
    private final ElectiveSubject art;
    private final ElectiveSubject pe;

    public ConsoleApp() {
        this.scanner = new Scanner(System.in);

        this.studentManager = new StudentManager();
        this.studentAverageCalculator = new StudentAverageCalculator();
        this.studentReportPrinter = new StudentReportPrinter(studentManager, studentAverageCalculator);

        this.gradeManager = new GradeManager();
        this.gradeAverageCalculator = new GradeAverageCalculator();
        this.gradeReportPrinter = new GradeReportPrinter(gradeManager, gradeAverageCalculator);

        this.math = new CoreSubject("Mathematics", "MATH101");
        this.english = new CoreSubject("English", "ENG101");
        this.science = new CoreSubject("Science", "SCI101");
        this.music = new ElectiveSubject("Music", "MUS101");
        this.art = new ElectiveSubject("Art", "ART101");
        this.pe = new ElectiveSubject("Physical Education", "PE101");
    }

    public void run() {
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
                    studentReportPrinter.printAllStudents();
                    break;
                case 3:
                    recordGrade();
                    break;
                case 4:
                    viewGradeReport();
                    break;
                case 5:
                    System.out.println();
                    System.out.println("Thank you for using Student Grade Management System!");
                    System.out.println("Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 5.");
                    break;
            }

            if (running) {
                System.out.println();
                System.out.println("Press Enter to continue...");
                scanner.nextLine();
            }
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("=============================================");
        System.out.println("   STUDENT GRADE MANAGEMENT - MAIN MENU");
        System.out.println("=============================================");
        System.out.println("1. Add Student");
        System.out.println("2. View Students");
        System.out.println("3. Record Grade");
        System.out.println("4. View Grade Report");
        System.out.println("5. Exit");
        System.out.print("Enter choice: ");
    }

    // Validation for the choices the user may enter
    private int readMenuChoice() {
        String input = scanner.nextLine();
        try {
            return Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void addStudent() {
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

    private void recordGrade() {
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
        System.out.println("Current Average: " + Math.round(student.calculateAverageGrade() * 10) / 10.0 + "%");

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
                subject = math;
            } else if (pick == 2) {
                subject = english;
            } else {
                subject = science;
            }
        } else {
            System.out.println();
            System.out.println("Available Elective Subjects:");
            System.out.println("1. Music");
            System.out.println("2. Art");
            System.out.println("3. Physical Education");
            int pick = readNumberBetween("Select subject (1-3): ", 1, 3);

            if (pick == 1) {
                subject = music;
            } else if (pick == 2) {
                subject = art;
            } else {
                subject = pe;
            }
        }

        double grade = readGrade();

        System.out.println();
        System.out.println("GRADE CONFIRMATION");
        System.out.println("---------------------------------------------");
        System.out.println("Student: " + student.getStudentId() + " - " + student.getName());
        System.out.println("Subject: " + subject.getSubjectName() + " (" + subject.getSubjectType() + ")");
        System.out.println("Grade: " + grade + "%");
        System.out.println("---------------------------------------------");

        System.out.print("Confirm grade? (Y/N): ");
        String confirm = scanner.nextLine();

        if (confirm.equalsIgnoreCase("Y")) {
            Grade newGrade = new Grade(student.getStudentId(), subject, grade);
            student.recordGrade(grade);
            gradeManager.addGrade(newGrade);
            System.out.println();
            System.out.println("Grade recorded successfully! (" + newGrade.getGradeId() + ")");
            newGrade.displayGradeDetails();
        } else {
            System.out.println("Grade entry cancelled.");
        }
    }

    private void viewGradeReport() {
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

        gradeReportPrinter.printReport(student);
    }

    // Input validation helpers

    // 1. choices
    private int readNumberBetween(String prompt, int min, int max) {
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

    // 2. grades
    private double readGrade() {
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

    // Sample data so the app has content
    private void loadSampleData() {
        RegularStudent aline = new RegularStudent("Aline Mwungeri", 16, "alne@school.sch", "+250780905");
        HonorsStudent jado = new HonorsStudent("Jado fils", 17, "jdo@school.sch", "+25078485");
        RegularStudent eke = new RegularStudent("Eke Nigerian", 15, "eke@school.sch", "+250782098");
        HonorsStudent emmy = new HonorsStudent("Emmy Biryogian", 17, "emmy@school.sch", "+25037367463");
        RegularStudent jordan = new RegularStudent("Jordan Cameronian", 16, "jordan@school.sch", "+250783738");

        studentManager.addStudent(aline);
        studentManager.addStudent(jado);
        studentManager.addStudent(eke);
        studentManager.addStudent(emmy);
        studentManager.addStudent(jordan);

        // Aline's grades
        addSampleGrade(aline, math, 75);
        addSampleGrade(aline, english, 80);
        addSampleGrade(aline, science, 78);
        addSampleGrade(aline, music, 85);
        addSampleGrade(aline, art, 74);

        // Jado's grades
        addSampleGrade(jado, math, 88);
        addSampleGrade(jado, english, 82);
        addSampleGrade(jado, science, 90);
        addSampleGrade(jado, music, 84);
        addSampleGrade(jado, art, 86);
        addSampleGrade(jado, pe, 81);

        // Eke's grades
        addSampleGrade(eke, math, 40);
        addSampleGrade(eke, english, 48);
        addSampleGrade(eke, science, 42);
        addSampleGrade(eke, art, 50);

        // Emmy's grades
        addSampleGrade(emmy, math, 95);
        addSampleGrade(emmy, english, 91);
        addSampleGrade(emmy, science, 94);
        addSampleGrade(emmy, music, 90);
        addSampleGrade(emmy, art, 92);
        addSampleGrade(emmy, pe, 95);

        // Jordan's grades
        addSampleGrade(jordan, math, 70);
        addSampleGrade(jordan, english, 65);
        addSampleGrade(jordan, science, 68);
        addSampleGrade(jordan, music, 66);
        addSampleGrade(jordan, pe, 67);
    }

    private void addSampleGrade(Student student, Subject subject, double score) {
        Grade g = new Grade(student.getStudentId(), subject, score);
        student.recordGrade(score);
        gradeManager.addGrade(g);
    }
}