package kanban.manager;

import java.util.ArrayList;

import kanban.collections.TaskLinkedList;
import kanban.task.Task;

public class InMemoryHistoryManager implements HistoryManager {

    private final TaskLinkedList<Task> history;
    private static final int LIMIT = 10;
    
    /**
     * @construtor
     */
    public InMemoryHistoryManager() {
        this.history = new TaskLinkedList<>();
    }
    
    /**
     * @add a task to the list
     */
    public void add(Task task) {
        if (this.history.size() == LIMIT) {
            this.history.removeFirst();
        }
        this.history.add(task);
    }
    
    /**
     * @remove a task from the list by id
     */
    @Override
    public void remove(Long id) {
        this.history.remove(id);
    }
    
    /**
     * @get the list of tasks in memory
     */
    @SuppressWarnings("unchecked")
    public ArrayList<Task> getHistory() {
        return history.toArrayList();
    }
    
    /**
     * @print the list of tasks in history line by line
     */
    public void printHistory() {
        for (Task task : getHistory()) {
            System.out.println(task.toString());
        }
    }
}
