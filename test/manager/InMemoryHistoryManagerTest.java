package manager;

import classes.tasks.Task;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    @Test
    void add() {
        TaskManager taskManager = Managers.getDefault();
        Task task = new Task("Test ", "Test  description");
        Task task2 = new Task("Test2 ", "Test  description");
        Task task3 = new Task("Test3 ", "Test  description");
        Task task4 = new Task("Test4 ", "Test  description");
        Task task5 = new Task("Test5 ", "Test  description");
        Task task6 = new Task("Test6 ", "Test  description");
        Task task7 = new Task("Test7 ", "Test  description");
        Task task8 = new Task("Test8 ", "Test  description");
        taskManager.createTask(task);
        taskManager.createTask(task2);
        taskManager.createTask(task3);
        taskManager.createTask(task4);
        taskManager.createTask(task5);
        taskManager.createTask(task6);
        taskManager.createTask(task7);
        taskManager.createTask(task8);
        taskManager.getTaskById(1);
        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        taskManager.getTaskById(3);
        taskManager.getTaskById(4);
        taskManager.getTaskById(4);
        taskManager.getTaskById(5);
        taskManager.getTaskById(6);
        taskManager.getTaskById(2);
        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        taskManager.getTaskById(3);
        taskManager.getTaskById(2);
        taskManager.getTaskById(3);

        final List<Task> history = taskManager.getHistory();
        for (Task tasks : history) {
            System.out.println(tasks);
        }
        assertNotNull(history, "История  пустая.");
        assertEquals(6, history.size(), "История  пустая.");
    }

    @Test
    void getHistory() {
        TaskManager taskManager = Managers.getDefault();
        for (int i = 0; i < 20; i++) {
            Task task = new Task("Test " + i, "Test  description");
            taskManager.createTask(task);
        }
        for (int i = 1; i <= 20; i++) {
            taskManager.getTaskById(i);
        }
        final List<Task> history = taskManager.getHistory();
        for (Task task : history) {
            System.out.println(task);
        }
        assertNotNull(history, "История не пустая.");
        assertEquals(20, history.size(), "История не пустая.");
        assertEquals(20, history.getLast().getId(), "Не та таска  в конце истории");
        assertEquals(1, history.getFirst().getId(), "Не та таска  в начале истории");
    }
}