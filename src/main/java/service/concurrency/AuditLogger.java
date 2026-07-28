package service.concurrency;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AuditLogger {

    private final ExecutorService loggingExecutor;
    private final Path logFile;

    public AuditLogger() {
        this.loggingExecutor = Executors.newSingleThreadExecutor();
        this.logFile = Paths.get("logs", "audit.log");
    }

    /**
     * Submits an audit entry to be written asynchronously. Returns
     * immediately - the calling thread does not wait for the write to
     * actually happen, since it's handed off to the dedicated logging
     * thread instead.
     */
    public void log(String event) {
        loggingExecutor.submit(() -> writeEntry(event));
    }

    private void writeEntry(String event) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String line = "[" + timestamp + "] " + event + System.lineSeparator();

        try {
            Files.createDirectories(logFile.getParent());
            Files.writeString(logFile, line, StandardCharsets.UTF_8,
                    java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.println("(Audit log write failed: " + e.getMessage() + ")");
        }
    }

    public void shutdown() {
        loggingExecutor.shutdown();
        try {
            loggingExecutor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}