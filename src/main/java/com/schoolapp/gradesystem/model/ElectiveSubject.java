package com.schoolapp.gradesystem.model;

public class ElectiveSubject extends Subject {

    public ElectiveSubject(String subjectName, String subjectCode) {
        super(subjectName, subjectCode);
    }

    public boolean isMandatory() {
        return false;
    }

    public String getSubjectType() {
        return "Elective";
    }

    public void displaySubjectDetails() {
        System.out.println(getSubjectName() + " (" + getSubjectCode() + ") - Elective Subject - Optional");
    }
}
