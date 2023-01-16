package kanban;

import java.util.ArrayList;

public class Main {

	public static void main(String[] args) {

		System.out.println("Поехали!");
		
		//Часть реализованного на будущее функционала: загрузка данных из файла пользователя. Здесь сохранено имя
		String userName = "Nikita"; 
		
		//Запуск менеджера задач с выгрузкой данных из файла по имени пользователя
		TaskManager manager = new TaskManager(userName);
		
		//Проверка выгрузки
		System.out.println(manager.getTasks().toString());
		
		//Проверка очистки выгрузки
		manager.clearTasks();
		System.out.println(manager.getTasks().toString());
		
		//Создание объектов с внесением в массив задач: обычная задача 1
		CommonTask commonTask1 = new CommonTask.Builder().
				setName("Тест имени таска 1").
				setDescription("Тест описания таска 1").
				setStatus("NEW").
				build();
		Long commonTaskId1 = manager.createTask(commonTask1);
		
		//Создание объектов с внесением в массив задач: обычная задача 2
		CommonTask commonTask2 = new CommonTask.Builder().
				setName("Тест имени таска 2").
				setDescription("Тест описания таска 2").
				setStatus("IN_PROGRESS").
				build();
		Long commonTaskId2 = manager.createTask(commonTask1);
		
		//Инициализация ряда данных с подзадачами для эпика 1
		ArrayList<Long> subTasks1 = new ArrayList<>();
		
		//Создание объектов с внесением в массив задач: эпик 1. Обновление статуса эпика
		EpicTask epicTask1 = new EpicTask.Builder().
				setName("Тест имени эпика 1").
				setDescription("Тест описания эпика 1").
				setSubTasks(subTasks1).
				build();
		Long epicId1 = manager.createTask(epicTask1);
		manager.updateEpicStatus(epicId1);
		
		//Инициализация ряда данных с подзадачами для эпика 2
		ArrayList<Long> subTasks2 = new ArrayList<>();
		
		//Создание объектов с внесением в массив задач: эпик 2. Обновление статуса эпика
		EpicTask epicTask2 = new EpicTask.Builder().
				setName("Тест имени эпика").
				setDescription("Тест описания эпика").
				setSubTasks(subTasks2).
				build();
		Long epicId2 = manager.createTask(epicTask2);
		manager.updateEpicStatus(epicId2);
		
		//Создание объектов с внесением в массив задач: сабтаск 1
		SubTask subTask1 = new SubTask.Builder().
				setName("Тест имени сабтаска 1").
				setDescription("Тест описания сабтаска 1").
				setStatus("DONE").
				setSuperTask(epicId1).
				build();
		Long subTaskId1 = manager.createTask(subTask1);
		subTasks1.add(subTaskId1);
		
		//Создание объектов с внесением в массив задач: сабтаск 2
		SubTask subTask2 = new SubTask.Builder().
				setName("Тест имени сабтаска 2").
				setDescription("Тест описания сабтаска 2").
				setStatus("NEW").
				setSuperTask(epicId1).
				build();
		Long subTaskId2 = manager.createTask(subTask2);
		subTasks1.add(subTaskId2);
		
		//Создание объектов с внесением в массив задач: сабтаск 3
		SubTask subTask3 = new SubTask.Builder().
				setName("Тест имени сабтаска 3").
				setDescription("Тест описания сабтаска 3").
				setStatus("IN_PROGRESS").
				setSuperTask(epicId2).
				build();
		Long subTaskId3 = manager.createTask(subTask3);
		subTasks2.add(subTaskId3);

		//Добавление id подзадач к эпикам и обновление статусов
		epicTask1.setSubTasks(subTasks1);
		manager.updateEpicStatus(epicId1);
		epicTask2.setSubTasks(subTasks2);
		manager.updateEpicStatus(epicId2);
		
		//Проверка внесенных изменений
		System.out.println(manager.getTasks().toString());
		
		//Изменение созданных задач и применение изменений к объектам в массиве
		commonTask1.setStatus("IN_PROGRESS");
		commonTask2.setStatus("DONE");
		subTask2.setStatus("DONE");
		subTask3.setStatus("DONE");
		manager.editTask(commonTaskId1, commonTask1);
		manager.editTask(commonTaskId2, commonTask2);
		manager.editTask(subTaskId2, subTask2);
		manager.editTask(subTaskId3, subTask3);
		manager.updateEpicStatus(epicId1);
		manager.updateEpicStatus(epicId2);
		
		//Проверка внесения изменений
		System.out.println(manager.getTasks().toString());
		
		//Проверка удаления задач по id
		manager.removeTask(commonTaskId1);
		manager.removeTask(epicId1);
		
		//Проверка внесения изменений
		System.out.println(manager.getTasks().toString());
		
		//Сохранение данных в файл
		FileHandler.saveUserTasks(userName, manager.getTasks());
	}
}
