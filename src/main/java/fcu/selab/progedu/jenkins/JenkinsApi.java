package fcu.selab.progedu.jenkins;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import fcu.selab.progedu.config.GitlabConfig;
import fcu.selab.progedu.config.JenkinsConfig;
import fcu.selab.progedu.conn.Conn;
import fcu.selab.progedu.exception.LoadConfigFailureException;
import sun.misc.BASE64Encoder;

public class JenkinsApi {
  private static JenkinsApi INSTANCE = new JenkinsApi();

  private Conn conn = Conn.getInstance();

  GitlabConfig gitData = GitlabConfig.getInstance();

  private String gitlabHostUrl;
  private String gitlabUsername;
  private String gitlabPassword;

  JenkinsConfig jenkinsData = JenkinsConfig.getInstance();

  private String jenkinsRootUrl;
  private String jenkinsRootUsername;
  private String jenkinsRootPassword;
  private String jenkinsApiToken;

  /**
   * constructor
   * 
   * @throws LoadConfigFailureException
   *           on properties call error
   */
  public JenkinsApi() {
    try {
      gitlabHostUrl = gitData.getGitlabHostUrl();
      gitlabUsername = gitData.getGitlabRootUsername();
      gitlabPassword = gitData.getGitlabRootPassword();
      jenkinsRootUrl = jenkinsData.getJenkinsRootUrl();
      jenkinsRootUsername = jenkinsData.getJenkinsRootUsername();
      jenkinsRootPassword = jenkinsData.getJenkinsRootPassword();
      jenkinsApiToken = jenkinsData.getJenkinsApiToken();
    } catch (LoadConfigFailureException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public static JenkinsApi getInstance() {
    return INSTANCE;
  }

  /**
   * Create gitlab root job on jenkins
   * 
   * @param proName
   *          The project name
   * @param jenkinsCrumb
   *          The jenkins crumb
   * @param fileType
   *          The file type
   * @param sb
   *          The config build job command
   */
  public void createRootJob(String proName, String jenkinsCrumb, String fileType,
      StringBuilder sb) {

    // ---Create Jenkins Job---
    String jobName = "root_" + proName;
    String proUrl = gitlabHostUrl + "/root/" + proName + ".git";
    postCreateJob(jobName, proUrl, jenkinsCrumb, fileType, sb);
    // ------------------------
  }

  /**
   * Create all user jenkins job
   * 
   * @param proName
   *          Project name
   * @param jenkinsCrumb
   *          Jenkins crumb
   * @param fileType
   *          File type
   * @param sb
   *          The config build job command
   * @throws LoadConfigFailureException
   *           on properties call error
   * @throws IOException
   *           on gitlab getuser call error
   */
  public void createJenkinsJob(String userName, String proName, String jenkinsCrumb,
      String fileType, StringBuilder sb) throws LoadConfigFailureException, IOException {

    // ---Create Jenkins Job---
    String jobName = userName + "_" + proName;
    String proUrl = gitData.getGitlabHostUrl() + "/" + userName + "/" + proName + ".git";
    postCreateJob(jobName, proUrl, jenkinsCrumb, fileType, sb);
    // ------------------------
  }

  /**
   * Httppost to create jenkins job
   * 
   * @param jobName
   *          Jenkins job name
   * @param proUrl
   *          Gitlab project url
   * @param jenkinsCrumb
   *          Jenkins crumb
   * @param fileType
   *          File type
   * @param sb
   *          The config build job command
   */
  public void postCreateJob(String jobName, String proUrl, String jenkinsCrumb, String fileType,
      StringBuilder sb) {
    HttpClient client = new DefaultHttpClient();

    String url = jenkinsRootUrl + "/createItem?name=" + jobName;
    try {
      HttpPost post = new HttpPost(url);

      post.addHeader("Content-Type", "application/xml");
      post.addHeader("Jenkins-Crumb", jenkinsCrumb);
      String filePath = null;
      if (fileType != null) {
        if (fileType.equals("Maven")) {
          filePath = this.getClass().getResource("config_maven.xml").getFile();
        } else if (fileType.equals("Javac")) {
          filePath = this.getClass().getResource("config_javac.xml").getFile();
        } else {
          filePath = this.getClass().getResource("config_maven.xml").getFile();
        }
      } else {
        filePath = this.getClass().getResource("config_javac.xml").getFile();
      }

      modifyXmlFileUrl(filePath, proUrl);
      if ("Javac".equals(fileType)) {
        modifyXmlFileCommand(filePath, sb);
      }

      StringBuilder sbConfig = getConfig(filePath);
      StringEntity se = new StringEntity(sbConfig.toString(),
          ContentType.create("text/xml", Consts.UTF_8));
      se.setChunked(true);
      post.setEntity(se);

      HttpResponse response = client.execute(post);
      HttpEntity resEntity = response.getEntity();

      if (resEntity != null) {
        String result = resEntity.toString();
      }
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (ClientProtocolException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Get the jenkins crumb
   * 
   * @param username
   *          Jenkins root user name
   * @param password
   *          Jenkins root password
   * @return jenkins crumb
   */
  public String getCrumb(String username, String password) {
    String jenkinsCrumb = null;
    HttpURLConnection conn = null;

    try {
      if (Thread.interrupted()) {
        throw new InterruptedException();
      }
      String strUrl = jenkinsRootUrl + "/crumbIssuer/api/json";
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

      BufferedReader reader = new BufferedReader(
          new InputStreamReader(conn.getInputStream(), "UTF-8"));
      String jsonString = reader.readLine();
      reader.close();

      JSONObject jsonObj = new JSONObject(jsonString);
      jenkinsCrumb = jsonObj.getString("crumb");
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (conn != null) {
        conn.disconnect();
      }
    }
    return jenkinsCrumb;
  }

  /**
   * Get the config file
   * 
   * @param filePath
   *          Config file path
   * @return config content
   */
  public StringBuilder getConfig(String filePath) {
    FileInputStream fis;
    StringBuilder sb = new StringBuilder();
    String strConfig = null;
    try {

      fis = new FileInputStream(filePath);
      InputStreamReader reader = new InputStreamReader(fis, "UTF8");
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

  /**
   * Change the config file's project url
   * 
   * @param filePath
   *          Config file path
   * @param url
   *          Project url
   */
  public void modifyXmlFileUrl(String filePath, String url) {
    try {
      String filepath = filePath;
      DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
      Document doc = docBuilder.parse(filepath);

      Node ndUrl = doc.getElementsByTagName("url").item(0);
      ndUrl.setTextContent(url);

      // write the content into xml file
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      DOMSource source = new DOMSource(doc);
      StreamResult result = new StreamResult(new File(filepath));
      transformer.transform(source, result);
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

  /**
   * Change the config file command (Maven or Javac)
   * 
   * @param filePath
   *          Config file path
   * @param sb
   *          Command string
   */
  public void modifyXmlFileCommand(String filePath, StringBuilder sb) {
    try {
      String filepath = filePath;
      DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
      Document doc = docBuilder.parse(filepath);

      Node ndUrl = doc.getElementsByTagName("command").item(0);
      ndUrl.setTextContent(sb.toString());

      // write the content into xml file
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      DOMSource source = new DOMSource(doc);
      StreamResult result = new StreamResult(new File(filepath));
      transformer.transform(source, result);
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

  /**
   * Get the jenkins job status color
   * 
   * @param jobApiJson
   *          job api json
   * @return color
   */
  public String getJobJsonColor(String jobApiJson) {
    String color = null;
    JSONObject j1 = new JSONObject(jobApiJson);
    color = j1.getString("color");
    return color;
  }

  /**
   * Get the jenkins job all status color
   * 
   * @param username
   *          Jenkins root user name
   * @param password
   *          Jenkins root password
   * @param buildUrl
   *          Jenkins job url
   * @return job status color
   */
  public String getJobBuildResult(String username, String password, String buildUrl) {
    String buildResult = null;
    HttpURLConnection conn = null;
    try {
      if (Thread.interrupted()) {
        throw new InterruptedException();
      }

      URL url = new URL(buildUrl);
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

      BufferedReader reader = new BufferedReader(
          new InputStreamReader(conn.getInputStream(), "UTF-8"));
      String jsonString1 = reader.readLine();
      reader.close();

      JSONObject j1 = new JSONObject(jsonString1);
      buildResult = j1.getString("result");

    } catch (Exception e) {
      System.out.print("Jenkins get job build result error : ");
      e.printStackTrace();
    } finally {
      if (conn != null) {
        conn.disconnect();
      }
    }
    return buildResult;
  }

  /**
   * get jekins job all build number
   * 
   * @param username
   *          jenkins user name
   * @param password
   *          jenkins user password
   * @param jobUrl
   *          jenkins job url
   * @return number list
   */
  public List<Integer> getJenkinsJobAllBuildNumber(String username, String password,
      String jobUrl) {
    List<Integer> numbers = new ArrayList<Integer>();
    HttpURLConnection conn = null;
    try {
      if (Thread.interrupted()) {
        throw new InterruptedException();
      }

      URL url = new URL(jobUrl);
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

      BufferedReader reader = new BufferedReader(
          new InputStreamReader(conn.getInputStream(), "UTF-8"));
      String jsonString1 = reader.readLine();
      reader.close();

      JSONObject j1 = new JSONObject(jsonString1);
      JSONArray builds = j1.getJSONArray("builds");
      for (int i = 0; i < builds.length(); i++) {
        JSONObject build = builds.getJSONObject(i);
        int buildNumber = build.optInt("number");
        numbers.add(buildNumber);
      }

    } catch (Exception e) {
      System.out.print("Jenkins get job build result error : ");
      e.printStackTrace();
    } finally {
      if (conn != null) {
        conn.disconnect();
      }
    }
    Collections.reverse(numbers);
    return numbers;
  }

  /**
   * Jenkins build the job
   * 
   * @param proName
   *          Jenkins job name
   * @param jenkinsCrumb
   *          Jenkins crumb
   * @throws IOException
   *           on gitlab getuser call error
   */
  public void buildJob(String userName, String proName, String jenkinsCrumb) throws IOException {
    String jobName = null;
    jobName = userName + "_" + proName;
    postBuildJob(jobName, jenkinsCrumb);
  }

  /**
   * Httppost to build jenkins job
   * 
   * @param jobName
   *          Jenkins job name
   * @param jenkinsCrumb
   *          Jenkins crumb
   */
  public void postBuildJob(String jobName, String jenkinsCrumb) {
    HttpClient client = new DefaultHttpClient();

    String url = jenkinsRootUrl + "/job/" + jobName + "/build";
    try {
      HttpPost post = new HttpPost(url);

      post.addHeader("Content-Type", "application/xml");
      post.addHeader("Jenkins-Crumb", jenkinsCrumb);

      List<NameValuePair> params = new ArrayList<NameValuePair>();
      params.add((NameValuePair) new BasicNameValuePair("token", jenkinsApiToken));

      UrlEncodedFormEntity ent = null;
      ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
      post.setEntity(ent);

      HttpResponse response = client.execute(post);
      HttpEntity resEntity = response.getEntity();

      if (resEntity != null) {
        String result = resEntity.toString();
        System.out.println("httppost build " + jobName + " , result : " + result);
      }
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (ClientProtocolException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Get a list of Jenkins jobs
   * 
   * @return list
   */
  public List<String> getJobNameList() {
    String jobUrl = "http://140.134.26.71:38080/api/json";
    String username = "GJen";
    String password = "zxcv1234";
    List<String> jobNames = new ArrayList<String>();
    HttpURLConnection conn = null;
    try {
      if (Thread.interrupted()) {
        throw new InterruptedException();
      }

      URL url = new URL(jobUrl);
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
      BufferedReader reader = new BufferedReader(
          new InputStreamReader(conn.getInputStream(), "UTF-8"));
      String jsonString1 = reader.readLine();
      reader.close();

      JSONObject jsonObject = new JSONObject(jsonString1);
      JSONArray ja = jsonObject.getJSONArray("jobs");

      for (int i = 0; i < ja.length(); i++) {
        JSONObject jo = ja.getJSONObject(i);
        String name = jo.getString("name");
        jobNames.add(name);
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (conn != null) {
        conn.disconnect();
      }
    }
    return jobNames;
  }

  /**
   * Delete the jenkins job
   * 
   * @param jobName
   *          Jenkins job name
   */
  public void deleteJob(String jobName) {
    HttpClient client = new DefaultHttpClient();

    String url = "http://GJen:02031fefb728e700973b6f3e5023a64c@140.134.26.71:38080/job/" + jobName
        + "/doDelete";
    try {
      HttpPost post = new HttpPost(url);

      post.addHeader("Content-Type", "application/x-www-form-urlencoded");
      post.addHeader("Jenkins-Crumb", "e390d46093102dac6c0ec903b77af0a0");

      HttpResponse response = client.execute(post);
      HttpEntity resEntity = response.getEntity();

      if (resEntity != null) {
        String result = EntityUtils.toString(response.getEntity());
        String result2 = resEntity.toString();
        System.out.println(jobName + " : " + result2);
      }
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (ClientProtocolException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Get last build number
   * 
   * @param jobApiJson
   *          api json
   * @return url
   */
  public String getLastBuildUrl(String jobApiJson) {
    String buildUrl = null;
    JSONObject j1 = new JSONObject(jobApiJson);
    JSONObject j2 = j1.getJSONObject("lastBuild");
    buildUrl = j2.get("url").toString();
    return buildUrl;
  }

  /**
   * Get
   * 
   * @param strUrl
   *          strUrl
   * @return console
   */
  public String getConsoleText(String strUrl) {
    String console = "";
    HttpURLConnection conn = null;

    try {
      if (Thread.interrupted()) {
        throw new InterruptedException();
      }
      URL url = new URL(strUrl);
      conn = (HttpURLConnection) url.openConnection();
      String input = "GJen:zxcv1234";
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

      BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
      String str = "";
      StringBuffer sb = new StringBuffer();
      while (null != ((str = br.readLine()))) {
        sb.append("\n");
        sb.append(str);
      }
      br.close();
      console = sb.toString();

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (conn != null) {
        conn.disconnect();
      }
    }
    return console;
  }

  /**
   * Get Job Api Json
   * 
   * @param username
   *          jenkins username
   * @param password
   *          jenkins password
   * @param jobUrl
   *          job url
   * @return api json
   */
  public String getJobApiJson(String username, String password, String jobUrl) {
    String result = null;
    HttpURLConnection conn = null;
    try {
      if (Thread.interrupted()) {
        throw new InterruptedException();
      }

      URL url = new URL(jobUrl);
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

      BufferedReader reader = new BufferedReader(
          new InputStreamReader(conn.getInputStream(), "UTF-8"));
      String jsonString1 = reader.readLine();
      reader.close();

      result = jsonString1;

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (conn != null) {
        conn.disconnect();
      }
    }
    return result;
  }
}