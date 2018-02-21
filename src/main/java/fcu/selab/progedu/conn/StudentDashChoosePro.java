package fcu.selab.progedu.conn;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.gitlab.api.models.GitlabProject;
import org.json.JSONArray;
import org.json.JSONObject;

import fcu.selab.progedu.config.JenkinsConfig;
import fcu.selab.progedu.exception.LoadConfigFailureException;
import fcu.selab.progedu.jenkins.JenkinsApi;
import fcu.selab.progedu.jenkins.JobStatus;
import fcu.selab.progedu.service.CommitResultService;

public class StudentDashChoosePro {
  JenkinsConfig jenkinsData;
  JenkinsApi jenkins;
  JobStatus jobStatus = new JobStatus();
  CommitResultService commitResultService = new CommitResultService();

  public StudentDashChoosePro() {
    jenkinsData = JenkinsConfig.getInstance();
    jenkins = new JenkinsApi();
  }

  /**
   * Get the choosed project
   * 
   * @param stuProjects
   *          all student projects
   * @param projectId
   *          the choosed project id
   * @return gitlab project
   */
  public GitlabProject getChoosedProject(List<GitlabProject> stuProjects, int projectId) {
    GitlabProject project = new GitlabProject();
    for (GitlabProject stuProject : stuProjects) {
      if (stuProject.getId() == projectId) {
        project = stuProject;
      }
    }
    return project;
  }

  /**
   * Get the choosed project url
   * 
   * @param project
   *          the choosed project
   * @return url
   */
  public String getChoosedProjectUrl(GitlabProject project) {
    String url = null;
    url = project.getHttpUrl();
    url = url.replace("0912fe2b3e43", "140.134.26.71:20080");
    return url;
  }

