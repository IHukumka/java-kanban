package kanban.tests;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.TreeSet;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import kanban.manager.TaskManager;
import kanban.task.CommonTask;
import kanban.task.EpicTask;
import kanban.task.Status;
import kanban.task.SubTask;
import kanban.task.Task;

// Я отчаянно нуждаюсь в помощи, дабы сохранить свои рассудок и психику.
// Все методы из ТЗ сделаны и работают. Проблема с тестами.
// 1. Я не уверен, что я написал тесты правильно и в нужном количестве.
// Если да, то боже храни тестировщиков.
// 2. Я пишу в spring tool suite для eclipse. JUnit поставил, но он не 
// запускает тесты. Я не понимаю в чем дело. У меня нет сил сейчас искать
// причину ошибки, но если с остальным мне дадут зеленый свет - я смогу
// начать ее искать.
// Помоги мне Иван Русанов, ты единственная надежда.

abstract class TaskManagerTest <T extends TaskManager>{

	static private TaskManager manager;

	@AfterEach
	void clean() {
		manager.clearTasks();
	}
	
	private CommonTask buildCommonTask(Status status) {
		return new CommonTask.Builder()
                .setName("Тест имени таска ")
                .setDescription("Тест описания таска ")
                .setStatus(status)
                .setStartTime(LocalDateTime.now())
                .setDuration(Duration.ofMinutes(15))
                .build();
	}
	
	private EpicTask buildEpicTask() {
		return new EpicTask.Builder()
                .setName("Тест имени эпика ")
                .setDescription("Тест описания эпика ")
                .build();
	}
	
	private SubTask buildSubTask(Status status, Long superId) {
		return new SubTask.Builder()
                .setName("Тест имени таска ")
                .setDescription("Тест описания таска ")
                .setStatus(status)
                .setStartTime(LocalDateTime.now())
                .setDuration(Duration.ofMinutes(15))
                .setSuperTask(superId)
                .build();
	}

	@BeforeEach
	public void setUp() throws Exception {
		
	}

	@Test
	public void testEpicUpdateEmptySubTasks() {
		
		Long epicId = manager.createEpicTask(buildEpicTask());
		
		Assertions.assertEquals(manager.getEpicTask(epicId).getStatus(), Status.NEW);
	}
	
	@Test
	void testEpicUpdateNewSubTasks() {
		
		Long epicId = manager.createEpicTask(buildEpicTask());
		manager.createSubTask(buildSubTask(Status.NEW, epicId));
		
		Assertions.assertEquals(manager.getEpicTask(epicId).getStatus(), Status.NEW);
	}
	
	@Test
	void testEpicUpdateNewAndDoneSubTasks() {
		
		Long epicId = manager.createEpicTask(buildEpicTask());
		manager.createSubTask(buildSubTask(Status.NEW, epicId));
		manager.createSubTask(buildSubTask(Status.DONE, epicId));
		
		Assertions.assertEquals(manager.getEpicTask(epicId).getStatus(), Status.IN_PROGRESS);
	}
	
	@Test
	void testEpicUpdateDoneSubTasks() {
		Long epicId = manager.createEpicTask(buildEpicTask());
		manager.createSubTask(buildSubTask(Status.DONE, epicId));
		
		Assertions.assertEquals(manager.getEpicTask(epicId).getStatus(), Status.DONE);
	}
	
	@Test
	void testEpicUpdateInProgressSubTasks() {
		
		Long epicId = manager.createEpicTask(buildEpicTask());
		manager.createSubTask(buildSubTask(Status.IN_PROGRESS, epicId));
		
		Assertions.assertEquals(manager.getEpicTask(epicId).getStatus(), Status.IN_PROGRESS);
	}
	
	@Test
	void testSubTaskSuperTask() {
		
		Long epicId = manager.createEpicTask(buildEpicTask());
		Long subId1 = manager.createSubTask(buildSubTask(Status.IN_PROGRESS, epicId));
		
		Assertions.assertEquals(manager.getSubTask(subId1).getSuperTask(), epicId);
		Assertions.assertEquals(manager.getEpicTask(epicId).getStatus(), Status.IN_PROGRESS);
	}
	
