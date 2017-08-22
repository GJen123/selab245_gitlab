package jenkins;

import java.util.List;

import org.gitlab.api.models.GitlabUser;
import org.junit.Test;

import fcu.selab.progedu.conn.Conn;
import fcu.selab.progedu.jenkins.JenkinsApi;

public class JenkinsApiTest {
  JenkinsApi jenkins = JenkinsApi.getInstance();
  String jobUrl = "http://140.134.26.71:38080/job/HelloMaven/api/json";
  Conn conn = Conn.getInstance();
  List<GitlabUser> users = conn.getUsers();

  @Test
  public void testDeleteJenkinsJob() {
    for (GitlabUser user : users) {
      String jobName = user.getUsername() + "_OOP-HelloWorld";
      jenkins.deleteJob(jobName);
    }
  }

  // @Test
  // public void testReadConsoleOutput() {
  // String console =
  // jenkins.getConsoleText("http://140.134.26.71:38080/job/HelloMaven/22/consoleText");
  // System.out.println("console : " + console);
  // }

  // @Test
  // public void testCheckstyleDes() {
  // JobStatus jobStatus = new JobStatus();
  // jobStatus.setName("HelloMaven");
  // jobStatus.setUrl("http://140.134.26.71:38080/job/HelloMaven/api/json");
  // jobStatus.setJobApiJson();
  // String des = jenkins.getCheckstyleDes(jobStatus.getJobApiJson());
  // int checkstyleErrorAmount = jenkins.getCheckstyleErrorAmount(des);
  // System.out.println("checkstyleErrorAmount : " + checkstyleErrorAmount);
  // assertEquals("Number of checkstyle violations is 3", des);
  // }
}