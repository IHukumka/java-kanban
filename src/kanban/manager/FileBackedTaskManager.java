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
import java.util.HashMap;

public class FileBackedTaskManager extends InMemoryTaskManager  {
    
    public static final String saveFile = System.getProperty("user.dir")+"\\tasks.json";
//    File file = new File(saveFile);
    
    public static void main(String[] args) {
    	// Запуск программы. Инициализация менеджера задач.
    	File file = new File(saveFile);
        TaskManager taskManager = Managers.getFileBackedManager(file);
        System.out.println(taskManager.toString());

        // Создание объектов с внесением в массив задач: обычные задачи.
        ArrayList<CommonTask> commonTasks = TaskGenerator.generateCommonTasks(5);
        ArrayList<Long> commonTasksIds = new ArrayList<>();
        for (CommonTask task : commonTasks) {
            Long commonTaskId = taskManager.createCommonTask(task);
            commonTasksIds.add(commonTaskId);
        }

        // Создание объектов с внесением в массив задач: эпики.
        ArrayList<EpicTask> epicTasks = TaskGenerator.generateEpicTasks(2);
        ArrayList<Long> epicTasksIds = new ArrayList<>();
        for (EpicTask task : epicTasks) {
            Long epicId = taskManager.createEpicTask(task);
            epicTasksIds.add(epicId);
        }
        

        // Создание объектов с внесением в массив задач: сабтаски.
        ArrayList<SubTask> subTasks = TaskGenerator.generateSubTasks(7, epicTasksIds);
        ArrayList<Long> subTasksIds = new ArrayList<>();
        for (SubTask task : subTasks) {
            Long subTaskId = taskManager.createSubTask(task);
            subTasksIds.add(subTaskId);
        }
        
        // Запрос задач для заполнения истории
        taskManager.getAllEpicTasks();
        taskManager.getAllCommonTasks();

        //Контрольное отображение истории
        System.out.println(taskManager.toString());
        
        // Создание нового менеджера, проверка загрузки данных из файла
        FileBackedTaskManager testManager = FileBackedTaskManager.loadFromFile(file);
        System.out.println(testManager.toString());
        
    }
    
    public FileBackedTaskManager(HashMap<Long,Task> loadedTasks) {
        super();
        for (Task task:loadedTasks.values()){
        	this.tasks.put(task.getId(),task);
        }
    }
    
    public void save() {
        
        File file = new File(saveFile);
        ArrayList<Task> tasksToSave = this.getAllTasks();

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.writeValue(file, tasksToSave);
            System.out.println("Данные сохранены!");
        }catch (IOException e) { 
            throw new ManagerSaveException("Ошибка записи данных в файл: " + saveFile);
        }
    }
    
    public static FileBackedTaskManager loadFromFile(File file) {
        
    	HashMap<Long,Task> loadedTasks = new HashMap<>();

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
            mapper.readerFor(Task.class);
            mapper.registerModule(new JavaTimeModule());
            TypeReference<ArrayList<Task>> typeRef = new TypeReference<ArrayList<Task>>() {};
            for (Task task:mapper.readValue(file,typeRef)) {
                loadedTasks.put(task.getId(), task);
            }
            System.out.println("Данные загружены!");
        }catch (IOException e) {
            System.out.println("Ошибка загрузки данных!");
            e.printStackTrace();
        }
        return new FileBackedTaskManager(loadedTasks);
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