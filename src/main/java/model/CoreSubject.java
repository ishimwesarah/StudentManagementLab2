package model;

public class CoreSubject extends Subject {

    public CoreSubject(String subjectName, String subjectCode) {
        super(subjectName, subjectCode);
    }
    @Override
    public boolean isMandatory() {
        return true;
    }
    @Override
    public String getSubjectType() {
        return "Core";
    }
    @Override
    public void displaySubjectDetails() {
        System.out.println(getSubjectName() + " (" + getSubjectCode() + ") - Core Subject - Mandatory");
    }
}
