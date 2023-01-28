package kanban.manager;

import java.util.ArrayList;

import kanban.task.Task;

public interface HistoryManager {
     
    /**
     * @put new Task to the history
     */
    public void add(Task task);
    
    /**
     * @return List of seen tasks
     */ 
    public ArrayList<Task> getHistory();
}