	@Test
	void testGetAllTasks() {
		
		EpicTask epic = buildEpicTask();
		SubTask sub = buildSubTask(Status.IN_PROGRESS, epic.getId());
		CommonTask common = buildCommonTask(Status.NEW);
		
		ArrayList<Task> newTasks = new ArrayList<>();
		Long epicId = manager.createEpicTask(epic);
		Long subId =manager.createSubTask(sub);
		Long commonId = manager.createCommonTask(common);
		newTasks.add(manager.getCommonTask(commonId));
		newTasks.add(manager.getCommonTask(subId));
		newTasks.add(manager.getCommonTask(epicId));
		
		ArrayList<Task> tasks = manager.getAllTasks();
		Assertions.assertEquals(newTasks, tasks);
	}
	
	@Test
	void testGetAllTasksEmpty() {
		Assertions.assertTrue(manager.getAllTasks().isEmpty());
	}

	@Test
	void testGetAllCommonTasks() {
		
		EpicTask epic = buildEpicTask();
		SubTask sub = buildSubTask(Status.IN_PROGRESS, epic.getId());
		CommonTask common = buildCommonTask(Status.NEW);
		
		ArrayList<Task> newTasks = new ArrayList<>();
		manager.createEpicTask(epic);
		manager.createSubTask(sub);
		Long commonId = manager.createCommonTask(common);
		newTasks.add(manager.getCommonTask(commonId));
		
		ArrayList<Task> tasks = manager.getAllEpicTasks();
		Assertions.assertEquals(newTasks, tasks);
	}
	
	@Test
	void testGetAllCommonTasksEmpty() {
		Assertions.assertTrue(manager.getAllCommonTasks().isEmpty());
	}

	@Test
	void testGetAllEpicTasks() {
		
		EpicTask epic = buildEpicTask();
		SubTask sub = buildSubTask(Status.IN_PROGRESS, epic.getId());
		CommonTask common = buildCommonTask(Status.NEW);
		
		ArrayList<Task> newTasks = new ArrayList<>();
		Long epicId = manager.createEpicTask(epic);
		manager.createSubTask(sub);
		manager.createCommonTask(common);
		newTasks.add(manager.getCommonTask(epicId));
		
		ArrayList<Task> tasks = manager.getAllEpicTasks();
		Assertions.assertEquals(newTasks, tasks);
	}
	
	@Test
	void testGetAllEpicTasksEmpty() {
		Assertions.assertTrue(manager.getAllEpicTasks().isEmpty());
	}

	@Test
	void testGetAllSubTasks() {
		
		EpicTask epic = buildEpicTask();
		SubTask sub = buildSubTask(Status.IN_PROGRESS, epic.getId());
		CommonTask common = buildCommonTask(Status.NEW);
		
		ArrayList<Task> newTasks = new ArrayList<>();
		manager.createEpicTask(epic);
		Long subId =manager.createSubTask(sub);
		manager.createCommonTask(common);
		newTasks.add(manager.getCommonTask(subId));
		
		ArrayList<Task> tasks = manager.getAllSubTasks();
		Assertions.assertEquals(newTasks, tasks);
	}
	
	@Test
	void testGetAllSubTasksEmpty() {
		Assertions.assertTrue(manager.getAllSubTasks().isEmpty());
	}

	@Test
	void testGetPrioritisedTasks() {
		EpicTask epic = buildEpicTask();
		SubTask sub = buildSubTask(Status.IN_PROGRESS, epic.getId());
		CommonTask common = buildCommonTask(Status.NEW);
		
		TreeSet<Task> newTasks = new TreeSet<>();
		Long epicId = manager.createEpicTask(epic);
		Long subId =manager.createSubTask(sub);
		Long commonId = manager.createCommonTask(common);
		newTasks.add(manager.getCommonTask(commonId));
		newTasks.add(manager.getCommonTask(subId));
		newTasks.add(manager.getCommonTask(epicId));
		
		TreeSet<Task> tasks = manager.getPrioritisedTasks();
		Assertions.assertEquals(newTasks, tasks);
	}
	
	@Test
	void testGetPrioritisedTasksEmpty() {
		Assertions.assertTrue(manager.getPrioritisedTasks().isEmpty());
	}

