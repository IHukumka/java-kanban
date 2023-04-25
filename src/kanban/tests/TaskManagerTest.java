package kanban.tests;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeSet;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import kanban.manager.TaskManager;
import kanban.task.CommonTask;
import kanban.task.EpicTask;
import kanban.task.Status;
import kanban.task.SubTask;
import kanban.task.Task;


public abstract class TaskManagerTest<T extends TaskManager>{

	protected static TaskManager manager;
	
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

	@Test
	public void testEpicUpdateEmptySubTasks() {
		
		Long epicId = manager.createEpicTask(buildEpicTask());
		
		Assertions.assertEquals(Status.NEW,manager.getEpicTask(epicId).getStatus());
	}
	
	@Test
	void testEpicUpdateNewSubTasks() {
		
		Long epicId = manager.createEpicTask(buildEpicTask());
		manager.createSubTask(buildSubTask(Status.NEW, epicId));
		
		Assertions.assertEquals(Status.NEW, manager.getEpicTask(epicId).getStatus());
	}
	
	@Test
	void testEpicUpdateNewAndDoneSubTasks() {
		
		EpicTask epic = new EpicTask.Builder().build();
    	Long epicId = manager.createEpicTask(epic);
    	
    	SubTask sub1 = new SubTask.Builder().setStatus(Status.NEW).setSuperTask(epicId).build();
    	SubTask sub2 = new SubTask.Builder().setStatus(Status.DONE).setSuperTask(epicId).build();
		manager.createSubTask(sub1);
		manager.createSubTask(sub2);
		
		Assertions.assertEquals(Status.IN_PROGRESS, manager.getEpicTask(epicId).getStatus());
	}
	
	@Test
	void testEpicUpdateDoneSubTasks() {
		Long epicId = manager.createEpicTask(buildEpicTask());
		manager.createSubTask(buildSubTask(Status.DONE, epicId));
		
		Assertions.assertEquals(Status.DONE, manager.getEpicTask(epicId).getStatus());
	}
	
	@Test
	void testEpicUpdateInProgressSubTasks() {
		
		Long epicId = manager.createEpicTask(buildEpicTask());
		manager.createSubTask(buildSubTask(Status.IN_PROGRESS, epicId));
		
		Assertions.assertEquals(Status.IN_PROGRESS, manager.getEpicTask(epicId).getStatus());
	}
	
	@Test
	void testEpicStartTime() {
		Long epicId = manager.createEpicTask(buildEpicTask());
		LocalDateTime testTime = LocalDateTime.now();
		SubTask sub1 = buildSubTask(Status.NEW, epicId);
		sub1.setStartTime(testTime);
		manager.createSubTask(sub1);
		SubTask sub2 = buildSubTask(Status.NEW, epicId);
		sub2.setStartTime(testTime.plusMinutes(30));
		manager.createSubTask(sub2);
		Assertions.assertEquals(testTime, manager.getEpicTask(epicId).getStartTime());
	}
	
	@Test
	void testEpicDuration() {
		Long epicId = manager.createEpicTask(buildEpicTask());
		LocalDateTime testTime = LocalDateTime.now();
		SubTask sub1 = buildSubTask(Status.NEW, epicId);
		sub1.setStartTime(testTime);
		manager.createSubTask(sub1);
		SubTask sub2 = buildSubTask(Status.NEW, epicId);
		sub2.setStartTime(testTime.plusMinutes(30));
		manager.createSubTask(sub2);
		Assertions.assertEquals(30L, manager.getEpicTask(epicId).getDuration().toMinutes());
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
		CommonTask common = buildCommonTask(Status.NEW);
		Long epicId = manager.createEpicTask(epic);
		SubTask sub = buildSubTask(Status.IN_PROGRESS, epicId);
		manager.createSubTask(sub);
		manager.createCommonTask(common);
		Assertions.assertTrue(manager.getAllTasks() instanceof ArrayList<Task>);
	}
	
	@Test
	void testGetAllTasksEmpty() {
		Assertions.assertTrue(manager.getAllTasks().isEmpty());
	}

	@Test
	void testGetAllCommonTasks() {
		
		CommonTask common = buildCommonTask(Status.NEW);
		
		ArrayList<Task> newTasks = new ArrayList<>();
		Long commonId = manager.createCommonTask(common);
		newTasks.add(manager.getCommonTask(commonId));
		
		ArrayList<Task> tasks = manager.getAllCommonTasks();
		Assertions.assertEquals(newTasks, tasks);
	}
	