  /**
   * Get the job build numbers
   * 
   * @param username
   *          user name
   * @param projectName
   *          project name
   * @return list of build numbers
   */
  public List<Integer> getBuildNumbers(String username, String projectName) {
    String jobName = username + "_" + projectName;
    String jobUrl = null;
    List<Integer> buildNumbers = null;
    try {
      jobUrl = jenkinsData.getJenkinsHostUrl() + "/job/" + jobName + "/api/json";
      buildNumbers = jenkins.getJenkinsJobAllBuildNumber(jenkinsData.getJenkinsRootUsername(),
          jenkinsData.getJenkinsRootPassword(), jobUrl);
    } catch (LoadConfigFailureException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return buildNumbers;
  }

  /**
   * count for SCM build
   * 
   * @param username
   *          student name
   * @param projectName
   *          project name
   * @return count
   */
  public List<Integer> getScmBuildCounts(String username, String projectName) {
    String jobName = username + "_" + projectName;
    jobStatus.setName(jobName);
    String jobUrl = "";
    List<Integer> numbers = new ArrayList<Integer>();
    List<Integer> counts = new ArrayList<Integer>();
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
        counts.add(i);
      } else {
        if (i == 1) { // teacher commit
          counts.add(i);
        }
      }
    }
    return counts;
  }

  /**
   * Get the jenkins last build color
   * 
   * @param username
   *          user name
   * @param projectName
   *          project name
   * @return color
   */
  public String getLastColor(String username, String projectName) {
    String color = null;
    color = commitResultService.getCommitResult(username, projectName);
    // String jobName = username + "_" + projectName;
    // try {
    // List<Integer> buildNumbers = getBuildNumbers(username, projectName);
    //
    // String buildUrl = jenkinsData.getJenkinsHostUrl() + "/job/" + jobName +
    // "/"
    // + buildNumbers.get(buildNumbers.size() - 1) + "/api/json";
    //
    // String buildApiJson =
    // jenkins.getJobBuildApiJson(jenkinsData.getJenkinsRootUsername(),
    // jenkinsData.getJenkinsRootPassword(), buildUrl);
    //
    // String result = jenkins.getJobBuildResult(buildApiJson);
    //
    // String projectJenkinsUrl = jenkinsData.getJenkinsHostUrl() + "/job/" +
    // jobName + "/"
    // + buildNumbers.get(buildNumbers.size() - 1) + "/consoleText";
    //
    // color = checkColor(result, projectJenkinsUrl);
    // } catch (LoadConfigFailureException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    return color;
  }

  private String checkColor(String result, String projectJenkinsUrl) {
    String color = null;
    if (result.equals("SUCCESS")) {
      color = "blue";

    } else {
      color = "red";
      // check if is checkstyle error
      String consoleText = jenkins.getConsoleText(projectJenkinsUrl);
      // boolean isCheckstyleError =
      // jenkins.checkIsCheckstyleError(consoleText);
      // if (isCheckstyleError == true) {
      // color = "orange";
      // }
      boolean ifCheckStyle = consoleText.contains("Checkstyle violation");
      if (ifCheckStyle) {
        color = "orange";
        // System.out.println(userName + "," + proName + ", " + num);
      }
    }
    return color;
  }

  /**
   * Get the last build number
   * 
   * @param username
   *          user name
   * @param projectName
   *          project name
   * @return number
   */
  public String getLastBuildNum(String username, String projectName) {
    String num = null;
    List<Integer> buildNumbers = getBuildNumbers(username, projectName);
    num = String.valueOf(buildNumbers.get(buildNumbers.size() - 1));
    return num;
  }

  /**
   * Get commit color
   * 
   * @param num
   *          commit number
   * @param username
   *          username
   * @param projectName
   *          project name
   * @return color
   */
  public String getCommitColor(int num, String username, String projectName, String apiJson) {
    String color = null;
    String jobName = username + "_" + projectName;
    try {
      String result = jenkins.getJobBuildResult(apiJson);

      String projectJenkinsUrl = jenkinsData.getJenkinsHostUrl() + "/job/" + jobName + "/" + num
          + "/consoleText";

      color = checkColor(result, projectJenkinsUrl);
    } catch (LoadConfigFailureException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return color;
  }

  /**
   * get
   * 
   * @param num
   *          buuild num
   * @param username
   *          student name
   * @param projectName
   *          project name
   * @return commit message
   */
  public String getCommitMessage(int num, String username, String projectName) {
    String jobName = username + "_" + projectName;
    String console = "";
    try {
      String projectJenkinsUrl = jenkinsData.getJenkinsHostUrl() + "/job/" + jobName + "/" + num
          + "/consoleText";
      console = jenkins.getConsoleTextCommitMessage(projectJenkinsUrl);
    } catch (LoadConfigFailureException e) {
      // TODO Auto-generated catch block3
      e.printStackTrace();
    }
    return console;
  }

  /**
   * get job build time
   * 
   * @param apiJson
   *          build api json
   * @return date
   */
  public String getCommitTime(String apiJson) {
    JSONObject json = new JSONObject(apiJson);
    long timestamp = json.getLong("timestamp");
    Date date = new Date(timestamp);
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    sdf.setTimeZone(TimeZone.getTimeZone("Asia/Taipei"));
    String strDate = sdf.format(date);
    return strDate;
  }

  /**
   * get job build api json
   * 
   * @param num
   *          build num
   * @param username
   *          student name
   * @param projectName
   *          project name
   * @return json string
   */
  public String getBuildApiJson(int num, String username, String projectName) {
    String jobName = username + "_" + projectName;
    String buildUrl;
    String buildApiJson = "";
    try {
      buildUrl = jenkinsData.getJenkinsHostUrl() + "/job/" + jobName + "/" + num + "/api/json";
      buildApiJson = jenkins.getJobBuildApiJson(jenkinsData.getJenkinsRootUsername(),
          jenkinsData.getJenkinsRootPassword(), buildUrl);
    } catch (LoadConfigFailureException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return buildApiJson;
  }

  /**
   * Get commit date
   * 
   * @param date
   *          commit date
   * @return string date
   */
  public String getCommitDate(Date date) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    String strDate = sdf.format(date);
    return strDate;
  }
}
