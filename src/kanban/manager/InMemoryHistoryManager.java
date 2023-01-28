package kanban.manager;

import java.util.ArrayList;

import kanban.task.Task;

public class InMemoryHistoryManager implements HistoryManager {

    private final ArrayList<Task> history;
    private static final short LIMIT = 10;
    
    public InMemoryHistoryManager() {
        this.history = new ArrayList<>();
    }
    
    public void add(Task task) {
        history.add(task);
    }

    public ArrayList<Task> getHistory() {
        ArrayList<Task> historyOuttake = new ArrayList<>();
        for (short i = 0; i < LIMIT && i < this.history.size(); i ++) {
            historyOuttake.add(history.get(i));
        }
        return historyOuttake;
    }
}
