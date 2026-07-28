package service.importing;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Orders bulk-import failures by severity for review, worst first.
 *
 * PriorityQueue keeps its highest-priority element accessible at the
 * front automatically as items are added, rather than requiring a
 * separate sort step afterward - a genuine fit here since failures
 * naturally arrive one at a time as each row is processed, not as a
 * complete batch that gets sorted once.
 */
public class FailureTriageService {

    public List<ImportFailure> orderBySeverity(List<ImportFailure> failures) {
        PriorityQueue<ImportFailure> queue = new PriorityQueue<>(
                Comparator.comparingInt(ImportFailure::getSeverity).reversed()
        );
        queue.addAll(failures);

        List<ImportFailure> ordered = new ArrayList<>();
        while (!queue.isEmpty()) {
            ordered.add(queue.poll());
        }
        return ordered;
    }
}