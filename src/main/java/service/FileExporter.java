package service;

import exception.ReportExportException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Writes report text to disk under a reports directory. Single
 * responsibility: file I/O only - it doesn't know or care how the report
 * content was built, that's ReportGenerator's job.
 *
 * The target directory is injected via the constructor (defaulting to
 * "reports" relative to the working directory) rather than hardcoded,
 * so tests can point this at a temp directory directly instead of trying
 * to fake out the JVM's working directory - which doesn't actually work,
 * since java.nio.file caches it at startup.
 */
public class FileExporter {

    private final Path reportsDirectory;

    public FileExporter() {
        this(Paths.get("reports"));
    }

    public FileExporter(Path reportsDirectory) {
        this.reportsDirectory = reportsDirectory;
    }

    /**
     * Writes the given content to {reportsDirectory}/{filename}.txt,
     * creating the directory if it doesn't exist yet.
     *
     * @return the full path the file was written to
     * @throws ReportExportException if the directory can't be created or
     *                                the file can't be written
     */
    public Path exportToFile(String filename, String content) throws ReportExportException {
        try {
            Files.createDirectories(reportsDirectory);

            Path filePath = reportsDirectory.resolve(filename + ".txt");
            Files.writeString(filePath, content, StandardCharsets.UTF_8);

            return filePath;
        } catch (IOException e) {
            throw new ReportExportException("Could not write report to file: " + e.getMessage());
        }
    }

    /**
     * Size of the given file in kilobytes, rounded to one decimal place.
     * Returns 0.0 if the file can't be read for some reason.
     */
    public double fileSizeInKb(Path filePath) {
        try {
            long bytes = Files.size(filePath);
            return Math.round((bytes / 1024.0) * 10) / 10.0;
        } catch (IOException e) {
            return 0.0;
        }
    }
}