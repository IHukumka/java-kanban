package kanban.tests;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import kanban.manager.TaskManager;
import kanban.task.CommonTask;
import kanban.task.EpicTask;
import kanban.task.Status;
import kanban.task.SubTask;
import kanban.task.Task;


public abstract class TaskManagerTest<T extends TaskManager>{

	protected static TaskManager manager;
	
	@Test
	public void testEpicUpdateEmptySubTasks() {
		
		Long epicId = manager.createEpicTask(buildEpicTask());
		
		assertEquals(Status.NEW,manager.getEpicTask(epicId).getStatus());
	}
	
	@Test
	void testEpicUpdateNewSubTasks() {
		
		Long epicId = manager.createEpicTask(buildEpicTask());
		manager.createSubTask(buildSubTask(Status.NEW, epicId));
		
		assertEquals(Status.NEW, manager.getEpicTask(epicId).getStatus());
	}
	
	@Test
	void testEpicUpdateNewAndDoneSubTasks() {
		
		EpicTask epic = new EpicTask.Builder().build();
    	Long epicId = manager.createEpicTask(epic);
    	
    	SubTask sub1 = new SubTask.Builder().setStatus(Status.NEW).setSuperTask(epicId).build();
    	SubTask sub2 = new SubTask.Builder().setStatus(Status.DONE).setSuperTask(epicId).build();
		manager.createSubTask(sub1);
		manager.createSubTask(sub2);
		
		assertEquals(Status.IN_PROGRESS, manager.getEpicTask(epicId).getStatus());
	}
	
	@Test
	void testEpicUpdateDoneSubTasks() {
		Long epicId = manager.createEpicTask(buildEpicTask());
		manager.createSubTask(buildSubTask(Status.DONE, epicId));
		
		assertEquals(Status.DONE, manager.getEpicTask(epicId).getStatus());
	}
	
	@Test
	void testEpicUpdateInProgressSubTasks() {
		
		Long epicId = manager.createEpicTask(buildEpicTask());
		manager.createSubTask(buildSubTask(Status.IN_PROGRESS, epicId));
		
		assertEquals(Status.IN_PROGRESS, manager.getEpicTask(epicId).getStatus());
	}
	
	@Test
	void testEpicStartTime() {
		LocalDateTime testTime = LocalDateTime.now();
		assertEquals(testTime, getEpicTaskForTimeTest(testTime).getStartTime());
	}
	
	@Test
	void testEpicDuration() {
		LocalDateTime testTime = LocalDateTime.now();
		assertEquals(testTime, getEpicTaskForTimeTest(testTime).getDuration().toMinutes());
	}
	
	@Test
	void testSubTaskSuperTask() {
		
		Long epicId = manager.createEpicTask(buildEpicTask());
		Long subId1 = manager.createSubTask(buildSubTask(Status.IN_PROGRESS, epicId));
		
		assertEquals(manager.getSubTask(subId1).getSuperTask(), epicId);
		assertEquals(manager.getEpicTask(epicId).getStatus(), Status.IN_PROGRESS);
	}
	
	@Test
	void testGetAllTasks() {
		
		EpicTask epic = buildEpicTask();
		CommonTask common = buildCommonTask();
		Long epicId = manager.createEpicTask(epic);
		SubTask sub = buildSubTask(Status.IN_PROGRESS, epicId);
		manager.createSubTask(sub);
		manager.createCommonTask(common);
		assertNotNull(manager.getAllTasks());
	}
	
	@Test
	void testGetAllTasksEmpty() {
		assertTrue(manager.getAllTasks().isEmpty());
	}

	@Test
	void testGetAllCommonTasks() {
		
		CommonTask common = buildCommonTask();
		
		ArrayList<Task> newTasks = new ArrayList<>();
		Long commonId = manager.createCommonTask(common);
		newTasks.add(manager.getCommonTask(commonId));
		
		ArrayList<Task> tasks = manager.getAllCommonTasks();
		assertEquals(newTasks, tasks);
	}
	
	@Test
	void testGetAllCommonTasksEmpty() {
		assertTrue(manager.getAllCommonTasks().isEmpty());
	}

