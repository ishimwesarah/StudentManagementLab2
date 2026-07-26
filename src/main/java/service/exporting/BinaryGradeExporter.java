package service.exporting;

import exception.ReportExportException;
import model.Exportable;
import model.Grade;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Exports grades using Java's built-in object serialization - the actual
 * Grade/Subject objects get written as raw bytes, not text. Not human
 * readable, but preserves exact object structure and can be read back
 * into real Grade objects without any parsing step.
 */
public class BinaryGradeExporter implements Exportable {

    private final Path exportDirectory;

    public BinaryGradeExporter() {
        this(Paths.get("exports"));
    }

    public BinaryGradeExporter(Path exportDirectory) {
        this.exportDirectory = exportDirectory;
    }

    @Override
    public Path export(List<Grade> grades, String filename) throws ReportExportException {
        try {
            Files.createDirectories(exportDirectory);
            Path filePath = exportDirectory.resolve(filename + ".bin");

            try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(filePath))) {
                out.writeObject(new ArrayList<>(grades));
            }

            return filePath;
        } catch (IOException e) {
            throw new ReportExportException("Could not write binary export: " + e.getMessage());
        }
    }

    @Override
    public String getFormatName() {
        return "Binary";
    }
}