	@Test
	void testClearTasks() {
		
		EpicTask epic = buildEpicTask();
		SubTask sub = buildSubTask(Status.IN_PROGRESS, epic.getId());
		CommonTask common = buildCommonTask(Status.NEW);
		
		ArrayList<Task> newTasks = new ArrayList<>();
		Long epicId = manager.createEpicTask(epic);
		Long subId =manager.createSubTask(sub);
		Long commonId = manager.createCommonTask(common);
		newTasks.add(manager.getCommonTask(commonId));
		newTasks.add(manager.getCommonTask(subId));
		newTasks.add(manager.getCommonTask(epicId));
		
		ArrayList<Task> tasks = manager.getAllTasks();
		Assertions.assertEquals(newTasks, tasks);
		
		manager.clearTasks();
		Assertions.assertTrue(manager.getAllTasks().isEmpty());
	}
	
	@Test
	void testClearTasksEmpty() {
		manager.clearTasks();
		Assertions.assertTrue(manager.getAllTasks().isEmpty());
	}

	@Test
	void testClearCommonTasks() {
		EpicTask epic = buildEpicTask();
		SubTask sub = buildSubTask(Status.IN_PROGRESS, epic.getId());
		CommonTask common = buildCommonTask(Status.NEW);
		
		ArrayList<Task> newTasks = new ArrayList<>();
		Long epicId = manager.createEpicTask(epic);
		Long subId =manager.createSubTask(sub);
		Long commonId = manager.createCommonTask(common);
		newTasks.add(manager.getCommonTask(commonId));
		newTasks.add(manager.getCommonTask(subId));
		newTasks.add(manager.getCommonTask(epicId));
		
		ArrayList<Task> tasks = manager.getAllTasks();
		Assertions.assertEquals(newTasks, tasks);
		
		newTasks.remove(common);
		manager.clearCommonTasks();
		Assertions.assertEquals(manager.getAllCommonTasks(),tasks);
	}
	
	@Test
	void testClearCommonTasksEmpty() {
		manager.clearCommonTasks();
		Assertions.assertTrue(manager.getAllCommonTasks().isEmpty());
	}

	@Test
	void testClearEpicTasks() {
		EpicTask epic = buildEpicTask();
		SubTask sub = buildSubTask(Status.IN_PROGRESS, epic.getId());
		CommonTask common = buildCommonTask(Status.NEW);
		
		ArrayList<Task> newTasks = new ArrayList<>();
		Long epicId = manager.createEpicTask(epic);
		Long subId =manager.createSubTask(sub);
		Long commonId = manager.createCommonTask(common);
		newTasks.add(manager.getCommonTask(commonId));
		newTasks.add(manager.getCommonTask(subId));
		newTasks.add(manager.getCommonTask(epicId));
		
		ArrayList<Task> tasks = manager.getAllTasks();
		Assertions.assertEquals(newTasks, tasks);
		
		newTasks.remove(epic);
		newTasks.remove(sub);
		manager.clearEpicTasks();
		Assertions.assertEquals(manager.getAllEpicTasks(),tasks);
	}
	
	@Test
	void testClearEpicTasksEmpty() {
		manager.clearEpicTasks();
		Assertions.assertTrue(manager.getAllEpicTasks().isEmpty());
	}

	@Test
	void testClearSubTasks() {
		EpicTask epic = buildEpicTask();
		SubTask sub = buildSubTask(Status.IN_PROGRESS, epic.getId());
		CommonTask common = buildCommonTask(Status.NEW);
		
		ArrayList<Task> newTasks = new ArrayList<>();
		Long epicId = manager.createEpicTask(epic);
		Long subId =manager.createSubTask(sub);
		Long commonId = manager.createCommonTask(common);
		newTasks.add(manager.getCommonTask(commonId));
		newTasks.add(manager.getCommonTask(subId));
		newTasks.add(manager.getCommonTask(epicId));
		
		ArrayList<Task> tasks = manager.getAllTasks();
		Assertions.assertEquals(newTasks, tasks);
		
		newTasks.remove(sub);
		manager.clearSubTasks();
		Assertions.assertEquals(manager.getAllSubTasks(),tasks);
	}
	
	@Test
	void testClearSubTasksEmpty() {
		manager.clearSubTasks();
		Assertions.assertTrue(manager.getAllSubTasks().isEmpty());
	}

