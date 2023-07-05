package kanban.tests;

import java.io.File;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import kanban.manager.InMemoryTaskManager;
import kanban.manager.Managers;
import kanban.manager.TaskManager;
import kanban.task.CommonTask;

class FileBackedTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
	
	final static File file = new File(System.getProperty("user.dir")+"\\test.json");

	@BeforeEach
	public void setUp() {
		manager = Managers.getFileBackedManager(file);
	}
	
	@AfterEach
	public void tearDown() {
		manager.clearTasks();
	}

	@Test
	void testSave() {
		CommonTask task = new CommonTask.Builder().setName("Test").build();
		Long taskId = manager.createCommonTask(task);
		TaskManager test = Managers.getFileBackedManager(file);
		assertEquals("Test", test.getCommonTask(taskId).getName() );
	}
	
	@Test
	void testLoadFromFile() {
		CommonTask task = new CommonTask.Builder().setName("Test").build();
		Long taskId = manager.createCommonTask(task);
		assertEquals("Test", manager.getCommonTask(taskId).getName() );
	}
	
	@Test
	void testLoadFromFileEmpty() {
		manager.clearTasks();
		assertTrue(Managers.getFileBackedManager(file).getAllTasks().isEmpty());
	}
	
	@Test
	void testLoadFromFileNoFile() {
		File testFile = new File(System.getProperty("user.dir")+"\\test2.json");
		assertTrue(Managers.getFileBackedManager(testFile).getAllTasks().isEmpty());
	}

}
