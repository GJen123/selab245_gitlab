package conn;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.gitlab.api.models.GitlabProject;

import fcu.selab.progedu.conn.Conn;
import fcu.selab.progedu.data.Project;
import fcu.selab.progedu.data.User;
import fcu.selab.progedu.db.CommitResultDbManager;
import fcu.selab.progedu.db.IDatabase;
import fcu.selab.progedu.db.MySqlDatabase;
import fcu.selab.progedu.db.ProjectDbManager;
import fcu.selab.progedu.db.UserDbManager;
import fcu.selab.progedu.service.JenkinsService;

public class TestInsertJenkinsData {

  public static void main(String[] args) {
    IDatabase database = new MySqlDatabase();
    Connection connection = database.getConnection();

    UserDbManager db = UserDbManager.getInstance();
    ProjectDbManager Pdb = ProjectDbManager.getInstance();
    List<User> users = db.listAllUsers();
    List<GitlabProject> gitProjects = new ArrayList<GitlabProject>();
    List<Project> dbProjects = Pdb.listAllProjects();
    Conn conn = Conn.getInstance();
    CommitResultDbManager commitDb = CommitResultDbManager.getInstance();
    JenkinsService jenkins = new JenkinsService();

    // TODO Auto-generated method stub
    for (User user : users) {
      String userName = user.getUserName();
      gitProjects = conn.getProject(user);
      Collections.reverse(gitProjects);
      for (Project dbProject : dbProjects) {
        String proName = null;
        for (GitlabProject gitProject : gitProjects) {
          if (dbProject.getName().equals(gitProject.getName())) {
            proName = dbProject.getName();
            break;
          } else {
            proName = "N/A";
          }
        }

        if ("N/A".equals(proName) || "".equals(proName) || null == proName) {
          if (proName == null) {
            proName = "N/A";
          }
        } else {
          String[] result = jenkins.getColor(proName, userName).split(",");
          String color = result[0].replace("circle ", "");
          int commit = Integer.valueOf(result[1]) - 1;
          String hw = proName.replace("OOP-HW", "");
          // System.out.println(user.getId() + ", " + hw + ", " + color + ", " +
          // commit);
          boolean check = commitDb.updateJenkinsCommitCount(connection, user.getId(), hw, commit,
              color);
          if (check) {
            System.out
                .println("success, " + user.getId() + ", " + hw + ", " + commit + ", " + color);
          }
        }
      }
    }
    try

    {
      connection.close();
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
