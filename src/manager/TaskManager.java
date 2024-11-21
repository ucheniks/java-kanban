package manager;

import classes_of_tasks.Epic;
import classes_of_tasks.Status;
import classes_of_tasks.Subtask;
import classes_of_tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    protected HashMap<Integer, Task> tasksList;
    protected HashMap<Integer, Epic> epicsList;
    protected HashMap<Integer, Subtask> subtasksList;
    private static int idCount;

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
        epicsList.get(subtask.getEpicId()).getSubtasksList().put(subtask.getId(),subtask);
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
    public ArrayList<Task> getListOfTasks(){
        ArrayList<Task> tasks=new ArrayList<>();
        tasks.addAll(tasksList.values());
        return tasks;
    }
    public ArrayList<Epic> getListOfEpics(){
        ArrayList<Epic> epics=new ArrayList<>();
        epics.addAll(epicsList.values());
        return epics;
    }
    public ArrayList<Subtask> getListOfSubtasks(){
        ArrayList<Subtask> subtasks=new ArrayList<>();
        subtasks.addAll(subtasksList.values());
        return subtasks;
    }

    public void updateTask(Task task){
        tasksList.put(task.getId(),task);
    }
    public void updateEpic(Epic epic){
        int numberOfNew=0;
        int numberOfDone=0;
        for (Subtask subtask : epic.getSubtasksList().values()) {
            if (subtask.getStatus().equals(Status.NEW)) {
                numberOfNew++;
            }
            if (subtask.getStatus().equals(Status.DONE)) {
                numberOfDone++;
            }
            if (numberOfNew==epic.getSubtasksList().size()) {
                epic.setStatus(Status.NEW);
            } else if (numberOfDone==epic.getSubtasksList().size()) {
                epic.setStatus(Status.DONE);
            } else {
                epic.setStatus(Status.IN_PROGRESS);
            }
        }
        epicsList.put(epic.getId(),epic);
    }
    public void updateSubtask(Subtask subtask){
        epicsList.get(subtask.getEpicId()).getSubtasksList().put(subtask.getId(),subtask);
        updateEpic(epicsList.get(subtask.getEpicId()));
        subtasksList.put(subtask.getId(),subtask);
    }
    public void deleteTaskById(int id) {
        tasksList.remove(id);
    }
    public void deleteEpicById(int id) {
        epicsList.remove(id);
        for (Subtask subtask : subtasksList.values()) {
            if (subtask.getEpicId()==id) {
                subtasksList.remove(subtask.getId());
            }
        }
    }
    public void deleteSubtaskById(int id) {
        epicsList.get(subtasksList.get(id).getEpicId()).getSubtasksList().remove(id);
        updateEpic(epicsList.get(subtasksList.get(id).getEpicId()));
        subtasksList.remove(id);
    }


    public ArrayList<Subtask> getListOfSubtasksOfEpic(Epic epic) {
        ArrayList<Subtask> subtasks=new ArrayList<>();
        subtasks.addAll(epic.getSubtasksList().values());
        return subtasks;
    }
}
