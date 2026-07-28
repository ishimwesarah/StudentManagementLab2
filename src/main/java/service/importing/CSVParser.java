package service.importing;

import exception.InvalidFileFormatException;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class CSVParser {

    private static final String EXPECTED_HEADER = "StudentID,SubjectName,SubjectType,Grade";
    private static final int EXPECTED_COLUMNS = 4;

    public List<CSVGradeRecord> parse(Path filePath) throws InvalidFileFormatException {
        List<CSVGradeRecord> records = new ArrayList<>();

        try (Stream<String> lineStream = java.nio.file.Files.lines(filePath)) {
            List<String> lines = lineStream.toList();

            if (lines.isEmpty()) {
                throw new InvalidFileFormatException("File is empty.");
            }

            String header = lines.get(0).trim();
            if (!header.equalsIgnoreCase(EXPECTED_HEADER)) {
                throw new InvalidFileFormatException(
                        "Invalid CSV header. Expected: \"" + EXPECTED_HEADER + "\", found: \"" + header + "\"");
            }

            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (line.isEmpty()) {
                    continue;
                }

                String[] columns = line.split(",", -1);
                if (columns.length != EXPECTED_COLUMNS) {
                    continue;
                }

                int rowNumber = i + 1;
                records.add(new CSVGradeRecord(
                        rowNumber,
                        columns[0].trim(),
                        columns[1].trim(),
                        columns[2].trim(),
                        columns[3].trim()
                ));
            }
        } catch (UncheckedIOException | IOException e) {
            throw new InvalidFileFormatException("Could not read file: " + e.getMessage());
        }

        return records;
    }
}