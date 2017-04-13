package jenkins;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.gitlab.api.models.GitlabUser;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.client.JenkinsHttpClient;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.JobWithDetails;

import conn.Conn;
import data.GitlabData;
import data.JenkinsData;
import sun.misc.BASE64Encoder;

public class JenkinsApi{
	
	private Conn conn = Conn.getInstance();
	
	GitlabData gitData = new GitlabData();
	
	JenkinsData jenkinsData = new JenkinsData();
	
	public void createRootJob(String Pname, String jenkinsCrumb, String fileType){
		
		//---Create Jenkins Job---
		String jobName = "root_"+Pname;
		String strUrl = jenkinsData.getHostUrl() + "/createItem?name="+jobName;
		String proUrl = gitData.getHostUrl() + "/root/" + Pname + ".git";
		postCreateJob(jenkinsData.getUserName(), jenkinsData.getPassWord(), strUrl, jobName, proUrl, jenkinsCrumb, fileType);
		//------------------------
	}
	
	public void createJenkinsJob(String Pname, String jenkinsCrumb, String fileType){
		List<GitlabUser> users = conn.getUsers();
		for(GitlabUser user : users){
			if (user.getId() == 1) continue;
			//---Create Jenkins Job---
			String jobName = user.getUsername()+"_"+Pname;
			String strUrl = jenkinsData.getHostUrl() + "/createItem?name="+jobName;
			String proUrl = gitData.getHostUrl() + "/" + user.getUsername() + "/" + Pname + ".git";
			postCreateJob(jenkinsData.getUserName(), jenkinsData.getPassWord(), strUrl, jobName, proUrl, jenkinsCrumb, fileType);
			//------------------------
		}
	}
	
//  用httppost create jenkins job
	public void postCreateJob(String username, String password, String strUrl, String jobName, String proUrl, String jenkinsCrumb, String fileType){
		HttpClient client = new DefaultHttpClient();
		
		String url = jenkinsData.getHostUrl() + "/createItem?name="+jobName;
        try {
            HttpPost post = new HttpPost(url);
            
            post.addHeader("Content-Type", "application/xml");
            post.addHeader("Jenkins-Crumb", jenkinsCrumb);
//            post.addHeader("Jenkins-Crumb", "e390d46093102dac6c0ec903b77af0a0");
            String filePath = null;
            //變更config.xml裡的url
            if(fileType.equals("Maven")){
            	filePath = this.getClass().getResource("config_maven.xml").getFile();
            }else if(fileType.equals("Javac")){
            	filePath = this.getClass().getResource("config_javac.xml").getFile();
            }else{
            	filePath = this.getClass().getResource("config_maven.xml").getFile();
            }
            
            modifyXmlFileUrl(filePath, proUrl);
            
            //讀config.xml
            String typeFilePath = null;
            if(fileType.equals("Maven")){
            	typeFilePath = "config_maven.xml";
            }else if(fileType.equals("Javac")){
            	typeFilePath = "config_javac.xml";
            }else{
            	typeFilePath = "config_maven.xml";
            }
            StringBuilder sb = getConfig(typeFilePath);
            StringEntity se = new StringEntity(sb.toString(), ContentType.create("text/xml", Consts.UTF_8));
            se.setChunked(true);
            post.setEntity(se);
            
            HttpResponse responsePOST = client.execute(post);
            HttpEntity resEntity = responsePOST.getEntity();

            if(resEntity != null){
            	String result = resEntity.toString();
                System.out.println("result: " + result);
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
	
	//  用httpget 抓jenkins的crumb值
	public String getCrumb(String username, String password, String strUrl){
		String JenkinsCrumb=null;
		HttpURLConnection conn = null;
		
        try {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            // 建立連線
            strUrl += "/crumbIssuer/api/json";
            URL url = new URL(strUrl);
            conn = (HttpURLConnection) url.openConnection();
            String input = username + ":" + password;
            BASE64Encoder enc = new BASE64Encoder();
        	String encoding = enc.encode(input.getBytes());
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
            String jsonString = reader.readLine();
            reader.close();
            
            JSONObject jsonObj = new JSONObject(jsonString);
            JenkinsCrumb = jsonObj.getString("crumb");
        }
        catch (Exception e) {
            System.out.println("abc : "+e);
        }
        finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
		return JenkinsCrumb;
	}
	
	//抓config.xml 並讀出來變成stringbuilder
	public StringBuilder getConfig(String typeFilePath){
		FileInputStream fis;
		StringBuilder sb = new StringBuilder();
		String strConfig=null;
		try {
			String filePath = this.getClass().getResource(typeFilePath).getFile();
			fis = new FileInputStream(filePath);
			InputStreamReader reader= new InputStreamReader(fis, "UTF8");
	        BufferedReader buf = new BufferedReader(reader);
	        while ((strConfig = buf.readLine()) != null) {
	        	sb.append(strConfig);
	        	sb.append("\n");
	            }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
        return sb;
	}
	
	//變更config.xml file裡的Url [jenkins]
	public void modifyXmlFileUrl(String filePath, String url){
		try {
            String filepath = filePath;
            DocumentBuilderFactory docFactory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(filepath);

            Node ndUrl = doc.getElementsByTagName("url").item(0);
            ndUrl.setTextContent(url);
            
            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory
                    .newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(filepath));
            transformer.transform(source, result);

            System.out.println("Done");

        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (TransformerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}
	
	//取得jenkins job的狀態顏色
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
	
	public String getJobColor(ArrayList<HashMap<String,String>> jobJson, String userName, String proName){
		String color = null;
		int i=0;
		for (HashMap<String, String> map : jobJson){
			for(String key : map.keySet()){
				if(key.equals(userName+"_"+proName)){
					color = jobJson.get(i).get(key);
					break;
				}
				i++;
			}
			if(color!=null){
				break;
			}
		}
		return color;
	}
	
	public void buildJob(String Pname, String jenkinsCrumb){
		
		String jobName = null;
		List<GitlabUser> users = conn.getUsers();
		for(GitlabUser user : users){
			jobName = user.getUsername()+"_"+Pname;
			postBuildJob(jobName, jenkinsCrumb);
		}
	}
	
	public void postBuildJob(String jobName, String jenkinsCrumb){
		HttpClient client = new DefaultHttpClient();
		
		String url = jenkinsData.getHostUrl() + "/job/"+jobName+"/build";
        try {
            HttpPost post = new HttpPost(url);
            
            post.addHeader("Content-Type", "application/xml");
            post.addHeader("Jenkins-Crumb", jenkinsCrumb);
            
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add((NameValuePair) new BasicNameValuePair("token",jenkinsData.getApiToken()));
            
            UrlEncodedFormEntity ent = null;
            ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
            post.setEntity(ent);
            
            HttpResponse responsePOST = client.execute(post);
            HttpEntity resEntity = responsePOST.getEntity();

            if(resEntity != null){
            	String result = resEntity.toString();
                System.out.println(jobName+" : abc : "+result);
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
	
	public String getColorPic(String color){
		String colorPic = null;
		if(color.equals("blue")){
			colorPic = "jenkins_pic/jenkins_blue.PNG";
		}else if(color.equals("red")){
			colorPic = "jenkins_pic/jenkins_red.PNG";
		}else{
			colorPic = "jenkins_pic/jenkins_gray.PNG";
		}
		return colorPic;
	}
	
}