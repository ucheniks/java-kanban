package manager;

import classes.tasks.Task;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private final HistoryManager historyManager = new InMemoryHistoryManager();

    @Test
    void historyWithDuplicates() {
        Task task = new Task("Test", "Description");
        for (int i = 0; i < 5; i++) {
            historyManager.add(task);
        }

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
    }

    @Test
    void removeFromMiddle() {
        Task t1 = new Task("T1", "D1");
        Task t2 = new Task("T2", "D2");
        Task t3 = new Task("T3", "D3");
        TaskManager taskManager = Managers.getDefault();
        taskManager.createTask(t1);
        taskManager.createTask(t2);
        taskManager.createTask(t3);
        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        taskManager.getTaskById(3);
        taskManager.deleteTaskById(2);
        assertEquals(List.of(taskManager.getTaskById(1), taskManager.getTaskById(3)), taskManager.getHistory());
    }
}