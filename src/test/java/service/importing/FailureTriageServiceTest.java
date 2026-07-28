package service.importing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("FailureTriageService")
class FailureTriageServiceTest {

    private FailureTriageService triageService;

    @BeforeEach
    void setUp() {
        triageService = new FailureTriageService();
    }

    @Test
    @DisplayName("orders failures with unknown student ID first, as the most severe")
    void orderBySeverity_unknownStudentIdFirst() {
        List<ImportFailure> failures = List.of(
                new ImportFailure(2, "Grade out of range (105)"),
                new ImportFailure(3, "Invalid student ID (STU999)"),
                new ImportFailure(5, "Unknown subject (Chemistry)")
        );

        List<ImportFailure> ordered = triageService.orderBySeverity(failures);

        assertEquals(3, ordered.get(0).getRowNumber());
        assertTrue(ordered.get(0).getReason().contains("Invalid student ID"));
    }

    @Test
    @DisplayName("orders all four severity levels correctly, worst to least severe")
    void orderBySeverity_fullOrderingIsCorrect() {
        List<ImportFailure> failures = List.of(
                new ImportFailure(1, "Grade out of range (105)"),
                new ImportFailure(2, "Invalid student ID (STU999)"),
                new ImportFailure(3, "Unknown subject (Chemistry)"),
                new ImportFailure(4, "Subject type mismatch for Math (expected Core, got Elective)")
        );

        List<ImportFailure> ordered = triageService.orderBySeverity(failures);

        assertEquals(3, ordered.get(0).getSeverity()); // Invalid student ID
        assertEquals(2, ordered.get(1).getSeverity()); // Unknown subject
        assertEquals(1, ordered.get(2).getSeverity()); // mismatch
        assertEquals(0, ordered.get(3).getSeverity()); // grade out of range
    }

    @Test
    @DisplayName("returns an empty list when there are no failures")
    void orderBySeverity_noFailures_returnsEmptyList() {
        assertTrue(triageService.orderBySeverity(List.of()).isEmpty());
    }
}