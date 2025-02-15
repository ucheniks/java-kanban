package manager;

import classes.tasks.Epic;
import classes.tasks.Status;
import classes.tasks.Subtask;
import classes.tasks.Task;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }

    // Специфичные тесты
    @Test
    void epicStatusAllNew() {
        Epic epic = new Epic("TestEpic", "Description");
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("TestSubtask", "Description", 1);
        subtask.setStatus(Status.NEW);
        taskManager.createSubtask(subtask);

        assertEquals(Status.NEW, taskManager.getEpicById(1).getStatus());
    }

    @Test
    void historyAfterDeletion() {
        for (int i = 0; i < 5; i++) {
            Task task = new Task("Task " + i, "Description");
            taskManager.createTask(task);
            taskManager.getTaskById(i + 1);
        }
        taskManager.deleteTaskById(3);

        List<Task> history = taskManager.getHistory();
        assertEquals(4, history.size());
        assertFalse(history.stream().anyMatch(t -> t.getId() == 3));
    }
}