package classes.tasks;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "'" + name + "'" + ", STATUS='" + status + "'" + "ID='" + id + "', " +
                "Принадлежит эпику - '" + epicId + "';\n";
    }
}
