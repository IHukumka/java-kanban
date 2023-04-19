package kanban.tests;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import kanban.manager.InMemoryTaskManager;
import kanban.manager.Managers;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager>  {

	@BeforeEach
	public void setUp() {
		manager = Managers.getDefault();
	}
	
	@AfterEach
	public void tearDown() {
		manager.clearTasks();
	}
}