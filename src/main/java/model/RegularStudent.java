package model;

// "extends Student" means: RegularStudent automatically gets
// everything Student already has (name, age, email, etc.)
// for free. We only need to write what is DIFFERENT here.
public class RegularStudent extends Student {

    private double passingGrade = 50.0;

    public RegularStudent(String name, int age, String email, String phone) {
        // super(...) sends the basic details up to the Student
        // class, which does the shared setup work (like the ID).
        super(name, age, email, phone);
    }

    public String getStudentType() {
        return "Regular";
    }

    public double getPassingGrade() {
        return passingGrade;
    }

    public void displayStudentDetails() {
        System.out.println("Student ID: " + getStudentId());
        System.out.println("Name: " + getName());
        System.out.println("Type: " + getStudentType());
        System.out.println("Age: " + getAge());
        System.out.println("Email: " + getEmail());
        System.out.println("Phone: " + getPhone());
        System.out.println("Passing Grade: 50%");
        System.out.println("Status: " + getStatus());
    }
}
