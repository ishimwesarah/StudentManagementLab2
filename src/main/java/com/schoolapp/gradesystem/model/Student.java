package com.schoolapp.gradesystem.model;

// "abstract class" = a template that is not 100% finished.
// You can never create a plain "Student" object directly.
// Only finished subclasses like RegularStudent or HonorsStudent
// can actually be created.
public abstract class Student implements Gradable {

    // All fields are "private". This means only code INSIDE this
    // class can touch them directly. Everyone else must use the
    // public getter/setter methods below.
    private String studentId;
    private String name;
    private int age;
    private String email;
    private String phone;
    private String status;

    // "static" means this single number is SHARED by every student
    // that ever gets created, instead of each student having its own.
    // This is how we make sure every student gets a different ID.
    private static int studentCounter = 0;

    // A simple array to store this student's grades.
    // 100 is just a safe maximum size we chose.
    private double[] grades = new double[100];
    private int numberOfGrades = 0;

    public Student(String name, int age, String email, String phone) {
        studentCounter = studentCounter + 1;

        // String.format("%03d", 6) turns the number 6 into "006".
        // It always makes the number 3 digits long, adding zeros in front.
        studentId = "STU" + String.format("%03d", studentCounter);

        this.name = name;
        this.age = age;
        this.email = email;
        this.phone = phone;
        status = "Active";
    }

    // ---------- Getters: let other classes READ our private data ----------
    public String getStudentId() {
        return studentId;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getStatus() {
        return status;
    }

    public int getNumberOfGrades() {
        return numberOfGrades;
    }

    // ---------- Setters: let other classes CHANGE our private data ----------
    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // ---------- Abstract methods ----------
    // These have NO body here. Every subclass MUST write its own
    // version, or the program will not compile.
    public abstract void displayStudentDetails();
    public abstract String getStudentType();
    public abstract double getPassingGrade();

    // ---------- Normal methods shared by every kind of student ----------

    // Adds up all recorded grades and divides by how many there are.
    public double calculateAverageGrade() {
        if (numberOfGrades == 0) {
            return 0.0;
        }

        double total = 0.0;
        for (int i = 0; i < numberOfGrades; i++) {
            total = total + grades[i];
        }
        return total / numberOfGrades;
    }

    // A student is passing if their average is at least their
    // passing grade. getPassingGrade() will give a different
    // answer depending on whether this is really a RegularStudent
    // or an HonorsStudent underneath.
    public boolean isPassing() {
        double average = calculateAverageGrade();
        double passingGrade = getPassingGrade();

        if (average >= passingGrade) {
            return true;
        } else {
            return false;
        }
    }

    // ---------- Required because we wrote "implements Gradable" ----------

    public boolean validateGrade(double grade) {
        if (grade >= 0 && grade <= 100) {
            return true;
        } else {
            return false;
        }
    }

    public boolean recordGrade(double grade) {
        if (validateGrade(grade) == false) {
            return false;
        }
        if (numberOfGrades >= grades.length) {
            return false;
        }

        grades[numberOfGrades] = grade;
        numberOfGrades = numberOfGrades + 1;
        return true;
    }
}
