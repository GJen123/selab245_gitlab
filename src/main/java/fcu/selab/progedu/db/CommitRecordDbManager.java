package fcu.selab.progedu.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class CommitRecordDbManager {

  private static CommitRecordDbManager DB_MANAGER = new CommitRecordDbManager();

  public static CommitRecordDbManager getInstance() {
    return DB_MANAGER;
  }

  private CommitRecordDbManager() {

  }

  private ProjectDbManager pdb = ProjectDbManager.getInstance();
  private IDatabase database = new MySqlDatabase();

  /**
   * insert student commit records into db
   * 
   * @param conn
   *          db connection
   * @param stuId
   *          studrnt id
   * @param hw
   *          hw number
   * @param color
   *          build result
   * @param time
   *          commit time
   * @return check
   */
  public boolean insertCommitRecord(Connection conn, int stuId, String hw, String color,
      String time) {
    PreparedStatement preStmt = null;
    String sql = "INSERT INTO Commit_Record" + "(stuId, hw, color, time) " + "VALUES(?, ?, ?, ?)";
    boolean check = false;

    try {
      preStmt = conn.prepareStatement(sql);
      preStmt.setInt(1, stuId);
      preStmt.setString(2, hw);
      preStmt.setString(3, color);
      preStmt.setString(4, time);

      preStmt.executeUpdate();
      preStmt.close();

      check = true;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return check;
  }

  /**
   * get all counts
   * 
   * @param conn
   *          db connection
   * @return counts
   */
  public int[] getCounts(Connection conn, String color) {
    String query = "SELECT hw,count(color) FROM Commit_Record where color like ? group by hw";
    PreparedStatement preStmt = null;
    List<String> lsProjects = pdb.listAllProjectNames();
    int plength = lsProjects.size();
    int[] counts = new int[plength];

    try {
      preStmt = conn.prepareStatement(query);
      preStmt.setString(1, color);
      ResultSet rs = preStmt.executeQuery();
      while (rs.next()) {
        int index = Integer.valueOf(rs.getString("hw"));
        counts[index - 1] = rs.getInt("count(color)");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        conn.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    return counts;
  }

  /**
   * check if record is in db
   * 
   * @param conn
   *          db connection
   * @param stuId
   *          student id
   * @param hw
   *          he number
   * @param color
   *          color
   * @param time
   *          commit time
   * @return boolean
   */
  public boolean checkRecord(Connection conn, int stuId, String hw, String color, String time) {
    String query = "SELECT * FROM Commit_Record where stuId=? and hw=? and color=? and time=?";
    PreparedStatement preStmt = null;
    boolean check = false;

    try {
      preStmt = conn.prepareStatement(query);
      preStmt.setInt(1, stuId);
      preStmt.setString(2, hw);
      preStmt.setString(3, color);
      preStmt.setString(4, time);
      ResultSet rs = preStmt.executeQuery();
      while (rs.next()) {
        check = true;
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return check;
  }

  /**
   * get Count Group By Hw And Time
   * 
   * @param hw
   *          hw number
   * @return records
   */
  public JSONArray getCountGroupByHwAndTime(String hw) {
    Connection conn = database.getConnection();
    String query = "select time, count(color) from Commit_Record where hw=? group by time";
    PreparedStatement preStmt = null;
    boolean check = false;
    JSONArray records = new JSONArray();

    JSONArray arr = new JSONArray();

    try {
      preStmt = conn.prepareStatement(query);
      preStmt.setString(1, hw);
      ResultSet rs = preStmt.executeQuery();
      while (rs.next()) {
        String[] dateString = rs.getString("time").split(" ");
        String date = dateString[0];
        String time = dateString[1].substring(0, dateString[1].length() - 2);
        int count = rs.getInt("count(color)");

        JSONObject record = new JSONObject();
        record.put("x", rs.getString("time").substring(0, rs.getString("time").length() - 2));
        // record.put("time", time);
        record.put("y", count);
        records.put(record);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return records;
  }
}
