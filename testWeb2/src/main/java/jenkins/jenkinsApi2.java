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

import sun.misc.BASE64Encoder;

public class jenkinsApi2{
	
	
	public ArrayList<HashMap<String,String>> getJobJson(String username, String password, String strUrl, String jobName){
		HttpURLConnection conn = null;
		ArrayList<HashMap<String,String>> data = new ArrayList<HashMap<String,String>>();
        try {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            // 建立連線
            
            URL url = new URL(strUrl);
            conn = (HttpURLConnection) url.openConnection();
            String input = username + ":" + password;
        	String encoding = new sun.misc.BASE64Encoder().encode(input.getBytes());
            conn.setRequestProperty("Authorization", "Basic " + encoding);
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.connect();
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            // 讀取資料
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    conn.getInputStream(), "UTF-8"));
            String jsonString1 = reader.readLine();
            reader.close();
            
            JSONObject j1 = new JSONObject(jsonString1);
            JSONArray ja = j1.getJSONArray("jobs");
            
            int JSONArrayLength = ja.length();

            for(int i=0;i<JSONArrayLength;i++){
            	JSONObject oj = ja.getJSONObject(i);
            	String name = oj.getString("name");
            	String color = oj.getString("color");
            	HashMap<String,String> hm = new HashMap<String,String>();
            	hm.put(name, color);
            	data.add(hm);
            }
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
		return data;
	}
	
}