	@Test
	void testGetAllEpicTasks() {
		
		EpicTask epic = buildEpicTask();
		CommonTask common = buildCommonTask();
		
		ArrayList<Task> newTasks = new ArrayList<>();
		Long epicId = manager.createEpicTask(epic);
		SubTask sub = buildSubTask(Status.IN_PROGRESS, epicId);
		manager.createSubTask(sub);
		manager.createCommonTask(common);
		newTasks.add(manager.getEpicTask(epicId));
		
		ArrayList<Task> tasks = manager.getAllEpicTasks();
		assertEquals(newTasks, tasks);
	}
	
	@Test
	void testGetAllEpicTasksEmpty() {
		assertTrue(manager.getAllEpicTasks().isEmpty());
	}

	@Test
	void testGetAllSubTasks() {
		
		EpicTask epic = buildEpicTask();
		Long epicId = manager.createEpicTask(epic);
		SubTask sub = buildSubTask(Status.IN_PROGRESS, epicId);
		ArrayList<Task> testList = new ArrayList<>();
		testList.add(sub);
		manager.createSubTask(sub);
		assertEquals(testList, manager.getAllSubTasks());
	}
	
	@Test
	void testGetAllSubTasksEmpty() {
		assertTrue(manager.getAllSubTasks().isEmpty());
	}

	@Test
	void testGetPrioritisedTasks() {
		TreeSet<Task> testTreeSet = new TreeSet<>(Comparator.comparing(Task::getStartTime));
		LocalDateTime testTime = LocalDateTime.now();
		
		CommonTask common = buildCommonTask();
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
		assertEquals(testTreeSet, tasks);
	}
	
	@Test
	void testGetPrioritisedTasksEmpty() {
		assertTrue(manager.getPrioritisedTasks().isEmpty());
	}

	@Test
	void testClearTasks() {

		CommonTask common = buildCommonTask();
		manager.createCommonTask(common);
		manager.clearTasks();
		assertTrue(manager.getAllTasks().isEmpty());
	}
	
	@Test
	void testClearTasksEmpty() {
		manager.clearTasks();
		assertTrue(manager.getAllTasks().isEmpty());
	}

	@Test
	void testClearCommonTasks() {
		CommonTask common = buildCommonTask();
		common.setName("test");
		Long commonId = manager.createCommonTask(common);
		
		assertEquals(manager.getCommonTask(commonId).getName(), "test");
		
		manager.clearCommonTasks();
		assertTrue(manager.getAllCommonTasks().isEmpty());
	}
	
	@Test
	void testClearCommonTasksEmpty() {
		manager.clearCommonTasks();
		assertTrue(manager.getAllCommonTasks().isEmpty());
	}

	@Test
	void testClearEpicTasks() {
		EpicTask epic = buildEpicTask();
		Long epicId = manager.createEpicTask(epic);
		SubTask sub = buildSubTask(Status.IN_PROGRESS, epicId);
		manager.createSubTask(sub);
		manager.clearEpicTasks();
		assertTrue(manager.getAllTasks().isEmpty());
	}
	
	@Test
	void testClearEpicTasksEmpty() {
		manager.clearEpicTasks();
		assertTrue(manager.getAllEpicTasks().isEmpty());
	}

	@Test
	void testClearSubTasks() {
		EpicTask epic = buildEpicTask();
		Long epicId = manager.createEpicTask(epic);
		SubTask sub = buildSubTask(Status.IN_PROGRESS, epicId);
		manager.createSubTask(sub);
		manager.clearEpicTasks();
		assertTrue(manager.getAllTasks().isEmpty());
	}
	
	@Test
	void testClearSubTasksEmpty() {
		manager.clearSubTasks();
		assertTrue(manager.getAllSubTasks().isEmpty());
	}

	@Test
	void testGetCommonTask() {
		CommonTask common = buildCommonTask();
		Long commonId = manager.createCommonTask(common);
		common.setId(commonId);
		assertEquals(common.getId(), manager.getCommonTask(commonId).getId());
	}
	
	@Test
	void testGetCommonTaskEmpty() {
		CommonTask common = buildCommonTask();
		Long commonId = manager.createCommonTask(common);
		manager.removeCommonTask(commonId);
		assertTrue(manager.getCommonTask(commonId).getId()==null);
	}
	
	@Test
	void testGetCommonTaskWrongId() {
		CommonTask common = buildCommonTask();
		manager.createCommonTask(common);
		assertNotNull(manager.getCommonTask(0L).getId());
	}

