package kanban.tests;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import kanban.api.HttpTaskServer;
import kanban.api.KVServer;
import kanban.task.CommonTask;
import kanban.task.EpicTask;
import kanban.task.SubTask;
import kanban.task.Task;

class HttpTaskServerTest {

	public static KVServer kvServer;
	public static HttpTaskServer taskServer;
	public static String url = "http://localhost:8080";
	public static String key = "User";
	public static HttpClient client;
	
	@BeforeEach
	void setUpBeforeEach() throws Exception {
		 
		CommonTask common = new CommonTask.Builder().setId(1L).setName("Common").build();
		EpicTask epic = new EpicTask.Builder().setId(2L).setName("Epic").build();
		SubTask sub = new SubTask.Builder().setId(3L).setName("Sub").setSuperTask(2L).build();

		ObjectMapper mapper = new ObjectMapper();
    	mapper.registerModule(new JavaTimeModule());
    	
    	client = HttpClient.newHttpClient();
    	
		kvServer = new KVServer();
	   	kvServer.start();
		
    	taskServer = new HttpTaskServer("http://localhost:8078", "User");
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
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    	
    	
		ArrayList<Task> epics = new ArrayList<Task>();
		epics.add(epic);
    	dataToSave = mapper.
	        	writerWithDefaultPrettyPrinter().
	        	writeValueAsString(epics);
		request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic"))
                .POST(BodyPublishers.ofString(dataToSave))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        ArrayList<Task> subs = new ArrayList<Task>();
		subs.add(sub);
    	dataToSave = mapper.
	        	writerWithDefaultPrettyPrinter().
	        	writeValueAsString(subs);
		request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/sub"))
                .POST(BodyPublishers.ofString(dataToSave))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
   
	}
	
	@AfterEach
	void setAfterEach() throws Exception {
		kvServer.stop();
		taskServer.stop();
	}

	
	@Test
	void testHandleGetAllTasks() throws IOException, InterruptedException {
		URI uri = URI.create(url+"/tasks/all");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
	}
	
	@Test
	void testHandleDeleteAllTasks() throws IOException, InterruptedException {
		URI uri = URI.create(url+"/tasks/all");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
	}
	
	@Test
	void testHandleGetTasksHistory() throws IOException, InterruptedException {
		URI uri = URI.create(url+"/tasks/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
	}
	
	@Test
	void testHandleGetTasksPriority() throws IOException, InterruptedException {
		URI uri = URI.create(url+"/tasks/priority");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
	}
	
	@Test
	void testHandleGetTaskEndTime()throws IOException, InterruptedException {
		URI uri = URI.create(url+"/tasks/endtime/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
	}
	
	@Test
	void testHandleGetTasksEpicSubs() throws IOException, InterruptedException {
		URI uri = URI.create(url+"/tasks/epic/subs/2");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
	}
	
	@Test
	void testHandleGetTasksEpicAll() throws IOException, InterruptedException {
		URI uri = URI.create(url+"/tasks/epic/all");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
	}
	
	@Test
	void testHandleDeleteTasksEpicAll() throws IOException, InterruptedException {
		URI uri = URI.create(url+"/tasks/epic/all");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
	}
	
	@Test
	void testHandleGetTasksEpic() throws IOException, InterruptedException {
		URI uri = URI.create(url+"/tasks/epic/2");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
	}
	
	@Test
	void testHandlePostTasksEpicEdit() throws IOException, InterruptedException {
		URI uri = URI.create(url+"/tasks/epic/edit/2");
		EpicTask epic = new EpicTask.Builder().setId(2L).setName("UnEpic").build();
		ArrayList<Task> tasks = new ArrayList<Task>();
		tasks.add(epic);
		ObjectMapper mapper = new ObjectMapper();
    	mapper.registerModule(new JavaTimeModule());
    	String dataToSave = mapper.
	        	writerWithDefaultPrettyPrinter().
	        	writeValueAsString(tasks);
		HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(BodyPublishers.ofString(dataToSave))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, response.statusCode());
	}
	
	@Test
	void testHandleDeleteTasksEpic() throws IOException, InterruptedException {
		URI uri = URI.create(url+"/tasks/epic/2");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
	}
	
	@Test
	void testHandlePostTasksEpic() throws IOException, InterruptedException {
		URI uri = URI.create(url+"/tasks/epic");
		EpicTask Epic = new EpicTask.Builder().setId(4L).setName("UnEpic").build();
		ArrayList<Task> tasks = new ArrayList<Task>();
		tasks.add(Epic);
		ObjectMapper mapper = new ObjectMapper();
    	mapper.registerModule(new JavaTimeModule());
    	String dataToSave = mapper.
	        	writerWithDefaultPrettyPrinter().
	        	writeValueAsString(tasks);
		HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(BodyPublishers.ofString(dataToSave))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, response.statusCode());
	}
	
