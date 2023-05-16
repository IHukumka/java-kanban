package kanban.main;

import java.io.IOException;

import kanban.api.KVServer;
import kanban.manager.Managers;
import kanban.manager.TaskManager;
import kanban.task.CommonTask;

public class Main {
	// Привет. Прошу прощения, что надолго пропал. Тяжелый месяц
	// Не могу понять зачем нужен KVServer, в чем идея такой 
	// реализации сервера. После столь объемного HTTPTaskSever
	// старнное ощущение, что он никак не используется. Дай,
	// пожалуйста, подсказку, куда двигаться. Вебинары, мягко говоря,
	// не помогают. ТЗ тем более
	static TaskManager manager;
		
    public static void main(String[] args) throws IOException {
    	KVServer server = new KVServer();
    	server.start();
    	try {
			manager = Managers.getDefault("http://localhost:8078", "User");
		} catch (Exception e) {
			e.printStackTrace();
		}
    	CommonTask task = new CommonTask.Builder().build();
    	manager.createCommonTask(task);
    }
}
