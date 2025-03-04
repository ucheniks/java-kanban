package servers;

import classes.tasks.Task;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;

import java.io.IOException;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler {
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