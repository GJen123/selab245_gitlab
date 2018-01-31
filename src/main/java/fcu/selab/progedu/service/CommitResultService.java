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
import fcu.selab.progedu.data.CommitResult;
import fcu.selab.progedu.db.CommitRecordDbManager;
import fcu.selab.progedu.db.CommitResultDbManager;
import fcu.selab.progedu.db.IDatabase;
import fcu.selab.progedu.db.MySqlDatabase;
import fcu.selab.progedu.db.UserDbManager;
import fcu.selab.progedu.exception.LoadConfigFailureException;
import fcu.selab.progedu.jenkins.JenkinsApi;

@Path("commits/")
public class CommitResultService {
  CommitResultDbManager db = CommitResultDbManager.getInstance();
  UserDbManager userDb = UserDbManager.getInstance();
  IDatabase database = new MySqlDatabase();

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
    Connection connection = database.getConnection();
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
    try {
      connection.close();
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
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
    Connection connection = database.getConnection();
    int[] array = db.getCommitSum(connection);
    JSONObject ob = new JSONObject();
    ob.put("data", array);
    ob.put("name", "commit counts");
    ob.put("type", "column");
    try {
      connection.close();
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    Response response = Response.ok().entity(ob.toString()).build();
    return response;
  }

  /**
   * get commit result by stuId and hw
   * 
   * @param proName
   *          project name
   * @param userName
   *          student id
   * @return hw, color, commit
   */
  @GET
  @Path("result")
  @Produces(MediaType.TEXT_PLAIN)
  public Response getCommitResultByStudentAndHw(@QueryParam("proName") String proName,
      @QueryParam("userName") String userName) {
    Connection connection = database.getConnection();
    int id = userDb.getUser(userName).getId();
    String hw = proName.replace("OOP-HW", "");
    CommitResult commitResult = db.getCommitResultByStudentAndHw(connection, id, hw);
    String circleColor = "circle " + commitResult.getColor();
    String result = userName + "_" + proName + "," + circleColor + ","
        + (commitResult.getCommit() + 1);

    try {
      connection.close();
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    Response response = Response.ok().entity(result).build();
    return response;
  }

  /**
   * get commit result by stuId and hw
   * 
   * @param userName
   *          student id
   * @param proName
   *          project name
   * @return color
   */
  public String getCommitResult(String userName, String proName) {
    Connection connection = database.getConnection();
    int id = userDb.getUser(userName).getId();
    String hw = proName.replace("OOP-HW", "");
    CommitResult commitResult = db.getCommitResultByStudentAndHw(connection, id, hw);
    String color = commitResult.getColor();

    try {
      connection.close();
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return color;
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

    JenkinsService jenkinsService = new JenkinsService();
    JenkinsApi jenkinsApi = new JenkinsApi();
    StudentDashChoosePro stuDashChoPro = new StudentDashChoosePro();
    JenkinsConfig jenkinsData = JenkinsConfig.getInstance();

    String[] result = jenkinsService.getColor(proName, userName).split(",");

    String color = result[0].replace("circle ", "");
    int commit = Integer.valueOf(result[1]) - 1;

    List<Integer> buildNum = stuDashChoPro.getScmBuildCounts(userName, proName);
    int num = 0;
    if (buildNum.size() > 1) {
      num = buildNum.size() - 1;
    }
    if (color.equals("red")) {
      String consoleText = checkErrorStyle(jenkinsData, userName, proName, buildNum.get(num));
      boolean isCheckStyle = jenkinsApi.checkIsCheckstyleError(consoleText);
      boolean isJunitError = jenkinsApi.checkIsJunitError(consoleText);
      System.out.println(isCheckStyle + ", " + isJunitError);
      if (isCheckStyle) {
        color = "orange";
      }
      if (isJunitError) {
        color = "green";
      }
    }
    if (color.contains("_anime")) {
      color = color.replaceAll("_anime", "");
    }

    String hw = proName.replace("OOP-HW", "");

    IDatabase database = new MySqlDatabase();
    Connection connection = database.getConnection();
    UserDbManager db = UserDbManager.getInstance();
    CommitResultDbManager commiResulttDb = CommitResultDbManager.getInstance();

    String buildApiJson = stuDashChoPro.getBuildApiJson(buildNum.get(num), userName, proName);
    String strDate = stuDashChoPro.getCommitTime(buildApiJson);
    String[] dates = strDate.split(" ");
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
    boolean inDb = commitRecordDb.checkRecord(connection, id, hw, color, dates[0], dates[1]);
    if (!inDb) {
      commitRecordDb.insertCommitRecord(connection, id, hw, color, dates[0], dates[1]);
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

  /**
   * delete build result of hw
   * 
   * @param hw
   *          hw
   */
  public void deleteResult(String hw) {
    IDatabase database = new MySqlDatabase();
    Connection connection = database.getConnection();

    String hwIndex = hw.replace("OOP-HW", "");
    db.deleteResult(connection, hwIndex);
  }
}
