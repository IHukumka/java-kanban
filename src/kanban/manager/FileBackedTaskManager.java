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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class FileBackedTaskManager extends InMemoryTaskManager  {
    
    public static final String saveFile = System.getProperty("user.dir")+"\\tasks.json";
//    File file = new File(saveFile);
    
    public static void main(String[] args) {
    	// Запуск программы. Инициализация менеджера задач.
    	File file = new File(saveFile);
    	Random r = new Random();
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
        for (int i = 0; i < 1; i++) {
            for (int x = 0; x < 3; x++) {
                taskManager.getCommonTask(commonTasksIds.get(r.nextInt(commonTasksIds.size())));
                taskManager.getEpicTask(epicTasksIds.get(r.nextInt(epicTasksIds.size())));
                taskManager.getSubTask(subTasksIds.get(r.nextInt(subTasksIds.size())));
            }
        }

        //Контрольное отображение истории
        System.out.println(taskManager.toString());
        System.out.println("История:");
        taskManager.getHistoryManager().printHistory();
        
        // Создание нового менеджера, проверка загрузки данных из файла
        FileBackedTaskManager testManager = FileBackedTaskManager.loadFromFile(file);
        System.out.println(testManager.toString());
        System.out.println("Загруженная история:");
        taskManager.getHistoryManager().printHistory();
        
    }
    
    public FileBackedTaskManager(HashMap<Long,Task> loadedTasks, ArrayList<Task> loadedHistory) {
        super();
        for (Task task:loadedTasks.values()){
        	this.tasks.put(task.getId(),task);
        }
        for (Task task:loadedHistory) {
            this.historyManager.add(task);
        }
    }
    
    public void save() {
        
        File file = new File(saveFile);
        ArrayList<Task> tasksToSave = this.getAllTasks();
        ArrayList<Task> historyToSave = this.getHistoryManager().getHistory();
        ArrayList<ArrayList<Task>> dataToSave = 
                new ArrayList<ArrayList<Task>>(List.of(tasksToSave, historyToSave));
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
        
    	HashMap<Long,Task> loadedTasks = new HashMap<>();
    	ArrayList<Task> loadedHistory = new ArrayList<>();

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
            mapper.readerFor(Task.class);
            mapper.registerModule(new JavaTimeModule());
            TypeReference<ArrayList<ArrayList<Task>>> typeRef = 
                    new TypeReference<ArrayList<ArrayList<Task>>>() {};
            int i = 0;        
            for(ArrayList<Task> taskList:mapper.readValue(file,typeRef)) {
                if (i == 0) {
                    
                    for (Task task:taskList) {
                        loadedTasks.put(task.getId(), task);
                    }
                    i++;
                } else if (i == 1) {
                    for (Task task:taskList) {
                        loadedHistory.add(task);
                    }
                } else {
                    break;
                }
            }
            System.out.println("Данные загружены!");
        }catch (IOException e) {
            System.out.println("Ошибка загрузки данных!");
            e.printStackTrace();
        }
        return new FileBackedTaskManager(loadedTasks,loadedHistory);
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