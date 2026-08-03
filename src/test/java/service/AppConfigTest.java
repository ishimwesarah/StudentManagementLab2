package service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("AppConfig")
class AppConfigTest {

    @Test
    @DisplayName("loads the configured export thread pool size from application.properties")
    void getExportThreadPoolSize_readsConfiguredValue() {
        AppConfig config = new AppConfig();

        int threadPoolSize = config.getExportThreadPoolSize();

        assertEquals(6, threadPoolSize);
    }
}