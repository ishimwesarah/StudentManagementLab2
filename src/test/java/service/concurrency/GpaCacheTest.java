package service.concurrency;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("GpaCache")
class GpaCacheTest {

    private GpaCache cache;

    @BeforeEach
    void setUp() {
        cache = new GpaCache(3);
    }

    @Test
    @DisplayName("stores and retrieves values correctly under the size limit")
    void put_underLimit_allEntriesRetained() {
        cache.put("STU001", 3.5);
        cache.put("STU002", 3.8);

        assertEquals(3.5, cache.get("STU001"));
        assertEquals(3.8, cache.get("STU002"));
        assertEquals(2, cache.size());
    }

    @Test
    @DisplayName("adding beyond maxSize evicts the least recently used entry, not just the oldest by insertion")
    void put_beyondMaxSize_evictsLeastRecentlyUsed() {
        cache.put("STU001", 3.5);
        cache.put("STU002", 3.8);
        cache.put("STU003", 2.9);
        // Cache is now full (size 3). Access STU001 - this makes it the
        // MOST recently used, even though it was inserted first.
        cache.get("STU001");

        // Adding a 4th entry should evict STU002, since it's now the
        // least recently used (STU001 was just touched, STU003 was
        // touched more recently than STU001 by insertion order alone -
        // but STU001's read moved it ahead of STU003 too).
        cache.put("STU004", 3.2);

        assertNull(cache.get("STU002"), "STU002 should have been evicted as least recently used");
        assertEquals(3.5, cache.get("STU001"), "STU001 should survive - it was accessed right before the new entry was added");
        assertEquals(3, cache.size());
    }

    @Test
    @DisplayName("cache never grows beyond maxSize, no matter how many entries are added")
    void put_manyEntries_neverExceedsMaxSize() {
        for (int i = 0; i < 10; i++) {
            cache.put("STU00" + i, (double) i);
        }

        assertEquals(3, cache.size());
    }

    @Test
    @DisplayName("markUpdated sets a non-null timestamp")
    void markUpdated_setsTimestamp() {
        cache.markUpdated();

        assertEquals(cache.getLastUpdated().getClass().getSimpleName(), "LocalDateTime");
    }
}