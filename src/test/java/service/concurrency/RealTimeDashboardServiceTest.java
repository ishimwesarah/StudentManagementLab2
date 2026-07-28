package service.concurrency;

import model.CoreSubject;
import model.Grade;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.ClassStatisticsCalculator;
import service.GradeManager;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RealTimeDashboardService")
class RealTimeDashboardServiceTest {

    private GradeManager gradeManager;
    private RealTimeDashboardService dashboardService;
    private CoreSubject math;

    @BeforeEach
    void setUp() {
        gradeManager = new GradeManager();
        dashboardService = new RealTimeDashboardService(gradeManager, new ClassStatisticsCalculator());
        math = new CoreSubject("Mathematics", "MATH101");
    }

    @AfterEach
    void tearDown() {
        dashboardService.stopAutoRefresh();
    }

    @Test
    @DisplayName("getLatestSnapshot returns null before any refresh has happened")
    void getLatestSnapshot_beforeAnyRefresh_returnsNull() {
        assertNull(dashboardService.getLatestSnapshot());
    }

    @Test
    @DisplayName("refreshNow computes correct statistics from current grade data")
    void refreshNow_computesCorrectStatistics() {
        gradeManager.addGrade(new Grade("STU001", math, 80));
        gradeManager.addGrade(new Grade("STU002", math, 90));

        dashboardService.refreshNow();

        DashboardSnapshot snapshot = dashboardService.getLatestSnapshot();
        assertNotNull(snapshot);
        assertEquals(85.0, snapshot.getMeanGrade());
        assertEquals(85.0, snapshot.getMedianGrade());
    }

    @Test
    @DisplayName("startAutoRefresh eventually produces a snapshot without manual refreshNow() calls")
    void startAutoRefresh_eventuallyProducesSnapshot() throws InterruptedException {
        gradeManager.addGrade(new Grade("STU001", math, 75));

        dashboardService.startAutoRefresh(1);

        boolean populated = waitUntil(() -> dashboardService.getLatestSnapshot() != null, 3000);

        assertTrue(populated, "Expected a snapshot to be produced automatically within 3 seconds");
    }

    @Test
    @DisplayName("a reader never observes a partially-built snapshot, even during repeated concurrent refreshes")
    void refreshNow_readerNeverSeesPartialSnapshot() throws InterruptedException {
        for (int i = 0; i < 20; i++) {
            gradeManager.addGrade(new Grade("STU00" + i, math, 60 + i));
        }

        AtomicBoolean sawInconsistentSnapshot = new AtomicBoolean(false);
        AtomicBoolean keepReading = new AtomicBoolean(true);

        Thread reader = new Thread(() -> {
            while (keepReading.get()) {
                DashboardSnapshot snapshot = dashboardService.getLatestSnapshot();
                if (snapshot != null) {
                    // Every field on a given snapshot instance was set
                    // together in one constructor call - if any field
                    // were ever null/unset while others were populated,
                    // that would indicate unsafe partial publication.
                    if (snapshot.getGradeDistribution() == null) {
                        sawInconsistentSnapshot.set(true);
                    }
                }
            }
        });

        reader.start();

        for (int i = 0; i < 10; i++) {
            dashboardService.refreshNow();
        }

        keepReading.set(false);
        reader.join();

        assertFalse(sawInconsistentSnapshot.get(), "A reader observed a snapshot with an unset field mid-refresh");
    }

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