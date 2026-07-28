package service.concurrency;

import model.Grade;
import model.Student;
import service.GPACalculator;
import service.GradeManager;
import service.StudentManager;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GpaRecalculationScheduler {

    private final StudentManager studentManager;
    private final GradeManager gradeManager;
    private final GPACalculator gpaCalculator;
    private final GpaCache gpaCache;

    private ScheduledExecutorService scheduler;

    public GpaRecalculationScheduler(StudentManager studentManager, GradeManager gradeManager,
                                     GPACalculator gpaCalculator, GpaCache gpaCache) {
        this.studentManager = studentManager;
        this.gradeManager = gradeManager;
        this.gpaCalculator = gpaCalculator;
        this.gpaCache = gpaCache;
    }

    /**
     * Starts recalculating every student's GPA on a fixed interval,
     * running in the background independently of the console's main thread.
     */
    public void start(long intervalSeconds) {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::recalculateAll, 0, intervalSeconds, TimeUnit.SECONDS);
    }

    public void stop() {
        if (scheduler != null) {
            scheduler.shutdown();
        }
    }

    private void recalculateAll() {
        for (Student student : studentManager.getAllStudents()) {
            List<Grade> grades = gradeManager.getGradesByStudent(student.getStudentId());
            double gpa = gpaCalculator.calculateCumulativeGpa(grades);
            gpaCache.put(student.getStudentId(), gpa);
        }
        gpaCache.markUpdated();
    }
}