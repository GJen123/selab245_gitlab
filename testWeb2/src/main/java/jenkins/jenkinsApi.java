package jenkins;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.client.JenkinsHttpClient;

public class jenkinsApi{
	
	URL url;
	URI uri;
	JenkinsServer jenkins;
	JenkinsHttpClient client;
	public jenkinsApi(){
		try {
			url = new URL("http://140.134.26.71:38080");
			uri = url.toURI();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		client = new JenkinsHttpClient( uri, "GJen", "zxcv1234");
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
			jenkins.createJob(jobName, jobXml, true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}