	@Test
	void testHandleGetTasksCommonAll() throws IOException, InterruptedException {
		URI uri = URI.create(url+"/tasks/common/all");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
	}
	
	@Test
	void testHandleDeleteTasksCommonAll() throws IOException, InterruptedException {
		URI uri = URI.create(url+"/tasks/common/all");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
	}
	
	@Test
	void testHandleGetTasksCommon() throws IOException, InterruptedException {
		URI uri = URI.create(url+"/tasks/common/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
	}
	
	@Test
	void testHandlePostTasksCommonEdit() throws IOException, InterruptedException {
		URI uri = URI.create(url+"/tasks/common/edit/1");
		CommonTask common = new CommonTask.Builder().setId(1L).setName("UnCommon").build();
		ArrayList<Task> tasks = new ArrayList<Task>();
		tasks.add(common);
		ObjectMapper mapper = new ObjectMapper();
    	mapper.registerModule(new JavaTimeModule());
    	String dataToSave = mapper.
	        	writerWithDefaultPrettyPrinter().
	        	writeValueAsString(tasks);
		HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(BodyPublishers.ofString(dataToSave))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, response.statusCode());
	}
	
	@Test
	void testHandleDeleteTasksCommon() throws IOException, InterruptedException {
		URI uri = URI.create(url+"/tasks/common/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
	}
	
	@Test
	void testHandlePostTasksCommon() throws IOException, InterruptedException {
		URI uri = URI.create(url+"/tasks/common");
		CommonTask common = new CommonTask.Builder().setId(4L).setName("UnCommon").build();
		ArrayList<Task> tasks = new ArrayList<Task>();
		tasks.add(common);
		ObjectMapper mapper = new ObjectMapper();
    	mapper.registerModule(new JavaTimeModule());
    	String dataToSave = mapper.
	        	writerWithDefaultPrettyPrinter().
	        	writeValueAsString(tasks);
		HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(BodyPublishers.ofString(dataToSave))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, response.statusCode());
	}
	
	@Test
	void testHandleGetTasksSubAll() throws IOException, InterruptedException {
		URI uri = URI.create(url+"/tasks/sub/all");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
	}
	
	@Test
	void testHandleDeleteTasksSubAll() throws IOException, InterruptedException {
		URI uri = URI.create(url+"/tasks/sub/all");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
	}
	
	@Test
	void testHandleGetTasksSub() throws IOException, InterruptedException {
		URI uri = URI.create(url+"/tasks/sub/3");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
	}
	
	@Test
	void testHandlePostTasksSubEdit() throws IOException, InterruptedException {
		URI uri = URI.create(url+"/tasks/sub/edit/3");
		SubTask sub = new SubTask.Builder().setId(3L).setName("UnSub").setSuperTask(2L).build();
		ArrayList<Task> tasks = new ArrayList<Task>();
		tasks.add(sub);
		ObjectMapper mapper = new ObjectMapper();
    	mapper.registerModule(new JavaTimeModule());
    	String dataToSave = mapper.
	        	writerWithDefaultPrettyPrinter().
	        	writeValueAsString(tasks);
		HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(BodyPublishers.ofString(dataToSave))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, response.statusCode());
	}
	
	@Test
	void testHandleDeleteTasksSub() throws IOException, InterruptedException {
		URI uri = URI.create(url+"/tasks/sub/3");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
	}
	
	@Test
	void testHandlePostTasksSub() throws IOException, InterruptedException {
		URI uri = URI.create(url+"/tasks/sub");
		SubTask sub = new SubTask.Builder().setId(4L).setName("UnSub").setSuperTask(2L).build();
		ArrayList<Task> tasks = new ArrayList<Task>();
		tasks.add(sub);
		ObjectMapper mapper = new ObjectMapper();
    	mapper.registerModule(new JavaTimeModule());
    	String dataToSave = mapper.
	        	writerWithDefaultPrettyPrinter().
	        	writeValueAsString(tasks);
		HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(BodyPublishers.ofString(dataToSave))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, response.statusCode());
	}
	
}
