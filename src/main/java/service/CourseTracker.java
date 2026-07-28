package service;

import model.Grade;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CourseTracker {

    private final GradeManager gradeManager;

    public CourseTracker(GradeManager gradeManager) {
        this.gradeManager = gradeManager;
    }

    public Set<String> getUniqueCourses() {
        Set<String> courses = new HashSet<>();
        for (Grade g : gradeManager.getAllGrades()) {
            courses.add(g.getSubject().getSubjectName());
        }
        return courses;
    }

    public Set<String> getUniqueCoursesForStudent(String studentId) {
        Set<String> courses = new HashSet<>();
        List<Grade> grades = gradeManager.getGradesByStudent(studentId);
        for (Grade g : grades) {
            courses.add(g.getSubject().getSubjectName());
        }
        return courses;
    }

    public int getUniqueCourseCount() {
        return getUniqueCourses().size();
    }
}