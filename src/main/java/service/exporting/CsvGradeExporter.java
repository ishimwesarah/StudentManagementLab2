package service.exporting;

import exception.ReportExportException;
import model.Exportable;
import model.Grade;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Exports grades as a CSV file, using the same column format CSVParser
 * expects on import (StudentID,SubjectName,SubjectType,Grade) - so data
 * exported here could be re-imported through BulkImportService without
 * any format mismatch.
 */
public class CsvGradeExporter implements Exportable {

    private final Path exportDirectory;

    public CsvGradeExporter() {
        this(Paths.get("exports"));
    }

    public CsvGradeExporter(Path exportDirectory) {
        this.exportDirectory = exportDirectory;
    }

    @Override
    public Path export(List<Grade> grades, String filename) throws ReportExportException {
        StringBuilder sb = new StringBuilder();
        sb.append("StudentID,SubjectName,SubjectType,Grade\n");

        for (Grade g : grades) {
            sb.append(g.getStudentId()).append(",")
                    .append(g.getSubject().getSubjectName()).append(",")
                    .append(g.getSubject().getSubjectType()).append(",")
                    .append(g.getGrade()).append("\n");
        }

        try {
            Files.createDirectories(exportDirectory);
            Path filePath = exportDirectory.resolve(filename + ".csv");
            Files.writeString(filePath, sb.toString(), StandardCharsets.UTF_8);
            return filePath;
        } catch (IOException e) {
            throw new ReportExportException("Could not write CSV export: " + e.getMessage());
        }
    }

    @Override
    public String getFormatName() {
        return "CSV";
    }
}