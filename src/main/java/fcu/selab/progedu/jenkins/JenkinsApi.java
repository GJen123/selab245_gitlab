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

import org.apache.commons.io.FileUtils;
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

import fcu.selab.progedu.config.CourseConfig;
import fcu.selab.progedu.config.GitlabConfig;
import fcu.selab.progedu.config.JenkinsConfig;
import fcu.selab.progedu.conn.Conn;
import fcu.selab.progedu.exception.LoadConfigFailureException;
import sun.misc.BASE64Encoder;

public class JenkinsApi {
  private static JenkinsApi INSTANCE = new JenkinsApi();

  private Conn conn = Conn.getInstance();

  GitlabConfig gitData = GitlabConfig.getInstance();
  CourseConfig courseData = CourseConfig.getInstance();

  private String gitlabHostUrl;
  private String gitlabUsername;
  private String gitlabPassword;

  JenkinsConfig jenkinsData = JenkinsConfig.getInstance();

  private String jenkinsRootUrl;
  private String jenkinsHostUrl;
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
      jenkinsHostUrl = jenkinsData.getJenkinsHostUrl();
      jenkinsRootUsername = jenkinsData.getJenkinsRootUsername();
      jenkinsRootPassword = jenkinsData.getJenkinsRootPassword();
      jenkinsApiToken = jenkinsData.getJenkinsApiToken();
    } catch (LoadConfigFailureException e) {
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
    String proUrl = gitlabHostUrl + "/root/" + proName + ".git";
    System.out.println("proUrl : " + proUrl);
    String filePath = modifyXml("root", proName, proUrl, fileType, sb);
    postCreateJob("root", proName, proUrl, jenkinsCrumb, filePath);
    backupConfig(filePath, proName);
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
      String fileType, StringBuilder sb) {

    // ---Create Jenkins Job---
    String proUrl = gitlabHostUrl + "/" + userName + "/" + proName + ".git";
    String filePath = modifyXml(userName, proName, proUrl, fileType, sb);
    postCreateJob(userName, proName, proUrl, jenkinsCrumb, filePath);
    // ------------------------
  }

  /**
   * Httppost to create jenkins job
   * 
   * @param proUrl
   *          Gitlab project url
   * @param jenkinsCrumb
   *          Jenkins crumb
   * @param filePath
   *          File path
   */
  public void postCreateJob(String userName, String proName, String proUrl, String jenkinsCrumb,
      String filePath) {
    HttpClient client = new DefaultHttpClient();
    String jobName = userName + "_" + proName;

    String url = jenkinsRootUrl + "/createItem?name=" + jobName;
    try {
      String tomcatUrl = courseData.getTomcatServerIp()
          + "/ProgEdu/webapi/project/checksum?proName=" + proName;
      String updateDbUrl = courseData.getTomcatServerIp() + "/ProgEdu/webapi/commits/update";
      HttpPost post = new HttpPost(url);

      post.addHeader("Content-Type", "application/xml");
      post.addHeader("Jenkins-Crumb", jenkinsCrumb);

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
    } catch (LoadConfigFailureException e) {
      e.printStackTrace();
    }
  }

  /**
   * modify job config
   * 
   * @param userName
   *          student id
   * @param proName
   *          project name
   * @param proUrl
   *          project url
   * @param fileType
   *          job type
   * @param sb
   *          command
   * @return config file path
   */
  public String modifyXml(String userName, String proName, String proUrl, String fileType,
      StringBuilder sb) {
    String filePath = null;
    String configType = getConfigType(fileType);
    filePath = this.getClass().getResource("/jenkins/" + configType).getPath();

    try {
      String tomcatUrl;
      tomcatUrl = courseData.getTomcatServerIp() + "/ProgEdu/webapi/project/checksum?proName="
          + proName;
      String updateDbUrl = courseData.getTomcatServerIp() + "/ProgEdu/webapi/commits/update";
      // proUrl project name toLowerCase
      proUrl = proUrl.toLowerCase();
      modifyXmlFileUrl(filePath, proUrl);
      if ("Javac".equals(fileType)) {
        modifyXmlFileCommand(filePath, sb, updateDbUrl, userName, proName);
      }
      if ("Maven".equals(fileType)) {
        modifyXmlFileProgEdu(filePath, userName, proName, tomcatUrl, updateDbUrl);
      }
    } catch (LoadConfigFailureException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return filePath;
  }

  /**
   * backup job config
   * 
   * @param source
   *          source file
   */
  public void backupConfig(String source, String proName) {
    String tempDir = System.getProperty("java.io.tmpdir");
    try {
      File sourceFile = new File(source);
      File desFile = new File(tempDir + "/configs/" + proName + ".xml");
      FileUtils.copyFile(sourceFile, desFile);
    } catch (IOException e) {
      // TODO Auto-generated catch block
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
      e.printStackTrace();
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (IOException e) {
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
      e.printStackTrace();
    } catch (TransformerException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    } catch (IOException e) {
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
   * @param updateDbUrl
   *          tomcat db url
   * @param userName
   *          user name
   */
  public void modifyXmlFileCommand(String filePath, StringBuilder sb, String updateDbUrl,
      String userName, String proName) {
    try {
      String filepath = filePath;
      DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
      Document doc = docBuilder.parse(filepath);

      Node ndUrl = doc.getElementsByTagName("command").item(0);
      ndUrl.setTextContent(sb.toString());

      Node progeduDbUrl = doc.getElementsByTagName("progeduDbUrl").item(0);
      progeduDbUrl.setTextContent(updateDbUrl);

      Node user = doc.getElementsByTagName("user").item(0);
      user.setTextContent(userName);

      Node ndProName = doc.getElementsByTagName("proName").item(0);
      ndProName.setTextContent(proName);

      // write the content into xml file
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      DOMSource source = new DOMSource(doc);
      StreamResult result = new StreamResult(new File(filepath));
      transformer.transform(source, result);
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
    } catch (TransformerException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Change the config file command (Maven or Javac)
   * 
   * @param filePath
   *          Config file path
   * @param userName
   *          user name
   */
  public void modifyXmlFileCommand(String filePath, String userName, String proName) {
    try {
      String filepath = filePath;
      DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
      Document doc = docBuilder.parse(filepath);

      Node user = doc.getElementsByTagName("user").item(0);
      user.setTextContent(userName);

      Node ndProName = doc.getElementsByTagName("proName").item(0);
      ndProName.setTextContent(proName);

      // write the content into xml file
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      DOMSource source = new DOMSource(doc);
      StreamResult result = new StreamResult(new File(filepath));
      transformer.transform(source, result);
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
    } catch (TransformerException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Change the config ProgEdu plugin content
   * 
   * @param userName
   *          user name
   * @param proName
   *          project name
   * @param tomcatUrl
   *          tomcat project url
   * @param updateDbUrl
   *          updating db url
   */
  public void modifyXmlFileProgEdu(String filePath, String userName, String proName,
      String tomcatUrl, String updateDbUrl) {
    try {
      String filepath = filePath;
      DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
      Document doc = docBuilder.parse(filepath);

      String strJobName = userName + "_" + proName;
      Node jobName = doc.getElementsByTagName("jobName").item(0);
      jobName.setTextContent(strJobName);

      Node testFileName = doc.getElementsByTagName("testFileName").item(0);
      testFileName.setTextContent(proName);

      Node proDetailUrl = doc.getElementsByTagName("proDetailUrl").item(0);
      proDetailUrl.setTextContent(tomcatUrl);

      Node progeduDbUrl = doc.getElementsByTagName("progeduDbUrl").item(0);
      progeduDbUrl.setTextContent(updateDbUrl);

      Node user = doc.getElementsByTagName("user").item(0);
      user.setTextContent(userName);

      Node ndProName = doc.getElementsByTagName("proName").item(0);
      ndProName.setTextContent(proName);

      // write the content into xml file
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      DOMSource source = new DOMSource(doc);
      StreamResult result = new StreamResult(new File(filepath));
      transformer.transform(source, result);
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
    } catch (TransformerException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Change the config ProgEdu plugin content
   * 
   * @param userName
   *          user name
   * @param proName
   *          project name
   */
  public void modifyXmlFileProgEdu(String filePath, String userName, String proName) {
    try {
      String filepath = filePath;
      DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
      Document doc = docBuilder.parse(filepath);

      String strJobName = userName + "_" + proName;
      Node jobName = doc.getElementsByTagName("jobName").item(0);
      jobName.setTextContent(strJobName);

      Node user = doc.getElementsByTagName("user").item(0);
      user.setTextContent(userName);

      Node ndProName = doc.getElementsByTagName("proName").item(0);
      ndProName.setTextContent(proName);

      // write the content into xml file
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      DOMSource source = new DOMSource(doc);
      StreamResult result = new StreamResult(new File(filepath));
      transformer.transform(source, result);
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
    } catch (TransformerException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    } catch (IOException e) {
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
  public void buildJob(String userName, String proName, String jenkinsCrumb) {
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
  public void deleteJob(String jobName, String crumb) {
    HttpClient client = new DefaultHttpClient();

    try {
      String url = jenkinsData.getJenkinsRootUrl() + "/job/" + jobName + "/doDelete";
      HttpPost post = new HttpPost(url);

      post.addHeader("Content-Type", "application/x-www-form-urlencoded");
      post.addHeader("Jenkins-Crumb", crumb);

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
      String input = jenkinsData.getJenkinsRootUsername() + ":"
          + jenkinsData.getJenkinsRootPassword();
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
   * Get
   * 
   * @param strUrl
   *          strUrl
   * @return console
   */
  public String getConsoleTextCommitMessage(String strUrl) {
    String console = "";
    HttpURLConnection conn = null;

    try {
      if (Thread.interrupted()) {
        throw new InterruptedException();
      }
      URL url = new URL(strUrl);
      conn = (HttpURLConnection) url.openConnection();
      String input = jenkinsData.getJenkinsRootUsername() + ":"
          + jenkinsData.getJenkinsRootPassword();
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
        if (str.contains("Commit message:")) {
          console = str;
          break;
        }
      }
      br.close();

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

  /**
   * Get checkstyle description
   * 
   * @param jobApiJson
   *          job api json
   * @return description
   */
  public JSONObject getCheckstyleDes(String jobApiJson) {
    JSONObject jsonCheckstyle = null;
    JSONObject jsonJobApi = new JSONObject(jobApiJson);
    JSONArray jsonHealthReport = jsonJobApi.getJSONArray("healthReport");
    if (jsonHealthReport.length() == 2) {
      jsonCheckstyle = jsonHealthReport.getJSONObject(1);
      // description = jsonCheckstyle.getString("description");
    }
    return jsonCheckstyle;
  }

  /**
   * Get error amount
   * 
   * @param checkstyleDes
   *          description
   * @return amount
   */
  public int getCheckstyleErrorAmount(JSONObject checkstyleDes) {
    String description = checkstyleDes.optString("description");
    int amount = 0;
    String num = description.substring(description.length() - 1, description.length());
    int index = 0;
    switch (num) {
      case "0":
        index = description.indexOf("is") + 3;
        num = description.substring(index, description.length());
        amount = Integer.valueOf(num);
        break;
      case "1":
        index = description.indexOf("is") + 3;
        num = description.substring(index, description.length());
        amount = Integer.valueOf(num);
        break;
      case "2":
        index = description.indexOf("is") + 3;
        num = description.substring(index, description.length());
        amount = Integer.valueOf(num);
        break;
      case "3":
        index = description.indexOf("is") + 3;
        num = description.substring(index, description.length());
        amount = Integer.valueOf(num);
        break;
      case "4":
        index = description.indexOf("is") + 3;
        num = description.substring(index, description.length());
        amount = Integer.valueOf(num);
        break;
      case "5":
        index = description.indexOf("is") + 3;
        num = description.substring(index, description.length());
        amount = Integer.valueOf(num);
        break;
      case "6":
        index = description.indexOf("is") + 3;
        num = description.substring(index, description.length());
        amount = Integer.valueOf(num);
        break;
      case "7":
        index = description.indexOf("is") + 3;
        num = description.substring(index, description.length());
        amount = Integer.valueOf(num);
        break;
      case "8":
        index = description.indexOf("is") + 3;
        num = description.substring(index, description.length());
        amount = Integer.valueOf(num);
        break;
      case "9":
        index = description.indexOf("is") + 3;
        num = description.substring(index, description.length());
        amount = Integer.valueOf(num);
        break;
      default:
        break;
    }
    return amount;
  }

  /**
   * Check if project is Maven
   * 
   * @param jobApiJson
   *          api json
   * @return boolean
   */
  public Boolean checkProjectIsMvn(String jobApiJson) {
    String checkstyleText = "checkstyle";
    Boolean found;
    found = jobApiJson.contains(checkstyleText);
    return found;
  }

  /**
   * Get job build api json
   * 
   * @param username
   *          root username
   * @param password
   *          root password
   * @param buildUrl
   *          job build url
   * @return api json
   */
  public String getJobBuildApiJson(String username, String password, String buildUrl) {
    String buildApiJson = null;
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

      buildApiJson = jsonString1;

    } catch (Exception e) {
      System.out.print("Jenkins get job build result error : ");
      e.printStackTrace();
    } finally {
      if (conn != null) {
        conn.disconnect();
      }
    }
    return buildApiJson;
  }

  /**
   * Get the jenkins job all status color
   * 
   * @param apiJson
   *          jenkins job api json
   * 
   * @return job status color
   */
  public String getJobBuildResult(String apiJson) {
    String buildResult = null;
    JSONObject jsonApiJson = new JSONObject(apiJson);
    String result = jsonApiJson.getString("result");
    buildResult = result;
    return buildResult;
  }

  /**
   * Check is checkstyle error
   * 
   * @param consoleText
   *          jenkins job console text
   * @return boolean
   */
  public boolean checkIsCheckstyleError(String consoleText) {
    boolean isCheckstyleError = false;
    String checkstyleError = "Failed during checkstyle execution";
    isCheckstyleError = consoleText.contains(checkstyleError);

    return isCheckstyleError;
  }

  /**
   * Check is JUnit error
   *
   * @param consoleText
   *          jenkins job console text
   * @return boolean
   */
  public boolean checkIsJunitError(String consoleText) {
    boolean isJunitError = false;
    if (consoleText.contains("T E S T S")) {
      // have run junit test
      if (consoleText.contains("Failed tests")) {
        // junit runs failure.
        isJunitError = true;
      } else {
        // junit runs pass.
        // return false.
      }
    } else {
      // do nothing
      // return false
    }
    return isJunitError;
  }

  /**
   * Get job build commit counts
   * 
   */
  public Integer getJobBuildCommit(String apiJson) {
    int jobCommits = 0;
    JSONObject jsonApiJson = new JSONObject(apiJson);
    JSONArray jsonBuilds = jsonApiJson.getJSONArray("builds");
    jobCommits = jsonBuilds.length();
    return jobCommits;
  }

  private String getConfigType(String fileType) {
    String configType = null;
    if (fileType != null) {
      if (fileType.equals("Maven")) {
        configType = "config_maven.xml";
      } else if (fileType.equals("Javac")) {
        configType = "config_javac.xml";
      } else {
        configType = "config_maven.xml";
      }
    } else {
      configType = "config_javac.xml";
    }
    return configType;
  }
}