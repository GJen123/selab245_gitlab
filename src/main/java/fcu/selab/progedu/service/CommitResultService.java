package fcu.selab.progedu.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import fcu.selab.progedu.config.CourseConfig;
import fcu.selab.progedu.config.JenkinsConfig;
import fcu.selab.progedu.conn.StudentDashChoosePro;
import fcu.selab.progedu.data.CommitResult;
import fcu.selab.progedu.data.User;
import fcu.selab.progedu.db.CommitRecordDbManager;
import fcu.selab.progedu.db.CommitRecordStateDbManager;
import fcu.selab.progedu.db.CommitResultDbManager;
import fcu.selab.progedu.db.IDatabase;
import fcu.selab.progedu.db.MySqlDatabase;
import fcu.selab.progedu.db.ProjectDbManager;
import fcu.selab.progedu.db.UserDbManager;
import fcu.selab.progedu.exception.LoadConfigFailureException;
import fcu.selab.progedu.jenkins.JenkinsApi;

@Path("commits/")
public class CommitResultService {
  CommitResultDbManager db = CommitResultDbManager.getInstance();
  CommitRecordDbManager commitRecordDb = CommitRecordDbManager.getInstance();
  CommitRecordStateDbManager crsdb = CommitRecordStateDbManager.getInstance();
  UserDbManager userDb = UserDbManager.getInstance();
  ProjectDbManager projectDb = ProjectDbManager.getInstance();

  /**
   * get counts by different color.
   * 
   * @param color
   *          color
   * @return counts
   */
  @GET
  @Path("color")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getCounts(@QueryParam("color") String color) {
    JSONObject commitCounts = db.getCounts(color);
    List<Integer> counts = new ArrayList<Integer>();
    List<String> pnames = projectDb.listAllProjectNames();

    for (String pname : pnames) {
      int count = commitCounts.optInt(pname);
      counts.add(count);
    }

    switch (color) {
      case "S":
        color = "success";
        break;
      case "CPF":
        color = "compile failure";
        break;
      case "CSF":
        color = "checkstyle failure";
        break;
      case "CTF":
        color = "JUnit failure";
        break;
      case "NB":
        color = "not build";
        break;
      default:
        break;
    }

    JSONObject ob = new JSONObject();
    ob.put("data", counts);
    ob.put("name", color);
    Response response = Response.ok().entity(ob.toString()).build();
    return response;
  }

  /**
   * get Commit Sum.
   * 
   * @return sum
   */
  @GET
  @Path("count")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getCommitSum() {
    List<Integer> array = crsdb.getCommitSum();
    JSONObject ob = new JSONObject();
    ob.put("data", array);
    ob.put("name", "commit counts");
    ob.put("type", "column");
    Response response = Response.ok().entity(ob.toString()).build();
    return response;
  }

  /**
   * get commit result by stuId and hw.
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
    int id = userDb.getUser(userName).getId();

    CommitResult commitResult = db.getCommitResultByStudentAndHw(id, proName);
    String circleColor = "circle " + commitResult.getColor();
    String result = userName + "_" + proName + "," + circleColor + ","
        + (commitResult.getCommit() + 1);

    Response response = Response.ok().entity(result).build();
    return response;
  }

  /**
   * get all commit result.
   *
   * @return hw, color, commit
   */
  @GET
  @Path("all")
  @Produces(MediaType.TEXT_PLAIN)
  public Response getCommitResult() {
    JSONArray array = new JSONArray();
    JSONObject result = new JSONObject();

    List<User> users = userDb.listAllUsers();
    for (User user : users) {
      JSONObject ob = db.getCommitResultByStudent(user.getId());
      array.put(ob);
    }
    result.put("result", array);

    Response response = Response.ok().entity(result.toString()).build();
    return response;
  }

  /**
   * get commit result by stuId and hw.
   * 
   * @param userName
   *          student id
   * @param proName
   *          project name
   * @return color
   */
  public String getCommitResult(String userName, String proName) {
    int id = userDb.getUser(userName).getId();
    String courseName = "";
    try {
      courseName = CourseConfig.getInstance().getCourseName();
    } catch (LoadConfigFailureException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    CommitResult commitResult = db.getCommitResultByStudentAndHw(id, proName);
    String color = commitResult.getColor();

    return color;
  }

  /**
   * update stu project commit record.
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
      if (isCheckStyle) {
        color = "CSF";
      }
      if (isJunitError) {
        color = "CTF";
      }
    }
    if (color.contains("_anime")) {
      color = color.replaceAll("_anime", "");
    }

    String buildApiJson = stuDashChoPro.getBuildApiJson(buildNum.get(num), userName, proName);
    String strDate = stuDashChoPro.getCommitTime(buildApiJson);
    String[] dates = strDate.split(" ");
    int id = userDb.getUser(userName).getId();

    switch (color) {
      case "blue":
        color = "S";
        break;
      case "red":
        color = "CPF";
        break;
      case "gray":
        color = "NB";
        break;

      default:
        break;
    }

    boolean check = db.checkJenkinsJobTimestamp(id, proName);
    if (check) {
      db.updateJenkinsCommitCount(id, proName, commit, color);
      db.updateJenkinsJobTimestamp(id, proName, strDate);
    } else {
      db.insertJenkinsCommitCount(id, proName, commit, color);
      db.updateJenkinsJobTimestamp(id, proName, strDate);
    }

    boolean inDb = commitRecordDb.checkRecord(id, proName, color, dates[0], dates[1]);
    if (!inDb) {
      commitRecordDb.insertCommitRecord(id, proName, color, dates[0], dates[1]);
    }

    updateCommitRecordState();
  }

  /**
   * update Commit_Record_State DB's data.
   */
  private void updateCommitRecordState() {
    // TODO Auto-generated method stub

    List<String> lsNames = new ArrayList<String>();
    lsNames = projectDb.listAllProjectNames();

    for (String name : lsNames) {

      Map<String, Integer> map = new HashMap<>();

      map = commitRecordDb.getCommitRecordStateCounts(name);

      int success = 0;
      int nb = 0;
      int ctf = 0;
      int csf = 0;
      int cpf = 0;

      if (map.containsKey("S")) {
        success = map.get("S");
      }

      if (map.containsKey("NB")) {
        nb = map.get("NB");
      }

      if (map.containsKey("CTF")) {
        ctf = map.get("CTF");
      }

      if (map.containsKey("CSF")) {
        csf = map.get("CSF");
      }

      if (map.containsKey("CPF")) {
        cpf = map.get("CPF");
      }

      int ccs = 0;
      ccs = success + ctf + csf + cpf;

      boolean check;
      check = crsdb.checkCommitRecordStatehw(name);

      if (check) {
        crsdb.updateCommitRecordState(name, success, csf, cpf, ctf, nb, ccs);

      } else {
        crsdb.addCommitRecordState(name, success, csf, cpf, ctf, nb, ccs);
      }

    }

  }

  /**
   * check if error is checkstyle error.
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
   * delete build result of hw.
   * 
   * @param hw
   *          hw
   */
  public void deleteResult(String hw) {
    IDatabase database = new MySqlDatabase();
    Connection connection = database.getConnection();
    db.deleteResult(hw);
  }
}
