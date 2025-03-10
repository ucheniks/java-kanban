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
        return String.format("%d,SUBTASK,%s,%s,%s,%d,%s,%s",
                id,
                name,
                status,
                description,
                epicId,
                startTime != null ? startTime.format(DATE_TIME_FORMATTER) : "",
                duration != null ? duration.toMinutes() : "");
    }

}