	@Test
	void testGetCommonTask() {
		CommonTask common = buildCommonTask(Status.NEW);
		Long commonId = manager.createCommonTask(common);
		common.setId(commonId);
		Assertions.assertEquals(common.getId(), manager.getCommonTask(commonId).getId());
	}
	
	@Test
	void testGetCommonTaskEmpty() {
		CommonTask common = buildCommonTask(Status.NEW);
		Long commonId = manager.createCommonTask(common);
		manager.removeCommonTask(commonId);
		Assertions.assertTrue(manager.getCommonTask(commonId).getId()==null);
	}
	
	@Test
	void testGetCommonTaskWrongId() {
		CommonTask common = buildCommonTask(Status.NEW);
		manager.createCommonTask(common);
		Assertions.assertTrue(manager.getCommonTask(0L).getId()==null);
	}

	@Test
	void testGetEpicTask() {
		EpicTask epic = buildEpicTask();
		Long epicId = manager.createEpicTask(epic);
		epic.setId(epicId);
		Assertions.assertEquals(epic.getId(), manager.getCommonTask(epicId).getId());
	}
	
	@Test
	void testGetEpicTaskEmpty() {
		EpicTask epic = buildEpicTask();
		Long epicId = manager.createEpicTask(epic);
		manager.removeEpicTask(epicId);
		Assertions.assertTrue(manager.getCommonTask(epicId).getId()==null);
	}
	
	@Test
	void testGetEpicTaskWrongId() {
		EpicTask epic = buildEpicTask();
		manager.createEpicTask(epic);
		Assertions.assertTrue(manager.getEpicTask(0L).getId()==null);
	}

	@Test
	void testGetSubTask() {
		EpicTask epic = buildEpicTask();
		Long epicId = manager.createEpicTask(epic);
		SubTask sub = buildSubTask(Status.NEW, epicId);
		Long subId = manager.createSubTask(sub);
		sub.setId(subId);
		Assertions.assertEquals(sub.getId(), manager.getCommonTask(subId).getId());
	}
	
	@Test
	void testGetsubTaskEmpty() {
		EpicTask epic = buildEpicTask();
		Long epicId = manager.createEpicTask(epic);
		SubTask sub = buildSubTask(Status.NEW, epicId);
		Long subId = manager.createSubTask(sub);
		manager.removeSubTask(subId);
		Assertions.assertTrue(manager.getCommonTask(subId).getId()==null);
	}
	
	@Test
	void testGetSubTaskWrongId() {
		EpicTask epic = buildEpicTask();
		Long epicId = manager.createEpicTask(epic);
		SubTask sub = buildSubTask(Status.NEW, epicId);
		manager.createSubTask(sub);
		Assertions.assertTrue(manager.getEpicTask(0L).getId()==null);
	}

	@Test
	void testEditCommonTask() {
		CommonTask common = buildCommonTask(Status.NEW);
		common.setDescription("test");
		Long commonId = manager.createCommonTask(common);
		Assertions.assertTrue(common.getDescription().equals(manager.getCommonTask(commonId).getDescription()));
		common.setDescription("test2");
		manager.editCommonTask(commonId, common);
		Assertions.assertTrue(common.getDescription().equals(manager.getCommonTask(commonId).getDescription()));
	}
	
	@Test
	void testEditCommonTaskEmpty() {
		CommonTask common = buildCommonTask(Status.NEW);
		common.setId(1L);
		common.setDescription("test");
		manager.editCommonTask(1L, common);
		Assertions.assertTrue(common.getDescription().equals(manager.getCommonTask(1L).getDescription()));
	}
	
	@Test
	void testEditCommonTaskWrongId() {
		CommonTask common = buildCommonTask(Status.NEW);
		common.setDescription("test");
		Long commonId = manager.createCommonTask(common);
		Assertions.assertTrue(common.getDescription().equals(manager.getCommonTask(commonId).getDescription()));
		common.setDescription("test2");
		manager.editCommonTask(1L, common);
		Assertions.assertFalse(common.getDescription().equals(manager.getCommonTask(commonId).getDescription()));
	}

