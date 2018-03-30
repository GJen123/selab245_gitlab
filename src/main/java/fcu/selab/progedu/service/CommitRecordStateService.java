package fcu.selab.progedu.service;

//public class CommitRecordStateService {
//
//}

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

import fcu.selab.progedu.db.CommitRecordDbManager;
import fcu.selab.progedu.db.CommitRecordStateDbManager;
import fcu.selab.progedu.db.IDatabase;
import fcu.selab.progedu.db.MySqlDatabase;
import fcu.selab.progedu.db.ProjectDbManager;

@Path("commits/")
public class CommitRecordStateService {
  CommitRecordStateDbManager commitRecordStateDb = CommitRecordStateDbManager.getInstance();
  CommitRecordDbManager commitRecordDb = CommitRecordDbManager.getInstance();
  ProjectDbManager projectDb = ProjectDbManager.getInstance();

  IDatabase database = new MySqlDatabase();
  Connection connection = database.getConnection();

  /**
   * get counts by different state
   * 
   * @param state
   * 
   * @return counts
   */
  @GET
  @Path("state/")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getCommitRecordStateCounts(@QueryParam("state") String state) {
    List<Integer> array = commitRecordStateDb.getCommitRecordStateCounts(connection, state);
    JSONObject ob = new JSONObject();
    ob.put("data", array);
    ob.put("name", state);
    Response response = Response.ok().entity(ob.toString()).build();
    return response;
  }

  /**
   * update data in Commit_Record_State table
   */
  public void updateCommitRecordState() {

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

      commitRecordStateDb.addCommitRecordState(name, success, csf, cpf, ctf, nb, ccs);

    }

  }

}
