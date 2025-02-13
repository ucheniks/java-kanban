package classes.tasks;

import manager.FileBackedTaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Epic extends Task {
    private HashMap<Integer, Subtask> subtasksList;
    private LocalDateTime endTime;

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

    public void updateTime() {
        if (subtasksList.isEmpty()) {
            setEmptyTime();
            return;
        }
        Optional<LocalDateTime> optionalStartTime = subtasksList.values().stream().
                map(Subtask::getStartTime).
                filter(time -> time != null).
                min(LocalDateTime::compareTo);

        Optional<Subtask> endSubtask = subtasksList.values().stream().
                filter(subtask -> (subtask.getStartTime() != null) && (subtask.getDuration() != null)).
                max(Comparator.comparing(subtask -> subtask.getStartTime().plus(subtask.getDuration())));


        long intDuration = subtasksList.values().stream().
                filter(subtask -> subtask.getDuration() != null).
                mapToLong(subtask -> subtask.getDuration().toMinutes()).
                sum();

        if ((optionalStartTime.isPresent()) && (endSubtask.isPresent())) {
            startTime = optionalStartTime.get();
            endTime = endSubtask.map(subtask -> subtask.getStartTime().plus(subtask.getDuration())).orElse(null);
            duration = Duration.ofMinutes(intDuration);
        }

    }

    public void setEmptyTime() {
        startTime = null;
        endTime = null;
        duration = null;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return String.format("%d,EPIC,%s,%s,%s, ,%s,%s",
                id,
                name,
                status,
                description,
                startTime != null ? startTime.format(DATE_TIME_FORMATTER) : "",
                duration != null ? duration.toMinutes() : "");
    }
}
