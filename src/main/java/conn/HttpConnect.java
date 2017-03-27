package conn;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
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
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

public class HttpConnect {
	
	public void httpPostReadme(String url, String content){
		String file_path = "README.md";
		String branch_name = "master";
		String encoding = "test";
		String commit_message = "README";
		HttpClient client = new DefaultHttpClient();
        try {
            HttpPost post = new HttpPost(url);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add((NameValuePair) new BasicNameValuePair("file_path",file_path));
            params.add((NameValuePair) new BasicNameValuePair("branch_name",branch_name));
            params.add((NameValuePair) new BasicNameValuePair("encoding",encoding));
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
	
	public void httpPutReadme(String url, String content){
		String file_path = "README.md";
		String branch_name = "master";
		String encoding = "test";
		String commit_message = "README";
		HttpClient client = new DefaultHttpClient();
        try {
            HttpPut post = new HttpPut(url);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add((NameValuePair) new BasicNameValuePair("file_path",file_path));
            params.add((NameValuePair) new BasicNameValuePair("branch_name",branch_name));
            params.add((NameValuePair) new BasicNameValuePair("encoding",encoding));
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
	
	public void httpPostFile(String file_path,String url, String fileContent){
		//String file_path = "src";
		String branch_name = "master";
		String encoding = "test";
		String commit_message = "HW";
		HttpClient client = new DefaultHttpClient();
        try {
            HttpPost post = new HttpPost(url);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add((NameValuePair) new BasicNameValuePair("file_path",file_path));
            params.add((NameValuePair) new BasicNameValuePair("branch_name",branch_name));
            params.add((NameValuePair) new BasicNameValuePair("encoding",encoding));
            params.add((NameValuePair) new BasicNameValuePair("content",fileContent));
            params.add((NameValuePair) new BasicNameValuePair("commit_message",commit_message));
            
            UrlEncodedFormEntity ent = null;
            ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
            post.setEntity(ent);

            HttpResponse responsePOST = client.execute(post);
            HttpEntity resEntity = responsePOST.getEntity();

            if(resEntity != null){
            	String result = resEntity.toString();
                System.out.println("Success : " + result);
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
