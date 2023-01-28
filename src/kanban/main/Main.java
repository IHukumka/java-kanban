package kanban.main;

import kanban.manager.Managers;
import kanban.manager.TaskManager;
import kanban.task.CommonTask;
import kanban.task.EpicTask;
import kanban.task.Status;
import kanban.task.SubTask;
import kanban.task.Task;

public class Main {

    public static void main(String[] args) {

        System.out.println("Поехали!");

        // Запуск менеджера задач (понял, красиво)
        TaskManager taskManager = Managers.getDefault();

        // Создание объектов с внесением в массив задач: обычная задача 1
        CommonTask commonTask1 = new CommonTask.Builder().
                setName       ("Тест имени таска 1").
                setDescription("Тест описания таска 1").
                setStatus     (Status.NEW).
                build();
        Long commonTaskId1 = taskManager.createCommonTask(commonTask1);

        // Создание объектов с внесением в массив задач: обычная задача 2
        CommonTask commonTask2 = new CommonTask.Builder().
                setName       ("Тест имени таска 2").
                setDescription("Тест описания таска 2").
                setStatus     (Status.IN_PROGRESS).
                build();
        Long commonTaskId2 = taskManager.createCommonTask(commonTask1);

        // Создание объектов с внесением в массив задач: эпик 1. Обновление статуса
        // эпика
        EpicTask epicTask1 = new EpicTask.Builder().
                setName       ("Тест имени эпика 1").
                setDescription("Тест описания эпика 1").
                build();
        Long epicId1 = taskManager.createEpicTask(epicTask1);

        // Создание объектов с внесением в массив задач: эпик 2. Обновление статуса
        // эпика
        EpicTask epicTask2 = new EpicTask.Builder().
                setName       ("Тест имени эпика 2").
                setDescription("Тест описания эпика 2").
                build();
        Long epicId2 = taskManager.createEpicTask(epicTask2);

        // Создание объектов с внесением в массив задач: сабтаск 1
        SubTask subTask1 = new SubTask.Builder().
                setName       ("Тест имени сабтаска 1").
                setDescription("Тест описания сабтаска 1").
                setStatus     (Status.DONE).
                setSuperTask  (epicId1).
                build();
        Long subTaskId1 = taskManager.createSubTask(subTask1);

        // Создание объектов с внесением в массив задач: сабтаск 2
        SubTask subTask2 = new SubTask.Builder().
                setName       ("Тест имени сабтаска 2").
                setDescription("Тест описания сабтаска 2").
                setStatus     (Status.NEW).
                setSuperTask  (epicId1).
                build();
        Long subTaskId2 = taskManager.createSubTask(subTask2);

        // Создание объектов с внесением в массив задач: сабтаск 3
        SubTask subTask3 = new SubTask.Builder().
                setName       ("Тест имени сабтаска 3").
                setDescription("Тест описания сабтаска 3").
                setStatus     (Status.IN_PROGRESS).
                setSuperTask (epicId2).
                build();
        Long subTaskId3 = taskManager.createSubTask(subTask3);

        // Проверка внесенных изменений
        System.out.println(taskManager.toString());

        // Изменение созданных задач и применение изменений к объектам в массиве
        commonTask1.setStatus(Status.IN_PROGRESS);
        commonTask2.setStatus(Status.DONE);
        subTask2.   setStatus(Status.DONE);
        subTask3.   setStatus(Status.DONE);
        taskManager.editCommonTask(commonTaskId1, commonTask1);
        taskManager.editCommonTask(commonTaskId2, commonTask2);
        taskManager.editSubTask   (subTaskId2, subTask2);
        taskManager.editSubTask   (subTaskId3, subTask3);

        // Проверка внесения изменений
        System.out.println(taskManager.toString());
        
        // Проверка вывода истории (исправил методы, но, кажется, криво)
        taskManager.getCommonTask(commonTaskId1);
        taskManager.getCommonTask(commonTaskId2);
        taskManager.getEpicTask  (epicId1);
        taskManager.getEpicTask  (epicId2);
        taskManager.getSubTask   (subTaskId1);
        taskManager.getSubTask   (subTaskId2);
        taskManager.getSubTask   (subTaskId3);
        System.out.println("История: " + taskManager.getHistoryManager().getHistory().toString());

        // Проверка удаления задач по id
        taskManager.removeCommonTask(commonTaskId1);
        taskManager.removeEpicTask(epicId1);

        // Проверка внесения изменений
        System.out.println(taskManager.toString());

        // Проверка отдельных методов получения списков задач
        for (Task task : taskManager.getAllCommonTasks()) {
            System.out.println(task.toString());
        }
        for (Task task : taskManager.getAllEpicTasks()) {
            System.out.println(task.toString());
        }
        for (Task task : taskManager.getAllSubTasks()) {
            System.out.println(task.toString());
        }

        // Проверка очистки выгрузки
        taskManager.clearTasks();
        System.out.println(taskManager.toString());
    }
}
