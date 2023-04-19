package kanban.manager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;
import java.util.TreeSet;

import kanban.exceptions.TimeInputException;
import kanban.task.CommonTask;
import kanban.task.EpicTask;
import kanban.task.Status;
import kanban.task.SubTask;
import kanban.task.Task;

public class InMemoryTaskManager implements TaskManager {

    protected final HashMap<Long, Task> tasks;
    protected final HistoryManager historyManager;
    protected final TreeSet<Task> prioritisedTasks;

    /**
     * @constructor
     */
    public InMemoryTaskManager() {
        this.tasks = new HashMap<>();
        this.historyManager = Managers.getDefaultHistory();
        this.prioritisedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    }
    
    private void checkIntersections(LocalDateTime checkedTime) throws TimeInputException{
    	for (Task task:this.getPrioritisedTasks()) {
    		if(checkedTime.isAfter(task.getStartTime()) &&
    		   checkedTime.isBefore(this.getTaskEndTime(task.getId()))){
    			throw new TimeInputException("Время уже занято: ", task.getStartTime(), this.getTaskEndTime(task.getId()));
    		}
    	}
    }

    /**
     * @return all the tasks
     */
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    /**
     * @return all Common tasks
     */
    public ArrayList<Task> getAllCommonTasks() {
        ArrayList<Task> commonTasks = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (task.getClass().equals(CommonTask.class)) {
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
    
    public TreeSet<Task> getPrioritisedTasks(){
    	return this.prioritisedTasks;
    }

    /**
     * @clear all the tasks
     */
    public void clearTasks() {
    	if (!this.historyManager.getHistory().isEmpty()) {
        	this.historyManager.clear();
    	}
    	this.tasks.clear();
        this.prioritisedTasks.clear();
    }

    /**
     * @clear all the Common tasks
     */
    public void clearCommonTasks() {
        for (Long task : tasks.keySet()) {
            if (tasks.get(task) instanceof CommonTask) {
                this.removeCommonTask(task);
            }
        }
    }

    /**
     * @clear all the Epic tasks
     */
    public void clearEpicTasks() {
        for (Long task : tasks.keySet()) {
            if (tasks.get(task) instanceof EpicTask) {
                this.removeEpicTask(task);
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
            task.setCallTime(LocalDateTime.now());
            this.editCommonTask(id, task);
            this.historyManager.add(task);
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
            task.setCallTime(LocalDateTime.now());
            this.editEpicTask(id, task);
            this.historyManager.add(task);
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
            task.setCallTime(LocalDateTime.now());
            this.editSubTask(id, task);
            this.historyManager.add(task);
            ;
        }
        return task;
    }

    /**
     * @edit the Common task by id
     */
    public void editCommonTask(Long id, CommonTask task) {
        if (tasks.get(id) instanceof CommonTask) {
        	try {
            	this.checkIntersections(task.getStartTime());
            	this.tasks.put(id, task);
                this.prioritisedTasks.add(task);
            } catch (TimeInputException e){
            	e.getDetailedMessage();
            }
        }
    }

    /**
     * @edit the Epic task by id
     */
    public void editEpicTask(Long id, EpicTask task) {
        if (tasks.get(id) instanceof EpicTask) {
            try {
            	this.checkIntersections(task.getStartTime());
            	this.tasks.put(id, task);
                this.prioritisedTasks.add(task);
            } catch (TimeInputException e){
            	e.getDetailedMessage();
            }
        } 
    }

    /**
     * @edit the Sub task by id
     */
    public void editSubTask(Long id, SubTask task) {
        if (tasks.get(id) instanceof SubTask) {
        	try {
            	this.checkIntersections(task.getStartTime());
            	this.tasks.put(id, task);
            	this.updateEpic(task.getSuperTask());
                this.prioritisedTasks.add(task);
            } catch (TimeInputException e){
            	e.getDetailedMessage();
            }
        }
    }

    /**
     * @remove the Common task by id
     */
    public void removeCommonTask(Long id) {
        if (this.tasks.get(id) instanceof CommonTask) {
        	this.prioritisedTasks.remove(tasks.get(id));
        	this.tasks.remove(id);
            this.historyManager.remove(id);
        }
    }

    /**
     * @remove the Epic task by id
     */
    public void removeEpicTask(Long id) {
        if (this.tasks.get(id) instanceof EpicTask) {
        	EpicTask epicToDelete = this.getEpicTask(id);
            ArrayList<Long> subTasks = epicToDelete.getSubTasks();
            for (Long subTask : subTasks) {
                this.tasks.remove(subTask);
                this.historyManager.remove(subTask);
            }
            this.prioritisedTasks.remove(epicToDelete);
            this.historyManager.remove(id);
            this.tasks.remove(id);
        }
    }

    /**
     * @remove the Sub task by id
     */
    public void removeSubTask(Long id) {
        if (this.tasks.get(id) instanceof SubTask) {
        	SubTask subToDelete = this.getSubTask(id);
        	Long superTaskId = subToDelete.getSuperTask();
            EpicTask superTask = this.getEpicTask(superTaskId);
        	this.prioritisedTasks.remove(tasks.get(id));
            this.historyManager.remove(id);
            this.tasks.remove(id);
            superTask.getSubTasks().remove(id);
            this.editEpicTask(superTaskId,superTask);
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
    
    public LocalDateTime getTaskEndTime(Long id) {
    	Task task = this.tasks.get(id);
    	LocalDateTime start = task.getStartTime();
    	
    	if (task instanceof EpicTask) {
    		for (Long sub:((EpicTask) task).getSubTasks()) {
    			LocalDateTime result = start.plusMinutes(tasks.get(sub).getDuration().toMinutes());
            	return result;
        	}
    	}
    	LocalDateTime result = start.plusMinutes(task.getDuration().toMinutes());
    	return result;
    }

    /**
     * @update epic task status by id
     */
    private void updateEpic(Long id) {

        ArrayList<Status> statuses = new ArrayList<>();
        TreeSet<LocalDateTime> timeStamps = new TreeSet<>();
        Long totalDuration = 0L;
        		
        // Создание ряда данных с подзадачами
        for (Long task : this.tasks.keySet()) {
            if (this.tasks.get(task) instanceof SubTask) {
                if (((SubTask) this.tasks.get(task)).getSuperTask().equals(id)) {
                    statuses.add(this.tasks.get(task).getStatus());
                    timeStamps.add(this.tasks.get(task).getStartTime());
                    totalDuration += this.tasks.get(task).getDuration().toMinutes();
                }
            }
        }

        // Логика выбора статуса
        if (statuses.isEmpty() || !statuses.contains(Status.IN_PROGRESS) && !statuses.contains(Status.DONE)) {
            this.tasks.get(id).setStatus(Status.NEW);
        } else if (!statuses.contains(Status.NEW) && !statuses.contains(Status.IN_PROGRESS)) {
            this.tasks.get(id).setStatus(Status.DONE);
        } else {
            this.tasks.get(id).setStatus(Status.IN_PROGRESS);
        }
        
        //Логика обновления времени
        if (!timeStamps.isEmpty()) {
        	tasks.get(id).setStartTime(timeStamps.first());
        } 
        tasks.get(id).setDuration(Duration.ofMinutes(totalDuration));
    }

    /**
     * @put new Common task to the tasks
     */
    public Long createCommonTask(CommonTask task) {
        Long newId = null;
        if (task instanceof CommonTask) {
        	try {
        		this.checkIntersections(task.getStartTime());
        		Random random = new Random();
        		newId = Math.abs(random.nextLong());
        		while (this.tasks.containsKey(newId)) {
        			newId = random.nextLong();
        		}
        		task.setId(newId);
        		this.tasks.put(newId, task);
        		this.prioritisedTasks.add(task);
        	} catch (TimeInputException e){
        		e.getDetailedMessage();
        	}
        }
        return newId;
    }

    /**
     * @put new Epic task to the tasks
     */
    public Long createEpicTask(EpicTask task) {
        Long newId = null;
        if (task instanceof EpicTask) {
        	try {
            	this.checkIntersections(task.getStartTime());
            Random random = new Random();
            newId = Math.abs(random.nextLong());
            while (this.tasks.containsKey(newId)) {
                newId = random.nextLong();
            }
            task.setId(newId);
            this.tasks.put(newId, task);
            this.prioritisedTasks.add(task);
            this.updateEpic(newId);
        	} catch (TimeInputException e){
            	e.getDetailedMessage();
            }
        }
        return newId;
    }

    /**
     * @put new Sub task to the tasks
     */
    public Long createSubTask(SubTask task) {
        Long newId = null;
        if (task instanceof SubTask) {
        	try {
            	this.checkIntersections(task.getStartTime());
            	Random random = new Random();
            	newId = Math.abs(random.nextLong());
            	while (this.tasks.containsKey(newId)) {
            		newId = random.nextLong();
            	}
            	task.setId(newId);
            	this.tasks.put(newId, task);
            	ArrayList<Long> subTasks = ((EpicTask) this.tasks.get(task.getSuperTask())).getSubTasks();
            	subTasks.add(newId);
            	((EpicTask) this.tasks.get(task.getSuperTask())).setSubTasks(subTasks);
            	this.updateEpic(task.getSuperTask());
            	this.prioritisedTasks.add(task);
        	} catch (TimeInputException e){
            	e.getDetailedMessage();
            }
        }
        return newId;
    }

    /**
     * @return String of all tasks
     */
    @Override
    public String toString() {
        String result = "";
        for (Task task : this.tasks.values()) {
            result += task.toString() + "\n";
        }
        return result;
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }
}