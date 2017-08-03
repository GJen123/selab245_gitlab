package jenkins;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import fcu.selab.progedu.jenkins.JenkinsApi;
import fcu.selab.progedu.jenkins.JobStatus;

public class JenkinsApiTest {
  JenkinsApi jenkins = new JenkinsApi();
  String jobUrl = "http://140.134.26.71:38080/job/HelloMaven/api/json";

  // @Test
  // public void testReadConsoleOutput() {
  // String console =
  // jenkins.getConsoleText("http://140.134.26.71:38080/job/HelloMaven/22/consoleText");
  // System.out.println("console : " + console);
  // }

  @Test
  public void testCheckstyleDes() {
    JobStatus jobStatus = new JobStatus();
    jobStatus.setName("HelloMaven");
    jobStatus.setUrl("http://140.134.26.71:38080/job/HelloMaven/api/json");
    jobStatus.setJobApiJson();
    String des = jenkins.getCheckstyleDes(jobStatus.getJobApiJson());
    int checkstyleErrorAmount = jenkins.getCheckstyleErrorAmount(des);
    System.out.println("checkstyleErrorAmount : " + checkstyleErrorAmount);
    assertEquals("Number of checkstyle violations is 3", des);
  }
}