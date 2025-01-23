package manager;

import classes.tasks.Epic;
import classes.tasks.Subtask;
import classes.tasks.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    private Path tempFile;
    private FileBackedTaskManager taskManager;

    @BeforeEach
    void set() throws IOException {
        File file = File.createTempFile("test", ".csv");
        tempFile = file.toPath();
        taskManager = new FileBackedTaskManager(tempFile);
    }

    @AfterEach
    void delete() throws IOException {
        Files.deleteIfExists(tempFile);
    }

    @Test
    void saveAndLoadEmptyFile() throws IOException {
        FileBackedTaskManager loadedTaskManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertNotNull(loadedTaskManager);
        assertEquals(0, loadedTaskManager.getListOfTasks().size(), "Список задач должен быть пустым");
        assertEquals(0, loadedTaskManager.getListOfEpics().size(), "Список эпиков должен быть пустым");
        assertEquals(0, loadedTaskManager.getListOfSubtasks().size(), "Список подзадач должен быть пустым");
    }

    @Test
    void saveAndLoadTasks() throws IOException {
        Task task1 = new Task("Task", "Description Task ");
        Epic epic1 = new Epic("Epic", "Description Epic ");
        taskManager.createTask(task1);
        taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask("Subtask", "Description Subtask ", 2);
        taskManager.createSubtask(subtask1);
        FileBackedTaskManager loadedTaskManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertNotNull(loadedTaskManager);
        assertEquals(1, loadedTaskManager.getListOfTasks().size(), "Должна быть 1 задача");
        assertEquals(1, loadedTaskManager.getListOfEpics().size(), "Должен быть 1 эпик");
        assertEquals(1, loadedTaskManager.getListOfSubtasks().size(), "Должна быть 1 подзадача");
        Task loadedTask = loadedTaskManager.getListOfTasks().get(0);
        assertEquals("Task", loadedTask.getName(), "Неверное имя задачи");
        Epic loadedEpic = loadedTaskManager.getListOfEpics().get(0);
        assertEquals("Epic", loadedEpic.getName(), "Неверное имя эпика");
        Subtask loadedSubtask = loadedTaskManager.getListOfSubtasks().get(0);
        assertEquals("Subtask", loadedSubtask.getName(), "Неверное имя подзадачи");
        assertEquals(loadedEpic.getId(), loadedSubtask.getEpicId(), "Неверный id эпика у подзадачи");
    }

    @Test
    void loadFromFile_skipFakeLine() throws IOException {
        Task task1 = new Task("Task", "Description Task");
        taskManager.createTask(task1);
        Files.write(tempFile, List.of("id,type,name,status,description,epic", "1,TASK,Task1,NEW,Description task1,", "fake line"));
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(1, loadedManager.getListOfTasks().size());
    }
}