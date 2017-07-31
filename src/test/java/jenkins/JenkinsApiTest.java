package jenkins;

import org.junit.Test;

import fcu.selab.progedu.jenkins.JenkinsApi;

public class JenkinsApiTest {
  JenkinsApi jenkins = new JenkinsApi();
  String jobUrl = "http://140.134.26.71:38080/job/HelloMaven/api/json";

  @Test
  public void testLastBuildNum() {
    String lastBuildUrl = jenkins.getLastBuildUrl("GJen", "zxcv1234", jobUrl);
    System.out.println("lastBuildUrl : " + lastBuildUrl);
  }

  @Test
  public void testReadConsoleOutput() {
    String console = jenkins.getConsoleText("http://140.134.26.71:38080/job/HelloMaven/22/consoleText");
    System.out.println("console : " + console);
  }
}