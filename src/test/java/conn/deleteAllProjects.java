package conn;

import java.util.List;

import org.gitlab.api.models.GitlabUser;

import fcu.selab.progedu.conn.Conn;
import fcu.selab.progedu.jenkins.JenkinsApi;

public class deleteAllProjects {
  static Conn conn = Conn.getInstance();
  static JenkinsApi jenkins = JenkinsApi.getInstance();
  static List<GitlabUser> users = conn.getUsers();

  public static void main(String[] args) {
    // TODO Auto-generated method stub
//    conn.deleteProjects("OOP-HW1");
    String crumb = jenkins.getCrumb("root", "zxcv1234");
    for (GitlabUser user : users) {
      String jobName = user.getUsername() + "_OOP-HW1";
      jenkins.deleteJob(jobName, crumb);
    }
  }

}
