/**
 * 
 */
package kanban.manager;

import kanban.exceptions.ManagerSaveException;
import kanban.task.CommonTask;
import kanban.task.EpicTask;
import kanban.task.SubTask;
import kanban.task.Task;
import kanban.tests.TaskGenerator;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.ArrayList;

public class FileBackedTaskManager extends InMemoryTaskManager  {
    
    public static final String saveFile = System.getProperty("user.dir")+"\\tasks.json";
    
    public FileBackedTaskManager(ArrayList<Task>[] loadedData) {
        super();
        if (loadedData[0] != null){
        	for (Task task:loadedData[0]) {
            	this.tasks.put(task.getId(), task);
            }
            for (Task task:loadedData[1]) {
            	this.historyManager.add(task);
            }
            for (Task task:loadedData[2]) {
            	this.prioritisedTasks.add(task);
            }
        }
    }
    
    
	public void save() {
		
        File file = new File(saveFile);
        @SuppressWarnings("unchecked")
        ArrayList<Task>[] dataToSave = new ArrayList[3];
        dataToSave[0] = this.getAllTasks();
        dataToSave[1] = this.getHistoryManager().getHistory();
        for(Task task:this.prioritisedTasks) {
        	dataToSave[2].add(task);
        }
        
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.writeValue(file, dataToSave);
            System.out.println("Данные сохранены!");
        }catch (IOException e) { 
            throw new ManagerSaveException("Ошибка записи данных в файл: " + saveFile);
        }
    }
    
    public static FileBackedTaskManager loadFromFile(File file) {
        
    	@SuppressWarnings("unchecked")
		ArrayList<Task>[] loadedData = new ArrayList[3];

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
            mapper.readerFor(Task.class);
            mapper.registerModule(new JavaTimeModule());
            TypeReference<ArrayList<Task>[]> typeRef = new TypeReference<ArrayList<Task>[]>() {};
            
            loadedData[0] = mapper.readValue(file,typeRef)[0];
            loadedData[1] = mapper.readValue(file,typeRef)[1];
            loadedData[2] = mapper.readValue(file,typeRef)[2];
            System.out.println("Данные загружены!");
        }catch (IOException|NullPointerException e) {
            System.out.println("Ошибка загрузки данных!");
            e.printStackTrace();
        }
        return new FileBackedTaskManager(loadedData);
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