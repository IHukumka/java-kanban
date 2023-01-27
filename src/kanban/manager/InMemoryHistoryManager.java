package kanban.manager;

import java.util.ArrayList;

import kanban.task.Task;

public class InMemoryHistoryManager implements HistoryManager {

    private ArrayList<Task> history;
    
    public InMemoryHistoryManager() {
        this.history = new ArrayList<>();
    }
    
    public void add(Task task) {
        history.add(task);
    }

    public ArrayList<Task> getHistory() {
        ArrayList<Task> historyOuttake = new ArrayList<>();
        for (int i = 0; i < 10 && i < this.history.size(); i ++) {
            historyOuttake.add(history.get(i));
        }
        return historyOuttake;
    }
}
