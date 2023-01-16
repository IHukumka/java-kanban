package kanban;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class TaskManager {

	private HashMap<Long, CommonTask> tasks;

	/**
	 * @return the tasks
	 */
	public HashMap<Long, CommonTask> getTasks() {
		return tasks;
	}

	/**
	 * @constructor
	 */
	public TaskManager(String userName) {
		this.tasks = FileHandler.loadUserTasks(userName);
	}
	
	/**
	 * @clear the tasks
	 */
	public void clearTasks() {
		this.tasks.clear();
	}
	
	/**
	 * @return the task by id
	 */
	public CommonTask getTask(Long id) {
		return this.tasks.get(id);
	}
	
	/**
	 * @edit the task by id
	 */
	public void editTask(Long id, CommonTask task) {
		this.tasks.put(id, task);
	}
	
	/**
	 * @remove the task by id
	 */
	public void removeTask(Long id) {
		this.tasks.remove(id);
	}
	
	/**
	 * @return the epic task subtasks by id
	 */
	public ArrayList<Long> getEpicSubTasks(Long id){
		return ((EpicTask) this.tasks.get(id)).getSubTasks();
	}
	
	/**
	 * @update epic task status by id
	 */
	public void updateEpicStatus(Long id) {

		ArrayList<String> statuses = new ArrayList<>();
		
		//Создание ряда данных с подзадачами
		for (Long task:this.tasks.keySet()) {
			if (this.tasks.get(task) instanceof SubTask) {
				if (((SubTask) this.tasks.get(task)).getSuperTask().equals(id)) {
					statuses.add(this.tasks.get(task).getStatus());
				}
			}
		}
		
		//Логика выбора статуса
		if (statuses.isEmpty() || !statuses.contains("IN_PROGRESS") && !statuses.contains("DONE") ) {
			this.tasks.get(id).setStatus("NEW");
		} else if (!statuses.contains("NEW") && !statuses.contains("IN_PROGRESS")) {
			this.tasks.get(id).setStatus("DONE");
		} else {
			this.tasks.get(id).setStatus("IN_PROGRESS");
		}
	}
	
	/**
	 * @put new task to the tasks
	 */
	public Long createTask(CommonTask task) {
		Random random = new Random();
		Long newId = Math.abs(random.nextLong());
		while(this.tasks.containsKey(newId)) {
			newId = random.nextLong();
		}
		this.tasks.put(newId, task);
		return newId;
	}
}