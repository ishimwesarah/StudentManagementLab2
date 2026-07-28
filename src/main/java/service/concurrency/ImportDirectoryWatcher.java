package service.concurrency;

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class ImportDirectoryWatcher {

    private final Path watchedDirectory;
    private final Consumer<Path> onNewCsvFile;
    private final ExecutorService watcherExecutor;
    private volatile boolean running;
    private WatchService watchService;

    public ImportDirectoryWatcher(Path watchedDirectory, Consumer<Path> onNewCsvFile) {
        this.watchedDirectory = watchedDirectory;
        this.onNewCsvFile = onNewCsvFile;
        this.watcherExecutor = Executors.newSingleThreadExecutor();
    }

    /**
     * Registers the watch synchronously, on the calling thread, before
     * returning - this matters. If registration happened on the
     * background thread instead (e.g. inside submit()), a file created
     * immediately after start() returns could race ahead of the actual
     * registration, and the OS would never report that creation event.
     */
    public void start() throws IOException {
        Files.createDirectories(watchedDirectory);
        watchService = FileSystems.getDefault().newWatchService();
        watchedDirectory.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

        running = true;
        watcherExecutor.submit(this::watchLoop);
    }

    public void stop() {
        running = false;
        watcherExecutor.shutdownNow();
        try {
            if (watchService != null) {
                watchService.close();
            }
        } catch (IOException ignored) {
            // already shutting down, nothing meaningful to do with this
        }
    }

    private void watchLoop() {
        try {
            while (running) {
                WatchKey key = watchService.take();

                for (WatchEvent<?> event : key.pollEvents()) {
                    if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                        Path createdFile = watchedDirectory.resolve((Path) event.context());
                        if (createdFile.toString().endsWith(".csv")) {
                            onNewCsvFile.accept(createdFile);
                        }
                    }
                }

                key.reset();
            }
        } catch (InterruptedException | ClosedWatchServiceException e) {
            Thread.currentThread().interrupt();
        }
    }
}