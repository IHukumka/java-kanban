package kanban.manager;

import java.io.File;
import java.io.IOException;

public abstract class Managers {

    public static TaskManager getDefault(String url, String key) throws IOException, Exception {
        return new HttpTaskManager(url, key);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
    
    public static TaskManager getFileBackedManager(File file) {
        return FileBackedTaskManager.loadFromFile(file);
    }
    
    public static TaskManager getInMemoryManager() {
        return new InMemoryTaskManager();
    }
}
