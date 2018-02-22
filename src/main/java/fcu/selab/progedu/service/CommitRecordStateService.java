package fcu.selab.progedu.service;

//public class CommitRecordStateService {
//
//}

import java.sql.Connection;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

import fcu.selab.progedu.db.CommitRecordStateDbManager;
import fcu.selab.progedu.db.IDatabase;
import fcu.selab.progedu.db.MySqlDatabase;

@Path("commits/state/")
public class CommitRecordStateService {
  CommitRecordStateDbManager commitRecordStatedDb = CommitRecordStateDbManager.getInstance();
  IDatabase database = new MySqlDatabase();
  Connection connection = database.getConnection();

  /**
   * get counts by different state
   * 
   * @param state
   * 
   * @return counts
   */
  @GETProgEdu.
  @Path("color")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getCommitRecordStateCounts(@QueryParam("state") String state) {
    List<Integer> array = commitRecordStatedDb.getCommitRecordStateCounts(connection, state);
    JSONObject ob = new JSONObject();
    ob.put("data", array);
    ob.put("name", state);
    Response response = Response.ok().entity(ob.toString()).build();
    return response;
  }

}
