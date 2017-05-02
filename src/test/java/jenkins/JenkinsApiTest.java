package jenkins;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import fcu.selab.progedu.config.JenkinsConfig;
import fcu.selab.progedu.exception.LoadConfigFailureException;
import fcu.selab.progedu.jenkins.JenkinsApi;

public class JenkinsApiTest {
  List<String> names = new ArrayList<String>();

  @Test
  public void TestGetCrumb() {

    JenkinsApi jenkins = JenkinsApi.getInstance();
    JenkinsConfig jenkinsData = JenkinsConfig.getInstance();
    String username;
    String password;
    String crumb = null;
    try {
      username = jenkinsData.getJenkinsRootUsername();
      password = jenkinsData.getJenkinsRootPassword();
      crumb = jenkins.getCrumb(username, password);
    } catch (LoadConfigFailureException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    System.out.println("crumb : " + crumb);

  }

}
