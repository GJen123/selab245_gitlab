package conn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import data.GitlabData;

public class EnterCheck{
	GitlabData gitData = new GitlabData();
	
	public String httpPost(String username, String password){
		String result = null;
		String response = null; 
		StringBuilder sb = new StringBuilder();
		String url = gitData.getHostUrl() + "/oauth/token";
		try {
			URL object=new URL(url);
	
			HttpURLConnection con = (HttpURLConnection) object.openConnection();
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Accept", "application/json");
			con.setRequestMethod("POST");
	
			JSONObject jsonObj   = new JSONObject();

			jsonObj.put("grant_type", "password");
			jsonObj.put("username", username);
			jsonObj.put("password", password);
	
			OutputStreamWriter wr;
			
				wr = new OutputStreamWriter(con.getOutputStream());
				wr.write(jsonObj.toString());
				wr.flush();
	
				//display what returns the POST request
	
				  
				int HttpResult = con.getResponseCode(); 
				if (HttpResult == HttpURLConnection.HTTP_OK) {
				    BufferedReader br = new BufferedReader(
				            new InputStreamReader(con.getInputStream(), "utf-8"));
				     
				    while ((response = br.readLine()) != null) {  
				        sb.append(response + "\n");  
				        result = sb.toString();
				    }
				    br.close(); 
				} else {
					result = con.getResponseMessage();
				    System.out.println(con.getResponseMessage());  
				}  
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
		
	}
	
	public String analysisJSON(String str){
		String access_token = null;
		if(!str.equals("")){
			JSONObject json = new JSONObject(str);
			if(json.has("access_token")){
				access_token = json.getString("access_token");
			}else{
				access_token = null;
			}
		}else{
			System.out.println("str == null");
		}
		
			
		return access_token;
	}
	
}