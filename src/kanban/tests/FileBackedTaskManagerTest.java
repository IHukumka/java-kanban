package kanban.tests;

import java.io.File;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
		Assertions.assertEquals("Test", test.getCommonTask(taskId).getName() );
	}
	
	@Test
	void testLoadFromFile() {
		CommonTask task = new CommonTask.Builder().setName("Test").build();
		Long taskId = manager.createCommonTask(task);
		TaskManager test = Managers.getFileBackedManager(file);
		Assertions.assertEquals("Test", test.getCommonTask(taskId).getName() );
	}
	
	@Test
	void testLoadFromFileEmpty() {
		manager.clearTasks();
		Assertions.assertTrue(Managers.getFileBackedManager(file).getAllTasks().isEmpty());
	}
	
	@Test
	void testLoadFromFileNoFile() {
		File testFile = new File(System.getProperty("user.dir")+"\\test2.json");
		Assertions.assertTrue(Managers.getFileBackedManager(testFile).getAllTasks().isEmpty());
	}

}
