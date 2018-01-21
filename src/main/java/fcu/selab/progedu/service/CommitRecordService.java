package fcu.selab.progedu.service;

import java.sql.Connection;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import fcu.selab.progedu.db.CommitRecordDbManager;
import fcu.selab.progedu.db.IDatabase;
import fcu.selab.progedu.db.MySqlDatabase;
import fcu.selab.progedu.db.ProjectDbManager;

@Path("commits/record/")
public class CommitRecordService {
  CommitRecordDbManager commitRecordDb = CommitRecordDbManager.getInstance();
  ProjectDbManager pdb = ProjectDbManager.getInstance();
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
    int[] array = commitRecordDb.getCounts(connection, color);
    switch (color) {
      case "blue":
        color = "build success";
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
    Response response = Response.ok().entity(ob.toString()).build();
    return response;
  }

  /**
   * get Count Group By Hw And Time
   * 
   * @param hw
   *          hw number
   * @return records
   */
  @GET
  @Path("records")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getCountGroupByHwAndTime(@QueryParam("hw") String hw) {
    JSONObject ob = new JSONObject();
    JSONArray records = commitRecordDb.getCountGroupByHwAndTime(hw);
    String deadline = pdb.getProjectByName("OOP-HW" + hw).getDeadline();
    ob.put("records", records);
    ob.put("title", "HW" + hw);
    ob.put("deadline", deadline);
    Response response = Response.ok().entity(ob.toString()).build();
    return response;
  }

  /**
   * delete build record of hw
   * 
   * @param hw
   *          hw
   */
  public void deleteRecord(String hw) {
    IDatabase database = new MySqlDatabase();
    Connection connection = database.getConnection();

    String hwIndex = hw.replace("OOP-HW", "");
    commitRecordDb.deleteRecord(connection, hwIndex);
  }

}
