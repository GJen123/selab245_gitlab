package conn;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.gitlab.api.models.GitlabProject;

import fcu.selab.progedu.conn.Conn;
import fcu.selab.progedu.conn.StudentDashChoosePro;
import fcu.selab.progedu.data.Project;
import fcu.selab.progedu.data.User;
import fcu.selab.progedu.db.CommitResultDbManager;
import fcu.selab.progedu.db.IDatabase;
import fcu.selab.progedu.db.MySqlDatabase;
import fcu.selab.progedu.db.ProjectDbManager;
import fcu.selab.progedu.db.UserDbManager;

public class TestGetJobTimestamp {

  public static void main(String[] args) {
    // TODO Auto-generated method stub
    IDatabase database = new MySqlDatabase();
    Connection connection = database.getConnection();
    UserDbManager db = UserDbManager.getInstance();
    List<User> users = db.listAllUsers();
    ProjectDbManager Pdb = ProjectDbManager.getInstance();
    List<Project> dbProjects = Pdb.listAllProjects();
    List<GitlabProject> gitProjects = new ArrayList<GitlabProject>();
    Conn conn = Conn.getInstance();
    CommitResultDbManager commitDb = CommitResultDbManager.getInstance();
    StudentDashChoosePro stuDashChoPro = new StudentDashChoosePro();
    for (User user : users) {
      String userName = user.getUserName();
      gitProjects = conn.getProject(user);
      Collections.reverse(gitProjects);
      for (Project dbProject : dbProjects) {
        String proName = null;
        for (GitlabProject gitProject : gitProjects) {
          if (dbProject.getName().equals(gitProject.getName())) {
            proName = dbProject.getName();
            List<Integer> buildNum = stuDashChoPro.getScmBuildCounts(userName, proName);
            int num = 0;
            if (buildNum.size() > 1) {
              num = buildNum.size() - 1;
            }
            String buildApiJson = stuDashChoPro.getBuildApiJson(buildNum.get(num), userName,
                proName);
            String strDate = stuDashChoPro.getCommitTime(buildApiJson);
            int id = db.getUser(userName).getId();
            String pro = proName.replace("OOP-HW", "");
            boolean check = commitDb.updateJenkinsJobTimestamp(connection, id, pro, strDate);
            if (check) {
              System.out.println(userName + ", " + proName + ", " + strDate);
            }
            break;
          }
        }
      }
    }
  }

}
