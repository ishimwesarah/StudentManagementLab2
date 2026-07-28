package service.concurrency;

import model.Grade;
import service.ClassStatisticsCalculator;
import service.GradeManager;

import java.util.List;
import java.util.concurrent.*;

public class RealTimeDashboardService {

    private final GradeManager gradeManager;
    private final ClassStatisticsCalculator statisticsCalculator;
    private final ExecutorService computeExecutor;
    private ScheduledExecutorService refreshScheduler;

    private volatile DashboardSnapshot latestSnapshot;

    public RealTimeDashboardService(GradeManager gradeManager, ClassStatisticsCalculator statisticsCalculator) {
        this.gradeManager = gradeManager;
        this.statisticsCalculator = statisticsCalculator;
        this.computeExecutor = Executors.newCachedThreadPool();
    }

    public void startAutoRefresh(long intervalSeconds) {
        refreshScheduler = Executors.newSingleThreadScheduledExecutor();
        refreshScheduler.scheduleAtFixedRate(this::refreshNow, 0, intervalSeconds, TimeUnit.SECONDS);
    }

    public void stopAutoRefresh() {
        if (refreshScheduler != null) {
            refreshScheduler.shutdown();
        }
        computeExecutor.shutdown();
    }

    public DashboardSnapshot getLatestSnapshot() {
        return latestSnapshot;
    }

    /**
     * Computes each dashboard statistic as its own task on the cached
     * thread pool, then combines the results into one immutable
     * snapshot and atomically publishes it via the volatile field.
     * Any reader calling getLatestSnapshot() at any moment either sees
     * the complete previous snapshot or the complete new one - never
     * a partially-built one.
     */
    public void refreshNow() {
        List<Grade> grades = gradeManager.getAllGrades();

        Future<Double> meanFuture = computeExecutor.submit(() -> statisticsCalculator.mean(grades));
        Future<Double> medianFuture = computeExecutor.submit(() -> statisticsCalculator.median(grades));
        Future<int[]> distributionFuture = computeExecutor.submit(() -> statisticsCalculator.gradeDistribution(grades));

        try {
            double mean = meanFuture.get();
            double median = medianFuture.get();
            int[] distribution = distributionFuture.get();
            int activeThreads = Thread.activeCount();

            latestSnapshot = new DashboardSnapshot(mean, median, distribution, activeThreads);
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("(Dashboard refresh failed: " + e.getMessage() + ")");
            Thread.currentThread().interrupt();
        }
    }
}