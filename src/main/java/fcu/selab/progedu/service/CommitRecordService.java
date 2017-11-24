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

@Path("commits/record/")
public class CommitRecordService {
  CommitRecordDbManager commitRecordDb = CommitRecordDbManager.getInstance();
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
    JSONArray ob = new JSONArray();
    ob = commitRecordDb.getCountGroupByHwAndTime(hw);
    Response response = Response.ok().entity(ob.toString()).build();
    return response;
  }

}
