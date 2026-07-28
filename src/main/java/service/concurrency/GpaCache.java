package service.concurrency;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A GPA cache with a bounded size and LRU (Least Recently Used) eviction -
 * once the cache reaches maxSize, adding a new entry automatically evicts
 * whichever entry hasn't been accessed (read or written) most recently.
 *
 * Built on LinkedHashMap in "access order" mode rather than a plain
 * HashMap - LinkedHashMap can track which entry was least recently
 * touched internally, and removeEldestEntry() is called automatically
 * whenever a put() would exceed the configured size, letting the JDK
 * do the actual LRU bookkeeping rather than writing it by hand.
 *
 * Wrapped with synchronized instead of ConcurrentHashMap here, because
 * removeEldestEntry()'s eviction decision must happen atomically with
 * the put() that triggered it - ConcurrentHashMap has no equivalent
 * hook for this, so a plain LinkedHashMap plus explicit locking is the
 * correct tool for bounded-size + eviction, even though it trades away
 * ConcurrentHashMap's lock-free reads.
 */
public class GpaCache {

    private final int maxSize;
    private final Map<String, Double> cachedGpaByStudentId;
    private volatile LocalDateTime lastUpdated;

    public GpaCache() {
        this(100);
    }

    public GpaCache(int maxSize) {
        this.maxSize = maxSize;
        this.cachedGpaByStudentId = new LinkedHashMap<>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, Double> eldest) {
                return size() > GpaCache.this.maxSize;
            }
        };
    }

    public synchronized void put(String studentId, double gpa) {
        cachedGpaByStudentId.put(studentId, gpa);
    }

    public synchronized Double get(String studentId) {
        return cachedGpaByStudentId.get(studentId);
    }

    public synchronized int size() {
        return cachedGpaByStudentId.size();
    }

    public void markUpdated() {
        lastUpdated = LocalDateTime.now();
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }
}