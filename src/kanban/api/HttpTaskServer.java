package kanban.api;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;

import kanban.manager.HttpTaskManager;
import kanban.manager.Managers;
import kanban.task.CommonTask;
import kanban.task.EpicTask;
import kanban.task.SubTask;
import kanban.task.Task;

import org.apache.commons.io.IOUtils;

public class HttpTaskServer {

	private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
	private static final int PORT = 8080;
	private HttpTaskManager manager;
	private HttpServer server;
	
	public HttpTaskServer(String url, String key){
		try {
			this.manager = (HttpTaskManager) Managers.getDefault(url, key);
			server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
	        server.createContext("/tasks", new TaskHandler());
	        server.createContext("/tasks/common", new CommonTaskHandler());
	        server.createContext("/tasks/epic", new EpicTaskHandler());
	        server.createContext("/tasks/sub", new SubTaskHandler());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public HttpTaskServer(HttpTaskManager manager){
		this.manager = manager;
		try {
			server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
	        server.createContext("/tasks/", new TaskHandler());
	        server.createContext("/tasks/common", new CommonTaskHandler());
	        server.createContext("/tasks/epic", new EpicTaskHandler());
	        server.createContext("/tasks/sub", new SubTaskHandler());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void start() {
		System.out.println("Запускаем таск сервер на порту " + PORT);
		server.start();
	}
	
	public void stop() {
		System.out.println("Сервер остановлен.");
		server.stop(0);
	}
	
	public class TaskHandler implements HttpHandler{
		
		@Override
		public void handle(HttpExchange exchange) throws IOException {
			
			String endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());
	    	
			switch(endpoint) {
				case "GET_TASKS_ALL":{
					handleGetAllTasks(exchange);
					break;
				}
				case "DELETE_TASKS_ALL":{
					handleDeleteAllTasks(exchange);
					break;
				}
				case "GET_TASKS_HISTORY":{
					hanldeGetHistory(exchange);
					break;
				}
				case "GET_TASKS_PRIORITY":{
					handleGetPrioritisedTasks(exchange);
					break;
				}
				case "GET_TASKS_ENDTIME":{
					handleGetTaskEndTime(exchange);
					break;
				}
				case "UNKNOWN":{
					writeResponse(exchange, "Такого эндпоинта не существует", 404);
				}
			}
		}
	
		private void handleGetTaskEndTime(HttpExchange exchange) throws IOException {
			String[] pathParts = exchange.getRequestURI().getPath().split("/");
			Long id = Long.parseLong(pathParts[3]);
			LocalDateTime endTime = null;
			try {
				endTime = manager.getTaskEndTime(id);
			} catch (NullPointerException e) {
				writeResponse(exchange, String.format("Задача id%s не найдена.", id), 400);
			}
			writeResponse(exchange, endTime.toString(), 200);
			
		}

		private void handleGetPrioritisedTasks(HttpExchange exchange) throws IOException {
			ArrayList<Task> tasks = new ArrayList<>();
			for (Task task : manager.getPrioritisedTasks()) {
				tasks.add(task);
			}
			ObjectMapper mapper = new ObjectMapper();
		    mapper.registerModule(new JavaTimeModule());
			String responseBody = mapper.
	        		writerWithDefaultPrettyPrinter().
	        		writeValueAsString(tasks);
			writeResponse(exchange, responseBody, 200);
		}

		private void hanldeGetHistory(HttpExchange exchange) throws IOException {
			ArrayList<Task> tasks = manager.getHistoryManager().getHistory();
			ObjectMapper mapper = new ObjectMapper();
		    mapper.registerModule(new JavaTimeModule());
			String responseBody = mapper.
	        		writerWithDefaultPrettyPrinter().
	        		writeValueAsString(tasks);
			writeResponse(exchange, responseBody, 200);
		}

		private void handleDeleteAllTasks(HttpExchange exchange) throws IOException {
			manager.clearTasks();
			writeResponse(exchange, String.format("Все задачи удалены."), 200);
		}

		private void handleGetAllTasks(HttpExchange exchange) throws IOException {
			ArrayList<Task> tasks = new ArrayList<>();
			tasks = manager.getAllTasks();
			ObjectMapper mapper = new ObjectMapper();
		    mapper.registerModule(new JavaTimeModule());
			String responseBody = mapper.
	        		writerWithDefaultPrettyPrinter().
	        		writeValueAsString(tasks);
			writeResponse(exchange, responseBody, 200);
		}

		private String getEndpoint(String requestPath, String requestMethod) {
			String[] pathParts = requestPath.split("/");
			String endpoint = "";
			boolean hasId = false;
			String unknown = "UNKNOWN";
			try {
				Long.parseLong(pathParts[pathParts.length-1]);
				String pathWithId = "";
				for (int i = 1; i < (pathParts.length-1); i++) {
					pathWithId += "_" + pathParts[i].toUpperCase();
				}
				endpoint = requestMethod + pathWithId;
				hasId = true;
			} catch (NumberFormatException e) {
				endpoint = requestMethod + requestPath.replaceAll("/", "_").toUpperCase();
			}
			
			if (pathParts.length == 3) {
				if (requestMethod.equals("GET")) {
					if (pathParts[2].equals("all")|
						pathParts[2].equals("priority")|
						pathParts[2].equals("history")){
						return endpoint;
					}
				} else if (requestMethod.equals("DELETE")) {
					return endpoint;
				}
			} else if (pathParts.length == 4) {
				if (requestMethod.equals("GET")) {
					if (pathParts[2].equals("endtime")) {
						if (hasId) {
							return endpoint;
						} 
					} 
				} 
			}
			return unknown;
		}
		
		private void writeResponse(HttpExchange exchange,
								   String responseString,
								   int responseCode) throws IOException {
				if(responseString.isBlank()) {
					exchange.sendResponseHeaders(responseCode, 0);
				} else {
					byte[] bytes = responseString.getBytes(DEFAULT_CHARSET);
					exchange.sendResponseHeaders(responseCode, bytes.length);
					try (OutputStream os = exchange.getResponseBody()) {
						os.write(bytes);
					}
				}
				exchange.close();
		}
	}
	
	public class CommonTaskHandler implements HttpHandler{

		@Override
		public void handle(HttpExchange exchange) throws IOException {
			
			String endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());
	    	
			switch(endpoint) {
				case "GET_TASKS_COMMON_ALL":{
					handleGetAllCommonTasks(exchange);
					break;
				}
				case "DELETE_TASKS_COMMON_ALL":{
					handleDeleteAllCommonTasks(exchange);
					break;
				}
				case "GET_TASKS_COMMON":{
					handleGetCommonTask(exchange);
				break;
				}
				case "POST_TASKS_COMMON_EDIT":{
					hadnleEditCommonTask(exchange);
					break;
				}
				case "DELETE_TASKS_COMMON":{
					handleDeleteCommonTask(exchange);
					break;
				}
				case "POST_TASKS_COMMON":{
					handleCreateCommonTask(exchange);
					break;
				}
				case "UNKNOWN":{
					writeResponse(exchange, "Такого эндпоинта не существует", 404);
				}
			}
		}
		
		private void handleCreateCommonTask(HttpExchange exchange) throws IOException {
			String body = IOUtils.toString(exchange.getRequestBody(), "utf-8");
			ObjectMapper mapper = new ObjectMapper();
		    mapper.registerModule(new JavaTimeModule());
		    TypeReference<ArrayList<Task>> typeRef = new TypeReference<ArrayList<Task>>(){};
            ArrayList<Task> deserialisedBody = mapper.readValue(body,typeRef);
            Long id = manager.createCommonTask((CommonTask) deserialisedBody.get(0));
            writeResponse(exchange, String.format("Задача id %s создана.", id), 201);
			
		}

		private void handleDeleteCommonTask(HttpExchange exchange) throws IOException {
			String[] pathParts = exchange.getRequestURI().getPath().split("/");
			Long id = Long.parseLong(pathParts[3]);
			manager.removeCommonTask(id);
			writeResponse(exchange, String.format("Задача id %s удалена.", id), 200);
			
		}

		private void hadnleEditCommonTask(HttpExchange exchange) throws IOException {
			String body = IOUtils.toString(exchange.getRequestBody(), "utf-8");
			ArrayList<Task> deserialisedBody = new ArrayList<>();
			ObjectMapper mapper = new ObjectMapper();
		    mapper.registerModule(new JavaTimeModule());
		    TypeReference<ArrayList<Task>> typeRef = new TypeReference<ArrayList<Task>>(){};
			try {
				deserialisedBody = mapper.readValue(body,typeRef);
			} catch (JsonParseException e) {
				writeResponse(exchange, String.format("Передан неверный формат данных"), 503);
			}
            Long id = deserialisedBody.get(0).getId();
            if (id == null) {
            	writeResponse(exchange, String.format("Задача id %s не найдена.", id), 400);
            }
            manager.editCommonTask(id, (CommonTask) deserialisedBody.get(0));
            writeResponse(exchange, String.format("Задача id %s изменена.", id), 201);
			
		}

		private void handleGetCommonTask(HttpExchange exchange) throws IOException {
			String[] pathParts = exchange.getRequestURI().getPath().split("/");
			Long id = Long.parseLong(pathParts[3]);
			ArrayList<Task> tasks = new ArrayList<>();
			CommonTask task = manager.getCommonTask(id);
			if (task.getId() == null) {
				writeResponse(exchange, String.format("Задача id %s не найдена.", id), 400);
			}
			tasks.add(task);
			ObjectMapper mapper = new ObjectMapper();
		    mapper.registerModule(new JavaTimeModule());
			String responseBody = mapper.
	        		writerWithDefaultPrettyPrinter().
	        		writeValueAsString(tasks);
			writeResponse(exchange, responseBody, 200);
			
		}

		private void handleDeleteAllCommonTasks(HttpExchange exchange) throws IOException {
			manager.clearCommonTasks();
			writeResponse(exchange, String.format("Все обычные задачи удалены."), 200);
		}

		private void handleGetAllCommonTasks(HttpExchange exchange) throws IOException {
			ArrayList<Task> tasks = new ArrayList<>();
			tasks = manager.getAllCommonTasks();
			ObjectMapper mapper = new ObjectMapper();
		    mapper.registerModule(new JavaTimeModule());
			String responseBody = mapper.
	        		writerWithDefaultPrettyPrinter().
	        		writeValueAsString(tasks);
			writeResponse(exchange, responseBody, 200);
			
		}
		
		private String getEndpoint(String requestPath, String requestMethod) {
			String[] pathParts = requestPath.split("/");
			String endpoint = "";
			boolean hasId = false;
			String unknown = "UNKNOWN";
			try {
				Long.parseLong(pathParts[pathParts.length-1]);
				String pathWithId = "";
				for (int i = 1; i < (pathParts.length-1); i++) {
					pathWithId += "_" + pathParts[i].toUpperCase();
				}
				endpoint = requestMethod + pathWithId;
				hasId = true;
			} catch (NumberFormatException e) {
				endpoint = requestMethod + requestPath.replaceAll("/", "_").toUpperCase();
			}
			
			if (pathParts.length == 3) {
				if (requestMethod.equals("POST")){
					if (pathParts[2].equals("common")) {
								return endpoint;		
					}
				}
			} else if (pathParts.length == 4) {
				if (requestMethod.equals("GET")) {
					if (pathParts[2].equals("common")) {
						if (pathParts[3].equals("all")) {
							return endpoint;
						} else if (hasId) {
							return endpoint;
						}
					} 
				} else if (requestMethod.equals("DELETE")) {
					if (pathParts[2].equals("common")) {
							if (pathParts[3].equals("all")) {
								return endpoint;
							} else if (hasId) {
								return endpoint;
							}
					} 
				} 
			} else if (pathParts.length == 5) {
				if (requestMethod.equals("POST")) {
					if (pathParts[2].equals("common")) {
							if (pathParts[3].equals("edit")) {
								return endpoint;
							}
					}
				} 
			}
			return unknown;
		}
		
		private void writeResponse(HttpExchange exchange,
				   String responseString,
				   int responseCode) throws IOException {
			if(responseString.isBlank()) {
				exchange.sendResponseHeaders(responseCode, 0);
			} else {
				byte[] bytes = responseString.getBytes(DEFAULT_CHARSET);
				exchange.sendResponseHeaders(responseCode, bytes.length);
				try (OutputStream os = exchange.getResponseBody()) {
					os.write(bytes);
				}
			}
			exchange.close();
		}
		
	}
	
	public class EpicTaskHandler implements HttpHandler{

		@Override
		public void handle(HttpExchange exchange) throws IOException {
			String endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());
	    	
			switch(endpoint) {
				case "GET_TASKS_EPIC_ALL":{
					handleGetAllEpicTasks(exchange);
					break;
				}
				case "DELETE_TASKS_EPIC_ALL":{
					handleDeleteAllEpicTasks(exchange);
					break;
				}
				case "GET_TASKS_EPIC":{
					handleGetEpicTask(exchange);
					break;
				}
				case "POST_TASKS_EPIC_EDIT":{
					handleEditEpicTask(exchange);
					break;
				}
				case "GET_TASKS_EPIC_SUBS":{
					handleGetEpicSubs(exchange);
					break;
				}
				case "DELETE_TASKS_EPIC":{
					handleDeleteEpicTask(exchange);
					break;
				}
				case "POST_TASKS_EPIC":{
					handleCreateEpicTask(exchange);
					break;
				}
				case "UNKNOWN":{
					writeResponse(exchange, "Такого эндпоинта не существует", 404);
				}
			}
		}
		
		private void handleCreateEpicTask(HttpExchange exchange) throws IOException {
			String body = IOUtils.toString(exchange.getRequestBody(), "utf-8");
			ObjectMapper mapper = new ObjectMapper();
		    mapper.registerModule(new JavaTimeModule());
		    TypeReference<ArrayList<Task>> typeRef = new TypeReference<ArrayList<Task>>(){};
            ArrayList<Task> deserialisedBody = mapper.readValue(body,typeRef);
            Long id = manager.createEpicTask((EpicTask) deserialisedBody.get(0));
            writeResponse(exchange, String.format("Задача id %s создана.", id), 201);
		}

		private void handleDeleteEpicTask(HttpExchange exchange) throws IOException {
			String[] pathParts = exchange.getRequestURI().getPath().split("/");
			Long id = Long.parseLong(pathParts[3]);
			manager.removeEpicTask(id);
			writeResponse(exchange, String.format("Задача id %s удалена.", id), 200);
			
		}

		private void handleGetEpicSubs(HttpExchange exchange) throws IOException {
			String[] pathParts = exchange.getRequestURI().getPath().split("/");
			Long id = Long.parseLong(pathParts[4]);
			ArrayList<Long> subs = new ArrayList<>();
			EpicTask task = manager.getEpicTask(id);
			if (task.getId() == null) {
				writeResponse(exchange, String.format("Задача id %s не найдена.", id), 400);
			}
			subs = manager.getEpicSubTasks(id);
			String responseBody = subs.toString();
			writeResponse(exchange, responseBody, 200);
		}

		private void handleEditEpicTask(HttpExchange exchange) throws IOException {
			String body = IOUtils.toString(exchange.getRequestBody(), "utf-8");
			ArrayList<Task> deserialisedBody = new ArrayList<>();
			ObjectMapper mapper = new ObjectMapper();
		    mapper.registerModule(new JavaTimeModule());
		    TypeReference<ArrayList<Task>> typeRef = new TypeReference<ArrayList<Task>>(){};
			try {
				deserialisedBody = mapper.readValue(body,typeRef);
			} catch (JsonParseException e) {
				writeResponse(exchange, String.format("Передан неверный формат данных"), 503);
			}
            Long id = deserialisedBody.get(0).getId();
            if (id == null) {
            	writeResponse(exchange, String.format("Задача id %s не найдена.", id), 400);
            }
            manager.editEpicTask(id, (EpicTask) deserialisedBody.get(0));
            writeResponse(exchange, String.format("Задача id %s изменена.", id), 201);
		}

		private void handleGetEpicTask(HttpExchange exchange) throws IOException {
			String[] pathParts = exchange.getRequestURI().getPath().split("/");
			Long id = Long.parseLong(pathParts[3]);
			ArrayList<Task> tasks = new ArrayList<>();
			EpicTask task = manager.getEpicTask(id);
			if (task.getId() == null) {
				writeResponse(exchange, String.format("Задача id %s не найдена.", id), 400);
			}
			tasks.add(task);
			ObjectMapper mapper = new ObjectMapper();
		    mapper.registerModule(new JavaTimeModule());
			String responseBody = mapper.
	        		writerWithDefaultPrettyPrinter().
	        		writeValueAsString(tasks);
			writeResponse(exchange, responseBody, 200);
			
		}

		private void handleDeleteAllEpicTasks(HttpExchange exchange) throws IOException {
			manager.clearEpicTasks();
			writeResponse(exchange, String.format("Все эпики удалены."), 200);
		}

		private void handleGetAllEpicTasks(HttpExchange exchange) throws IOException {
			ArrayList<Task> tasks = new ArrayList<>();
			tasks = manager.getAllEpicTasks();
			ObjectMapper mapper = new ObjectMapper();
		    mapper.registerModule(new JavaTimeModule());
			String responseBody = mapper.
	        		writerWithDefaultPrettyPrinter().
	        		writeValueAsString(tasks);
			writeResponse(exchange, responseBody, 200);
			
		}

		private String getEndpoint(String requestPath, String requestMethod) {
			String[] pathParts = requestPath.split("/");
			String endpoint = "";
			boolean hasId = false;
			String unknown = "UNKNOWN";
			try {
				Long.parseLong(pathParts[pathParts.length-1]);
				String pathWithId = "";
				for (int i = 1; i < (pathParts.length-1); i++) {
					pathWithId += "_" + pathParts[i].toUpperCase();
				}
				endpoint = requestMethod + pathWithId;
				hasId = true;
			} catch (NumberFormatException e) {
				endpoint = requestMethod + requestPath.replaceAll("/", "_").toUpperCase();
			}
			
			if (pathParts.length == 3) {
				if (requestMethod.equals("POST")){
					if (pathParts[2].equals("epic")) {
								return endpoint;
					}
				}
			} else if (pathParts.length == 4) {
				if (requestMethod.equals("GET")) {
					if (pathParts[2].equals("epic")) {
						if (pathParts[3].equals("all")) {
							return endpoint;
						} else if (hasId) {
							return endpoint;
						}
					} 
				} else if (requestMethod.equals("DELETE")) {
					if (pathParts[2].equals("epic")) {
							if (pathParts[3].equals("all")) {
								return endpoint;
							} else if (hasId) {
								return endpoint;
							}
					} 
				} 
			} else if (pathParts.length == 5) {
				if (requestMethod.equals("POST")) {
					if (pathParts[2].equals("epic")) {
							if (pathParts[3].equals("edit")) {
								return endpoint;
							}
					}
				} else if (requestMethod.equals("GET")) {
					if (pathParts[2].equals("epic")&&pathParts[3].equals("subs")) {
						return endpoint;
					}
				}
			}
			return unknown;
		}
		
		private void writeResponse(HttpExchange exchange,
				   String responseString,
				   int responseCode) throws IOException {
			if(responseString.isBlank()) {
				exchange.sendResponseHeaders(responseCode, 0);
			} else {
				byte[] bytes = responseString.getBytes(DEFAULT_CHARSET);
				exchange.sendResponseHeaders(responseCode, bytes.length);
				try (OutputStream os = exchange.getResponseBody()) {
					os.write(bytes);
				}
			}
			exchange.close();
		}
		
	}
	
	public class SubTaskHandler implements HttpHandler{

		@Override
		public void handle(HttpExchange exchange) throws IOException {
			
			String endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());
    	
			switch(endpoint) {
				case "GET_TASKS_SUB_ALL":{
					handleGetAllSubTasks(exchange);
					break;
				}
				case "DELETE_TASKS_SUB_ALL":{
					handleDeleteAllSubTasks(exchange);
					break;
				}
				case "GET_TASKS_SUB":{
					handleGetSubTask(exchange);
					break;
				}
				case "POST_TASKS_SUB_EDIT":{
					handleEditSubTask(exchange);
					break;
				}
				case "DELETE_TASKS_SUB":{
					handleDeleteSubTask(exchange);
					break;
				}
				case "POST_TASKS_SUB":{
					handleCreateSubTask(exchange);
					break;
				}
				case "UNKNOWN":{
					writeResponse(exchange, "Такого эндпоинта не существует", 404);
				}
			}
		}
		
		private void handleCreateSubTask(HttpExchange exchange) throws IOException {
			String body = IOUtils.toString(exchange.getRequestBody(), "utf-8");
			ObjectMapper mapper = new ObjectMapper();
		    mapper.registerModule(new JavaTimeModule());
		    TypeReference<ArrayList<Task>> typeRef = new TypeReference<ArrayList<Task>>(){};
            ArrayList<Task> deserialisedBody = mapper.readValue(body,typeRef);
            Long id = manager.createSubTask((SubTask) deserialisedBody.get(0));
            writeResponse(exchange, String.format("Задача id %s создана.", id), 201);
		}

		private void handleDeleteSubTask(HttpExchange exchange) throws IOException {
			String[] pathParts = exchange.getRequestURI().getPath().split("/");
			Long id = Long.parseLong(pathParts[3]);
			manager.removeSubTask(id);
			writeResponse(exchange, String.format("Задача id %s удалена.", id), 200);
		}

		private void handleEditSubTask(HttpExchange exchange) throws IOException {
			String body = IOUtils.toString(exchange.getRequestBody(), "utf-8");
			ArrayList<Task> deserialisedBody = new ArrayList<>();
			ObjectMapper mapper = new ObjectMapper();
		    mapper.registerModule(new JavaTimeModule());
		    TypeReference<ArrayList<Task>> typeRef = new TypeReference<ArrayList<Task>>(){};
			try {
				deserialisedBody = mapper.readValue(body,typeRef);
			} catch (JsonParseException e) {
				writeResponse(exchange, String.format("Передан неверный формат данных"), 503);
			}
            Long id = deserialisedBody.get(0).getId();
            if (id == null) {
            	writeResponse(exchange, String.format("Задача id %s не найдена.", id), 400);
            }
            manager.editSubTask(id, (SubTask) deserialisedBody.get(0));
            writeResponse(exchange, String.format("Задача id %s изменена.", id), 201);
		}

		private void handleGetSubTask(HttpExchange exchange) throws IOException {
			String[] pathParts = exchange.getRequestURI().getPath().split("/");
			Long id = Long.parseLong(pathParts[3]);
			ArrayList<Task> tasks = new ArrayList<>();
			SubTask task = manager.getSubTask(id);
			if (task.getId() == null) {
				writeResponse(exchange, String.format("Задача id %s не найдена.", id), 400);
			}
			tasks.add(task);
			ObjectMapper mapper = new ObjectMapper();
		    mapper.registerModule(new JavaTimeModule());
			String responseBody = mapper.
	        		writerWithDefaultPrettyPrinter().
	        		writeValueAsString(tasks);
			writeResponse(exchange, responseBody, 200);
		}

		private void handleDeleteAllSubTasks(HttpExchange exchange) throws IOException {
			manager.clearSubTasks();
			writeResponse(exchange, String.format("Все подзадачи удалены."), 200);
		}

		private void handleGetAllSubTasks(HttpExchange exchange) throws IOException {
			ArrayList<Task> tasks = new ArrayList<>();
			tasks = manager.getAllSubTasks();
			ObjectMapper mapper = new ObjectMapper();
		    mapper.registerModule(new JavaTimeModule());
			String responseBody = mapper.
	        		writerWithDefaultPrettyPrinter().
	        		writeValueAsString(tasks);
			writeResponse(exchange, responseBody, 200);
		}

		private String getEndpoint(String requestPath, String requestMethod) {
			String[] pathParts = requestPath.split("/");
			String endpoint = "";
			boolean hasId = false;
			String unknown = "UNKNOWN";
			try {
				Long.parseLong(pathParts[pathParts.length-1]);
				String pathWithId = "";
				for (int i = 1; i < (pathParts.length-1); i++) {
					pathWithId += "_" + pathParts[i].toUpperCase();
				}
				endpoint = requestMethod + pathWithId;
				hasId = true;
			} catch (NumberFormatException e) {
				endpoint = requestMethod + requestPath.replaceAll("/", "_").toUpperCase();
			}
			
			if (pathParts.length == 3) {
				if (requestMethod.equals("POST")){
					if (pathParts[2].equals("sub")) {
								return endpoint;
					}
				}
			} else if (pathParts.length == 4) {
				if (requestMethod.equals("GET")) {
					if (pathParts[2].equals("sub")) {
						if (pathParts[3].equals("all")) {
							return endpoint;
						} else if (hasId) {
							return endpoint;
						}
					} 
				} else if (requestMethod.equals("DELETE")) {
					if (pathParts[2].equals("sub")) {
							if (pathParts[3].equals("all")) {
								return endpoint;
							} else if (hasId) {
								return endpoint;
							}
					} 
				} 
			} else if (pathParts.length == 5) {
				if (requestMethod.equals("POST")) {
					if (pathParts[2].equals("sub")) {
							if (pathParts[3].equals("edit")) {
								return endpoint;
							}
					}
				} 
			}
			return unknown;
		}
		
		private void writeResponse(HttpExchange exchange,
				   String responseString,
				   int responseCode) throws IOException {
			if(responseString.isBlank()) {
				exchange.sendResponseHeaders(responseCode, 0);
			} else {
				byte[] bytes = responseString.getBytes(DEFAULT_CHARSET);
				exchange.sendResponseHeaders(responseCode, bytes.length);
				try (OutputStream os = exchange.getResponseBody()) {
					os.write(bytes);
				}
			}
			exchange.close();
		}
	}
}