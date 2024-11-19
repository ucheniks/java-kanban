public class Subtask extends Task {
    private String epicName;

    public Subtask(String name, String description) {
        super(name, description);
    }

    public void setEpicName(String epicName) {
        this.epicName = epicName;
    }

    public String getEpicName() {
        return epicName;
    }

    @Override
    public String toString() {
        return "Подзадача '" + name + "'" + ", STATUS='" + status + "'" + ":\n" +
                "      Принадлежит эпику - '" + epicName + "';\n";
    }
}