	@Test
	void testEditEpicTask() {
		EpicTask epic = buildEpicTask();
		epic.setDescription("test");
		Long epicId = manager.createEpicTask(epic);
		Assertions.assertTrue(epic.getDescription().equals(manager.getEpicTask(epicId).getDescription()));
		epic.setDescription("test2");
		manager.editEpicTask(1L, epic);
		Assertions.assertFalse(epic.getDescription().equals(manager.getEpicTask(epicId).getDescription()));
	}
	
	@Test
	void testEditEpicTaskEmpty() {
		EpicTask epic = buildEpicTask();
		epic.setId(1L);
		epic.setDescription("test");
		manager.editEpicTask(1L, epic);
		Assertions.assertTrue(epic.getDescription().equals(manager.getEpicTask(1L).getDescription()));
	}
	
	@Test
	void testEditEpicTaskWrongId() {
		EpicTask epic = buildEpicTask();
		epic.setDescription("test");
		Long epicId = manager.createEpicTask(epic);
		Assertions.assertTrue(epic.getDescription().equals(manager.getEpicTask(epicId).getDescription()));
		epic.setDescription("test2");
		manager.editEpicTask(epicId, epic);
		Assertions.assertTrue(epic.getDescription().equals(manager.getEpicTask(epicId).getDescription()));
	}

	@Test
	void testEditSubTask() {
		SubTask sub = buildSubTask(Status.NEW, 0L);
		sub.setDescription("test");
		Long subId = manager.createSubTask(sub);
		Assertions.assertTrue(sub.getDescription().equals(manager.getSubTask(subId).getDescription()));
		sub.setDescription("test2");
		manager.editSubTask(subId, sub);
		Assertions.assertTrue(sub.getDescription().equals(manager.getSubTask(subId).getDescription()));
	}
	
	@Test
	void testEditSubTaskEmpty() {
		SubTask sub = buildSubTask(Status.NEW, 0L);
		sub.setId(1L);
		sub.setDescription("test");
		manager.editSubTask(1L, sub);
		Assertions.assertTrue(sub.getDescription().equals(manager.getSubTask(1L).getDescription()));
	}
	
	@Test
	void testEditSubTaskWrongId() {
		SubTask sub = buildSubTask(Status.NEW, 0L);
		sub.setDescription("test");
		Long subId = manager.createSubTask(sub);
		Assertions.assertTrue(sub.getDescription().equals(manager.getSubTask(subId).getDescription()));
		sub.setDescription("test2");
		manager.editSubTask(1L, sub);
		Assertions.assertFalse(sub.getDescription().equals(manager.getSubTask(subId).getDescription()));
	}

	@Test
	void testRemoveCommonTask() {
		EpicTask epic = buildEpicTask();
		SubTask sub = buildSubTask(Status.IN_PROGRESS, epic.getId());
		CommonTask common = buildCommonTask(Status.NEW);
		
		ArrayList<Task> newTasks = new ArrayList<>();
		Long epicId = manager.createEpicTask(epic);
		Long subId =manager.createSubTask(sub);
		Long commonId = manager.createCommonTask(common);
		newTasks.add(manager.getCommonTask(commonId));
		newTasks.add(manager.getCommonTask(subId));
		newTasks.add(manager.getCommonTask(epicId));
		
		ArrayList<Task> tasks = manager.getAllTasks();
		Assertions.assertEquals(newTasks, tasks);
		
		newTasks.remove(common);
		manager.removeCommonTask(commonId);
		Assertions.assertEquals(manager.getAllCommonTasks(),tasks);
	}
	
	@Test
	void testRemoveCommonTaskEmpty() {
		manager.removeCommonTask(1L);
		Assertions.assertTrue(manager.getAllCommonTasks().isEmpty());
	}
	
	@Test
	void testRemoveCommonTaskWrongId() {
		EpicTask epic = buildEpicTask();
		SubTask sub = buildSubTask(Status.IN_PROGRESS, epic.getId());
		CommonTask common = buildCommonTask(Status.NEW);
		
		ArrayList<Task> newTasks = new ArrayList<>();
		Long epicId = manager.createEpicTask(epic);
		Long subId =manager.createSubTask(sub);
		Long commonId = manager.createCommonTask(common);
		newTasks.add(manager.getCommonTask(commonId));
		newTasks.add(manager.getCommonTask(subId));
		newTasks.add(manager.getCommonTask(epicId));
		
		ArrayList<Task> tasks = manager.getAllTasks();
		Assertions.assertEquals(newTasks, tasks);
		
		manager.removeCommonTask(1L);
		Assertions.assertEquals(manager.getAllCommonTasks(),tasks);
	}

