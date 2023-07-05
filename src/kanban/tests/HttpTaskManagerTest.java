package kanban.tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import kanban.api.KVServer;
import kanban.manager.HttpTaskManager;
import kanban.task.CommonTask;
import kanban.task.EpicTask;
import kanban.task.SubTask;

class HttpTaskManagerTest {

	public static KVServer kvServer;
	public static HttpTaskManager manager;
	public static Long commonId;
	public static Long epicId;
	public static Long subId;
	public static String url = "http://localhost:8078";
	public static String key = "Test";
	
	@BeforeEach
	void setUpBeforeEach() throws Exception {
		
		kvServer = new KVServer();
	   	kvServer.start();
		manager = new HttpTaskManager(url, key);
		
		CommonTask common = new CommonTask.Builder().setName("Common").build();
		commonId = manager.createCommonTask(common);
		
		EpicTask epic = new EpicTask.Builder().setName("Epic").build();
		epicId = manager.createEpicTask(epic);
		
		SubTask sub = new SubTask.Builder().setName("Sub").setSuperTask(epicId).build();
		subId = manager.createSubTask(sub);
		
		manager.save();
	}
	
	@AfterEach
	void setAfterEach() throws Exception {
		kvServer.stop();
	}

	@Test
	void testClearTasks() {
		manager.clearTasks();
		assertTrue(manager.getAllTasks().isEmpty());
	}

	@Test
	void testClearCommonTasks() {
		manager.clearCommonTasks();
		assertTrue(manager.getAllCommonTasks().isEmpty());
	}

	@Test
	void testClearEpicTasks() {
		manager.clearEpicTasks();
		assertTrue(manager.getAllEpicTasks().isEmpty());
		assertTrue(manager.getAllSubTasks().isEmpty());
	}

	@Test
	void testClearSubTasks() {
		manager.clearSubTasks();
		assertTrue(manager.getAllSubTasks().isEmpty());
	}

	@Test
	void testEditCommonTask() {
		CommonTask task = manager.getCommonTask(commonId);
		task.setName("Test");
		manager.editCommonTask(commonId, task);
		assertEquals("Test", manager.getCommonTask(commonId).getName());
	}

	@Test
	void testEditEpicTask() {
		EpicTask task = manager.getEpicTask(epicId);
		task.setName("Test");
		manager.editEpicTask(epicId, task);
		assertEquals("Test", manager.getEpicTask(epicId).getName());
	}

	@Test
	void testEditSubTask() {
		SubTask task = manager.getSubTask(subId);
		task.setName("Test");
		manager.editSubTask(commonId, task);
		assertEquals("Test", manager.getSubTask(subId).getName());
	}

	@Test
	void testRemoveCommonTask() {
		manager.removeCommonTask(commonId);
		assertTrue(manager.getAllCommonTasks().isEmpty());
	}

	@Test
	void testRemoveEpicTask() {
		manager.removeEpicTask(epicId);
		assertTrue(manager.getAllEpicTasks().isEmpty());
		assertTrue(manager.getAllSubTasks().isEmpty());
	}

	@Test
	void testRemoveSubTask() {
		manager.clearSubTasks();
		assertTrue(manager.getAllSubTasks().isEmpty());
		assertTrue(manager.getEpicSubTasks(epicId).isEmpty());
	}

	@Test
	void testCreateCommonTask() {
		CommonTask task = new CommonTask.Builder().setName("Test").build();
		Long newTaskId = manager.createCommonTask(task);
		assertEquals("Test", manager.getCommonTask(newTaskId).getName());
	}

	@Test
	void testCreateEpicTask() {
		EpicTask task = new EpicTask.Builder().setName("Test").build();
		Long newTaskId = manager.createEpicTask(task);
		assertEquals("Test", manager.getEpicTask(newTaskId).getName());
	}

	@Test
	void testCreateSubTask() {
		SubTask task = new SubTask.Builder().setName("Test").setSuperTask(epicId).build();
		Long newTaskId = manager.createSubTask(task);
		assertEquals("Test", manager.getSubTask(newTaskId).getName());
	}

	@Test
	void testSave() throws Exception {
		manager = new HttpTaskManager(url, key);
		manager.clearEpicTasks();
		manager.save();
		assertEquals(manager.getAllTasks(), manager.fromUrl(url, key));
	}

	@Test
	void testFromUrl() throws Exception {
		manager = new HttpTaskManager(url, key);
		manager.clearEpicTasks();
		manager.save();
		assertEquals(manager.getAllTasks(), manager.fromUrl(url, key));
	}

}
