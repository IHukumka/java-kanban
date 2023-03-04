package kanban.manager;

import java.io.File;

public abstract class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
    
    public static TaskManager getFileBackedManager(File file) {
        return FileBackedTaskManager.loadFromFile(file);
    }
}
