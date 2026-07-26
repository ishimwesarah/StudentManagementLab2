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
 * Exports grades as JSON. Built by hand with StringBuilder rather than
 * pulling in a JSON library (Jackson, Gson) - the data here is simple
 * enough that a dependency isn't justified just for this, matching the
 * same reasoning that kept SpotBugs out of the Lab 2 project.
 */
public class JsonGradeExporter implements Exportable {

    private final Path exportDirectory;

    public JsonGradeExporter() {
        this(Paths.get("exports"));
    }

    public JsonGradeExporter(Path exportDirectory) {
        this.exportDirectory = exportDirectory;
    }

    @Override
    public Path export(List<Grade> grades, String filename) throws ReportExportException {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");

        for (int i = 0; i < grades.size(); i++) {
            Grade g = grades.get(i);
            sb.append("  {\n");
            sb.append("    \"studentId\": \"").append(escape(g.getStudentId())).append("\",\n");
            sb.append("    \"subjectName\": \"").append(escape(g.getSubject().getSubjectName())).append("\",\n");
            sb.append("    \"subjectType\": \"").append(escape(g.getSubject().getSubjectType())).append("\",\n");
            sb.append("    \"grade\": ").append(g.getGrade()).append(",\n");
            sb.append("    \"date\": \"").append(escape(g.getDate())).append("\"\n");
            sb.append("  }");
            sb.append(i < grades.size() - 1 ? ",\n" : "\n");
        }

        sb.append("]\n");

        try {
            Files.createDirectories(exportDirectory);
            Path filePath = exportDirectory.resolve(filename + ".json");
            Files.writeString(filePath, sb.toString(), StandardCharsets.UTF_8);
            return filePath;
        } catch (IOException e) {
            throw new ReportExportException("Could not write JSON export: " + e.getMessage());
        }
    }

    @Override
    public String getFormatName() {
        return "JSON";
    }

    /**
     * Escapes characters that would otherwise break JSON string syntax -
     * a real subject name is very unlikely to contain these, but this
     * guards against it rather than assuming.
     */
    private String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}