	@Test
	void testGetEpicTask() {
		EpicTask epic = buildEpicTask();
		Long epicId = manager.createEpicTask(epic);
		epic.setId(epicId);
		assertEquals(epic.getId(), manager.getEpicTask(epicId).getId());
	}
	
	@Test
	void testGetEpicTaskEmpty() {
		EpicTask epic = buildEpicTask();
		Long epicId = manager.createEpicTask(epic);
		manager.removeEpicTask(epicId);
		assertTrue(manager.getCommonTask(epicId).getId()==null);
	}
	
	@Test
	void testGetEpicTaskWrongId() {
		EpicTask epic = buildEpicTask();
		manager.createEpicTask(epic);
		assertTrue(manager.getEpicTask(0L).getId()==null);
	}

	@Test
	void testGetSubTask() {
		EpicTask epic = buildEpicTask();
		Long epicId = manager.createEpicTask(epic);
		SubTask sub = buildSubTask(Status.NEW, epicId);
		Long subId = manager.createSubTask(sub);
		sub.setId(subId);
		assertEquals(sub.getId(), manager.getSubTask(subId).getId());
	}
	
	@Test
	void testGetsubTaskEmpty() {
		EpicTask epic = buildEpicTask();
		Long epicId = manager.createEpicTask(epic);
		SubTask sub = buildSubTask(Status.NEW, epicId);
		Long subId = manager.createSubTask(sub);
		manager.removeSubTask(subId);
		assertTrue(manager.getSubTask(subId).getId()==null);
	}
	
	@Test
	void testGetSubTaskWrongId() {
		EpicTask epic = buildEpicTask();
		Long epicId = manager.createEpicTask(epic);
		SubTask sub = buildSubTask(Status.NEW, epicId);
		manager.createSubTask(sub);
		assertTrue(manager.getSubTask(0L).getId()==null);
	}

	@Test
	void testEditCommonTask() {
		CommonTask common = buildCommonTask();
		common.setDescription("test");
		Long commonId = manager.createCommonTask(common);
		assertTrue(common.getDescription().equals(manager.getCommonTask(commonId).getDescription()));
		common.setDescription("test2");
		manager.editCommonTask(commonId, common);
		assertTrue(common.getDescription().equals(manager.getCommonTask(commonId).getDescription()));
	}
	
	@Test
	void testEditCommonTaskEmpty() {
		CommonTask common = buildCommonTask();
		common.setId(1L);
		common.setDescription("test");
		manager.editCommonTask(1L, common);
		assertFalse(common.getDescription().equals(manager.getCommonTask(1L).getDescription()));
	}
	
	@Test
	void testEditCommonTaskWrongId() {
		CommonTask common = buildCommonTask();
		common.setDescription("test");
		Long commonId = manager.createCommonTask(common);
		assertTrue(common.getDescription().equals(manager.getCommonTask(commonId).getDescription()));
		common.setDescription("test2");
		manager.editCommonTask(1L, common);
		assertTrue(common.getDescription().equals(manager.getCommonTask(commonId).getDescription()));
	}

	@Test
	void testEditEpicTask() {
		EpicTask epic = buildEpicTask();
		epic.setDescription("test");
		Long epicId = manager.createEpicTask(epic);
		assertTrue(epic.getDescription().equals(manager.getEpicTask(epicId).getDescription()));
		epic.setDescription("test2");
		manager.editEpicTask(1L, epic);
		assertTrue(epic.getDescription().equals(manager.getEpicTask(epicId).getDescription()));
	}
	
	@Test
	void testEditEpicTaskEmpty() {
		EpicTask epic = buildEpicTask();
		epic.setId(1L);
		epic.setDescription("test");
		manager.editEpicTask(1L, epic);
		assertFalse(epic.getDescription().equals(manager.getEpicTask(1L).getDescription()));
	}
	
	@Test
	void testEditEpicTaskWrongId() {
		EpicTask epic = buildEpicTask();
		epic.setDescription("test");
		Long epicId = manager.createEpicTask(epic);
		assertTrue(epic.getDescription().equals(manager.getEpicTask(epicId).getDescription()));
		epic.setDescription("test2");
		manager.editEpicTask(epicId, epic);
		assertTrue(epic.getDescription().equals(manager.getEpicTask(epicId).getDescription()));
	}