	@Test
	void testGetAllCommonTasksEmpty() {
		Assertions.assertTrue(manager.getAllCommonTasks().isEmpty());
	}

	@Test
	void testGetAllEpicTasks() {
		
		EpicTask epic = buildEpicTask();
		CommonTask common = buildCommonTask(Status.NEW);
		
		ArrayList<Task> newTasks = new ArrayList<>();
		Long epicId = manager.createEpicTask(epic);
		SubTask sub = buildSubTask(Status.IN_PROGRESS, epicId);
		manager.createSubTask(sub);
		manager.createCommonTask(common);
		newTasks.add(manager.getEpicTask(epicId));
		
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
		Long epicId = manager.createEpicTask(epic);
		SubTask sub = buildSubTask(Status.IN_PROGRESS, epicId);
		ArrayList<Task> testList = new ArrayList<>();
		testList.add(sub);
		manager.createSubTask(sub);
		Assertions.assertEquals(testList, manager.getAllSubTasks());
	}
	
	@Test
	void testGetAllSubTasksEmpty() {
		Assertions.assertTrue(manager.getAllSubTasks().isEmpty());
	}

	@Test
	void testGetPrioritisedTasks() {
		TreeSet<Task> testTreeSet = new TreeSet<>(Comparator.comparing(Task::getStartTime));
		LocalDateTime testTime = LocalDateTime.now();
		
		CommonTask common = buildCommonTask(Status.NEW);
		common.setStartTime(testTime);
		Long commonId = manager.createCommonTask(common);
		
		EpicTask epic = buildEpicTask();
		epic.setStartTime(testTime.plusMinutes(30));
		Long epicId = manager.createEpicTask(epic);
		
		SubTask sub = buildSubTask(Status.IN_PROGRESS, epicId);
		sub.setStartTime(testTime.plusMinutes(60));
		Long subId = manager.createSubTask(sub);
		
		testTreeSet.add(manager.getCommonTask(commonId));
		testTreeSet.add(manager.getSubTask(subId));
		testTreeSet.add(manager.getEpicTask(epicId));
		
		TreeSet<Task> tasks = manager.getPrioritisedTasks();
		Assertions.assertEquals(testTreeSet, tasks);
	}
	
	@Test
	void testGetPrioritisedTasksEmpty() {
		Assertions.assertTrue(manager.getPrioritisedTasks().isEmpty());
	}

	@Test
	void testClearTasks() {

		CommonTask common = buildCommonTask(Status.NEW);
		manager.createCommonTask(common);
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
		CommonTask common = buildCommonTask(Status.NEW);
		common.setName("test");
		Long commonId = manager.createCommonTask(common);
		
		Assertions.assertEquals(manager.getCommonTask(commonId).getName(), "test");
		
		manager.clearCommonTasks();
		Assertions.assertTrue(manager.getAllCommonTasks().isEmpty());
	}
	
	@Test
	void testClearCommonTasksEmpty() {
		manager.clearCommonTasks();
		Assertions.assertTrue(manager.getAllCommonTasks().isEmpty());
	}

	@Test
	void testClearEpicTasks() {
		EpicTask epic = buildEpicTask();
		Long epicId = manager.createEpicTask(epic);
		SubTask sub = buildSubTask(Status.IN_PROGRESS, epicId);
		manager.createSubTask(sub);
		manager.clearEpicTasks();
		Assertions.assertTrue(manager.getAllTasks().isEmpty());
	}
	
	@Test
	void testClearEpicTasksEmpty() {
		manager.clearEpicTasks();
		Assertions.assertTrue(manager.getAllEpicTasks().isEmpty());
	}

	@Test
	void testClearSubTasks() {
		EpicTask epic = buildEpicTask();
		Long epicId = manager.createEpicTask(epic);
		SubTask sub = buildSubTask(Status.IN_PROGRESS, epicId);
		manager.createSubTask(sub);
		manager.clearEpicTasks();
		Assertions.assertTrue(manager.getAllTasks().isEmpty());
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
		Assertions.assertEquals(epic.getId(), manager.getEpicTask(epicId).getId());
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
		Assertions.assertEquals(sub.getId(), manager.getSubTask(subId).getId());
	}
	
	@Test
	void testGetsubTaskEmpty() {
		EpicTask epic = buildEpicTask();
		Long epicId = manager.createEpicTask(epic);
		SubTask sub = buildSubTask(Status.NEW, epicId);
		Long subId = manager.createSubTask(sub);
		manager.removeSubTask(subId);
		Assertions.assertTrue(manager.getSubTask(subId).getId()==null);
	}
	
