package kanban.api;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;

public class KVTaskClient {
	
	private final String API_TOKEN;
	private String url; 
	private HttpClient client;
	
	public KVTaskClient(String url) throws IOException, Exception {
		this.url = url;
		URI uri = URI.create(url+"/register");
		HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
		client = HttpClient.newHttpClient();
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		API_TOKEN = response.body();
		this.url = url;
	}
	
	public void put(String key,String json) throws IOException, InterruptedException {
		URI uri = URI.create(url+"/save/" + key + "?API_TOKEN=" + API_TOKEN);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(BodyPublishers.ofString(json))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
	}
	
	public String load(String key) throws IOException, InterruptedException {
		String result = "";
		URI uri = URI.create(url+"/load/" + key + "?API_TOKEN=" + API_TOKEN);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		return result += response.body();
	}
}
