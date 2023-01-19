package kanban;

public class Main {

	public static void main(String[] args) {

		System.out.println("Поехали!");
		
		//Запуск менеджера задач с выгрузкой данных из файла по имени пользователя
		TaskManager manager = new TaskManager();
		
		//Создание объектов с внесением в массив задач: обычная задача 1
		CommonTask commonTask1 = new CommonTask.Builder().
				setName("Тест имени таска 1").
				setDescription("Тест описания таска 1").
				setStatus("NEW").
				build();
		Long commonTaskId1 = manager.createCommonTask(commonTask1);
		
		//Создание объектов с внесением в массив задач: обычная задача 2
		CommonTask commonTask2 = new CommonTask.Builder().
				setName("Тест имени таска 2").
				setDescription("Тест описания таска 2").
				setStatus("IN_PROGRESS").
				build();
		Long commonTaskId2 = manager.createCommonTask(commonTask1);
		
		//Создание объектов с внесением в массив задач: эпик 1. Обновление статуса эпика
		EpicTask epicTask1 = new EpicTask.Builder().
				setName("Тест имени эпика 1").
				setDescription("Тест описания эпика 1").
				build();
		Long epicId1 = manager.createEpicTask(epicTask1);
		
		//Создание объектов с внесением в массив задач: эпик 2. Обновление статуса эпика
		EpicTask epicTask2 = new EpicTask.Builder().
				setName("Тест имени эпика 2").
				setDescription("Тест описания эпика 2").
				build();
		Long epicId2 = manager.createEpicTask(epicTask2);
		
		//Создание объектов с внесением в массив задач: сабтаск 1
		SubTask subTask1 = new SubTask.Builder().
				setName("Тест имени сабтаска 1").
				setDescription("Тест описания сабтаска 1").
				setStatus("DONE").
				setSuperTask(epicId1).
				build();
		@SuppressWarnings("unused")
        Long subTaskId1 = manager.createSubTask(subTask1);
		
		//Создание объектов с внесением в массив задач: сабтаск 2
		SubTask subTask2 = new SubTask.Builder().
				setName("Тест имени сабтаска 2").
				setDescription("Тест описания сабтаска 2").
				setStatus("NEW").
				setSuperTask(epicId1).
				build();
		Long subTaskId2 = manager.createSubTask(subTask2);
		
		//Создание объектов с внесением в массив задач: сабтаск 3
		SubTask subTask3 = new SubTask.Builder().
				setName("Тест имени сабтаска 3").
				setDescription("Тест описания сабтаска 3").
				setStatus("IN_PROGRESS").
				setSuperTask(epicId2).
				build();
		Long subTaskId3 = manager.createSubTask(subTask3);
		
		//Проверка внесенных изменений
		System.out.println(manager.toString());
		
		//Изменение созданных задач и применение изменений к объектам в массиве
		commonTask1.setStatus("IN_PROGRESS");
		commonTask2.setStatus("DONE");
		subTask2.setStatus("DONE");
		subTask3.setStatus("DONE");
		manager.editCommonTask(commonTaskId1, commonTask1);
		manager.editCommonTask(commonTaskId2, commonTask2);
		manager.editSubTask(subTaskId2, subTask2);
		manager.editSubTask(subTaskId3, subTask3);
		
		//Проверка внесения изменений
		System.out.println(manager.toString());
		
		//Проверка удаления задач по id
		manager.removeCommonTask(commonTaskId1);
		manager.removeEpicTask(epicId1);
		
		//Проверка внесения изменений
		System.out.println(manager.toString());
		
		//Проверка отдельных методов получения списков задач
        for(Long task:manager.getAllCommonTasks()) {
            System.out.println(manager.taskToString(task));
        }
        for(Long task:manager.getAllEpicTasks()) {
            System.out.println(manager.taskToString(task));
        }
        for(Long task:manager.getAllSubTasks()) {
            System.out.println(manager.taskToString(task));
        }
        
        //Проверка очистки выгрузки
        manager.clearTasks();
        System.out.println(manager.toString());
	}
}