	@Test
	void testRemoveEpicTask() {
		EpicTask epic = buildEpicTask();
		SubTask sub = buildSubTask(Status.IN_PROGRESS, epic.getId());
		CommonTask common = buildCommonTask(Status.NEW);
		
		ArrayList<Task> newTasks = new ArrayList<>();
		Long epicId = manager.createEpicTask(epic);
		Long subId =manager.createSubTask(sub);
		Long commonId = manager.createCommonTask(common);
		newTasks.add(manager.getCommonTask(commonId));
		newTasks.add(manager.getCommonTask(subId));
		newTasks.add(manager.getCommonTask(epicId));
		
		ArrayList<Task> tasks = manager.getAllTasks();
		Assertions.assertEquals(newTasks, tasks);
		
		newTasks.remove(epic);
		newTasks.remove(sub);
		manager.removeEpicTask(epicId);
		Assertions.assertEquals(manager.getAllEpicTasks(),tasks);
	}
	
	@Test
	void testRemoveEpicTaskEmpty() {
		manager.removeEpicTask(1L);
		Assertions.assertTrue(manager.getAllEpicTasks().isEmpty());
	}
	
	@Test
	void testRemoveEpicTaskWrongId() {
		EpicTask epic = buildEpicTask();
		SubTask sub = buildSubTask(Status.IN_PROGRESS, epic.getId());
		CommonTask common = buildCommonTask(Status.NEW);
		
		ArrayList<Task> newTasks = new ArrayList<>();
		Long epicId = manager.createEpicTask(epic);
		Long subId =manager.createSubTask(sub);
		Long commonId = manager.createCommonTask(common);
		newTasks.add(manager.getCommonTask(commonId));
		newTasks.add(manager.getCommonTask(subId));
		newTasks.add(manager.getCommonTask(epicId));
		
		ArrayList<Task> tasks = manager.getAllTasks();
		Assertions.assertEquals(newTasks, tasks);
		
		manager.removeEpicTask(1L);
		Assertions.assertEquals(manager.getAllEpicTasks(),tasks);
	}

	@Test
	void testRemoveSubTask() {
		EpicTask epic = buildEpicTask();
		SubTask sub = buildSubTask(Status.IN_PROGRESS, epic.getId());
		CommonTask common = buildCommonTask(Status.NEW);
		
		ArrayList<Task> newTasks = new ArrayList<>();
		Long epicId = manager.createEpicTask(epic);
		Long subId =manager.createSubTask(sub);
		Long commonId = manager.createCommonTask(common);
		newTasks.add(manager.getCommonTask(commonId));
		newTasks.add(manager.getCommonTask(subId));
		newTasks.add(manager.getCommonTask(epicId));
		
		ArrayList<Task> tasks = manager.getAllTasks();
		Assertions.assertEquals(newTasks, tasks);
		
		newTasks.remove(sub);
		manager.removeSubTask(subId);
		Assertions.assertEquals(manager.getAllSubTasks(),tasks);
	}
	
	@Test
	void testRemoveSubTaskEmpty() {
		manager.removeSubTask(1L);
		Assertions.assertTrue(manager.getAllSubTasks().isEmpty());
	}
	
	@Test
	void testRemoveSubTaskWrongId() {
		EpicTask epic = buildEpicTask();
		SubTask sub = buildSubTask(Status.IN_PROGRESS, epic.getId());
		CommonTask common = buildCommonTask(Status.NEW);
		
		ArrayList<Task> newTasks = new ArrayList<>();
		Long epicId = manager.createEpicTask(epic);
		Long subId =manager.createSubTask(sub);
		Long commonId = manager.createCommonTask(common);
		newTasks.add(manager.getCommonTask(commonId));
		newTasks.add(manager.getCommonTask(subId));
		newTasks.add(manager.getCommonTask(epicId));
		
		ArrayList<Task> tasks = manager.getAllTasks();
		Assertions.assertEquals(newTasks, tasks);
		
		manager.removeSubTask(1L);
		Assertions.assertEquals(manager.getAllSubTasks(),tasks);
	}

