package kanban.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import kanban.task.CommonTask;
import kanban.task.EpicTask;
import kanban.task.SubTask;
import kanban.task.Task;

public class TaskManager {

    private HashMap<Long, Task> tasks;

    /**
     * @constructor
     */
    public TaskManager() {
        this.tasks = new HashMap<>();
    }

    /**
     * @return all the tasks
     */
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> allTasks = new ArrayList<>();
        for (Task task : tasks.values()) {
            allTasks.add(task);
        }
        return allTasks;
    }

    /**
     * @return all Common tasks
     */
    public ArrayList<Task> getAllCommonTasks() {
        ArrayList<Task> commonTasks = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (task instanceof CommonTask) {
                commonTasks.add(task);
            }
        }
        return commonTasks;
    }

    /**
     * @return All Epic tasks
     */
    public ArrayList<Task> getAllEpicTasks() {
        ArrayList<Task> epicTasks = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (task instanceof EpicTask) {
                epicTasks.add(task);
            }
        }
        return epicTasks;
    }

    /**
     * @return All Sub tasks
     */
    public ArrayList<Task> getAllSubTasks() {
        ArrayList<Task> subTasks = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (task instanceof SubTask) {
                subTasks.add(task);
            }
        }
        return subTasks;
    }

    /**
     * @clear all the tasks
     */
    public void clearTasks() {
        this.tasks.clear();
    }

    /**
     * @clear all the Common tasks
     */
    public void clearCommonTasks() {
        for (Long task : tasks.keySet()) {
            if (tasks.get(task) instanceof CommonTask) {
                tasks.remove(task);
            }
        }
    }

    /**
     * @clear all the Epic tasks
     */
    public void clearEpicTasks() {
        for (Long task : tasks.keySet()) {
            if (tasks.get(task) instanceof EpicTask) {
                tasks.remove(task);
            }
        }
    }

    /**
     * @clear all the Sub tasks
     */
    public void clearSubTasks() {
        for (Long task : tasks.keySet()) {
            if (tasks.get(task) instanceof SubTask) {
                this.removeSubTask(task);
            }
        }
    }

    /**
     * @return the Common task by id
     */
    public CommonTask getCommonTask(Long id) {
        CommonTask task = new CommonTask.Builder().build();
        if (tasks.get(id) instanceof CommonTask) {
            task = (CommonTask) this.tasks.get(id);
        }
        return task;
    }

    /**
     * @return the Epic task by id
     */
    public EpicTask getEpicTask(Long id) {
        EpicTask task = new EpicTask.Builder().build();
        if (tasks.get(id) instanceof EpicTask) {
            task = (EpicTask) this.tasks.get(id);
        }
        return task;
    }

    /**
     * @return the Sub task by id
     */
    public SubTask getSubTask(Long id) {
        SubTask task = new SubTask.Builder().build();
        if (tasks.get(id) instanceof SubTask) {
            task = (SubTask) this.tasks.get(id);
        }
        return task;
    }

    /**
     * @edit the Common task by id
     */
    public void editCommonTask(Long id, CommonTask task) {
        if (tasks.get(id) instanceof CommonTask) {
            this.tasks.put(id, task);
        }
    }

    /**
     * @edit the Epic task by id
     */
    public void editEpicTask(Long id, EpicTask task) {
        if (tasks.get(id) instanceof EpicTask) {
            this.tasks.put(id, task);
        }
    }

    /**
     * @edit the Sub task by id
     */
    public void editSubTask(Long id, SubTask task) {
        if (tasks.get(id) instanceof SubTask) {
            this.tasks.put(id, task);
            this.updateEpicStatus(task.getSuperTask());
        }
    }

    /**
     * @remove the Common task by id
     */
    public void removeCommonTask(Long id) {
        if (this.tasks.get(id) instanceof CommonTask) {
            this.tasks.remove(id);
        }
    }

    /**
     * @remove the Epic task by id
     */
    public void removeEpicTask(Long id) {
        if (this.tasks.get(id) instanceof EpicTask) {
            ArrayList<Long> subTasks = ((EpicTask) this.tasks.get(id)).getSubTasks();
            this.tasks.remove(id);
            for (Long subTask : subTasks) {
                this.tasks.remove(subTask);
            }
        }
    }

    /**
     * @remove the Sub task by id
     */
    public void removeSubTask(Long id) {
        if (this.tasks.get(id) instanceof SubTask) {
            Long superTaskId = ((SubTask) this.tasks.get(id)).getSuperTask();
            this.tasks.remove(id);
            ((EpicTask) this.tasks.get(superTaskId)).getSubTasks()
                    .remove(((EpicTask) this.tasks.get(superTaskId)).getSubTasks().indexOf(id));
            this.updateEpicStatus(superTaskId);
        }
    }

    /**
     * @return the epic task subtasks by id
     */
    public ArrayList<Long> getEpicSubTasks(Long id) {
        ArrayList<Long> subTasks = new ArrayList<>();
        if (this.tasks.get(id) instanceof EpicTask) {
            subTasks = ((EpicTask) this.tasks.get(id)).getSubTasks();
        }
        return subTasks;
    }

    /**
     * @update epic task status by id
     */
    private void updateEpicStatus(Long id) {

        ArrayList<String> statuses = new ArrayList<>();

        // Создание ряда данных с подзадачами
        for (Long task : this.tasks.keySet()) {
            if (this.tasks.get(task) instanceof SubTask) {
                if (((SubTask) this.tasks.get(task)).getSuperTask().equals(id)) {
                    statuses.add(this.tasks.get(task).getStatus());
                }
            }
        }

        // Логика выбора статуса
        if (statuses.isEmpty() || !statuses.contains("IN_PROGRESS") && !statuses.contains("DONE")) {
            this.tasks.get(id).setStatus("NEW");
        } else if (!statuses.contains("NEW") && !statuses.contains("IN_PROGRESS")) {
            this.tasks.get(id).setStatus("DONE");
        } else {
            this.tasks.get(id).setStatus("IN_PROGRESS");
        }
    }

    /**
     * @put new Common task to the tasks
     */
    public Long createCommonTask(CommonTask task) {
        Long newId = null;
        if (task instanceof CommonTask) {
            Random random = new Random();
            newId = Math.abs(random.nextLong());
            while (this.tasks.containsKey(newId)) {
                newId = random.nextLong();
            }
            this.tasks.put(newId, task);
        }
        return newId;
    }

    /**
     * @put new Epic task to the tasks
     */
    public Long createEpicTask(EpicTask task) {
        Long newId = null;
        if (task instanceof EpicTask) {
            Random random = new Random();
            newId = Math.abs(random.nextLong());
            while (this.tasks.containsKey(newId)) {
                newId = random.nextLong();
            }
            this.tasks.put(newId, task);
        }
        return newId;
    }

    /**
     * @put new Sub task to the tasks
     */
    public Long createSubTask(SubTask task) {
        Long newId = null;
        if (task instanceof SubTask) {
            Random random = new Random();
            newId = Math.abs(random.nextLong());
            while (this.tasks.containsKey(newId)) {
                newId = random.nextLong();
            }
            this.tasks.put(newId, task);
            ArrayList<Long> subTasks = ((EpicTask) this.tasks.get(task.getSuperTask())).getSubTasks();
            subTasks.add(newId);
            ((EpicTask) this.tasks.get(task.getSuperTask())).setSubTasks(subTasks);
            this.updateEpicStatus(task.getSuperTask());
        }
        return newId;
    }

    /**
     * @return String of all tasks
     */
    @Override
    public String toString() {
        String result = "";
        for (Long task : this.tasks.keySet()) {
            result += this.taskToString(task) + ", ";
        }
        return result;
    }

    /**
     * @return String of one tasks
     */
    public String taskToString(Long id) {
        return id + " " + this.tasks.get(id).toString();
    }
}