package servers;

import classes.tasks.Task;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TasksHandler extends BaseHttpHandler {
    public TasksHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String requestPath = exchange.getRequestURI().getPath();
            String requestMethod = exchange.getRequestMethod();
            System.out.println("Обработка запроса: " + requestMethod + " " + requestPath);

            Endpoint endPoint = getEndpoint(requestPath, requestMethod);
            switch (endPoint) {
                case GET_ID:
                    handleGetTaskById(exchange);
                    break;
                case GET_TASKS:
                    handleGetTasks(exchange);
                    break;
                case POST:
                    handlePostTask(exchange);
                    break;
                case DELETE:
                    handleDeleteTask(exchange);
                    break;
                default:
                    sendBadRequest(exchange);
            }
        } catch (Exception e) {
            System.err.println("Ошибка при обработке запроса: " + e.getMessage());
            sendInternalError(exchange);
        }
    }

    private void handleGetTaskById(HttpExchange exchange) throws IOException {
        try {
            String stringId = exchange.getRequestURI().getPath().split("/")[2];
            int id = Integer.parseInt(stringId);
            Task task = taskManager.getTaskById(id);
            String responseString = gson.toJson(task);
            sendText(exchange, responseString);
            System.out.println("Отправлена задача с ID: " + id);
        } catch (NumberFormatException e) {
            System.err.println("Неверный формат ID: " + e.getMessage());
            sendBadRequest(exchange);
        } catch (NullPointerException e) {
            sendNotFound(exchange);
        } catch (Exception e) {
            System.err.println("Ошибка при обработке запроса: " + e.getMessage());
            sendInternalError(exchange);
        }
    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        try {
            String responseString = gson.toJson(taskManager.getListOfTasks());
            sendText(exchange, responseString);
            System.out.println("Отправлен список задач");
        } catch (Exception e) {
            System.err.println("Ошибка при обработке запроса: " + e.getMessage());
            sendInternalError(exchange);
        }
    }

    private void handlePostTask(HttpExchange exchange) throws IOException {
        try {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Task taskInConsole = gson.fromJson(body, Task.class);
            if (taskInConsole == null) {
                System.err.println("Не удалось десериализовать задачу из JSON");
                sendBadRequest(exchange);
                return;
            }
            Task task = new Task(taskInConsole.getName(), taskInConsole.getDescription());
            if (taskInConsole.getStatus() != null) task.setStatus(taskInConsole.getStatus());
            if (taskInConsole.getDuration() != null) task.setDuration(taskInConsole.getDuration().toMinutes());
            if (taskInConsole.getStartTime() != null)
                task.setStartTime(taskInConsole.getStartTime().format(HttpTaskServer.DATE_TIME_FORMATTER));
            task.setId(taskInConsole.getId());
            if (task.getId() == 0) {
                List<Task> tasksBeforeCreate = taskManager.getListOfTasks();
                taskManager.createTask(task);
                List<Task> tasksAfterCreate = taskManager.getListOfTasks();
                if (!tasksBeforeCreate.equals(tasksAfterCreate)) {
                    exchange.sendResponseHeaders(201, 0);
                    System.out.println("Создана новая задача: " + task.getName());
                } else {
                    sendHasInteractions(exchange);
                }
            } else {
                System.out.println(taskManager.getListOfTasks());
                taskManager.updateTask(task);
                Task taskInManager = taskManager.getTaskById(task.getId());
                if (taskInManager != null) {
                    exchange.sendResponseHeaders(201, 0);
                    System.out.println("Обновлена задача с ID: " + task.getId());
                } else {
                    sendHasInteractions(exchange);
                }
            }
            exchange.close();
        } catch (NullPointerException e) {
            sendNotFound(exchange);
        } catch (Exception e) {
            System.err.println("Ошибка при обработке запроса: " + e.getMessage());
            sendInternalError(exchange);
        }
    }

    private void handleDeleteTask(HttpExchange exchange) throws IOException {
        try {
            String stringIdToDelete = exchange.getRequestURI().getPath().split("/")[2];
            int idToDelete = Integer.parseInt(stringIdToDelete);
            Task task = taskManager.getTaskById(idToDelete);
            taskManager.deleteTaskById(idToDelete);
            String responseString = gson.toJson(task);
            sendText(exchange, responseString);
            System.out.println("Удалена задача с ID: " + idToDelete);
        } catch (NumberFormatException e) {
            System.err.println("Неверный формат ID: " + e.getMessage());
            sendBadRequest(exchange);
        } catch (NullPointerException e) {
            sendNotFound(exchange);
        } catch (Exception e) {
            System.err.println("Ошибка при обработке запроса: " + e.getMessage());
            sendInternalError(exchange);
        }
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");
        if (requestMethod.equals("GET")) {
            if (pathParts.length == 2) {
                return Endpoint.GET_TASKS;
            } else if (pathParts.length == 3
                    && isInteger(pathParts[2])) {
                return Endpoint.GET_ID;
            }
        } else if (requestMethod.equals("POST")
                && pathParts.length == 2) {
            return Endpoint.POST;
        } else if (requestMethod.equals("DELETE")
                && pathParts.length == 3
                && isInteger(pathParts[2])) {
            return Endpoint.DELETE;
        }
        return Endpoint.UNKNOWN;
    }

    enum Endpoint {
        GET_TASKS,
        GET_ID,
        POST,
        DELETE,
        UNKNOWN
    }
}