	@Test
	void testEditSubTask() {
		EpicTask epic = buildEpicTask();
		epic.setDescription("test");
		Long epicId = manager.createEpicTask(epic);
		SubTask sub = buildSubTask(Status.IN_PROGRESS, epicId);
		sub.setDescription("test");
		Long subId = manager.createSubTask(sub);
		assertTrue(sub.getDescription().equals(manager.getSubTask(subId).getDescription()));
		sub.setDescription("test2");
		manager.editSubTask(subId, sub);
		assertTrue(sub.getDescription().equals(manager.getSubTask(subId).getDescription()));
	}
	
	@Test
	void testEditSubTaskEmpty() {
		SubTask sub = buildSubTask(Status.NEW, 0L);
		sub.setId(1L);
		sub.setDescription("test");
		manager.editSubTask(1L, sub);
		assertFalse(sub.getDescription().equals(manager.getSubTask(1L).getDescription()));
	}
	
	@Test
	void testEditSubTaskWrongId() {
		EpicTask epic = buildEpicTask();
		Long epicId = manager.createEpicTask(epic);
		SubTask sub = buildSubTask(Status.IN_PROGRESS, epicId);
		sub.setDescription("test");
		Long subId = manager.createSubTask(sub);
		assertTrue(sub.getDescription().equals(manager.getSubTask(subId).getDescription()));
		sub.setDescription("test2");
		manager.editSubTask(1L, sub);
		assertTrue(sub.getDescription().equals(manager.getSubTask(subId).getDescription()));
	}

	@Test
	void testRemoveCommonTask() {
		CommonTask common = buildCommonTask();
		Long commonId = manager.createCommonTask(common);
		manager.removeCommonTask(commonId);
		assertTrue(manager.getAllCommonTasks().isEmpty());
	}
	
	@Test
	void testRemoveCommonTaskEmpty() {
		manager.removeCommonTask(1L);
		assertTrue(manager.getAllCommonTasks().isEmpty());
	}
	
	@Test
	void testRemoveCommonTaskWrongId() {
		EpicTask epic = buildEpicTask();
		CommonTask common = buildCommonTask();
		common.setName("test");
		Long epicId = manager.createEpicTask(epic);
		SubTask sub = buildSubTask(Status.IN_PROGRESS, epicId);
		manager.createSubTask(sub);
		Long commonId = manager.createCommonTask(common);
		
		
		manager.removeCommonTask(1L);
		assertEquals(manager.getCommonTask(commonId).getName(),"test");
	}

	@Test
	void testRemoveEpicTask() {
		EpicTask epic = buildEpicTask();
		Long epicId = manager.createEpicTask(epic);
		SubTask sub = buildSubTask(Status.IN_PROGRESS, epicId);
		manager.createSubTask(sub);
		manager.removeEpicTask(epicId);
		assertTrue(manager.getAllTasks().isEmpty());
	}
	
	@Test
	void testRemoveEpicTaskEmpty() {
		manager.removeEpicTask(1L);
		assertTrue(manager.getAllEpicTasks().isEmpty());
	}
	
	@Test
	void testRemoveEpicTaskWrongId() {
		EpicTask epic = buildEpicTask();
		epic.setName("test");
		Long epicId = manager.createEpicTask(epic);
		
		assertEquals(epic.getName(), manager.getEpicTask(epicId).getName());
		
		manager.removeEpicTask(1L);
		assertEquals(epic.getName(), manager.getEpicTask(epicId).getName());
	}

	@Test
	void testRemoveSubTask() {
		EpicTask epic = buildEpicTask();
		Long epicId = manager.createEpicTask(epic);
		SubTask sub = buildSubTask(Status.IN_PROGRESS, epicId);
		Long subId = manager.createSubTask(sub);
		manager.removeSubTask(subId);
		assertTrue(manager.getAllSubTasks().isEmpty());
	}
	
	@Test
	void testRemoveSubTaskEmpty() {
		manager.removeSubTask(1L);
		assertTrue(manager.getAllSubTasks().isEmpty());
	}
	
	@Test
	void testRemoveSubTaskWrongId() {
		EpicTask epic = buildEpicTask();
		Long epicId = manager.createEpicTask(epic);
		SubTask sub = buildSubTask(Status.IN_PROGRESS, epicId);
		Long subId = manager.createSubTask(sub);
		
		assertEquals(sub.getSuperTask(), manager.getSubTask(subId).getSuperTask());
		
		manager.removeSubTask(1L);
		assertEquals(sub.getSuperTask(), manager.getSubTask(subId).getSuperTask());
	}

