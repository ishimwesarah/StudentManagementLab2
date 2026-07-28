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

    public ImportDirectoryWatcher(Path watchedDirectory, Consumer<Path> onNewCsvFile) {
        this.watchedDirectory = watchedDirectory;
        this.onNewCsvFile = onNewCsvFile;
        this.watcherExecutor = Executors.newSingleThreadExecutor();
    }

    public void start() throws IOException {
        Files.createDirectories(watchedDirectory);
        running = true;
        watcherExecutor.submit(this::watchLoop);
    }

    public void stop() {
        running = false;
        watcherExecutor.shutdownNow();
    }

    private void watchLoop() {
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            watchedDirectory.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

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
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            System.out.println("(Directory watcher failed: " + e.getMessage() + ")");
        }
    }
}