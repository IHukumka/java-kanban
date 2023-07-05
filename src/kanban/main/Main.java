package kanban.main;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import kanban.api.KVServer;
import kanban.task.CommonTask;
import kanban.task.EpicTask;
import kanban.task.SubTask;
import kanban.task.Task;
import kanban.api.*;
public class Main {
	
    public static void main(String[] args) throws IOException, InterruptedException {

		CommonTask common = new CommonTask.Builder().setId(1L).setName("Common").build();
		EpicTask epic = new EpicTask.Builder().setId(2L).setName("Epic").build();
		SubTask sub = new SubTask.Builder().setId(3L).setName("Sub").setSuperTask(2L).build();

		ObjectMapper mapper = new ObjectMapper();
    	mapper.registerModule(new JavaTimeModule());
    	
    	HttpClient client = HttpClient.newHttpClient();
    	
		KVServer kvServer = new KVServer();
	   	kvServer.start();
		
    	HttpTaskServer taskServer = new HttpTaskServer("http://localhost:8078", "User");
    	taskServer.start();
    	
    	ArrayList<Task> commons = new ArrayList<Task>();
		commons.add(common);
		mapper = new ObjectMapper();
    	mapper.registerModule(new JavaTimeModule());
    	String dataToSave = mapper.
	        	writerWithDefaultPrettyPrinter().
	        	writeValueAsString(commons);
		HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/common"))
                .POST(BodyPublishers.ofString(dataToSave))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
    	
    	
		ArrayList<Task> epics = new ArrayList<Task>();
		epics.add(epic);
    	dataToSave = mapper.
	        	writerWithDefaultPrettyPrinter().
	        	writeValueAsString(epics);
		request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic"))
                .POST(BodyPublishers.ofString(dataToSave))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        
        ArrayList<Task> subs = new ArrayList<Task>();
		subs.add(sub);
    	dataToSave = mapper.
	        	writerWithDefaultPrettyPrinter().
	        	writeValueAsString(subs);
		request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/sub"))
                .POST(BodyPublishers.ofString(dataToSave))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
    	
    }
}