package classes.tasks;

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

    public void updateStatus() {
        int numberOfNew = 0;
        int numberOfDone = 0;
        for (Subtask subtask : subtasksList.values()) {
            if (subtask.getStatus().equals(Status.NEW)) {
                numberOfNew++;
            }
            if (subtask.getStatus().equals(Status.DONE)) {
                numberOfDone++;
            }
            if (numberOfNew == subtasksList.size()) {
                setStatus(Status.NEW);
            } else if (numberOfDone == subtasksList.size()) {
                setStatus(Status.DONE);
            } else {
                setStatus(Status.IN_PROGRESS);
            }
        }
    }

    @Override
    public String toString() {
        return String.format("%d,EPIC,%s,%s,%s,", id, name, status, description);
    }
}
