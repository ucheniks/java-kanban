package manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private Path tempFile;

    @Override
    protected FileBackedTaskManager createTaskManager() {
        try {
            tempFile = Files.createTempFile("tasks", ".csv");
            return FileBackedTaskManager.loadFromFile(tempFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    void cleanup() throws IOException {
        Files.deleteIfExists(tempFile);
    }

    @Test
    void loadFromFileWithBrokenData() throws IOException {
        Files.writeString(tempFile, "id,type,name,status,description,epic,startTime,duration\nerror");

        assertDoesNotThrow(() -> FileBackedTaskManager.loadFromFile(tempFile));
    }
}