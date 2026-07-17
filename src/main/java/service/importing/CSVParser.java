package service.importing;

import exception.InvalidFileFormatException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads and validates the structure of a bulk-import CSV file. Single
 * responsibility: parsing only - it doesn't know what a valid student ID
 * or grade range is, that's BulkImportService's job.
 *
 * Expected format:
 *   StudentID,SubjectName,SubjectType,Grade
 *   STU001,Mathematics,Core,85
 */
public class CSVParser {

    private static final String EXPECTED_HEADER = "StudentID,SubjectName,SubjectType,Grade";
    private static final int EXPECTED_COLUMNS = 4;

    /**
     * Parses the given CSV file into a list of records. Rows with the wrong
     * number of columns are skipped (not thrown) since bulk import is meant
     * to tolerate partial failures - see BulkImportService for how skipped
     * rows get reported. A missing/incorrect header, or an unreadable file,
     * throws immediately since that indicates the whole file is wrong.
     */
    public List<CSVGradeRecord> parse(Path filePath) throws InvalidFileFormatException {
        List<String> lines;
        try {
            lines = Files.readAllLines(filePath);
        } catch (IOException e) {
            throw new InvalidFileFormatException("Could not read file: " + e.getMessage());
        }

        if (lines.isEmpty()) {
            throw new InvalidFileFormatException("File is empty.");
        }

        String header = lines.get(0).trim();
        if (!header.equalsIgnoreCase(EXPECTED_HEADER)) {
            throw new InvalidFileFormatException(
                    "Invalid CSV header. Expected: \"" + EXPECTED_HEADER + "\", found: \"" + header + "\"");
        }

        List<CSVGradeRecord> records = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) {
                continue;
            }

            String[] columns = line.split(",", -1);
            if (columns.length != EXPECTED_COLUMNS) {
                continue; // malformed row - BulkImportService will count this via row-number gaps
            }

            int rowNumber = i + 1; // 1-indexed, matching how a spreadsheet would show it
            records.add(new CSVGradeRecord(
                    rowNumber,
                    columns[0].trim(),
                    columns[1].trim(),
                    columns[2].trim(),
                    columns[3].trim()
            ));
        }

        return records;
    }
}