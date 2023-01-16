package kanban;

import java.io.File;
import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;

public abstract class FileHandler {
	
	//Путь к файлу данных
	public static String directory = System.getProperty("user.dir") + "\\userFiles"; 
																						 

	// метод выгрузки данных из файла
	public static HashMap<Long, CommonTask> loadUserTasks(String userName) {
		
		HashMap<Long, CommonTask> userTasks = new HashMap<>(); // collection of task objects
		File file = new File(directory + userName + ".json");
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			userTasks = mapper.readValue(file,HashMap.class);
			for (HashMap.Entry<Long, CommonTask> entry : userTasks.entrySet()) {
		        System.out.println(entry.getKey() + "=" + entry.getValue());
		    }
		}catch (IOException e) {
			System.out.println("Error");
		}
		return userTasks;
	}
	
	//метод загрузки данных в файл
	public static void saveUserTasks(String userName, HashMap<Long, CommonTask> userTasks) {
		
		File file = new File(directory + userName + ".json");
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.writeValue(file, userTasks);
			System.out.println("Данные сохранены!");
		}catch (IOException e) {
			System.out.println("Error");
		}
	}
}