	@Test
	void testGetSubTaskWrongId() {
		EpicTask epic = buildEpicTask();
		Long epicId = manager.createEpicTask(epic);
		SubTask sub = buildSubTask(Status.NEW, epicId);
		manager.createSubTask(sub);
		Assertions.assertTrue(manager.getSubTask(0L).getId()==null);
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
		Assertions.assertFalse(common.getDescription().equals(manager.getCommonTask(1L).getDescription()));
	}
	
	@Test
	void testEditCommonTaskWrongId() {
		CommonTask common = buildCommonTask(Status.NEW);
		common.setDescription("test");
		Long commonId = manager.createCommonTask(common);
		Assertions.assertTrue(common.getDescription().equals(manager.getCommonTask(commonId).getDescription()));
		common.setDescription("test2");
		manager.editCommonTask(1L, common);
		Assertions.assertTrue(common.getDescription().equals(manager.getCommonTask(commonId).getDescription()));
	}

	@Test
	void testEditEpicTask() {
		EpicTask epic = buildEpicTask();
		epic.setDescription("test");
		Long epicId = manager.createEpicTask(epic);
		Assertions.assertTrue(epic.getDescription().equals(manager.getEpicTask(epicId).getDescription()));
		epic.setDescription("test2");
		manager.editEpicTask(1L, epic);
		Assertions.assertTrue(epic.getDescription().equals(manager.getEpicTask(epicId).getDescription()));
	}
	
	@Test
	void testEditEpicTaskEmpty() {
		EpicTask epic = buildEpicTask();
		epic.setId(1L);
		epic.setDescription("test");
		manager.editEpicTask(1L, epic);
		Assertions.assertFalse(epic.getDescription().equals(manager.getEpicTask(1L).getDescription()));
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
		EpicTask epic = buildEpicTask();
		epic.setDescription("test");
		Long epicId = manager.createEpicTask(epic);
		SubTask sub = buildSubTask(Status.IN_PROGRESS, epicId);
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
		Assertions.assertFalse(sub.getDescription().equals(manager.getSubTask(1L).getDescription()));
	}
	
	@Test
	void testEditSubTaskWrongId() {
		EpicTask epic = buildEpicTask();
		Long epicId = manager.createEpicTask(epic);
		SubTask sub = buildSubTask(Status.IN_PROGRESS, epicId);
		sub.setDescription("test");
		Long subId = manager.createSubTask(sub);
		Assertions.assertTrue(sub.getDescription().equals(manager.getSubTask(subId).getDescription()));
		sub.setDescription("test2");
		manager.editSubTask(1L, sub);
		Assertions.assertTrue(sub.getDescription().equals(manager.getSubTask(subId).getDescription()));
	}

	@Test
	void testRemoveCommonTask() {
		CommonTask common = buildCommonTask(Status.NEW);
		Long commonId = manager.createCommonTask(common);
		manager.removeCommonTask(commonId);
		Assertions.assertTrue(manager.getAllCommonTasks().isEmpty());
	}
	
	@Test
	void testRemoveCommonTaskEmpty() {
		manager.removeCommonTask(1L);
		Assertions.assertTrue(manager.getAllCommonTasks().isEmpty());
	}
	
	@Test
	void testRemoveCommonTaskWrongId() {
		EpicTask epic = buildEpicTask();
		CommonTask common = buildCommonTask(Status.NEW);
		common.setName("test");
		Long epicId = manager.createEpicTask(epic);
		SubTask sub = buildSubTask(Status.IN_PROGRESS, epicId);
		manager.createSubTask(sub);
		Long commonId = manager.createCommonTask(common);
		
		
		manager.removeCommonTask(1L);
		Assertions.assertEquals(manager.getCommonTask(commonId).getName(),"test");
	}

	@Test
	void testRemoveEpicTask() {
		EpicTask epic = buildEpicTask();
		Long epicId = manager.createEpicTask(epic);
		SubTask sub = buildSubTask(Status.IN_PROGRESS, epicId);
		manager.createSubTask(sub);
		manager.removeEpicTask(epicId);
		Assertions.assertTrue(manager.getAllTasks().isEmpty());
	}
	
	@Test
	void testRemoveEpicTaskEmpty() {
		manager.removeEpicTask(1L);
		Assertions.assertTrue(manager.getAllEpicTasks().isEmpty());
	}
	
