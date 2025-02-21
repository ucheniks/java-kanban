package servers;

import classes.tasks.Epic;
import classes.tasks.Subtask;
import classes.tasks.Task;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HttpTaskServer {
    private final TaskManager taskManager;
    private final HttpServer httpServer;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        this.httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
        httpServer.createContext("/tasks", new TasksHandler(taskManager));
        httpServer.createContext("/subtasks", new SubtasksHandler(taskManager));
        httpServer.createContext("/epics", new EpicsHandler(taskManager));
        httpServer.createContext("/history", new HistoryHandler(taskManager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager));
    }

    public void start() {
        httpServer.start();
        System.out.println("HTTP-сервер запущен на порту 8080");
    }

    public void stop() {
        httpServer.stop(0);
        System.out.println("HTTP-сервер остановлен");
    }

    public static void main(String[] args) throws IOException {
        TaskManager taskManager = Managers.getDefault();
        HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.start();
    }

    class TasksHandler extends BaseHttpHandler {
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
                    task.setStartTime(taskInConsole.getStartTime().format(DATE_TIME_FORMATTER));
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
                    System.out.println("f[f[f[");
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

    class SubtasksHandler extends BaseHttpHandler {
        public SubtasksHandler(TaskManager taskManager) {
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
                        handleGetSubtaskById(exchange);
                        break;
                    case GET_SUBTASKS:
                        handleGetSubtasks(exchange);
                        break;
                    case POST:
                        handlePostSubtask(exchange);
                        break;
                    case DELETE:
                        handleDeleteSubtask(exchange);
                        break;
                    default:
                        sendBadRequest(exchange);
                }
            } catch (Exception e) {
                System.err.println("Ошибка при обработке запроса: " + e.getMessage());
                sendInternalError(exchange);
            }
        }

        private void handleGetSubtaskById(HttpExchange exchange) throws IOException {
            try {
                String stringId = exchange.getRequestURI().getPath().split("/")[2];
                int id = Integer.parseInt(stringId);
                Subtask subtask = taskManager.getSubtaskById(id);
                String responseString = gson.toJson(subtask);
                sendText(exchange, responseString);
                System.out.println("Отправлена подзадача с ID: " + id);

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

        private void handleGetSubtasks(HttpExchange exchange) throws IOException {
            try {
                String responseString = gson.toJson(taskManager.getListOfSubtasks());
                sendText(exchange, responseString);
                System.out.println("Отправлен список подзадач");
            } catch (Exception e) {
                System.err.println("Ошибка при обработке запроса: " + e.getMessage());
                sendInternalError(exchange);
            }
        }

        private void handlePostSubtask(HttpExchange exchange) throws IOException {
            try {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Subtask subtaskInConsole = gson.fromJson(body, Subtask.class);
                if (subtaskInConsole == null) {
                    System.err.println("Не удалось десериализовать подзадачу из JSON");
                    sendBadRequest(exchange);
                    return;
                }
                Subtask subtask = new Subtask(subtaskInConsole.getName(), subtaskInConsole.getDescription(), subtaskInConsole.getEpicId());
                if (subtaskInConsole.getStatus() != null) subtask.setStatus(subtaskInConsole.getStatus());
                if (subtaskInConsole.getDuration() != null)
                    subtask.setDuration(subtaskInConsole.getDuration().toMinutes());
                if (subtaskInConsole.getStartTime() != null)
                    subtask.setStartTime(subtaskInConsole.getStartTime().format(DATE_TIME_FORMATTER));
                subtask.setId(subtaskInConsole.getId());
                Epic epic = taskManager.getEpicById(subtask.getEpicId());
                if (subtask.getId() == 0) {
                    List<Subtask> subtasksBeforeCreate = taskManager.getListOfSubtasks();
                    taskManager.createSubtask(subtask);
                    List<Subtask> subtasksAfterCreate = taskManager.getListOfSubtasks();
                    if (!subtasksBeforeCreate.equals(subtasksAfterCreate)) {
                        exchange.sendResponseHeaders(201, 0);
                        System.out.println("Создана новая подзадача: " + subtask.getName());
                    } else {
                        sendHasInteractions(exchange);
                    }
                } else {
                    taskManager.updateSubtask(subtask);
                    Subtask subtaskInManager = taskManager.getSubtaskById(subtask.getId());
                    if (subtaskInManager != null) {
                        exchange.sendResponseHeaders(201, 0);
                        System.out.println("Обновлена подзадача с ID: " + subtask.getId());
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

        private void handleDeleteSubtask(HttpExchange exchange) throws IOException {
            try {
                String stringIdToDelete = exchange.getRequestURI().getPath().split("/")[2];
                int idToDelete = Integer.parseInt(stringIdToDelete);
                Subtask subtask = taskManager.getSubtaskById(idToDelete);
                taskManager.deleteSubtaskById(idToDelete);
                String responseString = gson.toJson(subtask);
                sendText(exchange, responseString);
                System.out.println("Удалена подзадача с ID: " + idToDelete);
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
                    return Endpoint.GET_SUBTASKS;
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
            GET_SUBTASKS,
            GET_ID,
            POST,
            DELETE,
            UNKNOWN
        }
    }

    class EpicsHandler extends BaseHttpHandler {
        public EpicsHandler(TaskManager taskManager) {
            super(taskManager);
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestPath = exchange.getRequestURI().getPath();
            String requestMethod = exchange.getRequestMethod();
            System.out.println("Обработка запроса: " + requestMethod + " " + requestPath);

            Endpoint endPoint = getEndpoint(requestPath, requestMethod);
            switch (endPoint) {
                case GET_EPICS:
                    handleGetEpics(exchange);
                    break;
                case GET_ID:
                    handleGetEpicById(exchange);
                    break;
                case GET_SUBTASKS:
                    handleGetEpicSubtasks(exchange);
                    break;
                case POST:
                    handlePostEpic(exchange);
                    break;
                case DELETE:
                    handleDeleteEpic(exchange);
                    break;
                default:
                    sendBadRequest(exchange);
            }
        }

        private void handleGetEpicById(HttpExchange exchange) throws IOException {
            try {
                String stringId = exchange.getRequestURI().getPath().split("/")[2];
                int id = Integer.parseInt(stringId);
                Epic epic = taskManager.getEpicById(id);
                String responseString = gson.toJson(epic);
                sendText(exchange, responseString);
                System.out.println("Отправлен эпик с ID: " + id);

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

        private void handleGetEpics(HttpExchange exchange) throws IOException {
            try {
                String responseString = gson.toJson(taskManager.getListOfEpics());
                sendText(exchange, responseString);
                System.out.println("Отправлен список эпиков");
            } catch (Exception e) {
                System.err.println("Ошибка при обработке запроса: " + e.getMessage());
                sendInternalError(exchange);
            }
        }

        private void handleGetEpicSubtasks(HttpExchange exchange) throws IOException {
            try {
                String stringId = exchange.getRequestURI().getPath().split("/")[2];
                int id = Integer.parseInt(stringId);
                List<Subtask> subtasks = taskManager.getListOfSubtasksOfEpic(taskManager.getEpicById(id));
                String responseString = gson.toJson(subtasks);
                sendText(exchange, responseString);
                System.out.println("Отправлен список подзадач эпика с ID: " + id);
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

        private void handlePostEpic(HttpExchange exchange) throws IOException {
            try {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Epic epic = gson.fromJson(body, Epic.class);
                if (epic == null) {
                    System.err.println("Не удалось десериализовать эпик из JSON");
                    sendBadRequest(exchange);
                    return;
                }
                epic = new Epic(epic.getName(), epic.getDescription());
                taskManager.createEpic(epic);
                exchange.sendResponseHeaders(201, 0);
                System.out.println("Создан новый эпик: " + epic.getName());
                exchange.close();
            } catch (Exception e) {
                System.err.println("Ошибка при обработке запроса: " + e.getMessage());
                sendInternalError(exchange);
            }
        }

        private void handleDeleteEpic(HttpExchange exchange) throws IOException {
            try {
                String stringIdToDelete = exchange.getRequestURI().getPath().split("/")[2];
                int idToDelete = Integer.parseInt(stringIdToDelete);
                Epic epic = taskManager.getEpicById(idToDelete);
                taskManager.deleteEpicById(idToDelete);
                String responseString = gson.toJson(epic);
                sendText(exchange, responseString);
                System.out.println("Удален эпик с ID: " + idToDelete);
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
                    return Endpoint.GET_EPICS;
                } else if (pathParts.length == 3
                        && isInteger(pathParts[2])) {
                    return Endpoint.GET_ID;
                } else if (pathParts.length == 4
                        && isInteger(pathParts[2])
                        && pathParts[3].equals("subtasks")) {
                    return Endpoint.GET_SUBTASKS;
                } else {
                    return Endpoint.UNKNOWN;
                }
            } else if (requestMethod.equals("POST")
                    && pathParts.length == 2) {
                return Endpoint.POST;
            } else if (requestMethod.equals("DELETE")
                    && isInteger(pathParts[2])) {
                return Endpoint.DELETE;
            } else {
                return Endpoint.UNKNOWN;
            }
        }

        enum Endpoint {
            GET_EPICS,
            GET_ID,
            GET_SUBTASKS,
            POST,
            DELETE,
            UNKNOWN
        }
    }

    class HistoryHandler extends BaseHttpHandler {
        public HistoryHandler(TaskManager taskManager) {
            super(taskManager);
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                String requestPath = exchange.getRequestURI().getPath();
                String requestMethod = exchange.getRequestMethod();
                System.out.println("Обработка запроса: " + requestMethod + " " + requestPath);

                if (requestMethod.equals("GET")
                        && requestPath.split("/").length == 2) {
                    List<Task> history = taskManager.getHistory();
                    String responseString = gson.toJson(history);
                    sendText(exchange, responseString);
                    System.out.println("Отправлена история задач");
                } else {
                    sendBadRequest(exchange);
                }
            } catch (Exception e) {
                System.err.println("Ошибка при обработке запроса: " + e.getMessage());
                sendInternalError(exchange);
            }
        }
    }

    class PrioritizedHandler extends BaseHttpHandler {
        public PrioritizedHandler(TaskManager taskManager) {
            super(taskManager);
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                String requestPath = exchange.getRequestURI().getPath();
                String requestMethod = exchange.getRequestMethod();
                System.out.println("Обработка запроса: " + requestMethod + " " + requestPath);

                if (requestMethod.equals("GET")
                        && requestPath.split("/").length == 2) {
                    List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
                    String responseString = gson.toJson(prioritizedTasks);
                    sendText(exchange, responseString);
                    System.out.println("Отправлен список задач по приоритету");
                } else {
                    sendBadRequest(exchange);
                }
            } catch (Exception e) {
                System.err.println("Ошибка при обработке запроса: " + e.getMessage());
                sendInternalError(exchange);
            }
        }
    }
}