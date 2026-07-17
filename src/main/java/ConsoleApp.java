import exception.InvalidFileFormatException;
import exception.InvalidGradeException;
import exception.ReportExportException;
import exception.StudentNotFoundException;
import model.*;
import service.*;
import service.importing.BulkImportResult;
import service.importing.BulkImportService;
import service.importing.CSVParser;
import service.importing.ImportFailure;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class ConsoleApp {

    private final Scanner scanner;

    private final StudentManager studentManager;
    private final StudentAverageCalculator studentAverageCalculator;
    private final StudentReportPrinter studentReportPrinter;

    private final GradeManager gradeManager;
    private final GradeAverageCalculator gradeAverageCalculator;
    private final GradeReportPrinter gradeReportPrinter;
    private final GPACalculator gpaCalculator;
    private final GPAReportPrinter gpaReportPrinter;
    private final ClassStatisticsCalculator classStatisticsCalculator;
    private final ClassStatisticsPrinter classStatisticsPrinter;
    private final StudentSearchService studentSearchService;
    private final ReportGenerator reportGenerator;
    private final FileExporter fileExporter;
    private final BulkImportService bulkImportService;

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

        this.gpaCalculator = new GPACalculator();
        this.gpaReportPrinter = new GPAReportPrinter(gradeManager, studentManager, gpaCalculator);

        this.classStatisticsCalculator = new ClassStatisticsCalculator();
        this.classStatisticsPrinter = new ClassStatisticsPrinter(gradeManager, studentManager, classStatisticsCalculator);

        this.studentSearchService = new StudentSearchService(studentManager);

        this.reportGenerator = new ReportGenerator(gradeAverageCalculator);
        this.fileExporter = new FileExporter();

        this.math = new CoreSubject("Mathematics", "MATH101");
        this.english = new CoreSubject("English", "ENG101");
        this.science = new CoreSubject("Science", "SCI101");
        this.music = new ElectiveSubject("Music", "MUS101");
        this.art = new ElectiveSubject("Art", "ART101");
        this.pe = new ElectiveSubject("Physical Education", "PE101");

        this.bulkImportService = new BulkImportService(
                new CSVParser(), studentManager, gradeManager,
                List.of(math, english, science, music, art, pe)
        );
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
                    viewStudentGpa();
                    break;
                case 6:
                    classStatisticsPrinter.printClassStatistics();
                    break;
                case 7:
                    searchStudents();
                    break;
                case 8:
                    exportGradeReport();
                    break;
                case 9:
                    bulkImportGrades();
                    break;
                case 10:
                    System.out.println();
                    System.out.println("Thank you for using Student Grade Management System!");
                    System.out.println("Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 10.");
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
        System.out.println("5. Calculate Student GPA");
        System.out.println("6. View Class Statistics");
        System.out.println("7. Search Students");
        System.out.println("8. Export Grade Report");
        System.out.println("9. Bulk Import Grades");
        System.out.println("10. Exit");
        System.out.print("Enter choice: ");
    }

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

        Student student = promptForExistingStudent();
        if (student == null) {
            System.out.println("Grade entry cancelled.");
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

        double grade = promptForGrade();
        if (grade < 0) {
            System.out.println("Grade entry cancelled.");
            return;
        }

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

        Student student = promptForExistingStudent();
        if (student == null) {
            System.out.println("Cancelled.");
            return;
        }

        gradeReportPrinter.printReport(student);
    }

    private void viewStudentGpa() {
        System.out.println();
        System.out.println("CALCULATE STUDENT GPA");
        System.out.println("---------------------------------------------");

        Student student = promptForExistingStudent();
        if (student == null) {
            System.out.println("Cancelled.");
            return;
        }

        gpaReportPrinter.printGpaReport(student);
    }

    private void searchStudents() {
        System.out.println();
        System.out.println("SEARCH STUDENTS");
        System.out.println("---------------------------------------------");
        System.out.println("Search options:");
        System.out.println("1. By Student ID");
        System.out.println("2. By Name (partial match)");
        System.out.println("3. By Grade Range");
        System.out.println("4. By Student Type");
        int option = readNumberBetween("Select option (1-4): ", 1, 4);

        List<Student> results;

        switch (option) {
            case 1:
                System.out.print("Enter Student ID: ");
                results = studentSearchService.searchById(scanner.nextLine());
                break;
            case 2:
                System.out.print("Enter name (partial or full): ");
                results = studentSearchService.searchByName(scanner.nextLine());
                break;
            case 3:
                double min = readGradeBound("Enter minimum grade (0-100): ");
                double max = readGradeBound("Enter maximum grade (0-100): ");
                results = studentSearchService.searchByGradeRange(min, max);
                break;
            default:
                System.out.println();
                System.out.println("1. Regular");
                System.out.println("2. Honors");
                int typeChoice = readNumberBetween("Select type (1-2): ", 1, 2);
                results = studentSearchService.searchByType(typeChoice == 1 ? "Regular" : "Honors");
                break;
        }

        printSearchResults(results);
    }

    private double readGradeBound(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            try {
                double value = Double.parseDouble(input.trim());
                if (value >= 0 && value <= 100) {
                    return value;
                }
                System.out.println("Please enter a value between 0 and 100.");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private void printSearchResults(List<Student> results) {
        System.out.println();
        System.out.println("SEARCH RESULTS (" + results.size() + " found)");
        System.out.println("---------------------------------------------");

        if (results.isEmpty()) {
            System.out.println("No matching students found.");
            System.out.println("---------------------------------------------");
            return;
        }

        System.out.println("STU ID  | NAME            | TYPE    | AVG");
        System.out.println("---------------------------------------------");
        for (Student s : results) {
            double avg = Math.round(s.calculateAverageGrade() * 10) / 10.0;
            System.out.println(s.getStudentId() + " | " + s.getName() + " | " + s.getStudentType() + " | " + avg + "%");
        }
        System.out.println("---------------------------------------------");
    }

    private void exportGradeReport() {
        System.out.println();
        System.out.println("EXPORT GRADE REPORT");
        System.out.println("---------------------------------------------");

        Student student = promptForExistingStudent();
        if (student == null) {
            System.out.println("Cancelled.");
            return;
        }

        List<Grade> grades = gradeManager.getGradesByStudent(student.getStudentId());

        System.out.println();
        System.out.println("Student: " + student.getStudentId() + " - " + student.getName());
        System.out.println("Type: " + student.getStudentType() + " Student");
        System.out.println("Total Grades: " + grades.size());

        System.out.println();
        System.out.println("Export options:");
        System.out.println("1. Summary Report (overview only)");
        System.out.println("2. Detailed Report (all grades)");
        System.out.println("3. Both");
        int option = readNumberBetween("Select option (1-3): ", 1, 3);

        System.out.print("Enter filename (without extension): ");
        String filename = scanner.nextLine().trim();

        try {
            if (option == 1 || option == 3) {
                String summary = reportGenerator.generateSummaryReport(student, grades);
                String summaryFilename = option == 3 ? filename + "_summary" : filename;
                writeAndConfirm(summaryFilename, summary);
            }
            if (option == 2 || option == 3) {
                String detailed = reportGenerator.generateDetailedReport(student, grades);
                String detailedFilename = option == 3 ? filename + "_detailed" : filename;
                writeAndConfirm(detailedFilename, detailed);
            }
        } catch (ReportExportException e) {
            System.out.println();
            System.out.println("\u2717 ERROR: ReportExportException");
            System.out.println("  " + e.getMessage());
        }
    }

    private void writeAndConfirm(String filename, String content) throws ReportExportException {
        Path path = fileExporter.exportToFile(filename, content);
        double sizeKb = fileExporter.fileSizeInKb(path);

        System.out.println();
        System.out.println("Report exported successfully!");
        System.out.println("  File: " + path.getFileName());
        System.out.println("  Location: " + path.getParent() + "/");
        System.out.println("  Size: " + sizeKb + " KB");
    }

    private void bulkImportGrades() {
        System.out.println();
        System.out.println("BULK IMPORT GRADES");
        System.out.println("---------------------------------------------");
        System.out.println("Place your CSV file in: ./imports/");
        System.out.println();
        System.out.println("CSV Format Required:");
        System.out.println("StudentID,SubjectName,SubjectType,Grade");
        System.out.println("Example: STU001,Mathematics,Core,85");
        System.out.println();

        System.out.print("Enter filename (without extension): ");
        String filename = scanner.nextLine().trim();
        Path filePath = Paths.get("imports", filename + ".csv");

        try {
            BulkImportResult result = bulkImportService.importFromFile(filePath);
            printImportSummary(result);
            writeImportLog(result);
        } catch (InvalidFileFormatException e) {
            System.out.println();
            System.out.println("\u2717 ERROR: InvalidFileFormatException");
            System.out.println("  " + e.getMessage());
        }
    }

    private void printImportSummary(BulkImportResult result) {
        System.out.println();
        System.out.println("IMPORT SUMMARY");
        System.out.println("---------------------------------------------");
        System.out.println("Total Rows: " + result.getTotalRows());
        System.out.println("Successfully Imported: " + result.getSuccessCount());
        System.out.println("Failed: " + result.getFailureCount());

        if (result.getFailureCount() > 0) {
            System.out.println();
            System.out.println("Failed Records:");
            for (ImportFailure failure : result.getFailures()) {
                System.out.println("  Row " + failure.getRowNumber() + ": " + failure.getReason());
            }
        }
    }

    private void writeImportLog(BulkImportResult result) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String logFilename = "import_log_" + LocalDate.now().format(formatter) + ".txt";

        StringBuilder sb = new StringBuilder();
        sb.append("BULK IMPORT LOG\n");
        sb.append("Total Rows: ").append(result.getTotalRows()).append("\n");
        sb.append("Successfully Imported: ").append(result.getSuccessCount()).append("\n");
        sb.append("Failed: ").append(result.getFailureCount()).append("\n\n");

        for (ImportFailure failure : result.getFailures()) {
            sb.append("Row ").append(failure.getRowNumber()).append(": ").append(failure.getReason()).append("\n");
        }

        try {
            Path logDir = Paths.get("imports");
            Files.createDirectories(logDir);
            Path logPath = logDir.resolve(logFilename);
            Files.writeString(logPath, sb.toString(), StandardCharsets.UTF_8);
            System.out.println();
            System.out.println("See " + logFilename + " for details.");
        } catch (IOException e) {
            System.out.println();
            System.out.println("(Could not write import log: " + e.getMessage() + ")");
        }
    }

    private Student promptForExistingStudent() {
        while (true) {
            System.out.print("Enter Student ID: ");
            String studentId = scanner.nextLine();

            try {
                return studentManager.findStudent(studentId);
            } catch (StudentNotFoundException e) {
                System.out.println();
                System.out.println("\u2717 ERROR: StudentNotFoundException");
                System.out.println("  " + e.getMessage());
                System.out.println();
                System.out.println("  Available student IDs: " + String.join(", ", studentManager.getAllStudentIds()));
                System.out.println();
                System.out.print("  Try again? (Y/N): ");
                String retry = scanner.nextLine();
                if (!retry.equalsIgnoreCase("Y")) {
                    return null;
                }
            }
        }
    }

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

    private double parseGrade() throws InvalidGradeException {
        System.out.print("Enter grade (0-100): ");
        String input = scanner.nextLine();

        double value;
        try {
            value = Double.parseDouble(input.trim());
        } catch (NumberFormatException e) {
            throw new InvalidGradeException("Grade must be a valid number. You entered: '" + input.trim() + "'");
        }

        if (value < 0 || value > 100) {
            throw new InvalidGradeException("Grade must be between 0 and 100. You entered: " + value);
        }

        return value;
    }

    private double promptForGrade() {
        while (true) {
            try {
                return parseGrade();
            } catch (InvalidGradeException e) {
                System.out.println();
                System.out.println("\u2717 ERROR: InvalidGradeException");
                System.out.println("  " + e.getMessage());
                System.out.println();
                System.out.print("  Try again? (Y/N): ");
                String retry = scanner.nextLine();
                if (!retry.equalsIgnoreCase("Y")) {
                    return -1;
                }
            }
        }
    }

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

        addSampleGrade(aline, math, 75);
        addSampleGrade(aline, english, 80);
        addSampleGrade(aline, science, 78);
        addSampleGrade(aline, music, 85);
        addSampleGrade(aline, art, 74);

        addSampleGrade(jado, math, 88);
        addSampleGrade(jado, english, 82);
        addSampleGrade(jado, science, 90);
        addSampleGrade(jado, music, 84);
        addSampleGrade(jado, art, 86);
        addSampleGrade(jado, pe, 81);

        addSampleGrade(eke, math, 40);
        addSampleGrade(eke, english, 48);
        addSampleGrade(eke, science, 42);
        addSampleGrade(eke, art, 50);

        addSampleGrade(emmy, math, 95);
        addSampleGrade(emmy, english, 91);
        addSampleGrade(emmy, science, 94);
        addSampleGrade(emmy, music, 90);
        addSampleGrade(emmy, art, 92);
        addSampleGrade(emmy, pe, 95);

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