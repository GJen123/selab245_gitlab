package conn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.gitlab.api.models.GitlabProject;

import fcu.selab.progedu.config.JenkinsConfig;
import fcu.selab.progedu.conn.Conn;
import fcu.selab.progedu.conn.StudentDashChoosePro;
import fcu.selab.progedu.data.Project;
import fcu.selab.progedu.data.User;
import fcu.selab.progedu.db.CommitRecordDbManager;
import fcu.selab.progedu.db.IDatabase;
import fcu.selab.progedu.db.MySqlDatabase;
import fcu.selab.progedu.db.ProjectDbManager;
import fcu.selab.progedu.db.UserDbManager;
import fcu.selab.progedu.exception.LoadConfigFailureException;

public class TestInsertCommitRecord {

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
    CommitRecordDbManager commitDb = CommitRecordDbManager.getInstance();
    StudentDashChoosePro stuDashChoPro = new StudentDashChoosePro();
    JenkinsConfig jenkinsData = JenkinsConfig.getInstance();

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
            for (Integer num : buildNum) {
              String buildApiJson = stuDashChoPro.getBuildApiJson(num, userName, proName);
              String strDate = stuDashChoPro.getCommitTime(buildApiJson);
              String[] dates = strDate.split(" ");
              String color = stuDashChoPro.getCommitColor(num, userName, proName, buildApiJson);
              if (num == 1) {
                continue;
              }
              if (color.equals("red")) {
                String style = checkErrorStyle(jenkinsData, userName, proName, num);
                boolean ifCheckStyle = style.contains("Checkstyle violation");
                if (ifCheckStyle) {
                  color = "orange";
                  // System.out.println(userName + "," + proName + ", " + num);
                }
              }
              String hw = proName.replace("OOP-HW", "");
              boolean inDb = commitDb.checkRecord(connection, user.getId(), hw, color, dates[0],
                  dates[1]);
              if (!inDb) {
                boolean check = commitDb.insertCommitRecord(connection, user.getId(), hw, color,
                    dates[0], dates[1]);
                if (check) {
                  System.out.println(user.getId() + ", " + hw + ", " + color + ", " + strDate);
                }
              }
            }
            break;
          }
        }
      }
    }
    try {
      connection.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static String checkErrorStyle(JenkinsConfig jenkinsData, String userName, String proName,
      int num) {
    String jsonString = "";
    try {
      HttpURLConnection connUrl = null;
      String consoleUrl = jenkinsData.getJenkinsHostUrl() + "/job/" + userName + "_" + proName + "/"
          + num + "/consoleText";
      URL url = new URL(consoleUrl);
      connUrl = (HttpURLConnection) url.openConnection();
      connUrl.setReadTimeout(10000);
      connUrl.setConnectTimeout(15000);
      connUrl.setRequestMethod("GET");
      connUrl.connect();
      if (Thread.interrupted()) {
        throw new InterruptedException();
      }

      BufferedReader reader = new BufferedReader(
          new InputStreamReader(connUrl.getInputStream(), "UTF-8"));
      String line = "";
      while ((line = reader.readLine()) != null) {
        jsonString += line;
      }
      reader.close();
    } catch (LoadConfigFailureException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (MalformedURLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return jsonString;
  }
}
