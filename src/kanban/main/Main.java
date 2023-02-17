package kanban.main;

import java.util.ArrayList;
import java.util.Random;

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
        TaskManager taskManager = Managers.getDefault();
        Random r = new Random();

        // Создание объектов с внесением в массив задач: обычные задачи.
        ArrayList<CommonTask> commonTasks = TaskGenerator.generateCommonTasks(2);
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
        ArrayList<SubTask> subTasks = TaskGenerator.generateSubTasks(3, epicTasksIds);
        ArrayList<Long> subTasksIds = new ArrayList<>();
        for (SubTask task : subTasks) {
            Long subTaskId = taskManager.createSubTask(task);
            subTasksIds.add(subTaskId);
        }

        // Тесты x3
        for (int i = 0; i < 1; i++) {
            // Вызов случайных задач. Паузы вставлены для наглядности
            for (int x = 0; x < 3; x++) {
                taskManager.getCommonTask(commonTasksIds.get(r.nextInt(commonTasksIds.size())));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                taskManager.getEpicTask(epicTasksIds.get(r.nextInt(epicTasksIds.size())));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                taskManager.getSubTask(subTasksIds.get(r.nextInt(subTasksIds.size())));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // Проверка вывода истории
            System.out.println("История после вызовов:");
            taskManager.getHistoryManager().printHistory();

            // Удаление случайных задач
            for (int x = 0; x < 1; x++) {
                taskManager.removeCommonTask(commonTasksIds.get(r.nextInt(commonTasksIds.size())));
                taskManager.removeEpicTask(epicTasksIds.get(r.nextInt(epicTasksIds.size())));
                taskManager.removeSubTask(subTasksIds.get(r.nextInt(subTasksIds.size())));
            }

            // Проверка вывода истории
            System.out.println("История после удаления задач:");
            taskManager.getHistoryManager().printHistory();
        }
    }
}
