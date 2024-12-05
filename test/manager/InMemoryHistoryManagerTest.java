package manager;

import classes.tasks.Epic;
import classes.tasks.Task;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    @Test
    void add() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        Task task = new Task("Test ", "Test  description");
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
    void getHistory() {
        TaskManager taskManager = Managers.getDefault();
        for (int i = 0; i < 20; i++) {
            Task task = new Task("Test " + i, "Test  description");
            taskManager.createTask(task);
        }
        for (int i = 1; i < 20; i++) {
            taskManager.getTaskById(i);
        }
        final List<Task> history = taskManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(10, history.size(), "История не пустая.");
        assertEquals(19, history.getLast().getId(), "Не та таска  в конце истории");
        assertEquals(10, history.getFirst().getId(), "Не та таска  в начале истории");
    }
}