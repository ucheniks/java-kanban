import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Subtask> subtasksList;

    public Epic(String name, String description) {
        super(name, description);
        subtasksList = new ArrayList<>();
    }

    public ArrayList<Subtask> getSubtasksList() {
        return subtasksList;
    }

    public void addSubtask(Subtask subtask) {
        subtask.setEpicName(this.name);
        subtasksList.add(subtask);
    }

    @Override
    public String toString() {
        return "Эпик '" + name + "'" + ", STATUS='" + status + "'" + ":\n" + subtasksList.toString();
    }
}
