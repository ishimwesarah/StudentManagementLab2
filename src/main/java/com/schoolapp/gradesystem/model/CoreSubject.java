package com.schoolapp.gradesystem.model;

public class CoreSubject extends Subject {

    public CoreSubject(String subjectName, String subjectCode) {
        super(subjectName, subjectCode);
    }

    public boolean isMandatory() {
        return true;
    }

    public String getSubjectType() {
        return "Core";
    }

    public void displaySubjectDetails() {
        System.out.println(getSubjectName() + " (" + getSubjectCode() + ") - Core Subject - Mandatory");
    }
}
