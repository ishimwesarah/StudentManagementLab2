package service.concurrency;

import java.time.LocalDateTime;

/**
 * An immutable, complete picture of the dashboard at one moment in time.
 * Because every field is final and set once in the constructor, a
 * reference to a fully-constructed DashboardSnapshot is always safe to
 * read from any thread - there's no way to observe it "half-updated."
 */
public class DashboardSnapshot {

    private final double meanGrade;
    private final double medianGrade;
    private final int[] gradeDistribution;
    private final int activeThreadCount;
    private final LocalDateTime generatedAt;

    public DashboardSnapshot(double meanGrade, double medianGrade, int[] gradeDistribution, int activeThreadCount) {
        this.meanGrade = meanGrade;
        this.medianGrade = medianGrade;
        this.gradeDistribution = gradeDistribution;
        this.activeThreadCount = activeThreadCount;
        this.generatedAt = LocalDateTime.now();
    }

    public double getMeanGrade() {
        return meanGrade;
    }

    public double getMedianGrade() {
        return medianGrade;
    }

    public int[] getGradeDistribution() {
        return gradeDistribution;
    }

    public int getActiveThreadCount() {
        return activeThreadCount;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }
}