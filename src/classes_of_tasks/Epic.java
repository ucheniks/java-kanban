package classes_of_tasks;

import java.util.HashMap;

public class Epic extends Task {
    private HashMap<Integer, Subtask> subtasksList;

    public Epic(String name, String description) {
        super(name, description);
        subtasksList = new HashMap<>();
    }

    public HashMap<Integer, Subtask> getSubtasksList() {
        return subtasksList;
    }

    @Override
    public String toString() {
        return "Эпик '" + name + "'" + ", STATUS='" + status + "'" + " ID='"+id+" ':\n" + subtasksList.toString();
    }
}
