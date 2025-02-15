package manager;

import classes.tasks.Epic;
import classes.tasks.Status;
import classes.tasks.Subtask;
import classes.tasks.Task;
import exceptions.ManagerSaveException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeParseException;


public class FileBackedTaskManager extends InMemoryTaskManager {
    private final Path file;
    private static final String HEADER = "id,type,name,status,description,epic,startTime,duration";

    public FileBackedTaskManager(Path file) {
        super();
        this.file = file;
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    @Override
    public void deleteSubtasks() {
        super.deleteSubtasks();
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    private void save() {
        try (BufferedWriter writer = Files.newBufferedWriter(file)) {
            writer.write(HEADER);
            writer.newLine();
            for (Task task : tasksList.values()) {
                writer.write(task.toString());
                writer.newLine();
            }
            for (Epic epic : epicsList.values()) {
                writer.write(epic.toString());
                writer.newLine();
            }
            for (Subtask subtask : subtasksList.values()) {
                writer.write(subtask.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении данных в файл: " + e.getMessage());
        }
    }

    public static FileBackedTaskManager loadFromFile(Path file) throws IOException {
        FileBackedTaskManager taskManager = new FileBackedTaskManager(file);
        int maxId = 0;

        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String firstLine = reader.readLine();
            if (firstLine == null) {
                return taskManager;
            }
            if (!firstLine.equals(HEADER)) {
                throw new ManagerSaveException("Неверный формат файла: отсутствует заголовок");
            }
            String line = reader.readLine();
            while (line != null) {
                Task task = taskManager.fromString(line);
                if (task != null) {
                    if (task.getId() > maxId) {
                        maxId = task.getId();
                    }
                    if (task instanceof Epic) {
                        taskManager.epicsList.put(task.getId(), (Epic) task);
                    } else if (task instanceof Subtask) {
                        Subtask subtask = (Subtask) task;
                        taskManager.subtasksList.put(subtask.getId(), subtask);
                        Epic epic = taskManager.epicsList.get(subtask.getEpicId());
                        if (epic != null) {
                            epic.getSubtasksList().put(subtask.getId(), subtask);
                            epic.updateStatus();
                            epic.updateTime();
                        } else {
                            System.out.println("Не найден эпик подзадачи, id подзадачи: " + subtask.getId());
                        }
                    } else {
                        taskManager.tasksList.put(task.getId(), task);
                    }
                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при чтении данных из файла: " + e.getMessage());
        }

        taskManager.idCount = maxId + 1;
        return taskManager;
    }

    private Task fromString(String value) {
        String[] values = value.split(",");
        if (values.length < 7) {
            System.out.println("Ошибка при чтении строки из файла: неверный формат данных");
            return null;
        }
        try {
            TaskType taskType = TaskType.valueOf(values[1]);
            int id = Integer.parseInt(values[0]);
            String name = values[2];
            Status status = Status.valueOf(values[3]);
            String description = values[4];
            String startTime = values[6];
            long duration = Long.parseLong(values[7]);
            switch (taskType) {
                case TASK:
                    Task task = new Task(name, description);
                    task.setId(id);
                    task.setStatus(status);
                    task.setStartTime(startTime);
                    task.setDuration(duration);
                    return task;
                case EPIC:
                    Epic epic = new Epic(name, description);
                    epic.setId(id);
                    epic.setStatus(status);
                    epic.setStartTime(startTime);
                    epic.setDuration(duration);
                    return epic;
                case SUBTASK:
                    int epicId = Integer.parseInt(values[5]);
                    Subtask subtask = new Subtask(name, description, epicId);
                    subtask.setId(id);
                    subtask.setStatus(status);
                    subtask.setStartTime(startTime);
                    subtask.setDuration(duration);
                    return subtask;
                default:
                    System.out.println("Неизвестный тип задачи");
                    return null;
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка при конвертирования числа из String в Integer: " + e.getMessage());
            return null;
        } catch (IllegalArgumentException e) {
            System.out.println("Неверный аргумент: " + e.getMessage());
            return null;
        } catch (DateTimeParseException e) {
            System.out.println("Неверный формат даты и время: " + e.getMessage());
            return null;
        }
    }


    private static enum TaskType {
        TASK,
        EPIC,
        SUBTASK
    }
}
