package manager;

import classes.tasks.Epic;
import classes.tasks.Status;
import classes.tasks.Subtask;
import classes.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasksList;
    private final HashMap<Integer, Epic> epicsList;
    private final HashMap<Integer, Subtask> subtasksList;
    private final HistoryManager historyManager;
    private int idCount;

    public InMemoryTaskManager() {
        tasksList = new HashMap<>();
        epicsList = new HashMap<>();
        subtasksList = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
    }

    @Override
    public void createTask(Task task) {
        idCount++;
        tasksList.put(idCount, task);
        task.setId(idCount);
    }

    @Override
    public void createEpic(Epic epic) {
        idCount++;
        epicsList.put(idCount, epic);
        epic.setId(idCount);
    }

    @Override
    public void createSubtask(Subtask subtask) {
        idCount++;
        subtasksList.put(idCount, subtask);
        subtask.setId(idCount);
        Epic epic = epicsList.get(subtask.getEpicId());
        HashMap<Integer, Subtask> subtasksListOfEpic = epic.getSubtasksList();
        subtasksListOfEpic.put(subtask.getId(), subtask);
        epic.updateStatus();
    }

    @Override
    public void deleteTasks() {
        deleteAllTasksFromHistory();
        tasksList.clear();

    }

    @Override
    public void deleteEpics() {
        deleteAllEpicsFromHistory();
        epicsList.clear();
        subtasksList.clear();
    }

    @Override
    public void deleteSubtasks() {
        deleteAllSubtasksFromHistory();
        subtasksList.clear();
        for (Epic epic : epicsList.values()) {
            epic.getSubtasksList().clear();
            epic.setStatus(Status.NEW);
        }
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
        tasksList.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        epicsList.put(epic.getId(), epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        Epic epic = epicsList.get(subtask.getEpicId());
        HashMap<Integer, Subtask> subtasksListOfEpic = epic.getSubtasksList();
        subtasksListOfEpic.put(subtask.getId(), subtask);
        epic.updateStatus();
        subtasksList.put(subtask.getId(), subtask);
    }

    @Override
    public void deleteTaskById(int id) {
        deleteTaskByIdFromHistory(id);
        tasksList.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        deleteEpicByIdFromHistory(id);
        epicsList.remove(id);
    }

    @Override
    public void deleteSubtaskById(int id) {
        Epic epic = epicsList.get(subtasksList.get(id).getEpicId());
        HashMap<Integer, Subtask> subtasksListOfEpic = epic.getSubtasksList();
        subtasksListOfEpic.remove(id);
        epic.updateStatus();
        deleteSubtaskByIdFromHistory(id, epic);
        subtasksList.remove(id);
    }


    @Override
    public ArrayList<Subtask> getListOfSubtasksOfEpic(Epic epic) {
        return new ArrayList<>(epic.getSubtasksList().values());
    }

    @Override
    public ArrayList<Task> getHistory() {
        return historyManager.getHistory();
    }

    private void deleteAllTasksFromHistory() {
        historyManager.getHistory().removeIf(tasksList::containsValue);
    }

    private void deleteAllEpicsFromHistory() {
        historyManager.getHistory().removeIf(epicsList::containsValue);
        historyManager.getHistory().removeIf(subtasksList::containsValue);
    }

    private void deleteAllSubtasksFromHistory() {
        historyManager.getHistory().removeIf(subtasksList::containsValue);
    }

    private void deleteTaskByIdFromHistory(int id) {
        historyManager.getHistory().removeIf(task -> (task.getId() == id));
    }

    private void deleteEpicByIdFromHistory(int id) {
        ArrayList<Task> subtaskOfEpicToDelete = new ArrayList<>();
        for (Subtask subtask : subtasksList.values()) {
            if (subtask.getEpicId() == id) {
                subtaskOfEpicToDelete.add(subtask);
            }
        }
        for (Task subtask : subtaskOfEpicToDelete) {
            subtasksList.remove(subtask.getId());
        }
        historyManager.getHistory().removeIf(subtaskOfEpicToDelete::contains);
        historyManager.getHistory().removeIf(task -> (task.getId() == id));
    }

    private void deleteSubtaskByIdFromHistory(int id, Epic epic) {
        historyManager.getHistory().removeIf(task -> (task.getId() == id));
        ArrayList<Integer> indexOfEpicToUpdate = new ArrayList<>();
        for (int i = 0; i < historyManager.getHistory().size(); i++) {
            Task task = historyManager.getHistory().get(i);
            if (task.getId() == epic.getId()) {
                indexOfEpicToUpdate.add(i);
            }
        }
        for (Integer index : indexOfEpicToUpdate) {
            historyManager.getHistory().remove((int) index);
            historyManager.getHistory().add((int) index, epic);
        }
    }
}
