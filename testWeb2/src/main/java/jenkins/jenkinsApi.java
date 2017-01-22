package jenkins;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import com.offbytwo.jenkins.JenkinsServer;

public class jenkinsApi{
	
	URL url;
	URI uri;
	JenkinsServer jenkins;
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
		jenkins = new JenkinsServer( uri, "GJen", "zxcv1234");
	}
	
	public void createJob(String jobName, String jobXml){
		try {
			jenkins.createJob(jobName, jobXml);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}