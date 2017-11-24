package fcu.selab.progedu.service;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import fcu.selab.progedu.config.JenkinsConfig;
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
   * @return color
   */
  @GET
  @Path("color")
  @Produces(MediaType.TEXT_PLAIN)
  public Response getMainTableColor(@QueryParam("proName") String proName,
      @QueryParam("userName") String userName) {
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
      if (isMaven) {
        if (color.equals("red")) {
          JSONObject checkstyleDes = jenkins.getCheckstyleDes(apiJson);
          checkstyleErrorAmount = jenkins.getCheckstyleErrorAmount(checkstyleDes);
          if (checkstyleErrorAmount != 0) {
            color = "orange";
          }
        }
      }
    }

    String circleColor = "";
    if (commitCount == 1) {
      circleColor = "circle gray";
    } else {
      if (color != null) {
        circleColor = "circle " + color;
      } else {
        circleColor = "circle gray";
      }
    }
    String result = userName + "_" + proName + "," + circleColor + "," + commitCount;
    Response response = Response.ok().entity(result).build();
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
      if (isMaven) {
        if (color.equals("red")) {
          JSONObject checkstyleDes = jenkins.getCheckstyleDes(apiJson);
          checkstyleErrorAmount = jenkins.getCheckstyleErrorAmount(checkstyleDes);
          if (checkstyleErrorAmount != 0) {
            color = "orange";
          }
        }
      }
    }

    String circleColor = "";
    if (commitCount == 1) {
      circleColor = "circle gray";
    } else {
      if (color != null) {
        circleColor = "circle " + color;
      } else {
        circleColor = "circle gray";
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
      if ("Started by an SCM change".equals(shortDescription)) {
        commitCount++;
      } else {
        if (i == 1) { // teacher commit
          commitCount++;
        }
      }
    }
    return commitCount;
  }
}
