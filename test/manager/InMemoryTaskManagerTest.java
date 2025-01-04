package manager;

import classes.tasks.Epic;
import classes.tasks.Status;
import classes.tasks.Subtask;
import classes.tasks.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    public TaskManager taskManager;

    @BeforeEach
    public void initTaskManager() {
        taskManager = Managers.getDefault();
    }

    @Test
    void createTask() {
        Task task = new Task("Test ", "Test  description");
        taskManager.createTask(task);
        final int taskId = 1;

        final Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getListOfTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.getFirst(), "Задачи не совпадают.");
    }

    @Test
    void createEpic() {
        Epic epic = new Epic("Test ", "Test  description");
        taskManager.createEpic(epic);
        final int epicId = 1;

        final Epic savedEpic = taskManager.getEpicById(epicId);

        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(epic, savedEpic, "Задачи не совпадают.");

        final List<Epic> epics = taskManager.getListOfEpics();

        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic, epics.getFirst(), "Задачи не совпадают.");
    }

    @Test
    void createSubtask() {
        Epic epic = new Epic("Test ", "Test  description");
        Subtask subtask = new Subtask("Test a", "Test a description", 1);
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        final int subtaskId = 2;

        final Subtask savedSubtask = taskManager.getSubtaskById(subtaskId);

        assertNotNull(savedSubtask, "Задача не найдена.");
        assertEquals(subtask, savedSubtask, "Задачи не совпадают.");

        final List<Subtask> subtasks = taskManager.getListOfSubtasks();

        assertNotNull(subtasks, "Задачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество задач.");
        assertEquals(subtask, subtasks.getFirst(), "Задачи не совпадают.");
    }


    @Test
    void deleteTasks() {
        for (int i = 0; i < 20; i++) {
            Task task = new Task("Test " + i, "Test  description");
            taskManager.createTask(task);
        }
        for (int i = 0; i < 20; i++) {
            Epic task = new Epic("Epic " + i, "Test  description");
            taskManager.createEpic(task);
        }
        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        taskManager.getTaskById(3);
        taskManager.getTaskById(1);
        taskManager.getEpicById(21);
        taskManager.getEpicById(21);
        taskManager.getEpicById(22);
        taskManager.getEpicById(23);
        taskManager.getTaskById(1);
        taskManager.getTaskById(1);
        taskManager.getTaskById(1);

        final List<Task> history = taskManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(6, history.size(), "История не пустая.");
        taskManager.deleteTasks();
        final List<Task> history1 = taskManager.getHistory();
        assertEquals(new ArrayList<>(), taskManager.getListOfTasks(), "Задачи не удалились");
        assertEquals(3, history1.size(), "Неправильно удалилось");
    }

    @Test
    void deleteEpics() {
        for (int i = 0; i < 20; i++) {
            Task task = new Task("Test " + i, "Test  description");
            taskManager.createTask(task);
        }
        for (int i = 0; i < 20; i++) {
            Epic task = new Epic("Epic " + i, "Test  description");
            taskManager.createEpic(task);
        }
        taskManager.createSubtask(new Subtask("Test", "test description", 21));
        taskManager.createSubtask(new Subtask("Test", "test description", 21));
        taskManager.createSubtask(new Subtask("Test", "test description", 22));
        taskManager.createSubtask(new Subtask("Test", "test description", 23));
        taskManager.createSubtask(new Subtask("Test", "test description", 24));
        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        taskManager.getTaskById(3);
        taskManager.getTaskById(1);
        taskManager.getEpicById(21);
        taskManager.getEpicById(21);
        taskManager.getEpicById(22);
        taskManager.getEpicById(23);
        taskManager.getTaskById(1);
        taskManager.getTaskById(1);
        taskManager.getTaskById(1);
        taskManager.getSubtaskById(41);
        taskManager.getSubtaskById(42);
        taskManager.getSubtaskById(43);
        taskManager.deleteEpics();
        final List<Task> history1 = taskManager.getHistory();
        assertEquals(new ArrayList<>(), taskManager.getListOfEpics(), "Задачи не удалились");
        assertEquals(3, history1.size(), "Неправильно удалилась");
    }

    @Test
    void deleteSubtasks() {
        Epic epic = new Epic("Test", "TestTest");
        Epic epic1 = new Epic("Test1", "TestTest1");
        taskManager.createEpic(epic);
        taskManager.createEpic(epic1);
        for (int i = 0; i < 10; i++) {
            Subtask subtask = new Subtask("Test " + i, "Test  description", 1);
            taskManager.createSubtask(subtask);
        }
        for (int i = 10; i < 20; i++) {
            Subtask subtask = new Subtask("Test " + i, "Test  description", 2);
            taskManager.createSubtask(subtask);
        }
        taskManager.getSubtaskById(3);
        taskManager.getSubtaskById(4);
        taskManager.getSubtaskById(5);
        taskManager.getSubtaskById(3);
        taskManager.getSubtaskById(4);
        taskManager.getSubtaskById(5);
        taskManager.getSubtaskById(3);
        taskManager.getSubtaskById(4);
        taskManager.getEpicById(1);
        taskManager.getEpicById(2);
        taskManager.getSubtaskById(5);
        taskManager.deleteSubtasks();
        assertEquals(new ArrayList<>(), taskManager.getListOfSubtasks(), "Задачи не удалились");
        assertEquals(new ArrayList<>(), taskManager.getListOfSubtasksOfEpic(epic), "Задачи не удалились в эпике");
        assertEquals(new ArrayList<>(), taskManager.getListOfSubtasksOfEpic(epic1), "Задачи не удалились в эпике 1");
        assertEquals(Status.NEW, taskManager.getEpicById(1).getStatus(), "Статус не изменился");
        assertEquals(Status.NEW, taskManager.getEpicById(1).getStatus(), "Статус не изменился");
    }


    @Test
    void updateTask() {
        Task task = new Task("Test ", "Test  description");
        taskManager.createTask(task);
        task.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(task);
        assertEquals(Status.IN_PROGRESS, taskManager.getTaskById(1).getStatus(), "Задача не обновилась");
        assertEquals(1, taskManager.getListOfTasks().size(), "Задача добавилась как новая");
    }

    @Test
    void updateEpic() {
        Epic epic = new Epic("Test ", "Test  description");
        taskManager.createEpic(epic);
        epic.setStatus(Status.IN_PROGRESS);
        taskManager.updateEpic(epic);
        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(1).getStatus(), "Задача не обновилась");
        assertEquals(1, taskManager.getListOfEpics().size(), "Задача добавилась как новая");
    }

    @Test
    void updateSubtask() {
        Epic epic = new Epic("Test ", "Test  description");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Test a", "Test a description", 1);
        taskManager.createSubtask(subtask);
        subtask.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask);
        assertEquals(Status.IN_PROGRESS, taskManager.getSubtaskById(2).getStatus(), "Задача не обновилась");
        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(1).getStatus(), "Статус эпика не поменялся");
        Subtask subtask1 = new Subtask("Test aa", "Test aa description", 1);
        taskManager.createSubtask(subtask1);
        subtask1.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);
        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(1).getStatus(), "Статус эпика  поменялся");
        subtask.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask);
        assertEquals(Status.DONE, taskManager.getEpicById(1).getStatus(), "Эпик не стал Done");
    }


    @Test
    void getListOfSubtasksOfEpic() {
        Epic epic = new Epic("Test ", "Test  description");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Test a", "Test a description", 1);
        taskManager.createSubtask(subtask);
        Subtask subtask1 = new Subtask("Test aa", "Test aa description", 1);
        taskManager.createSubtask(subtask1);
        assertEquals(2, taskManager.getListOfSubtasksOfEpic(epic).size(), "Не то количество подзадач");
    }

    @Test
    void deleteTaskById() {
        for (int i = 0; i < 20; i++) {
            Task task = new Task("Test " + i, "Test  description");
            taskManager.createTask(task);
        }
        for (int i = 0; i < 20; i++) {
            Epic task = new Epic("Epic " + i, "Test  description");
            taskManager.createEpic(task);
        }
        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        taskManager.getTaskById(3);
        taskManager.getTaskById(1);
        taskManager.getEpicById(21);
        taskManager.getEpicById(21);
        taskManager.getEpicById(22);
        taskManager.getEpicById(23);
        taskManager.getTaskById(1);
        taskManager.getTaskById(1);
        taskManager.getTaskById(1);

        final List<Task> history = taskManager.getHistory();
        taskManager.deleteTaskById(1);
        assertEquals(6, history.size(), "Неправильно удалилось");
    }

    @Test
    void deleteEpicById() {
        for (int i = 0; i < 20; i++) {
            Task task = new Task("Test " + i, "Test  description");
            taskManager.createTask(task);
        }
        for (int i = 0; i < 20; i++) {
            Epic task = new Epic("Epic " + i, "Test  description");
            taskManager.createEpic(task);
        }
        taskManager.createSubtask(new Subtask("Test", "test description", 21));
        taskManager.createSubtask(new Subtask("Test", "test description", 21));
        taskManager.createSubtask(new Subtask("Test", "test description", 22));
        taskManager.createSubtask(new Subtask("Test", "test description", 23));
        taskManager.createSubtask(new Subtask("Test", "test description", 24));
        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        taskManager.getTaskById(3);
        taskManager.getTaskById(1);
        taskManager.getEpicById(21);
        taskManager.getEpicById(21);
        taskManager.getEpicById(22);
        taskManager.getEpicById(23);
        taskManager.getTaskById(1);
        taskManager.getTaskById(1);
        taskManager.getTaskById(1);
        taskManager.getSubtaskById(41);
        taskManager.getSubtaskById(42);
        taskManager.getSubtaskById(43);
        assertEquals(5, taskManager.getListOfSubtasks().size(), "Неправильно добавились подзадачи");
        taskManager.deleteEpicById(21);
        final List<Task> history = taskManager.getHistory();
        assertEquals(3, taskManager.getListOfSubtasks().size(), "Неправильно удалились подзадачи");
        assertEquals(19, taskManager.getListOfEpics().size(), "Неправильно удалился эпик");
        assertEquals(6, history.size(), "Неправильно удалилась");
    }

    @Test
    void deleteSubtaskById() {
        for (int i = 0; i < 20; i++) {
            Task task = new Task("Test " + i, "Test  description");
            taskManager.createTask(task);
        }
        for (int i = 0; i < 20; i++) {
            Epic task = new Epic("Epic " + i, "Test  description");
            taskManager.createEpic(task);
        }
        taskManager.createSubtask(new Subtask("Test", "test description", 21));
        taskManager.createSubtask(new Subtask("Test", "test description", 21));
        taskManager.createSubtask(new Subtask("Test", "test description", 22));
        taskManager.createSubtask(new Subtask("Test", "test description", 23));
        taskManager.createSubtask(new Subtask("Test", "test description", 24));
        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        taskManager.getTaskById(3);
        taskManager.getTaskById(1);
        taskManager.getEpicById(21);
        taskManager.getEpicById(21);
        taskManager.getEpicById(22);
        taskManager.getEpicById(23);
        taskManager.getTaskById(1);
        taskManager.getTaskById(1);
        taskManager.getTaskById(1);
        taskManager.getSubtaskById(41);
        taskManager.getSubtaskById(42);
        taskManager.getSubtaskById(43);
        taskManager.deleteSubtaskById(41);
        final List<Task> history = taskManager.getHistory();
        System.out.println(history);
        assertEquals(4, taskManager.getListOfSubtasks().size(), "Неправильно удалились подзадачи");
        assertEquals(1, taskManager.getListOfSubtasksOfEpic(taskManager.getEpicById(21)).size(), "Неправильно обновился эпик");
    }
}