package manager;

import classes.tasks.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager{
    ArrayList<Task> historyOfTasks;

    public InMemoryHistoryManager() {
        historyOfTasks=new ArrayList<>();
    }

    @Override
    public void add(Task task){
        if (historyOfTasks.size()<10){
            historyOfTasks.add(task);
        } else {
            historyOfTasks.removeFirst();
            historyOfTasks.add(task);
        }
    }
    @Override
    public ArrayList<Task> getHistory() {
        return historyOfTasks;
    }
}
