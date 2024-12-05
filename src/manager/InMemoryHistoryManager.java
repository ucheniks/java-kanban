package manager;

import classes.tasks.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private final ArrayList<Task> historyOfTasks;
    private static final int SIZE=10;
    public InMemoryHistoryManager() {
        historyOfTasks = new ArrayList<>();
    }

    @Override
    public void add(Task task) {
        if (historyOfTasks.size() == SIZE) {
            historyOfTasks.removeFirst();
        }
        historyOfTasks.add(task);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return historyOfTasks;
    }
}
