package classes.tasks;

import manager.FileBackedTaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task {
    protected static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    protected String name;
    protected String description;
    protected int id;
    protected Status status;
    protected Duration duration;
    protected LocalDateTime startTime;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        status = Status.NEW;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setDuration(long duration) {
        this.duration = Duration.ofMinutes(duration);
    }

    public void setStartTime(String startTime) {
        this.startTime = LocalDateTime.parse(startTime, DATE_TIME_FORMATTER);
    }

    public Duration getDuration() {
        return duration;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
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

    public LocalDateTime getEndTime() {
        LocalDateTime endTime = startTime.plus(duration);
        return endTime;
    }

    @Override
    public String toString() {
        return String.format("%d,TASK,%s,%s,%s, ,%s,%s",
                id,
                name,
                status,
                description,
                startTime != null ? startTime.format(DATE_TIME_FORMATTER) : "",
                duration != null ? duration.toMinutes() : "");
    }
}
