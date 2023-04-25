package kanban.manager;

import java.io.IOException;
import java.util.ArrayList;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import kanban.api.KVTaskClient;
import kanban.task.CommonTask;
import kanban.task.EpicTask;
import kanban.task.SubTask;
import kanban.task.Task;

public class HttpTaskManager extends FileBackedTaskManager{
	
	private static KVTaskClient client;
	private String key;
	private static ObjectMapper mapper = new ObjectMapper();
	
	public HttpTaskManager(String url, String key) throws IOException, Exception {
		super();
		client = new KVTaskClient(url);
		for(Task task:fromUrl(url,key)) {
        	this.tasks.put(task.getId(), task);
        	this.historyManager.add(task);
        	this.prioritisedTasks.add(task);
        }
	}
	
	
	public static ArrayList<Task> fromUrl(String url, String key) throws Exception {		
		ArrayList<Task> loadedData = new ArrayList<>();
		try {
            mapper.registerModule(new JavaTimeModule());
            TypeReference<ArrayList<Task>> typeRef = new TypeReference<ArrayList<Task>>() {};
            loadedData = mapper.readValue(client.load(key),typeRef);
        } catch (IOException|NullPointerException e) {
            System.out.println("Ошибка загрузки данных!");
            e.printStackTrace();
        } catch (InterruptedException e) {
			e.printStackTrace();
		}
		return loadedData;
	}
	
	@Override
	public void save(){
		super.save();
		try {
			String dataToSave = mapper.
	        		writerWithDefaultPrettyPrinter().
	        		writeValueAsString(tasks);
			client.put(key, dataToSave);
		} catch (IOException e){
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
    public void clearTasks() {
        super.clearTasks();
        this.save();
    }

    @Override
    public void clearCommonTasks() {
        super.clearCommonTasks();
        this.save();
    }

    @Override
    public void clearEpicTasks() {
        super.clearEpicTasks();
        this.save();
    }

    @Override
    public void clearSubTasks() {
        super.clearSubTasks();
        this.save();
    }

    @Override
    public void editCommonTask(Long id, CommonTask task) {
        super.editCommonTask(id,task);
        this.save();
    }

    @Override
    public void editEpicTask(Long id, EpicTask task) {
        super.editEpicTask(id,task);
        this.save();
    }

    @Override
    public void editSubTask(Long id, SubTask task) {
        super.editSubTask(id,task);
        this.save();
    }

    @Override
    public void removeCommonTask(Long id) {
        super.removeCommonTask(id);
        this.save();
    }

    @Override
    public void removeEpicTask(Long id) {
        super.removeEpicTask(id);
        this.save();
    }

    @Override
    public void removeSubTask(Long id) {
        super.removeSubTask(id);
        this.save();
    }

    @Override
    public Long createCommonTask(CommonTask task) {
        Long newTask = super.createCommonTask(task);
        this.save();
        return newTask;
    }

    @Override
    public Long createEpicTask(EpicTask task) {
        Long newTask = super.createEpicTask(task);
        this.save();
        return newTask;
    }

    @Override
    public Long createSubTask(SubTask task) {
        Long newTask = super.createSubTask(task);
        this.save();
        return newTask;
    }
}
