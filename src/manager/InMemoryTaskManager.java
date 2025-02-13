package manager;

import classes.tasks.Epic;
import classes.tasks.Status;
import classes.tasks.Subtask;
import classes.tasks.Task;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected final HashMap<Integer, Task> tasksList;
    protected final HashMap<Integer, Epic> epicsList;
    protected final HashMap<Integer, Subtask> subtasksList;
    protected final HistoryManager historyManager;
    protected TreeSet<Task> prioritizedTasks;
    protected int idCount;

    public InMemoryTaskManager() {
        tasksList = new HashMap<>();
        epicsList = new HashMap<>();
        subtasksList = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
        prioritizedTasks = new TreeSet<>((task1, task2) -> task1.getStartTime().compareTo(task2.getStartTime()));
    }

    @Override
    public void createTask(Task task) {
        if (!add(task)) {
            return;
        }
        idCount++;
        tasksList.put(idCount, task);
        task.setId(idCount);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    @Override
    public void createEpic(Epic epic) {
        idCount++;
        epicsList.put(idCount, epic);
        epic.setId(idCount);
    }

    @Override
    public void createSubtask(Subtask subtask) {
        if (!add(subtask)) {
            return;
        }
        idCount++;
        subtasksList.put(idCount, subtask);
        subtask.setId(idCount);
        Epic epic = epicsList.get(subtask.getEpicId());
        HashMap<Integer, Subtask> subtasksListOfEpic = epic.getSubtasksList();
        subtasksListOfEpic.put(subtask.getId(), subtask);
        epic.updateStatus();
        epic.updateTime();
        if (subtask.getStartTime() != null) {
            prioritizedTasks.add(subtask);
        }
    }

    @Override
    public void deleteTasks() {
        deleteAllTasksFromHistory();
        tasksList.clear();
        prioritizedTasks = prioritizedTasks.stream()
                .filter(task -> (task instanceof Subtask) || (task instanceof Epic))
                .collect(Collectors.toCollection(() -> new TreeSet<>(prioritizedTasks.comparator())));
    }

    @Override
    public void deleteEpics() {
        deleteAllEpicsFromHistory();
        epicsList.clear();
        subtasksList.clear();
        prioritizedTasks = prioritizedTasks.stream()
                .filter(task -> !(task instanceof Subtask))
                .collect(Collectors.toCollection(() -> new TreeSet<>(prioritizedTasks.comparator())));
    }

    @Override
    public void deleteSubtasks() {
        deleteAllSubtasksFromHistory();
        subtasksList.clear();
        for (Epic epic : epicsList.values()) {
            epic.getSubtasksList().clear();
            epic.setStatus(Status.NEW);
            epic.setEmptyTime();
        }
        prioritizedTasks = prioritizedTasks.stream()
                .filter(task -> !(task instanceof Subtask))
                .collect(Collectors.toCollection(() -> new TreeSet<>(prioritizedTasks.comparator())));
    }

    @Override
    public Task getTaskById(int id) {
        historyManager.add(tasksList.get(id));
        return tasksList.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        historyManager.add(epicsList.get(id));
        return epicsList.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        historyManager.add(subtasksList.get(id));
        return subtasksList.get(id);
    }

    @Override
    public ArrayList<Task> getListOfTasks() {
        return new ArrayList<>(tasksList.values());
    }

    @Override
    public ArrayList<Epic> getListOfEpics() {
        return new ArrayList<>(epicsList.values());
    }

    @Override
    public ArrayList<Subtask> getListOfSubtasks() {
        return new ArrayList<>(subtasksList.values());
    }

    @Override
    public void updateTask(Task task) {
        Task oldTask = tasksList.get(task.getId());
        if (oldTask == null) {
            return;
        }
        prioritizedTasks.remove(oldTask);
        LocalDateTime oldStartTime = oldTask.getStartTime();
        LocalDateTime newStartTime = task.getStartTime();

        boolean timeChanged = (oldStartTime == null && newStartTime != null) ||
                (oldStartTime != null && newStartTime == null) ||
                (oldStartTime != null && newStartTime != null && !oldStartTime.equals(newStartTime));

        if (timeChanged && !add(task)) {
            deleteTaskById(task.getId());
            return;
        }

        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }

        tasksList.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        epicsList.put(epic.getId(), epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        Subtask oldTask = subtasksList.get(subtask.getId());
        if (oldTask == null) {
            return;
        }
        prioritizedTasks.remove(oldTask);
        LocalDateTime oldStartTime = oldTask.getStartTime();
        LocalDateTime newStartTime = subtask.getStartTime();

        boolean timeChanged = (oldStartTime == null && newStartTime != null) ||
                (oldStartTime != null && newStartTime == null) ||
                (oldStartTime != null && newStartTime != null && !oldStartTime.equals(newStartTime));

        if (timeChanged && !add(subtask)) {
            deleteSubtaskById(subtask.getId());
            return;
        }

        if (subtask.getStartTime() != null) {
            prioritizedTasks.add(subtask);
        }
        Epic epic = epicsList.get(subtask.getEpicId());
        HashMap<Integer, Subtask> subtasksListOfEpic = epic.getSubtasksList();
        subtasksListOfEpic.put(subtask.getId(), subtask);
        epic.updateStatus();
        epic.updateTime();
        subtasksList.put(subtask.getId(), subtask);
    }

    @Override
    public void deleteTaskById(int id) {
        deleteTaskByIdFromHistory(id);
        prioritizedTasks.remove(tasksList.get(id));
        tasksList.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        ArrayList<Task> subtaskOfEpicToDelete = deleteEpicByIdFromHistory(id);
        prioritizedTasks = prioritizedTasks.stream()
                .filter(task -> !subtaskOfEpicToDelete.contains(task))
                .collect(Collectors.toCollection(() -> new TreeSet<Task>((task1, task2) -> task1.getStartTime().compareTo(task2.getStartTime()))));
        epicsList.remove(id);
    }

    @Override
    public void deleteSubtaskById(int id) {
        prioritizedTasks.remove(subtasksList.get(id));
        Epic epic = epicsList.get(subtasksList.get(id).getEpicId());
        HashMap<Integer, Subtask> subtasksListOfEpic = epic.getSubtasksList();
        subtasksListOfEpic.remove(id);
        epic.updateStatus();
        epic.updateTime();
        deleteSubtaskByIdFromHistory(id);
        subtasksList.remove(id);
    }


    @Override
    public ArrayList<Subtask> getListOfSubtasksOfEpic(Epic epic) {
        return new ArrayList<>(epic.getSubtasksList().values());
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return prioritizedTasks.stream().toList();
    }

    private void deleteAllTasksFromHistory() {
        List<Task> tasksInHistory = historyManager.getHistory();
        tasksList.values().stream()
                .filter(tasksInHistory::contains)
                .forEach(task -> historyManager.remove(task.getId()));
    }

    private void deleteAllEpicsFromHistory() {
        List<Task> epicsInHistory = historyManager.getHistory();
        epicsList.values().stream()
                .filter(epicsInHistory::contains)
                .forEach(task -> historyManager.remove(task.getId()));
        subtasksList.values().stream()
                .filter(epicsInHistory::contains)
                .forEach(task -> historyManager.remove(task.getId()));
    }

    private void deleteAllSubtasksFromHistory() {
        List<Task> epicsInHistory = historyManager.getHistory();
        subtasksList.values().stream()
                .filter(epicsInHistory::contains)
                .forEach(task -> historyManager.remove(task.getId()));
    }

    private void deleteTaskByIdFromHistory(int id) {
        historyManager.remove(id);
    }

    private ArrayList<Task> deleteEpicByIdFromHistory(int id) {
        ArrayList<Task> subtaskOfEpicToDelete = subtasksList.values().stream()
                .filter(subtask -> subtask.getEpicId() == id)
                .collect(Collectors.toCollection(ArrayList::new));

        subtaskOfEpicToDelete.forEach(subtask -> {
            subtasksList.remove(subtask.getId());
            historyManager.remove(subtask.getId());
        });

        historyManager.remove(id);
        return subtaskOfEpicToDelete;
    }

    private void deleteSubtaskByIdFromHistory(int id) {
        historyManager.remove(id);
    }

    private boolean checkOnCrossing(Task task1, Task task2) {
        if (task1.getStartTime().isBefore(task2.getStartTime())) {
            return task1.getStartTime()
                    .plus(task1.getDuration().minusMinutes(1))
                    .isBefore(task2.getStartTime());
        } else if (task2.getStartTime().isBefore(task1.getStartTime())) {
            return task2.getStartTime()
                    .plus(task2.getDuration().minusMinutes(1))
                    .isBefore(task1.getStartTime());
        } else {
            return false;
        }
    }

    private boolean add(Task task) {
        if (prioritizedTasks.isEmpty() || task.getStartTime() == null) {
            return true;
        }
        return prioritizedTasks.stream().allMatch(task1 -> checkOnCrossing(task1, task));
    }
}
