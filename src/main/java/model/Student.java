package model;


public abstract class Student implements Gradable {

    private String studentId;
    private String name;
    private int age;
    private String email;
    private String phone;
    private String status;

    private static int studentCounter = 1;

    private double[] grades = new double[100];
    private int numberOfGrades = 0;


    public Student(String name, int age, String email, String phone) {
        studentId = "STU" + String.format("%03d", studentCounter++);

        this.name = name;
        this.age = age;
        this.email = email;
        this.phone = phone;
        status = "Active";
    }

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

    /**
     * @return how many grades have been recorded for this student so far
     */
    public int getNumberOfGrades() {
        return numberOfGrades;
    }

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

    /**
     * Prints this student's full profile to the console. Format differs
     * by subtype (e.g. HonorsStudent additionally shows honors eligibility).
     */
    public abstract void displayStudentDetails();

    /**
     * @return a short label identifying the concrete student type, e.g.
     *         {@code "Regular"} or {@code "Honors"}
     */
    public abstract String getStudentType();

    /**
     * @return the minimum average grade this student type needs to be
     *         considered passing
     */
    public abstract double getPassingGrade();

    /**
     * @return the simple average of all recorded grades, or {@code 0.0}
     *         if no grades have been recorded yet
     */
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

    /**
     * @return {@code true} if this student's current average meets or
     *         exceeds {@link #getPassingGrade()}
     */
    public boolean isPassing() {
        double average = calculateAverageGrade();
        double passingGrade = getPassingGrade();

        if (average >= passingGrade) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param grade the grade to validate
     * @return {@code true} if the grade falls within the accepted 0-100 range
     */
    @Override
    public boolean validateGrade(double grade) {
        if (grade >= 0 && grade <= 100) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Validates and stores a new grade for this student.
     *
     * @param grade the grade to record
     * @return {@code true} if the grade was valid and there was room to
     *         store it; {@code false} if the grade was invalid or the
     *         student's grade history is full
     */
    @Override
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