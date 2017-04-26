package jenkins;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import fcu.selab.progedu.jenkins.JenkinsApi;

public class JenkinsApiTest {
  List<String> names = new ArrayList<String>();

  @Test
  public void TestGetCrumb() {

    JenkinsApi jenkins = JenkinsApi.getInstance();
    names = jenkins.getJobNameList();
    Collections.reverse(names);
    // for(int i =0;i<10;i++){
    // String name = names.get(i);
    // jenkins.deleteJob(name);
    // }
    for (String name : names) {
      System.out.println("name : " + name);
      try {
        jenkins.deleteJob(name);
        Thread.sleep(500);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

  }

}
