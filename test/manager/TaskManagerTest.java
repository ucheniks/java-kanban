package manager;

import classes.tasks.Epic;
import classes.tasks.Status;
import classes.tasks.Subtask;
import classes.tasks.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    protected abstract T createTaskManager();

    @BeforeEach
    void initTaskManager() {
        taskManager = createTaskManager();
    }


    @Test
    void createTask() {
        Task task = new Task("Test", "Description");
        taskManager.createTask(task);
        Task savedTask = taskManager.getTaskById(task.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
    }

    @Test
    void createEpic() {
        Epic epic = new Epic("TestEpic", "Description");
        taskManager.createEpic(epic);
        Epic savedEpic = taskManager.getEpicById(epic.getId());

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");
    }

    @Test
    void createSubtask() {
        Epic epic = new Epic("TestEpic", "Description");
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("TestSubtask", "Description", epic.getId());
        taskManager.createSubtask(subtask);
        Subtask savedSubtask = taskManager.getSubtaskById(subtask.getId());

        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(subtask, savedSubtask, "Подзадачи не совпадают.");
    }

    @Test
    void updateTask() {
        Task task = new Task("Test", "Description");
        taskManager.createTask(task);
        task.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(task);

        assertEquals(Status.IN_PROGRESS, taskManager.getTaskById(task.getId()).getStatus(), "Статус задачи не обновлён.");
    }

    @Test
    void updateEpic() {
        Epic epic = new Epic("TestEpic", "Description");
        taskManager.createEpic(epic);
        epic.setStatus(Status.IN_PROGRESS);
        taskManager.updateEpic(epic);

        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(epic.getId()).getStatus(), "Статус эпика не обновлён.");
    }

    @Test
    void updateSubtask() {
        Epic epic = new Epic("TestEpic", "Description");
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("TestSubtask", "Description", epic.getId());
        taskManager.createSubtask(subtask);
        subtask.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask);

        assertEquals(Status.IN_PROGRESS, taskManager.getSubtaskById(subtask.getId()).getStatus(), "Статус подзадачи не обновлён.");
    }

    @Test
    void deleteTaskById() {
        Task task = new Task("Test", "Description");
        taskManager.createTask(task);
        taskManager.deleteTaskById(task.getId());

        System.out.println(taskManager.getListOfTasks());
    }

    @Test
    void deleteEpicById() {
        Epic epic = new Epic("TestEpic", "Description");
        taskManager.createEpic(epic);
        taskManager.deleteEpicById(epic.getId());
        System.out.println(taskManager.getListOfEpics());
    }

    @Test
    void deleteSubtaskById() {
        Epic epic = new Epic("TestEpic", "Description");
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("TestSubtask", "Description", epic.getId());
        taskManager.createSubtask(subtask);
        taskManager.deleteSubtaskById(subtask.getId());
        System.out.println(taskManager.getListOfSubtasks());
    }

    @Test
    void getListOfTasks() {
        Task task1 = new Task("Task1", "Description");
        Task task2 = new Task("Task2", "Description");
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        List<Task> tasks = taskManager.getListOfTasks();
        assertEquals(2, tasks.size(), "Неверное количество задач.");
        assertTrue(tasks.contains(task1), "Список задач не содержит task1.");
        assertTrue(tasks.contains(task2), "Список задач не содержит task2.");
    }

    @Test
    void getListOfEpics() {
        Epic epic1 = new Epic("Epic1", "Description");
        Epic epic2 = new Epic("Epic2", "Description");
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        List<Epic> epics = taskManager.getListOfEpics();
        assertEquals(2, epics.size(), "Неверное количество эпиков.");
        assertTrue(epics.contains(epic1), "Список эпиков не содержит epic1.");
        assertTrue(epics.contains(epic2), "Список эпиков не содержит epic2.");
    }

    @Test
    void getListOfSubtasks() {
        Epic epic = new Epic("TestEpic", "Description");
        taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask("Subtask1", "Description", epic.getId());
        Subtask subtask2 = new Subtask("Subtask2", "Description", epic.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        List<Subtask> subtasks = taskManager.getListOfSubtasks();
        assertEquals(2, subtasks.size(), "Неверное количество подзадач.");
        assertTrue(subtasks.contains(subtask1), "Список подзадач не содержит subtask1.");
        assertTrue(subtasks.contains(subtask2), "Список подзадач не содержит subtask2.");
    }

    @Test
    void getListOfSubtasksOfEpic() {
        Epic epic = new Epic("TestEpic", "Description");
        taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask("Subtask1", "Description", epic.getId());
        Subtask subtask2 = new Subtask("Subtask2", "Description", epic.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        List<Subtask> subtasks = taskManager.getListOfSubtasksOfEpic(epic);
        assertEquals(2, subtasks.size(), "Неверное количество подзадач у эпика.");
        assertTrue(subtasks.contains(subtask1), "Список подзадач эпика не содержит subtask1.");
        assertTrue(subtasks.contains(subtask2), "Список подзадач эпика не содержит subtask2.");
    }

    @Test
    void getPrioritizedTasks() {
        Task task1 = new Task("Task1", "Description");
        task1.setStartTime("19.06.2005 10:00");
        task1.setDuration(60);
        taskManager.createTask(task1);

        Task task2 = new Task("Task2", "Description");
        task2.setStartTime("19.06.2005 09:00");
        task2.setDuration(60);
        taskManager.createTask(task2);

        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        assertEquals(task2, prioritizedTasks.get(0), "Задачи не отсортированы по времени.");
    }

    @Test
    void checkTaskTimeIntersection() {
        Task task1 = new Task("Task1", "Description");
        task1.setStartTime("19.06.2005 10:00");
        task1.setDuration(60);
        taskManager.createTask(task1);

        Task task2 = new Task("Task2", "Description");
        task2.setStartTime("19.06.2005 10:30");
        task2.setDuration(60);
        taskManager.createTask(task2);
        assertEquals(1, taskManager.getListOfTasks().size(), "Пересечение времени не обнаружено.");
        assertEquals(task1, taskManager.getListOfTasks().getFirst(), "Пересечение времени не обнаружено.");
    }
}