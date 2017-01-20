package conn;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

public class httpConnect {
	public int httpGetProjectEvent(String strUrl){
		HttpURLConnection conn = null;
		int total_count=0;
        try {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            // 建立連線
            
            URL url = new URL(strUrl);
            conn = (HttpURLConnection) url.openConnection();
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
            
            JSONArray ja = new JSONArray(jsonString1);
            int JSONArrayLength = ja.length();
            
            
            for(int i=0;i<JSONArrayLength;i++){
            	JSONObject oj = ja.getJSONObject(i);
            	JSONObject ojData = oj.getJSONObject("data");
            	int total_commit_count = ojData.getInt("total_commits_count");
            	if(total_commit_count==1){
            		total_count++;
            	}
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
        return total_count;
	}

	public List<String> httpGetStudentOwnedProjectName(String private_token){
		HttpURLConnection conn = null;
		List<String> projects_Name = new ArrayList<String>();
        try {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            // 建立連線
            String strurl = "http://140.134.26.71:20080/api/v3/projects/owned?private_token="+private_token;
            URL url = new URL(strurl);
            conn = (HttpURLConnection) url.openConnection();
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

            JSONArray ja = new JSONArray(jsonString1);
            int JSONArrayLength = ja.length();
            for(int i=0;i<JSONArrayLength;i++){
            	JSONObject jsonObject = ja.getJSONObject(i);
            	String name = jsonObject.getString("name");
            	projects_Name.add(name);
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
        return projects_Name;
	}

	public List<String> httpGetStudentOwnedProjectUrl(String private_token){
		HttpURLConnection conn = null;
		List<String> projects_Url = new ArrayList<String>();
        try {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            // 建立連線
            String strurl = "http://140.134.26.71:20080/api/v3/projects/owned?private_token="+private_token;
            URL url = new URL(strurl);
            conn = (HttpURLConnection) url.openConnection();
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

            JSONArray ja = new JSONArray(jsonString1);
            int JSONArrayLength = ja.length();
            for(int i=0;i<JSONArrayLength;i++){
            	JSONObject jsonObject = ja.getJSONObject(i);
            	String web_url = jsonObject.getString("web_url");
            	web_url = web_url.replace("http://0912fe2b3e43", "http://140.134.26.71:20080");
            	projects_Url.add(web_url);
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
        return projects_Url;
	}
	
	public List<Integer> httpGetStudentOwnedProjectId(String private_token){
		HttpURLConnection conn = null;
		List<Integer> projects_Id = new ArrayList<Integer>();
        try {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            // 建立連線
            String strurl = "http://140.134.26.71:20080/api/v3/projects/owned?private_token="+private_token;
            URL url = new URL(strurl);
            conn = (HttpURLConnection) url.openConnection();
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

            JSONArray ja = new JSONArray(jsonString1);
            int JSONArrayLength = ja.length();
            for(int i=0;i<JSONArrayLength;i++){
            	JSONObject jsonObject = ja.getJSONObject(i);
            	int id = jsonObject.getInt("id");
            	projects_Id.add(id);
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
        return projects_Id;
	}
	
	public void httpPostReadme(String url, String content){
		String file_path = "README.md";
		String branch_name = "master";
		String author_email = "admin@example.com";
		String author_name = "root";
		String commit_message = "README";
		HttpClient client = new DefaultHttpClient();
        try {
            HttpPost post = new HttpPost(url);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add((NameValuePair) new BasicNameValuePair("file_path",file_path));
            params.add((NameValuePair) new BasicNameValuePair("branch_name",branch_name));
            params.add((NameValuePair) new BasicNameValuePair("author_email",author_email));
            params.add((NameValuePair) new BasicNameValuePair("author_name",author_name));
            params.add((NameValuePair) new BasicNameValuePair("content",content));
            params.add((NameValuePair) new BasicNameValuePair("commit_message",commit_message));
            
            UrlEncodedFormEntity ent = null;
            ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
            post.setEntity(ent);

            HttpResponse responsePOST = client.execute(post);
            HttpEntity resEntity = responsePOST.getEntity();

            if(resEntity != null){
                System.out.println("Success");
            }else{

            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
}
