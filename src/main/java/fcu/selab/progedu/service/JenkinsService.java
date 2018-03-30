package fcu.selab.progedu.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import fcu.selab.progedu.config.JenkinsConfig;
import fcu.selab.progedu.conn.StudentDashChoosePro;
import fcu.selab.progedu.exception.LoadConfigFailureException;
import fcu.selab.progedu.jenkins.JenkinsApi;
import fcu.selab.progedu.jenkins.JobStatus;

@Path("jenkins/")
public class JenkinsService {
  JenkinsConfig jenkinsData = JenkinsConfig.getInstance();
  JenkinsApi jenkins = JenkinsApi.getInstance();
  JobStatus jobStatus = new JobStatus();

  /**
   * return string
   * 
   * @return "hello!"
   */
  @GET
  @Path("hello")
  @Produces(MediaType.TEXT_PLAIN)
  public Response sayHello() {
    String str = "hello! ";
    Response response = Response.ok().entity(str).build();
    return response;
  }

  /**
   * get project built color
   * 
   * @param proName
   *          project name
   * @param userName
   *          student name
   * @return color and commit count
   */

  public String getColor(String proName, String userName) {
    // ---Jenkins---
    String jobName = userName + "_" + proName;
    jobStatus.setName(jobName);
    String jobUrl = "";
    try {
      jobUrl = jenkinsData.getJenkinsHostUrl() + "/job/" + jobName;
    } catch (LoadConfigFailureException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    jobStatus.setUrl(jobUrl + "/api/json");

    // Get job status
    jobStatus.setJobApiJson();
    String apiJson = jobStatus.getJobApiJson();
    boolean isMaven = jenkins.checkProjectIsMvn(apiJson);
    int commitCount = getProjectCommitCount(proName, userName);

    String color = null;
    int checkstyleErrorAmount = 0;
    if (null != apiJson) {
      color = jenkins.getJobJsonColor(apiJson);
    }

    String circleColor = "";
    if (commitCount == 1) {
      circleColor = "circle NB";
    } else {
      if (color != null) {
        circleColor = "circle " + color;
      } else {
        circleColor = "circle NB";
      }
    }
    String result = circleColor + "," + commitCount;
    return result;
  }

  /**
   * get project commit count
   * 
   * @param proName
   *          project name
   * @param userName
   *          student name
   * @return count
   */
  @GET
  @Path("commits")
  @Produces(MediaType.APPLICATION_JSON)
  public int getProjectCommitCount(@QueryParam("proName") String proName,
      @QueryParam("userName") String userName) {
    // ---Jenkins---
    String jobName = userName + "_" + proName;
    jobStatus.setName(jobName);
    String jobUrl = "";
    List<Integer> numbers = new ArrayList<Integer>();
    String jenkinsHostUrl = "";
    try {
      jenkinsHostUrl = jenkinsData.getJenkinsHostUrl();
      jobUrl = jenkinsHostUrl + "/job/" + jobName + "/api/json";
      numbers = jenkins.getJenkinsJobAllBuildNumber(jenkinsData.getJenkinsRootUsername(),
          jenkinsData.getJenkinsRootPassword(), jobUrl);
    } catch (LoadConfigFailureException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    int commitCount = 0;
    for (int i : numbers) {
      jobStatus.setUrl(jenkinsHostUrl + "/job/" + jobName + "/" + i + "/api/json");
      // Get job status
      jobStatus.setJobApiJson();
      String apiJson = jobStatus.getJobApiJson();
      JSONObject json = new JSONObject(apiJson);
      JSONArray actions = json.getJSONArray("actions");
      JSONArray causes = actions.getJSONObject(0).getJSONArray("causes");
      String shortDescription = causes.getJSONObject(0).optString("shortDescription");
      if (shortDescription.contains("SCM")) {
        commitCount++;
      } else {
        if (i == 1) { // teacher commit
          commitCount++;
        }
      }
    }
    return commitCount;
  }

  /**
   * get student build detail info
   * 
   * @param num
   *          build num
   * @param userName
   *          student id
   * @param proName
   *          project name
   * @return build detail
   */
  @GET
  @Path("buildDetail")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getBuildDetail(@QueryParam("num") int num,
      @QueryParam("userName") String userName, @QueryParam("proName") String proName) {
    StudentDashChoosePro stuDashChoPro = new StudentDashChoosePro();
    String buildApiJson = stuDashChoPro.getBuildApiJson(num, userName, proName);
    final String strDate = stuDashChoPro.getCommitTime(buildApiJson);
    String commitMessage = stuDashChoPro.getCommitMessage(num, userName, proName);
    commitMessage = commitMessage.replace("Commit message: ", "");
    if (null != commitMessage || !"".equals(commitMessage)) {
      commitMessage = commitMessage.substring(1, commitMessage.length() - 1);
    }

    String color = stuDashChoPro.getCommitColor(num, userName, proName, buildApiJson);
    if (num == 1) {
      color = "NB";
    }
    if (color.equals("red")) {
      String consoleText = checkErrorStyle(jenkinsData, userName, proName, num);
      boolean isCheckStyle = jenkins.checkIsCheckstyleError(consoleText);
      boolean isJUnitStyle = jenkins.checkIsJunitError(consoleText);
      if (isCheckStyle) {
        color = "CSF";
      }
      if (isJUnitStyle) {
        color = "CTF";
      }
    }
    color = "circle " + color;

    JSONObject ob = new JSONObject();
    ob.put("num", num);
    ob.put("color", color);
    ob.put("date", strDate);
    ob.put("message", commitMessage);
    Response response = Response.ok().entity(ob.toString()).build();
    return response;
  }

  /**
   * get build error type
   * 
   * @param jenkinsData
   *          connect to jenkins
   * @param userName
   *          student id
   * @param proName
   *          project name
   * @param num
   *          build num
   * @return type
   */
  public static String checkErrorStyle(JenkinsConfig jenkinsData, String userName, String proName,
      int num) {
    String jsonString = "";
    try {
      HttpURLConnection connUrl = null;
      String consoleUrl = jenkinsData.getJenkinsHostUrl() + "/job/" + userName + "_" + proName + "/"
          + num + "/consoleText";
      URL url = new URL(consoleUrl);
      connUrl = (HttpURLConnection) url.openConnection();
      connUrl.setReadTimeout(10000);
      connUrl.setConnectTimeout(15000);
      connUrl.setRequestMethod("GET");
      connUrl.connect();
      if (Thread.interrupted()) {
        throw new InterruptedException();
      }

      BufferedReader reader = new BufferedReader(
          new InputStreamReader(connUrl.getInputStream(), "UTF-8"));
      String line = "";
      while ((line = reader.readLine()) != null) {
        jsonString += line;
      }
      reader.close();
    } catch (LoadConfigFailureException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (MalformedURLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return jsonString;
  }

  /**
   * get test folder
   * 
   * @param filePath
   *          folder directory
   * @return zip file
   */
  @GET
  @Path("getTestFile")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getTestFile(@QueryParam("filePath") String filePath) {
    File file = new File(filePath);

    ResponseBuilder response = Response.ok((Object) file);
    response.header("Content-Disposition", "attachment;filename=");
    return response.build();
  }
}
