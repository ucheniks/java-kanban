import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    HashMap<String, ArrayList<Object>> tasksList;

    public TaskManager() {
        tasksList = new HashMap<>();
        for (int i = 0; i < 3; i++) {
            ArrayList<Object> tasks = new ArrayList<>();
            switch (i) {
                case 0:
                    tasksList.put("ОБЫЧНЫЕ ЗАДАЧИ", tasks);
                    break;
                case 1:
                    tasksList.put("ЭПИКИ", tasks);
                    break;
                case 2:
                    tasksList.put("ПОДЗАДАЧИ", tasks);
                    break;
            }
        }
    }

    public void addTask(Object task) {
        if (task.getClass().equals(Task.class)) {
            tasksList.get("ОБЫЧНЫЕ ЗАДАЧИ").add(task);
        } else if (task.getClass().equals(Epic.class)) {
            tasksList.get("ЭПИКИ").add(task);
        } else if (task.getClass().equals(Subtask.class)) {
            tasksList.get("ПОДЗАДАЧИ").add(task);
        } else {
            System.out.println("Ошибка, неверный тип задачи!");
        }
    }


    public ArrayList<Object> getListOfTasks(String taskGroup) {
        ArrayList<Object> listOfTasks = new ArrayList<>();
        for (Object object : tasksList.get(taskGroup)) {
            if (object.getClass().equals(Epic.class)) {
                Epic epic = (Epic) object;
                listOfTasks.add(epic);
            } else if (object.getClass().equals(Task.class)) {
                Task task = (Task) object;
                listOfTasks.add(task);
            } else if (object.getClass().equals(Subtask.class)) {
                Subtask subtask = (Subtask) object;
                listOfTasks.add(subtask);
            }
        }
        return listOfTasks;
    }

    public void deleteTasks(String taskGroup) {
        tasksList.get(taskGroup.toUpperCase()).clear();
    }

    public Object getTaskById(int Id) {
        for (Object object : tasksList.get("ОБЫЧНЫЕ ЗАДАЧИ")) {
            Task task = (Task) object;
            if (task.getId() == Id) {
                return task;
            }
        }
        for (Object object : tasksList.get("ЭПИКИ")) {
            Epic epic = (Epic) object;
            if (epic.getId() == Id) {
                return epic;
            }
        }
        for (Object object : tasksList.get("ПОДЗАДАЧИ")) {
            Subtask subtask = (Subtask) object;
            if (subtask.getId() == Id) {
                return subtask;
            }
        }
        return null;
    }

    public void updateTask(Object task) {
        if (task.getClass().equals(Task.class)) {
            for (int i = 0; i < tasksList.get("ОБЫЧНЫЕ ЗАДАЧИ").size(); i++) {
                Task task1 = (Task) tasksList.get("ОБЫЧНЫЕ ЗАДАЧИ").get(i);
                if (((Task) task).getId() == task1.getId()) {
                    tasksList.get("ОБЫЧНЫЕ ЗАДАЧИ").remove(i);
                    tasksList.get("ОБЫЧНЫЕ ЗАДАЧИ").add(i, task);
                }
            }
        } else if (task.getClass().equals(Epic.class)) {
            for (int i = 0; i < tasksList.get("ЭПИКИ").size(); i++) {
                Task task1 = (Task) tasksList.get("ЭПИКИ").get(i);
                if (((Task) task).getId() == task1.getId()) {
                    tasksList.get("ЭПИКИ").remove(i);
                    tasksList.get("ЭПИКИ").add(i, task);
                }
            }
        } else if (task.getClass().equals(Subtask.class)) {
            for (int i = 0; i < tasksList.get("ПОДЗАДАЧИ").size(); i++) {
                Task task1 = (Task) tasksList.get("ПОДЗАДАЧИ").get(i);
                if (((Task) task).getId() == task1.getId()) {
                    tasksList.get("ПОДЗАДАЧИ").remove(i);
                    tasksList.get("ПОДЗАДАЧИ").add(i, task);
                }
            }
            for (Object object : tasksList.get("ЭПИКИ")) {
                Epic epic = (Epic) object;
                if (epic.getName().equals(((Subtask) task).getEpicName())) {
                    int numberOfNew = 0;
                    int numberOfDone = 0;
                    for (Subtask subtask : epic.getSubtasksList()) {
                        if (subtask.getStatus().equals(Status.NEW)) {
                            numberOfNew++;
                        }
                        if (subtask.getStatus().equals(Status.DONE)) {
                            numberOfDone++;
                        }
                    }
                    if (numberOfNew == epic.getSubtasksList().size()) {
                        epic.setStatus(Status.NEW);
                        updateTask(epic);
                    } else if (numberOfDone == epic.getSubtasksList().size()) {
                        epic.setStatus(Status.DONE);
                        updateTask(epic);
                    } else {
                        epic.setStatus(Status.IN_PROGRESS);
                        updateTask(epic);
                    }
                }
            }
        }
    }

    public void deleteTaskById(int Id) {
        for (Object object : tasksList.get("ОБЫЧНЫЕ ЗАДАЧИ")) {
            Task task = (Task) object;
            if (task.getId() == Id) {
                tasksList.get("ОБЫЧНЫЕ ЗАДАЧИ").remove(task);
                return;
            }
        }
        for (Object object : tasksList.get("ЭПИКИ")) {
            Epic epic = (Epic) object;
            if (epic.getId() == Id) {
                tasksList.get("ЭПИКИ").remove(epic);
                return;
            }
        }
        for (Object object : tasksList.get("ПОДЗАДАЧИ")) {
            Subtask subtask = (Subtask) object;
            if (subtask.getId() == Id) {
                tasksList.get("ПОДЗАДАЧИ").remove(subtask);
                return;
            }
        }
    }

    public ArrayList<Subtask> getListOfSubtasksOfEpic(Epic epic) {
        for (Object object : tasksList.get("ЭПИКИ")) {
            Epic epic0 = (Epic) object;
            if (epic0.getId() == epic.getId()) {
                return epic0.getSubtasksList();
            }
        }
        return null;
    }
}
