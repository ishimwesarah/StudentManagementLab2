package service.concurrency;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GpaCache {

    private final Map<String, Double> cachedGpaByStudentId = new ConcurrentHashMap<>();
    private volatile LocalDateTime lastUpdated;

    public void put(String studentId, double gpa) {
        cachedGpaByStudentId.put(studentId, gpa);
    }

    public Double get(String studentId) {
        return cachedGpaByStudentId.get(studentId);
    }

    public int size() {
        return cachedGpaByStudentId.size();
    }

    public void markUpdated() {
        lastUpdated = LocalDateTime.now();
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }
}