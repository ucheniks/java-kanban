package servers;

import classes.tasks.Epic;
import classes.tasks.Subtask;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubtasksHandler extends BaseHttpHandler {
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
                subtask.setStartTime(subtaskInConsole.getStartTime().format(HttpTaskServer.DATE_TIME_FORMATTER));
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