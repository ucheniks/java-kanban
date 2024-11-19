public class Task {
    protected String name;
    protected String description;
    protected static int idCount;
    protected int id;
    protected Status status;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        idCount++;
        id = idCount;
        status = Status.NEW;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "'" + name + "'" + ", STATUS='" + status + "'";
    }
}
