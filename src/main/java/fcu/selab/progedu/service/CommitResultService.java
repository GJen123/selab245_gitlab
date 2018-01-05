package fcu.selab.progedu.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

import fcu.selab.progedu.config.JenkinsConfig;
import fcu.selab.progedu.conn.StudentDashChoosePro;
import fcu.selab.progedu.db.CommitRecordDbManager;
import fcu.selab.progedu.db.CommitResultDbManager;
import fcu.selab.progedu.db.IDatabase;
import fcu.selab.progedu.db.MySqlDatabase;
import fcu.selab.progedu.db.UserDbManager;
import fcu.selab.progedu.exception.LoadConfigFailureException;

@Path("commits/")
public class CommitResultService {
  CommitResultDbManager db = CommitResultDbManager.getInstance();
  IDatabase database = new MySqlDatabase();
  Connection connection = database.getConnection();

  /**
   * get counts by different color
   * 
   * @param color
   *          color
   * @return counts
   */
  @GET
  @Path("color")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getCounts(@QueryParam("color") String color) {
    int[] array = db.getCounts(connection, color);
    switch (color) {
      case "blue":
        color = "build success";
        break;
      case "gray":
        color = "not build";
        break;
      case "red":
        color = "compile error";
        break;
      case "orange":
        color = "check style error";
        break;
      default:
        break;
    }
    JSONObject ob = new JSONObject();
    ob.put("data", array);
    ob.put("name", color);
    // ob.put("type", "spline");
    Response response = Response.ok().entity(ob.toString()).build();
    return response;
  }

  /**
   * get Commit Sum
   * 
   * @return sum
   */
  @GET
  @Path("count")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getCommitSum() {
    int[] array = db.getCommitSum(connection);
    JSONObject ob = new JSONObject();
    ob.put("data", array);
    ob.put("name", "commit counts");
    ob.put("type", "column");
    Response response = Response.ok().entity(ob.toString()).build();
    return response;
  }

  /**
   * update stu project commit record
   * 
   * @param userName
   *          stu id
   * @param proName
   *          project name
   */
  @POST
  @Path("update")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public void updateCommitResult(@FormParam("user") String userName,
      @FormParam("proName") String proName) {
    proName = proName.toUpperCase();

    IDatabase database = new MySqlDatabase();
    Connection connection = database.getConnection();

    JenkinsService jenkins = new JenkinsService();
    StudentDashChoosePro stuDashChoPro = new StudentDashChoosePro();
    JenkinsConfig jenkinsData = JenkinsConfig.getInstance();

    UserDbManager db = UserDbManager.getInstance();
    CommitResultDbManager commiResulttDb = CommitResultDbManager.getInstance();

    String[] result = jenkins.getColor(proName, userName).split(",");

    String color = result[0].replace("circle ", "");
    int commit = Integer.valueOf(result[1]) - 1;

    List<Integer> buildNum = stuDashChoPro.getScmBuildCounts(userName, proName);
    int num = 0;
    if (buildNum.size() > 1) {
      num = buildNum.size() - 1;
    }
    if (color.equals("red")) {
      String style = checkErrorStyle(jenkinsData, userName, proName, num);
      boolean ifCheckStyle = style.contains("Checkstyle violation");
      if (ifCheckStyle) {
        color = "orange";
      }
    }

    String hw = proName.replace("OOP-HW", "");

    String buildApiJson = stuDashChoPro.getBuildApiJson(buildNum.get(num), userName, proName);
    String strDate = stuDashChoPro.getCommitTime(buildApiJson);
    int id = db.getUser(userName).getId();

    boolean check = commiResulttDb.checkJenkinsJobTimestamp(connection, id, hw);
    if (check) {
      commiResulttDb.updateJenkinsCommitCount(connection, id, hw, commit, color);
      commiResulttDb.updateJenkinsJobTimestamp(connection, id, hw, strDate);
    } else {
      commiResulttDb.insertJenkinsCommitCount(connection, id, hw, commit, color);
      commiResulttDb.updateJenkinsJobTimestamp(connection, id, hw, strDate);
    }

    CommitRecordDbManager commitRecordDb = CommitRecordDbManager.getInstance();
    boolean inDb = commitRecordDb.checkRecord(connection, id, hw, color, strDate);
    if (!inDb) {
      commitRecordDb.insertCommitRecord(connection, id, hw, color, strDate);
    }

    try {
      connection.close();
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * check if error is checkstyle error
   * 
   * @param jenkinsData
   *          connect jenkins
   * @param userName
   *          stu id
   * @param proName
   *          project name
   * @param num
   *          build number
   * @return result string
   */
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
