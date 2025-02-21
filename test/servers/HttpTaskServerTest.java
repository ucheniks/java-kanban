package servers;

import classes.tasks.Epic;
import classes.tasks.Status;
import classes.tasks.Subtask;
import classes.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTest {

    private TaskManager manager;
    private HttpTaskServer taskServer;
    private Gson gson;

    @BeforeEach
    public void setUp() throws IOException {
        manager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(manager);
        taskServer.start();
        gson = BaseHttpHandler.createGson();
    }

    @AfterEach
    public void tearDown() {
        taskServer.stop();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("Test task", "Test description");
        task.setDuration(30);
        task.setStartTime("21.02.2025 23:46");

        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Неверный статус ответа");
        List<Task> tasks = manager.getListOfTasks();
        assertNotNull(tasks, "Задачи не возвращаются");
        assertEquals(1, tasks.size(), "Некорректное количество задач");
        assertEquals("Test task", tasks.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        Task task = new Task("Test task", "Test description");
        task.setDuration(30);
        task.setStartTime("21.02.2025 23:46");
        manager.createTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный статус ответа");

        Task returnedTask = gson.fromJson(response.body(), Task.class);
        assertNotNull(returnedTask, "Задача не возвращается");
        assertEquals(task.getId(), returnedTask.getId(), "Некорректный ID задачи");
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        Task task = new Task("Test task", "Test description");
        task.setDuration(30);
        task.setStartTime("21.02.2025 23:46");
        manager.createTask(task);

        task.setStatus(Status.IN_PROGRESS);
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


        assertEquals(201, response.statusCode(), "Неверный статус ответа");


        Task updatedTask = manager.getTaskById(task.getId());
        assertNotNull(updatedTask, "Задача не найдена");
        assertEquals(Status.IN_PROGRESS, updatedTask.getStatus(), "Статус задачи не обновился");
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        Task task = new Task("Test task", "Test description");
        task.setDuration(30);
        task.setStartTime("21.02.2025 23:46");
        manager.createTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный статус ответа");

        System.out.println(manager.getListOfTasks());
    }

    @Test
    public void testAddSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Test epic", "Test epic description");
        manager.createEpic(epic);

        Subtask subtask = new Subtask("Test subtask", "Test subtask description", epic.getId());
        subtask.setDuration(15);
        subtask.setStartTime("21.02.2025 23:46");

        String subtaskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Неверный статус ответа");

        List<Subtask> subtasks = manager.getListOfSubtasks();
        assertNotNull(subtasks, "Подзадачи не возвращаются");
        assertEquals(1, subtasks.size(), "Некорректное количество подзадач");
        assertEquals("Test subtask", subtasks.get(0).getName(), "Некорректное имя подзадачи");
    }

    @Test
    public void testGetHistory() throws IOException, InterruptedException {
        Task task = new Task("Test task", "Test description");
        task.setDuration(30);
        task.setStartTime("21.02.2025 23:46");
        manager.createTask(task);
        manager.getTaskById(task.getId());

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный статус ответа");

        List<Task> history = gson.fromJson(response.body(), new ListOfTaskTypeToken().getType());
        assertNotNull(history, "История не возвращается");
        assertEquals(1, history.size(), "Некорректное количество задач в истории");
        assertEquals(task.getId(), history.get(0).getId(), "Некорректная задача в истории");
    }

    @Test
    public void testGetPrioritizedTasks() throws IOException, InterruptedException {
        Task task = new Task("Test task", "Test description");
        task.setDuration(30);
        task.setStartTime("21.02.2025 23:46");
        manager.createTask(task);

        Task task1 = new Task("Test task 2", "Test description 2");
        task1.setDuration(30);
        task1.setStartTime("22.02.2025 23:46");
        manager.createTask(task1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный статус ответа");

        List<Task> prioritizedTasks = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());
        assertNotNull(prioritizedTasks, "Приоритетные задачи не возвращаются");
        assertEquals(2, prioritizedTasks.size(), "Некорректное количество приоритетных задач");
        assertEquals(task.getId(), prioritizedTasks.get(0).getId(), "Некорректная задача в списке приоритетных");
    }

    @Test
    public void testGetNonExistentTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/999"); // Несуществующий ID
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Неверный статус ответа для несуществующей задачи");
    }

    @Test
    public void testDeleteNonExistentTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/999"); // Несуществующий ID
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Неверный статус ответа для несуществующей задачи");
    }

    @Test
    public void testAddTaskWithTimeOverlap() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "Description 1");
        task1.setStartTime("21.02.2025 23:46");
        task1.setDuration(30);
        manager.createTask(task1);

        Task task2 = new Task("Task 2", "Description 2");
        task2.setStartTime("22.02.2025 00:01");
        task2.setDuration(30);

        String taskJson = gson.toJson(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode(), "Неверный статус ответа для задачи с пересекающимся временем");
    }

    @Test
    public void testAddSubtaskToNonExistentEpic() throws IOException, InterruptedException {
        Subtask subtask = new Subtask("Test subtask", "Test subtask description", 999); // Несуществующий epicId
        subtask.setDuration(15);
        subtask.setStartTime("21.02.2025 23:46");

        String subtaskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Неверный статус ответа для подзадачи с несуществующим эпиком");
    }

    @Test
    public void testUpdateNonExistentTask() throws IOException, InterruptedException {
        Task task = new Task("Test task", "Test description");
        task.setId(999);
        task.setStatus(Status.IN_PROGRESS);
        task.setDuration(30);
        task.setStartTime("21.02.2025 23:46");

        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Неверный статус ответа для обновления несуществующей задачи");
    }

    @Test
    public void testGetEmptyHistory() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный статус ответа для пустой истории");

        List<Task> history = gson.fromJson(response.body(), new ListOfTaskTypeToken().getType());
        assertNotNull(history, "История не возвращается");
        assertTrue(history.isEmpty(), "История должна быть пустой");
    }

    @Test
    public void testGetEmptyPrioritizedTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный статус ответа для пустого списка приоритетных задач");

        List<Task> prioritizedTasks = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());
        assertNotNull(prioritizedTasks, "Приоритетные задачи не возвращаются");
        assertTrue(prioritizedTasks.isEmpty(), "Список приоритетных задач должен быть пустым");
    }

    class ListOfTaskTypeToken extends TypeToken<List<Task>> {

    }
}