package kanban.tests;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import kanban.task.CommonTask;
import kanban.task.EpicTask;
import kanban.task.Status;
import kanban.task.SubTask;

public abstract class TaskGenerator {
	
	static final long DATE1 = 1672520400000L;
    static final long DATE2 = 1704056400000L;

    // Генератор случайных обычных задач.
    public static ArrayList<CommonTask> generateCommonTasks(int quantity) {
        ArrayList<CommonTask> result = new ArrayList<>();
        Random r = new Random();
        for (int i = 0; i < quantity; i++) {
            int id = r.nextInt(100);
            Long millis = ThreadLocalRandom.current().nextLong(DATE2 - DATE1) + DATE1;
            LocalDateTime start = LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault());
            Duration duration = Duration.ofMinutes(r.nextInt(90));
            CommonTask commonTask = new CommonTask.Builder()
                    .setName("Тест имени таска " + id)
                    .setDescription("Тест описания таска " + id)
                    .setStatus(Status.NEW)
                    .setStartTime(start)
                    .setDuration(duration)
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
            Long millis = ThreadLocalRandom.current().nextLong(DATE2 - DATE1) + DATE1;
            LocalDateTime start = LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault());
            EpicTask epicTask = new EpicTask.Builder()
                    .setName("Тест имени эпика " + id)
                    .setDescription("Тест описания эпика " + id)
                    .setStatus(Status.NEW)
                    .setStartTime(start)
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
            Long millis = ThreadLocalRandom.current().nextLong(DATE2 - DATE1) + DATE1;
            LocalDateTime start = LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault());
            Duration duration = Duration.ofMinutes(r.nextInt(90));
            SubTask subTask = new SubTask.Builder()
                    .setName("Тест имени саба " + id)
                    .setDescription("Тест описания саба " + id)
                    .setStatus(Status.NEW)
                    .setSuperTask(epicIds.get(r.nextInt(epicIds.size())))
                    .setStartTime(start)
                    .setDuration(duration)
                    .build();
            result.add(subTask);
        }
        return result;
    }
}
