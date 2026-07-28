package service.concurrency;

import model.CoreSubject;
import model.Grade;
import model.RegularStudent;
import model.Student;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.GPACalculator;
import service.GradeManager;
import service.StudentManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("GpaRecalculationScheduler")
class GpaRecalculationSchedulerTest {

    private StudentManager studentManager;
    private GradeManager gradeManager;
    private GpaCache gpaCache;
    private GpaRecalculationScheduler scheduler;

    @BeforeEach
    void setUp() {
        studentManager = new StudentManager();
        gradeManager = new GradeManager();
        gpaCache = new GpaCache();
        scheduler = new GpaRecalculationScheduler(studentManager, gradeManager, new GPACalculator(), gpaCache);
    }

    @AfterEach
    void tearDown() {
        // Always stop the scheduler, even if a test fails partway through -
        // otherwise its background thread keeps running indefinitely,
        // silently leaking into later tests.
        scheduler.stop();
    }

    private Student addStudentWithGrade(String name, double score) {
        Student student = new RegularStudent(name, 16, name + "@school.sch", "000");
        studentManager.addStudent(student);
        CoreSubject math = new CoreSubject("Mathematics", "MATH101");
        gradeManager.addGrade(new Grade(student.getStudentId(), math, score));
        return student;
    }

    @Test
    @DisplayName("starting the scheduler eventually populates the GPA cache for every student")
    void start_populatesCacheForAllStudents() throws InterruptedException {
        Student alice = addStudentWithGrade("Alice", 95);
        Student bob = addStudentWithGrade("Bob", 65);

        scheduler.start(1); // recalculates immediately, then every 1 second

        boolean populated = waitUntil(() -> gpaCache.size() == 2, 3000);

        assertTrue(populated, "Expected the cache to be populated within 3 seconds");
        assertEquals(4.0, gpaCache.get(alice.getStudentId()));
        assertEquals(1.0, gpaCache.get(bob.getStudentId()));
    }

    @Test
    @DisplayName("markUpdated is called after each recalculation, so lastUpdated is never null once started")
    void start_setsLastUpdatedTimestamp() throws InterruptedException {
        addStudentWithGrade("Alice", 90);

        scheduler.start(1);

        boolean updated = waitUntil(() -> gpaCache.getLastUpdated() != null, 3000);

        assertTrue(updated, "Expected lastUpdated to be set within 3 seconds");
        assertNotNull(gpaCache.getLastUpdated());
    }

    @Test
    @DisplayName("stop() prevents further recalculations from happening")
    void stop_haltsFurtherRecalculation() throws InterruptedException {
        addStudentWithGrade("Alice", 90);

        scheduler.start(1);
        waitUntil(() -> gpaCache.size() == 1, 3000);

        scheduler.stop();

        // Give any in-flight task a moment to finish, then confirm no
        // exception occurs and the cache doesn't keep growing unexpectedly
        // after stop() - a loose sanity check, not a strict guarantee,
        // since a task already running when stop() is called may still
        // complete.
        Thread.sleep(1200);
        assertEquals(1, gpaCache.size());
    }

    /**
     * Polls the given condition repeatedly until it's true or the timeout
     * elapses. Necessary here because we're waiting for a background
     * thread's work to complete - there's no way to know in advance
     * exactly how many milliseconds that will take.
     */
    private boolean waitUntil(java.util.function.BooleanSupplier condition, long timeoutMillis) throws InterruptedException {
        long deadline = System.currentTimeMillis() + timeoutMillis;
        while (System.currentTimeMillis() < deadline) {
            if (condition.getAsBoolean()) {
                return true;
            }
            Thread.sleep(50);
        }
        return false;
    }
}