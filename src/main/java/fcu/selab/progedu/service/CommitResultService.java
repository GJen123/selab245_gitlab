package fcu.selab.progedu.service;

import java.sql.Connection;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

import fcu.selab.progedu.db.CommitResultDbManager;
import fcu.selab.progedu.db.IDatabase;
import fcu.selab.progedu.db.MySqlDatabase;

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

  public void getCommitTimestamp() {

  }
}