	@Test
	void testGetEpicSubTasks() {
		EpicTask epic = buildEpicTask();
		Long epicId = manager.createEpicTask(epic);
		SubTask sub = buildSubTask(Status.IN_PROGRESS, epicId);
		manager.createSubTask(sub);
		sub.setDescription("test");
		assertEquals(manager.getSubTask(manager.getEpicSubTasks(epicId).get(0)).getDescription(),sub.getDescription());
	}
	
	@Test
	void testGetEpicSubTasksEmpty() {
		assertTrue(manager.getEpicSubTasks(1L).isEmpty());
	}
	
	@Test
	void testGetEpicSubTasksWrongId() {
		EpicTask epic = buildEpicTask();
		Long epicId = manager.createEpicTask(epic);
		SubTask sub = buildSubTask(Status.IN_PROGRESS, epicId);
		sub.setDescription("test");
		manager.createSubTask(sub);
		assertTrue(manager.getEpicSubTasks(1L).isEmpty());
	}

	@Test
	void testGetTaskEndTime() {
		CommonTask task = buildCommonTask();
		LocalDateTime time = task.getStartTime();
		task.setDuration(Duration.ofMinutes(15));
		Long taskId = manager.createCommonTask(task);
		assertEquals(manager.getTaskEndTime(taskId),time.plusMinutes(15));
	}
	
	@Test
	void testGetTaskEndTimeEmpty() {
		assertThrows(NullPointerException.class, () -> manager.getTaskEndTime(1L));
	}
	
	@Test
	void testGetTaskEndTimeWrongId() {
		CommonTask task = buildCommonTask();
		LocalDateTime time = LocalDateTime.now();
		task.setStartTime(time);
		task.setDuration(Duration.ofMinutes(15));
		manager.createCommonTask(task);
		assertThrows(NullPointerException.class, () -> manager.getTaskEndTime(1L));
	}

	@Test
	void testCreateCommonTask() {
		CommonTask task = buildCommonTask();
		Long taskId = manager.createCommonTask(task);
		assertEquals(task.getClass(), manager.getCommonTask(taskId).getClass());
	}

	@Test
	void testCreateEpicTask() {
		EpicTask task = buildEpicTask();
		Long taskId = manager.createEpicTask(task);
		assertEquals(task.getClass(), manager.getEpicTask(taskId).getClass());
	}

	@Test
	void testCreateSubTask() {
		Long epicId = manager.createEpicTask(buildEpicTask());
		SubTask task = buildSubTask(Status.NEW,epicId);
		Long taskId = manager.createSubTask(task);
		assertEquals(task.getClass(), manager.getSubTask(taskId).getClass());
	}

	@Test
	void testToString() {
		
		ArrayList<Task> newTasks = new ArrayList<>();
		
		CommonTask common = buildCommonTask();		
		Long commonId = manager.createCommonTask(common);
		
		newTasks.add(manager.getCommonTask(commonId));
		
		String stringTest = "";
		for(Task task:newTasks) {
			stringTest += task.toString() + "\n";
		}
		
		assertEquals(stringTest,manager.toString());
	}
	
	@Test
	void testToStringEmpty() {
		assertEquals(manager.toString(), "");
	}

	@Test
	void testGetHistoryManager() {
		CommonTask common = buildCommonTask();
		Long commonId = manager.createCommonTask(common);
		ArrayList<Task> testList = new ArrayList<>();
		testList.add(manager.getCommonTask(commonId));
		assertEquals(testList, manager.getHistoryManager().getHistory());
	}
	
	private CommonTask buildCommonTask() {
		return new CommonTask.Builder()
                .setName("Тест имени таска ")
                .setDescription("Тест описания таска ")
                .setStatus(Status.NEW)
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
	
	private EpicTask getEpicTaskForTimeTest(LocalDateTime testTime) {
		Long epicId = manager.createEpicTask(buildEpicTask());
		SubTask sub1 = buildSubTask(Status.NEW, epicId);
		sub1.setStartTime(testTime);
		manager.createSubTask(sub1);
		SubTask sub2 = buildSubTask(Status.NEW, epicId);
		sub2.setStartTime(testTime.plusMinutes(30));
		manager.createSubTask(sub2);
		return manager.getEpicTask(epicId);
	}
}
