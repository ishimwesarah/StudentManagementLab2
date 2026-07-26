package model;

public class HonorsStudent extends Student {

    private double passingGrade = 60.0;
    private boolean honorsEligible = false;

    public HonorsStudent(String name, int age, String email, String phone) {
        super(name, age, email, phone);
    }
    @Override
    public String getStudentType() {
        return "Honors";
    }
    @Override
    public double getPassingGrade() {
        return passingGrade;
    }

    // Looks at the CURRENT average grade and decides if this
    // student currently qualifies for honors (85% or higher).
    public boolean checkHonorsEligibility() {
        double average = calculateAverageGrade();

        if (average >= 85.0) {
            honorsEligible = true;
        } else {
            honorsEligible = false;
        }

        return honorsEligible;
    }

    public boolean isHonorsEligible() {
        return honorsEligible;
    }

    @Override
    public void displayStudentDetails() {
        checkHonorsEligibility();

        System.out.println("Student ID: " + getStudentId());
        System.out.println("Name: " + getName());
        System.out.println("Type: " + getStudentType());
        System.out.println("Age: " + getAge());
        System.out.println("Email: " + getEmail());
        System.out.println("Phone: " + getPhone());
        System.out.println("Passing Grade: 60%");

        if (honorsEligible) {
            System.out.println("Honors Eligible: Yes");
        } else {
            System.out.println("Honors Eligible: No");
        }

        System.out.println("Status: " + getStatus());
    }
}
