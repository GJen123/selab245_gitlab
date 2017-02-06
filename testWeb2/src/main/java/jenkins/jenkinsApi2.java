package jenkins;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class jenkinsApi2{
	public List<String> listJobs(String url) {
		Client client = Client.create();
//		client.addFilter(new com.sun.jersey.api.client.filter.HTTPBasicAuthFilter(USERNAME, PASSWORD));
		WebResource webResource = client.resource(url+"/api/xml");
		ClientResponse response = webResource.get(ClientResponse.class);
		String jsonResponse = response.getEntity(String.class);
		client.destroy();
//		System.out.println("Response listJobs:::::"+jsonResponse);
		
		// Assume jobs returned are in xml format, TODO using an XML Parser would be better here
		// Get name from <job><name>...
		List<String> jobList = new ArrayList<String>();
		String[] jobs = jsonResponse.split("job>"); // 1, 3, 5, 7, etc will contain jobs
		for(String job: jobs){
			String[] names = job.split("name>");
			if(names.length == 3) {
				String name = names[1];
				name = name.substring(0,name.length()-2); // Take off </ for the closing name tag: </name>
				jobList.add(name);
//				System.out.println("name:"+name);
			}
//			System.out.println("job:"+job);
//			for(String name: names){
//				System.out.println("name:"+name);
//			}
		}
		return jobList;
	}

	public static String deleteJob(String url, String jobName) {
		Client client = Client.create();
//		client.addFilter(new com.sun.jersey.api.client.filter.HTTPBasicAuthFilter(USERNAME, PASSWORD));
		WebResource webResource = client.resource(url+"/job/"+jobName+"/doDelete");
		ClientResponse response = webResource.post(ClientResponse.class);
		String jsonResponse = response.getEntity(String.class);
		client.destroy();
//		System.out.println("Response deleteJobs:::::"+jsonResponse);
		return jsonResponse;
	}
	
	public static String copyJob(String url, String newJobName, String oldJobName){
		Client client = Client.create();
//		client.addFilter(new com.sun.jersey.api.client.filter.HTTPBasicAuthFilter(USERNAME, PASSWORD));
		WebResource webResource = client.resource(url+"/createItem?name="+newJobName+"&mode=copy&from="+oldJobName);
		ClientResponse response = webResource.type("application/xml").get(ClientResponse.class);
		String jsonResponse = response.getEntity(String.class);
		client.destroy();
//		System.out.println("Response copyJob:::::"+jsonResponse);
		return jsonResponse;
	}
	
	public static String createJob(String url, String newJobName, String configXML){
		Client client = Client.create();
//		client.addFilter(new com.sun.jersey.api.client.filter.HTTPBasicAuthFilter(USERNAME, PASSWORD));
		WebResource webResource = client.resource(url+"/createItem?name="+newJobName);
		ClientResponse response = webResource.type("application/xml").post(ClientResponse.class, configXML);
		String jsonResponse = response.getEntity(String.class);
		client.destroy();
		System.out.println("Response createJob:::::"+jsonResponse);
		return jsonResponse;
	}

	public static String readJob(String url, String jobName){
		Client client = Client.create();
//		client.addFilter(new com.sun.jersey.api.client.filter.HTTPBasicAuthFilter(USERNAME, PASSWORD));
		WebResource webResource = client.resource(url+"/job/"+jobName+"/config.xml");
		ClientResponse response = webResource.get(ClientResponse.class);
		String jsonResponse = response.getEntity(String.class);
		client.destroy();
//		System.out.println("Response readJob:::::"+jsonResponse);
		return jsonResponse;
	}
	
	public ArrayList<HashMap<String,String>> getJobJson(String strUrl, String jobName){
		HttpURLConnection conn = null;
		ArrayList<HashMap<String,String>> data = new ArrayList<HashMap<String,String>>();
		System.out.println("gogogo");
        try {
            if (Thread.interrupted()) {
            	System.out.println("excep");
                throw new InterruptedException();
            }
            // 建立連線
            
            URL url = new URL(strUrl);
            conn = (HttpURLConnection) url.openConnection();
            String username = "admin";
            String password = "iecsfcu123456";
            String input = username + ":" + password;
        	String encoding = new sun.misc.BASE64Encoder().encode(input.getBytes());
            conn.setRequestProperty("Authorization", "Basic " + encoding);
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.connect();
            System.out.println("abcabc");
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            // 讀取資料
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    conn.getInputStream(), "UTF-8"));
            String jsonString1 = reader.readLine();
            reader.close();
            
            JSONObject j1 = new JSONObject(jsonString1);
            System.out.println("j1 : "+j1);
            JSONArray ja = j1.getJSONArray("jobs");
            System.out.println("ja : "+ja.toString());
            int JSONArrayLength = ja.length();
            
            
            for(int i=0;i<JSONArrayLength;i++){
            	JSONObject oj = ja.getJSONObject(i);
            	String name = oj.getString("name");
            	System.out.println("name : "+name);
            	String color = oj.getString("color");
            	System.out.println("color : "+color);
            	HashMap<String,String> hm = new HashMap<String,String>();
            	hm.put(name, color);
            	data.add(hm);
            }
        }
        catch (Exception e) {
            System.out.println("abc : "+e.toString());
        }
        finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
		return data;
	}
	
}