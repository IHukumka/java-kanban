package kanban.manager;

import kanban.task.CommonTask;
import kanban.task.EpicTask;
import kanban.task.SubTask;
import kanban.task.Task;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.ArrayList;


public class FileBackedTaskManager extends InMemoryTaskManager  {
    
	private File file = new File(System.getProperty("user.dir")+"\\tasks.json");
    
    public FileBackedTaskManager(ArrayList<Task> loadedData, File file) {
    	super();
        for(Task task:loadedData) {
        	this.tasks.put(task.getId(), task);
        	this.historyManager.add(task);
        	this.prioritisedTasks.add(task);
        }
    }
    
    public FileBackedTaskManager() {
    	
    }
    
	public void save() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.writeValue(file, this.getAllTasks());
        }catch (IOException e) { 
            System.out.println("Ошибка записи данных в файл: " + file.getAbsolutePath());
        }
    }
    
    public static FileBackedTaskManager loadFromFile(File file) {
        
		ArrayList<Task> loadedData = new ArrayList<>();

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            TypeReference<ArrayList<Task>> typeRef = new TypeReference<ArrayList<Task>>() {};
            
            loadedData = mapper.readValue(file,typeRef);
        } catch (IOException e) {
            System.out.println("Ошибка загрузки данных!");
            e.printStackTrace();
        } 
        return new FileBackedTaskManager(loadedData, file);
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
    
    public static class LongSerializer extends JsonSerializer<Long> {
        @Override
        public void serialize(Long value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString(value.toString());
        }
    }

    public static class LongDeserializer extends JsonDeserializer<Long> {
        @Override
        public Long deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return p.getValueAsLong();
        }
    }
}