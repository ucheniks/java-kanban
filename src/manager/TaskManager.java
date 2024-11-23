package manager;

import classes.tasks.Epic;
import classes.tasks.Status;
import classes.tasks.Subtask;
import classes.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    protected HashMap<Integer, Task> tasksList;
    protected HashMap<Integer, Epic> epicsList;
    protected HashMap<Integer, Subtask> subtasksList;
    private int idCount;

    public TaskManager() {
        tasksList = new HashMap<>();
        epicsList = new HashMap<>();
        subtasksList = new HashMap<>();
    }

    public void createTask(Task task) {
        idCount++;
        tasksList.put(idCount, task);
        task.setId(idCount);
    }

    public void createEpic(Epic epic) {
        idCount++;
        epicsList.put(idCount, epic);
        epic.setId(idCount);
    }

    public void createSubtask(Subtask subtask) {
        idCount++;
        subtasksList.put(idCount, subtask);
        subtask.setId(idCount);
        Epic epic = epicsList.get(subtask.getEpicId());
        HashMap<Integer, Subtask> subtasksListOfEpic = epic.getSubtasksList();
        subtasksListOfEpic.put(subtask.getId(), subtask);
        epic.updateStatus();
    }

    public void deleteTasks() {
        tasksList.clear();
    }

    public void deleteEpics() {
        epicsList.clear();
        subtasksList.clear();
    }

    public void deleteSubtasks() {
        subtasksList.clear();
        for (Epic epic : epicsList.values()) {
            epic.getSubtasksList().clear();
            epic.setStatus(Status.NEW);
        }
    }

    public Task getTaskById(int id) {
        return tasksList.get(id);
    }

    public Epic getEpicById(int id) {
        return epicsList.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtasksList.get(id);
    }

    public ArrayList<Task> getListOfTasks() {
        return new ArrayList<>(tasksList.values());
    }

    public ArrayList<Epic> getListOfEpics() {
        return new ArrayList<>(epicsList.values());
    }

    public ArrayList<Subtask> getListOfSubtasks() {
        return new ArrayList<>(subtasksList.values());
    }

    public void updateTask(Task task) {
        tasksList.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        epicsList.put(epic.getId(), epic);
    }

    public void updateSubtask(Subtask subtask) {
        Epic epic = epicsList.get(subtask.getEpicId());
        HashMap<Integer, Subtask> subtasksListOfEpic = epic.getSubtasksList();
        subtasksListOfEpic.put(subtask.getId(), subtask);
        epic.updateStatus();
        subtasksList.put(subtask.getId(), subtask);
    }

    public void deleteTaskById(int id) {
        tasksList.remove(id);
    }

    public void deleteEpicById(int id) {
        epicsList.remove(id);
        for (Subtask subtask : subtasksList.values()) {
            if (subtask.getEpicId() == id) {
                subtasksList.remove(subtask.getId());
            }
        }
    }

    public void deleteSubtaskById(int id) {
        Epic epic = epicsList.get(subtasksList.get(id).getEpicId());
        HashMap<Integer, Subtask> subtasksListOfEpic = epic.getSubtasksList();
        subtasksListOfEpic.remove(id);
        epic.updateStatus();
        subtasksList.remove(id);
    }


    public ArrayList<Subtask> getListOfSubtasksOfEpic(Epic epic) {
        return new ArrayList<>(epic.getSubtasksList().values());
    }
}
