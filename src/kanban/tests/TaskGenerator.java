package kanban.tests;

import java.util.ArrayList;
import java.util.Random;

import kanban.task.CommonTask;
import kanban.task.EpicTask;
import kanban.task.Status;
import kanban.task.SubTask;

public abstract class TaskGenerator {

    // Генератор случайных обычных задач.
    public static ArrayList<CommonTask> generateCommonTasks(int quantity) {
        ArrayList<CommonTask> result = new ArrayList<>();
        Random r = new Random();
        for (int i = 0; i < quantity; i++) {
            int id = r.nextInt(100);
            CommonTask commonTask = new CommonTask.Builder()
                    .setName("Тест имени таска " + id)
                    .setDescription("Тест описания таска " + id)
                    .setStatus(Status.NEW)
                    .build();
            result.add(commonTask);
        }
        return result;
    }
    
    // Генератор случайных эпиков.
    public static ArrayList<EpicTask> generateEpicTasks(int quantity) {
        ArrayList<EpicTask> result = new ArrayList<>();
        Random r = new Random();
        for (int i = 0; i < quantity; i++) {
            int id = r.nextInt(100);
            EpicTask epicTask = new EpicTask.Builder()
                    .setName("Тест имени эпика " + id)
                    .setDescription("Тест описания эпика " + id)
                    .build();
            result.add(epicTask);
        }
        return result;
    }
    
    // Генератор случайных подзадач.
    public static ArrayList<SubTask> generateSubTasks(int quantity, ArrayList<Long> epicIds) {
        ArrayList<SubTask> result = new ArrayList<>();
        Random r = new Random();
        for (int i = 0; i < quantity; i++) {
            int id = r.nextInt(100);
            SubTask subTask = new SubTask.Builder()
                    .setName("Тест имени саба " + id)
                    .setDescription("Тест описания саба " + id)
                    .setStatus(Status.NEW)
                    .setSuperTask(epicIds.get(r.nextInt(epicIds.size())))
                    .build();
            result.add(subTask);
        }
        return result;
    }
}
