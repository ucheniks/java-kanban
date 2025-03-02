package servers;

import classes.tasks.Epic;
import classes.tasks.Subtask;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EpicsHandler extends BaseHttpHandler {
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
