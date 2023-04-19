package kanban.tests;

import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import kanban.manager.HistoryManager;
import kanban.manager.Managers;
import kanban.task.CommonTask;
import kanban.task.Task;

class HistoryManagerTest {
	
	private static HistoryManager manager;
 
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		manager = Managers.getDefaultHistory();
	}

	@AfterEach
	void tearDown() throws Exception {
		manager.getHistory().clear();
	}

	@Test
	void testAdd() {
		CommonTask task = new CommonTask.Builder().build();
		ArrayList<Task> testList = new ArrayList<>();
		testList.add(task);
		manager.add(task);
		Assertions.assertEquals(testList,manager.getHistory());
	}

	@Test
	void testRemove() {
		CommonTask task = new CommonTask.Builder().build();
		manager.add(task);
		manager.remove(task.getId());
		Assertions.assertTrue(manager.getHistory().isEmpty());
	}

	@Test
	void testGetHistory() {
		CommonTask task = new CommonTask.Builder().build();
		ArrayList<Task> testList = new ArrayList<>();
		testList.add(task);
		manager.add(task);
		Assertions.assertEquals(manager.getHistory(),testList);
	}
	
	@Test
	void testClear(){
		CommonTask task = new CommonTask.Builder().build();
		manager.add(task);
		manager.clear();
		Assertions.assertTrue(manager.getHistory().isEmpty());
	}

}
