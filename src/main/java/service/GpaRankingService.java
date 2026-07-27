package service;

import model.Grade;
import model.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class GpaRankingService {

    private final GradeManager gradeManager;
    private final StudentManager studentManager;
    private final GPACalculator gpaCalculator;

    public GpaRankingService(GradeManager gradeManager, StudentManager studentManager, GPACalculator gpaCalculator) {
        this.gradeManager = gradeManager;
        this.studentManager = studentManager;
        this.gpaCalculator = gpaCalculator;
    }

    public TreeMap<Double, List<Student>> buildRankingMap() {
        TreeMap<Double, List<Student>> rankings = new TreeMap<>();

        for (Student student : studentManager.getAllStudents()) {
            List<Grade> grades = gradeManager.getGradesByStudent(student.getStudentId());
            double gpa = gpaCalculator.calculateCumulativeGpa(grades);

            rankings.computeIfAbsent(gpa, key -> new ArrayList<>()).add(student);
        }

        return rankings;
    }

    public List<Student> getTopStudents(int count) {
        List<Student> result = new ArrayList<>();
        TreeMap<Double, List<Student>> rankings = buildRankingMap();

        for (Double gpa : rankings.descendingKeySet()) {
            for (Student student : rankings.get(gpa)) {
                if (result.size() >= count) {
                    return result;
                }
                result.add(student);
            }
        }
        return result;
    }

    public int calculateRank(Student targetStudent) {
        TreeMap<Double, List<Student>> rankings = buildRankingMap();

        List<Grade> targetGrades = gradeManager.getGradesByStudent(targetStudent.getStudentId());
        double targetGpa = gpaCalculator.calculateCumulativeGpa(targetGrades);

        int rank = 1;
        for (Map.Entry<Double, List<Student>> entry : rankings.descendingMap().entrySet()) {
            if (entry.getKey().equals(targetGpa)) {
                return rank;
            }
            rank += entry.getValue().size();
        }
        return rank;
    }
}