	@Test
	void testGetEpicSubTasks() {
		EpicTask epic = buildEpicTask();
		SubTask sub = buildSubTask(Status.IN_PROGRESS, epic.getId());
		sub.setDescription("test");
		Long epicId = manager.createEpicTask(epic);
		manager.createSubTask(sub);
		Assertions.assertEquals(manager.getSubTask(manager.getEpicSubTasks(epicId).get(0)).getDescription(),sub.getDescription());
	}
	
	@Test
	void testGetEpicSubTasksEmpty() {
		Assertions.assertTrue(manager.getEpicSubTasks(1L).isEmpty());
	}
	
	@Test
	void testGetEpicSubTasksWrongId() {
		EpicTask epic = buildEpicTask();
		SubTask sub = buildSubTask(Status.IN_PROGRESS, epic.getId());
		sub.setDescription("test");
		manager.createEpicTask(epic);
		manager.createSubTask(sub);
		Assertions.assertTrue(manager.getEpicSubTasks(1L).isEmpty());
	}

	@Test
	void testGetTaskEndTime() {
		CommonTask task = buildCommonTask(Status.NEW);
		LocalDateTime time = LocalDateTime.now();
		task.setStartTime(time);
		task.setDuration(Duration.ofMinutes(15));
		Long taskId = manager.createCommonTask(task);
		Assertions.assertEquals(manager.getTaskEndTime(taskId),time.plusMinutes(15));
	}
	
	@Test
	void testGetTaskEndTimeEmpty() {
		Assertions.assertTrue(manager.getTaskEndTime(1L)==null);
	}
	
	@Test
	void testGetTaskEndTimeWrongId() {
		CommonTask task = buildCommonTask(Status.NEW);
		LocalDateTime time = LocalDateTime.now();
		task.setStartTime(time);
		task.setDuration(Duration.ofMinutes(15));
		manager.createCommonTask(task);
		Assertions.assertEquals(manager.getTaskEndTime(1L),null);
	}

	@Test
	void testCreateCommonTask() {
		CommonTask task = buildCommonTask(Status.NEW);
		Long taskId = manager.createCommonTask(task);
		Assertions.assertEquals(task.getClass(), manager.getCommonTask(taskId).getClass());
	}

	@Test
	void testCreateEpicTask() {
		EpicTask task = buildEpicTask();
		Long taskId = manager.createEpicTask(task);
		Assertions.assertEquals(task.getClass(), manager.getCommonTask(taskId).getClass());
	}

	@Test
	void testCreateSubTask() {
		SubTask task = buildSubTask(Status.NEW,1L);
		Long taskId = manager.createSubTask(task);
		Assertions.assertEquals(task.getClass(), manager.getCommonTask(taskId).getClass());
	}

	@Test
	void testToString() {
		EpicTask epic = buildEpicTask();
		SubTask sub = buildSubTask(Status.IN_PROGRESS, epic.getId());
		CommonTask common = buildCommonTask(Status.NEW);
		
		ArrayList<Task> newTasks = new ArrayList<>();
		Long epicId = manager.createEpicTask(epic);
		Long subId = manager.createSubTask(sub);
		Long commonId = manager.createCommonTask(common);
		newTasks.add(manager.getCommonTask(commonId));
		newTasks.add(manager.getCommonTask(subId));
		newTasks.add(manager.getCommonTask(epicId));
		
		String stringTest = "";
		for(Task task:newTasks) {
			stringTest += task.toString() + "\n";
		}
		
		Assertions.assertEquals(stringTest,manager.toString());
	}
	
	@Test
	void testToStringEmpty() {
		Assertions.assertEquals(manager.toString(), "");
	}
/*
	@Test
	void testGetHistoryManager() {
		EpicTask epic = buildEpicTask();
		SubTask sub = buildSubTask(Status.IN_PROGRESS, epic.getId());
		CommonTask common = buildCommonTask(Status.NEW);
		
		ArrayList<Task> newTasks = new ArrayList<>();
		Long epicId = manager.createEpicTask(epic);
		Long subId = manager.createSubTask(sub);
		Long commonId = manager.createCommonTask(common);
	}*/
}
