package kanban.manager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import kanban.task.CommonTask;
import kanban.task.EpicTask;
import kanban.task.SubTask;
import kanban.task.Task;

public interface TaskManager {

    public static final HashMap<Long, Task> tasks = new HashMap<Long, Task>();

    /**
     * @return all the tasks
     */
    public ArrayList<Task> getAllTasks();

    /**
     * @return all Common tasks
     */
    public ArrayList<Task> getAllCommonTasks();

    /**
     * @return All Epic tasks
     */
    public ArrayList<Task> getAllEpicTasks();

    /**
     * @return All Sub tasks
     */
    public ArrayList<Task> getAllSubTasks();

    /**
     * @clear all the tasks
     */
    public void clearTasks();

    /**
     * @clear all the Common tasks
     */
    public void clearCommonTasks();

    /**
     * @clear all the Epic tasks
     */
    public void clearEpicTasks();

    /**
     * @clear all the Sub tasks
     */
    public void clearSubTasks();

    /**
     * @return the Common task by id
     */
    public CommonTask getCommonTask(Long id);

    /**
     * @return the Epic task by id
     */
    public EpicTask getEpicTask(Long id);

    /**
     * @return the Sub task by id
     */
    public SubTask getSubTask(Long id);

    /**
     * @edit the Common task by id
     */
    public void editCommonTask(Long id, CommonTask task);

    /**
     * @edit the Epic task by id
     */
    public void editEpicTask(Long id, EpicTask task);

    /**
     * @edit the Sub task by id
     */
    public void editSubTask(Long id, SubTask task);

    /**
     * @remove the Common task by id
     */
    public void removeCommonTask(Long id);

    /**
     * @remove the Epic task by id
     */
    public void removeEpicTask(Long id);

    /**
     * @remove the Sub task by id
     */
    public void removeSubTask(Long id);

    /**
     * @return the epic task subtasks by id
     */
    public ArrayList<Long> getEpicSubTasks(Long id);

    /**
     * @put new Common task to the tasks
     */
    public Long createCommonTask(CommonTask task);

    /**
     * @put new Epic task to the tasks
     */
    public Long createEpicTask(EpicTask task);

    /**
     * @put new Sub task to the tasks
     */
    public Long createSubTask(SubTask task);

    /**
     * @return String of all tasks
     */
    @Override
    public String toString();

    public HistoryManager getHistoryManager();

	public TreeSet<Task> getPrioritisedTasks();

	public LocalDateTime getTaskEndTime(Long taskId);
}