package fcu.selab.progedu.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class CommitRecordDbManager {

  private static CommitRecordDbManager DB_MANAGER = new CommitRecordDbManager();

  public static CommitRecordDbManager getInstance() {
    return DB_MANAGER;
  }

  private IDatabase database = new MySqlDatabase();

  /**
   * insert student commit records into db
   *
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
  public boolean insertCommitRecord(int stuId, String hw, String color, String date, String time) {
    Connection conn = database.getConnection();
    PreparedStatement preStmt = null;
    String sql = "INSERT INTO Commit_Record" + "(stuId, hw, color, date, time) "
        + "VALUES(?, ?, ?, ?, ?)";
    boolean check = false;

    try {
      preStmt = conn.prepareStatement(sql);
      preStmt.setInt(1, stuId);
      preStmt.setString(2, hw);
      preStmt.setString(3, color);
      preStmt.setString(4, date);
      preStmt.setString(5, time);

      preStmt.executeUpdate();
      preStmt.close();

      check = true;
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        preStmt.close();
        conn.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    return check;
  }

  /**
   * get all counts
   *
   * @return counts
   */
  public List<Integer> getCounts(String color) {
    Connection conn = database.getConnection();
    String query = "SELECT hw,count(color) FROM Commit_Record where color like ? group by hw";
    PreparedStatement preStmt = null;
    List<Integer> array = new ArrayList<Integer>();

    try {
      preStmt = conn.prepareStatement(query);
      preStmt.setString(1, color);
      ResultSet rs = preStmt.executeQuery();
      while (rs.next()) {
        array.add(rs.getInt("count(color)"));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        preStmt.close();
        conn.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    return array;
  }

  /**
   * get each hw's CommitRecordStateCounts
   * 
   * @param hw
   *          hw number
   * @return map
   */
  public Map<String, Integer> getCommitRecordStateCounts(String hw) {
    Connection conn = database.getConnection();
    String query = "SELECT hw,color,count(color) FROM Commit_Record where hw = ? group by color";
    PreparedStatement preStmt = null;

    Map<String, Integer> map = new HashMap<>();

    try {
      preStmt = conn.prepareStatement(query);
      preStmt.setString(1, hw);
      ResultSet rs = preStmt.executeQuery();
      while (rs.next()) {
        map.put(rs.getString("color"), rs.getInt("count(color)"));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        preStmt.close();
        conn.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    return map;
  }

  /**
   * check if record is in db
   *
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
  public boolean checkRecord(int stuId, String hw, String color, String date, String time) {
    Connection conn = database.getConnection();
    String query = "SELECT * FROM Commit_Record where stuId=? and hw=? and date=? and time=?";
    PreparedStatement preStmt = null;
    boolean check = false;

    try {
      preStmt = conn.prepareStatement(query);
      preStmt.setInt(1, stuId);
      preStmt.setString(2, hw);
      preStmt.setString(3, date);
      preStmt.setString(4, time);
      ResultSet rs = preStmt.executeQuery();
      while (rs.next()) {
        check = true;
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        preStmt.close();
        conn.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    return check;
  }

  /**
   * update record color
   * 
   * @param stuId
   *          student
   * @param hw
   *          hw
   * @param color
   *          color
   * @param date
   *          date
   * @param time
   *          time
   */
  public void updateRecordStatus(int stuId, String hw, String color, String date, String time) {
    Connection conn = database.getConnection();
    String sql = "UPDATE Commit_Record SET color=? where stuId=? and hw=? and date=? and time=?";
    PreparedStatement preStmt = null;

    try {
      preStmt = conn.prepareStatement(sql);
      preStmt.setString(1, color);
      preStmt.setInt(2, stuId);
      preStmt.setString(3, hw);
      preStmt.setString(4, date);
      preStmt.setString(5, time);

      preStmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        preStmt.close();
        conn.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
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
    String query = "select date, time, count(color) from Commit_Record where hw=? "
        + "group by date, time";
    PreparedStatement preStmt = null;
    JSONArray records = new JSONArray();

    try {
      preStmt = conn.prepareStatement(query);
      preStmt.setString(1, hw);
      ResultSet rs = preStmt.executeQuery();
      while (rs.next()) {
        String date = rs.getString("date");
        String time = rs.getString("time");
        String[] times = time.split(":");
        final double timeValue = Integer.valueOf(times[0]) + (Integer.valueOf(times[1])) * 0.01;

        Timestamp ts = new Timestamp(System.currentTimeMillis());
        ts = Timestamp.valueOf(date + " " + time);

        Timestamp xlabel = new Timestamp(System.currentTimeMillis());
        xlabel = Timestamp.valueOf(date + " " + time);
        xlabel.setHours(0);
        xlabel.setMinutes(0);
        xlabel.setSeconds(0);
        int count = rs.getInt("count(color)");
        JSONObject record = new JSONObject();
        // record.put("x", date.substring(date.length() - 2));
        record.put("x", xlabel.getTime());
        record.put("y", timeValue);
        record.put("r", count);
        record.put("t", ts.getTime());
        records.put(record);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        preStmt.close();
        conn.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    return records;
  }

  /**
   * delete built record of specific hw
   *
   * @param hw
   *          hw
   */
  public void deleteRecord(String hw) {
    Connection conn = database.getConnection();
    PreparedStatement preStmt = null;
    String sql = "DELETE FROM Commit_Record WHERE hw=?";

    try {
      preStmt = conn.prepareStatement(sql);
      preStmt.setString(1, hw);

      preStmt.executeUpdate();
      preStmt.close();

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        preStmt.close();
        conn.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }
}