	@Test
	void testRemoveEpicTaskWrongId() {
		EpicTask epic = buildEpicTask();
		epic.setName("test");
		Long epicId = manager.createEpicTask(epic);
		
		Assertions.assertEquals(epic.getName(), manager.getEpicTask(epicId).getName());
		
		manager.removeEpicTask(1L);
		Assertions.assertEquals(epic.getName(), manager.getEpicTask(epicId).getName());
	}

	@Test
	void testRemoveSubTask() {
		EpicTask epic = buildEpicTask();
		Long epicId = manager.createEpicTask(epic);
		SubTask sub = buildSubTask(Status.IN_PROGRESS, epicId);
		Long subId = manager.createSubTask(sub);
		manager.removeSubTask(subId);
		Assertions.assertTrue(manager.getAllSubTasks().isEmpty());
	}
	
	@Test
	void testRemoveSubTaskEmpty() {
		manager.removeSubTask(1L);
		Assertions.assertTrue(manager.getAllSubTasks().isEmpty());
	}
	
	@Test
	void testRemoveSubTaskWrongId() {
		EpicTask epic = buildEpicTask();
		Long epicId = manager.createEpicTask(epic);
		SubTask sub = buildSubTask(Status.IN_PROGRESS, epicId);
		Long subId = manager.createSubTask(sub);
		
		Assertions.assertEquals(sub.getSuperTask(), manager.getSubTask(subId).getSuperTask());
		
		manager.removeSubTask(1L);
		Assertions.assertEquals(sub.getSuperTask(), manager.getSubTask(subId).getSuperTask());
	}

	@Test
	void testGetEpicSubTasks() {
		EpicTask epic = buildEpicTask();
		Long epicId = manager.createEpicTask(epic);
		SubTask sub = buildSubTask(Status.IN_PROGRESS, epicId);
		manager.createSubTask(sub);
		sub.setDescription("test");
		Assertions.assertEquals(manager.getSubTask(manager.getEpicSubTasks(epicId).get(0)).getDescription(),sub.getDescription());
	}
	
	@Test
	void testGetEpicSubTasksEmpty() {
		Assertions.assertTrue(manager.getEpicSubTasks(1L).isEmpty());
	}
	
	@Test
	void testGetEpicSubTasksWrongId() {
		EpicTask epic = buildEpicTask();
		Long epicId = manager.createEpicTask(epic);
		SubTask sub = buildSubTask(Status.IN_PROGRESS, epicId);
		sub.setDescription("test");
		manager.createSubTask(sub);
		Assertions.assertTrue(manager.getEpicSubTasks(1L).isEmpty());
	}

	@Test
	void testGetTaskEndTime() {
		CommonTask task = buildCommonTask(Status.NEW);
		LocalDateTime time = task.getStartTime();
		task.setDuration(Duration.ofMinutes(15));
		Long taskId = manager.createCommonTask(task);
		Assertions.assertEquals(manager.getTaskEndTime(taskId),time.plusMinutes(15));
	}
	
	@Test
	void testGetTaskEndTimeEmpty() {
		Assertions.assertThrows(NullPointerException.class, () -> manager.getTaskEndTime(1L));
	}
	
	@Test
	void testGetTaskEndTimeWrongId() {
		CommonTask task = buildCommonTask(Status.NEW);
		LocalDateTime time = LocalDateTime.now();
		task.setStartTime(time);
		task.setDuration(Duration.ofMinutes(15));
		manager.createCommonTask(task);
		Assertions.assertThrows(NullPointerException.class, () -> manager.getTaskEndTime(1L));
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
		Assertions.assertEquals(task.getClass(), manager.getEpicTask(taskId).getClass());
	}

	@Test
	void testCreateSubTask() {
		Long epicId = manager.createEpicTask(buildEpicTask());
		SubTask task = buildSubTask(Status.NEW,epicId);
		Long taskId = manager.createSubTask(task);
		Assertions.assertEquals(task.getClass(), manager.getSubTask(taskId).getClass());
	}

	@Test
	void testToString() {
		
		ArrayList<Task> newTasks = new ArrayList<>();
		
		CommonTask common = buildCommonTask(Status.NEW);		
		Long commonId = manager.createCommonTask(common);
		
		newTasks.add(manager.getCommonTask(commonId));
		
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

	@Test
	void testGetHistoryManager() {
		CommonTask common = buildCommonTask(Status.NEW);
		Long commonId = manager.createCommonTask(common);
		ArrayList<Task> testList = new ArrayList<>();
		testList.add(manager.getCommonTask(commonId));
		Assertions.assertEquals(testList, manager.getHistoryManager().getHistory());
	}
}
