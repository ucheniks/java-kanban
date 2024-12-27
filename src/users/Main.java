package users;

import classes.tasks.Epic;
import classes.tasks.Subtask;
import classes.tasks.Task;
import manager.Managers;
import manager.TaskManager;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("Task 1", "Description of Task 1");
        Task task2 = new Task("Task 2", "Description of Task 2");
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        Epic epic1 = new Epic("Epic without subtasks", "Description of epic without subtasks");
        taskManager.createEpic(epic1);
        Epic epic2 = new Epic("Epic with 3 subtasks", "Description of epic with 3 subtasks");
        taskManager.createEpic(epic2);
        Subtask subtask1 = new Subtask("Subtask 1 of epic 2", "Description of subtask 1 of epic 2", 4);
        Subtask subtask2 = new Subtask("Subtask 2 of epic 2", "Description of subtask 2 of epic 2", 4);
        Subtask subtask3 = new Subtask("Subtask 3 of epic 2", "Description of subtask 3 of epic 2", 4);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);
        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        System.out.println(taskManager.getHistory());
        System.out.println("            ");
        taskManager.getTaskById(1);
        taskManager.getTaskById(1);
        System.out.println(taskManager.getHistory());
        System.out.println("            ");
        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        System.out.println(taskManager.getHistory());
        System.out.println("            ");
        taskManager.getSubtaskById(5);
        System.out.println(taskManager.getHistory());
        System.out.println("            ");
        taskManager.getSubtaskById(5);
        taskManager.getSubtaskById(6);
        System.out.println(taskManager.getHistory());
        System.out.println("            ");
        taskManager.getSubtaskById(6);
        taskManager.getSubtaskById(7);
        taskManager.getSubtaskById(5);
        taskManager.getSubtaskById(7);
        System.out.println(taskManager.getHistory());
        System.out.println("            ");
        taskManager.getEpicById(3);
        taskManager.getEpicById(3);
        taskManager.getEpicById(4);
        taskManager.getEpicById(4);
        System.out.println(taskManager.getHistory());
        System.out.println("            ");
        taskManager.deleteTaskById(2);
        System.out.println(taskManager.getHistory());
        System.out.println("            ");
        taskManager.deleteSubtaskById(5);
        System.out.println(taskManager.getHistory());
        System.out.println("            ");
        taskManager.deleteEpicById(4);
        System.out.println(taskManager.getHistory());
    }
}
