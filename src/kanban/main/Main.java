package kanban.main;

import java.util.ArrayList;

import kanban.manager.Managers;
import kanban.manager.TaskManager;
import kanban.task.CommonTask;
import kanban.task.EpicTask;
import kanban.task.SubTask;
import kanban.tests.TaskGenerator;

public class Main {
    
    public static void main(String[] args) {
        
        // Запуск программы. Инициализация менеджера задач. Инициализация рандомайзера
        System.out.println("Поехали!");
        TaskManager taskManager = Managers.getFileBackedManager();
        
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

        
        System.out.println(taskManager.toString());
    }
}
