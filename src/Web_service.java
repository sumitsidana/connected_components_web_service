
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
public class Web_service {


	public Map<Integer, Boolean> BFS(Map<String,List<String>> graph, int start, Map<Integer,Boolean>visited, Queue<Integer>queue){
		visited.put(start, true);
		while(!(queue.isEmpty())){
			int v = queue.poll();
			List<String>adjacentNodes = graph.get(v+"");
			for(String node: adjacentNodes){
				int adjacentNode = Integer.parseInt(node);
				if(!(visited.get(adjacentNode))){
					visited.put(adjacentNode, true);
					queue.add(adjacentNode);
				}
			}

		}
		return visited;
	}
	public static void main(String[] args) throws Exception {
		HttpServer server = HttpServer.create(new InetSocketAddress(5000), 0);
		server.createContext("/connected_components", new MyHandler());
		server.setExecutor(null); // creates a default executor
		server.start();
	}

	public static void printHeaders( HttpExchange exchange, PrintStream response) {
		Headers requestHeaders = exchange.getRequestHeaders();
		Set<String> keySet = requestHeaders.keySet();
		Iterator<String> iter = keySet.iterator();
		while( iter.hasNext()) {
			String key = iter.next();
			response.println( key + " = " + requestHeaders.get(key));
		}
	}
	public static void printBody( HttpExchange exchange, PrintStream response) throws IOException {
		BufferedReader body = new BufferedReader( new InputStreamReader( exchange.getRequestBody()));
		String bodyLine;
		while( (bodyLine = body.readLine()) != null) {
			response.println( bodyLine);
		}
	}

	static class MyHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange t) throws IOException {

			String requestMethod = t.getRequestMethod();

			if( requestMethod.equalsIgnoreCase( "POST")) {
				BufferedReader body = new BufferedReader( new InputStreamReader(t.getRequestBody()));
				String bodyLine;
				while( (bodyLine = body.readLine()) != null) {
					JSONParser parser = new JSONParser();
					try {
						JSONObject json = (JSONObject) parser.parse(bodyLine);
						//						System.out.println(json.keySet());
						int size = json.size();
						Map<Integer,Boolean>visited = new LinkedHashMap<Integer,Boolean>();

						for(Iterator iterator = json.keySet().iterator(); iterator.hasNext();) {
							String key = (String) iterator.next();
							visited.put(Integer.parseInt(key), false);
						}

						int num_connected_components = 0;

						for(Iterator iterator = json.keySet().iterator(); iterator.hasNext();){
							String key = (String) iterator.next();
							if(!(visited.get(Integer.parseInt(key)))){
								num_connected_components++;
								Queue<Integer>queue = new LinkedList<Integer>(); 
								queue.add(Integer.parseInt(key));
								visited = new Web_service().BFS(json,Integer.parseInt(key),visited,queue);
							}
						}
						System.out.println(num_connected_components);
			            OutputStream os = t.getResponseBody();
			            os.write(num_connected_components);
			            os.close();


					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
}
