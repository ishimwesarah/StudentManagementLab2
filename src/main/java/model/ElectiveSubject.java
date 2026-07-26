package model;

public class ElectiveSubject extends Subject {

    public ElectiveSubject(String subjectName, String subjectCode) {
        super(subjectName, subjectCode);
    }
    @Override
    public boolean isMandatory() {
        return false;
    }
    @Override
    public String getSubjectType() {
        return "Elective";
    }
    @Override
    public void displaySubjectDetails() {
        System.out.println(getSubjectName() + " (" + getSubjectCode() + ") - Elective Subject - Optional");
    }
}
