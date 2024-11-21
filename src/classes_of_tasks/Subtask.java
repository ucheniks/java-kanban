package classes_of_tasks;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description,int epicId) {
        super(name, description);
        this.epicId=epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "Подзадача '" + name + "'" + ", STATUS='" + status + "'" + "ID='"+id+" ':\n" +
                "      Принадлежит эпику - '" + epicId + "';\n";
    }
}
