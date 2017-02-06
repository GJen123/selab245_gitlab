package jenkins;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
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

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.client.JenkinsHttpClient;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.JobWithDetails;

public class jenkinsApi{
	
	URL url;
	URI uri;
	JenkinsServer jenkins;
	JenkinsHttpClient client;
	public jenkinsApi(){
		try {
			url = new URL("http://140.134.26.71:28080");
			uri = url.toURI();
			System.out.println("uri : "+uri.toString());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		client = new JenkinsHttpClient( uri, "admin", "iecsfcu123456");
		jenkins = new JenkinsServer(client);
//		System.out.println("-----------jenkins---------\n");
//		System.out.println(jenkins);
//		try {
//			System.out.println(jenkins.getJob("test2").getName());
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		System.out.println("\n-------------------------\n");
	}
	
	public void createJob(String jobName, String jobXml){
		try {
			jenkins.createJob(jobName, jobXml);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public JobWithDetails getJob(String jobName){
		JobWithDetails job = null;
		try {
			job = jenkins.getJob(jobName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return job;
	}
	
	public String getJobXml(String jobName){
		String jobXml=null;
		try {
			jobXml = jenkins.getJobXml(jobName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jobXml;
	}
	
	public void postCreateJob(String jobName){
		HttpClient client = new DefaultHttpClient();
		String url = "http://GJen:02031fefb728e700973b6f3e5023a64c@140.134.26.71:38080/createItem?name="+jobName;
        try {
            HttpPost post = new HttpPost(url);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            //params.add((NameValuePair) new BasicNameValuePair("file_path",file_path));
            
            
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