package fcu.selab.progedu.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import fcu.selab.progedu.data.CommitResult;
import fcu.selab.progedu.data.User;
import org.json.JSONArray;
import org.json.JSONObject;

public class CommitResultDbManager {

  private static CommitResultDbManager DB_MANAGER = new CommitResultDbManager();

  public static CommitResultDbManager getInstance() {
    return DB_MANAGER;
  }

  private CommitResultDbManager() {

  }

  private ProjectDbManager pdb = ProjectDbManager.getInstance();
  private UserDbManager udb = UserDbManager.getInstance();

  /**
   * aggregate jenkins situation
   * 
   * @param conn
   *          db connection
   * @param id
   *          student id
   * @param hw
   *          hw name
   * @param commit
   *          commit count
   * @param color
   *          build color
   * @return check
   */
  public boolean insertJenkinsCommitCount(Connection conn, int id, String hw, int commit,
      String color) {
    PreparedStatement preStmt = null;
    String sql = "INSERT INTO Commit_Result" + "(stuId, hw, commit, color) " + "VALUES(?, ?, ?, ?)";
    boolean check = false;

    try {
      preStmt = conn.prepareStatement(sql);
      preStmt.setInt(1, id);
      preStmt.setString(2, hw);
      preStmt.setInt(3, commit);
      preStmt.setString(4, color);

      preStmt.executeUpdate();
      preStmt.close();

      check = true;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return check;
  }

  /**
   * insert job last commit time to db
   * 
   * @param conn
   *          db connection
   * @param id
   *          stu id
   * @param hw
   *          hw number
   * @param time
   *          commit time
   * @return check
   */
  public boolean updateJenkinsJobTimestamp(Connection conn, int id, String hw, String time) {
    PreparedStatement preStmt = null;
    String sql = "UPDATE Commit_Result SET time=? WHERE stuId=? AND hw=?";
    boolean check = false;

    try {
      preStmt = conn.prepareStatement(sql);
      preStmt.setString(1, time);
      preStmt.setInt(2, id);
      preStmt.setString(3, hw);

      preStmt.executeUpdate();
      preStmt.close();

      check = true;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return check;
  }

  /**
   * check if result is in db
   * 
   * @param conn
   *          db connection
   * @param id
   *          student id
   * @param hw
   *          he number
   * @return boolean
   */
  public boolean checkJenkinsJobTimestamp(Connection conn, int id, String hw) {
    PreparedStatement preStmt = null;
    String query = "SELECT * FROM Commit_Result WHERE stuId=? AND hw=?";
    boolean check = false;

    try {
      preStmt = conn.prepareStatement(query);
      preStmt.setInt(1, id);
      preStmt.setString(2, hw);

      ResultSet rs = preStmt.executeQuery();
      while (rs.next()) {
        check = true;
      }
      preStmt.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return check;
  }

  /**
   * update jenkins situation
   * 
   * @param conn
   *          db connection
   * @param id
   *          student id
   * @param hw
   *          hw name
   * @param commit
   *          commit count
   * @param color
   *          build color
   * @return check
   */
  public boolean updateJenkinsCommitCount(Connection conn, int id, String hw, int commit,
      String color) {
    PreparedStatement preStmt = null;
    String sql = "UPDATE Commit_Result SET commit=?, color=? WHERE stuId=? AND hw=?";
    boolean check = false;

    try {
      preStmt = conn.prepareStatement(sql);
      preStmt.setInt(1, commit);
      preStmt.setString(2, color);
      preStmt.setInt(3, id);
      preStmt.setString(4, hw);

      preStmt.executeUpdate();
      preStmt.close();

      check = true;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return check;
  }

  /**
   * get student all project commit counts
   * 
   * @param stuId
   *          student id
   * @return counts
   */
  public List<Integer> getCommit(Connection conn, int stuId) {
    List<Integer> commits = new ArrayList<Integer>();

    String query = "SELECT * FROM Commit_Result where stuId = ?";
    PreparedStatement preStmt = null;

    try {
      preStmt = conn.prepareStatement(query);
      preStmt.setInt(1, stuId);
      ResultSet rs = preStmt.executeQuery();
      while (rs.next()) {

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

    return commits;
  }

  /**
   * get all counts
   * 
   * @param conn
   *          db connection
   * @return counts
   */
  public List<Integer> getCounts(Connection conn, String color) {
    String query = "SELECT hw,count(color) FROM Commit_Result " + "where color like ? group by hw";
    PreparedStatement preStmt = null;
    List<Integer> array;
    array = new ArrayList<Integer>();

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
        conn.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    return array;
  }

  /**
   * get commits sum group by hw
   * 
   * @param conn
   *          db connection
   * @return commits sum
   */

  public List<Integer> getCommitSum(Connection conn) {
    String query = "SELECT commitCounts FROM Commit_Record_State";
    PreparedStatement preStmt = null;
    ResultSet rs = null;
    List<Integer> array = new ArrayList<Integer>();

    try {
      preStmt = conn.prepareStatement(query);
      rs = preStmt.executeQuery();
      while (rs.next()) {
        array.add(rs.getInt("commitCounts"));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        preStmt.close();
        rs.close();
        conn.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    return array;
  }

  /**
   * get commit result by student
   * 
   * @param conn
   *          db connection
   * @param id
   *          stuId
   * @param hw
   *          hw
   * @return commit result
   */
  public CommitResult getCommitResultByStudentAndHw(Connection conn, int id, String hw) {
    PreparedStatement preStmt = null;
    String query = "SELECT * FROM Commit_Result WHERE stuId=? AND hw=?";
    CommitResult result = new CommitResult();

    try {
      preStmt = conn.prepareStatement(query);
      preStmt.setInt(1, id);
      preStmt.setString(2, hw);

      ResultSet rs = preStmt.executeQuery();
      while (rs.next()) {
        String color = rs.getString("color");
        int commit = rs.getInt("commit");

        result.setStuId(id);
        result.setHw(hw);
        result.setColor(color);
        result.setCommit(commit);
      }
      preStmt.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return result;
  }
  /**
   * get commit result by student
   *
   * @param conn
   *          db connection
   * @param id
   *          stuId
   * @return commit result
   */
  public JSONObject getCommitResultByStudent(Connection conn, int id) {
    PreparedStatement preStmt = null;
    String query = "SELECT * FROM Commit_Result WHERE stuId=?";
    JSONObject ob = new JSONObject();
    JSONArray array = new JSONArray();
    String name = "";
    int gitlabId = -1;

    try {
      preStmt = conn.prepareStatement(query);
      preStmt.setInt(1, id);

      ResultSet rs = preStmt.executeQuery();
      while (rs.next()) {

        String hw = rs.getString("hw");

        User user = udb.getUser(conn, id);
        name = user.getUserName();
        gitlabId = user.getGitLabId();

        String color = rs.getString("color");
        int commit = rs.getInt("commit");

        JSONObject eachHw = new JSONObject();
        eachHw.put("hw", hw);
        eachHw.put("commit", commit);
        eachHw.put("color", color);
        array.put(eachHw);
      }

      ob.put("userName", name);
      ob.put("gitlabId", gitlabId);
      ob.put("commits", array);

      preStmt.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return ob;
  }

  /**
   * list all commir result
   * @param conn db connection
   * @return array
   */
  public JSONArray listAllCommitResult(Connection conn) {
    PreparedStatement preStmt = null;
    String query = "SELECT * FROM Commit_Result";
    JSONArray array = new JSONArray();

    try {
      preStmt = conn.prepareStatement(query);

      ResultSet rs = preStmt.executeQuery();
      while (rs.next()) {
        JSONObject ob = new JSONObject();
        int id = rs.getInt("stuId");
        String color = rs.getString("color");
        int commit = rs.getInt("commit");

        User user = udb.getUser(conn, id);

        ob.put("user", user.getUserName());
        ob.put("color", color);
        ob.put("commit", commit + 1);
        array.put(ob);
      }
      preStmt.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return array;
  }

  /**
   * get hw commit timestamp
   * 
   * @param conn
   *          db connection
   * @param hw
   *          hw number
   */
  public void getCommitTimestamp(Connection conn, String hw) {
    String query = "SELECT hw, count(commit), time FROM Commit_Result"
        + "where hw=? and commit!=0 group by time";
    PreparedStatement preStmt = null;

    String[] csvTitle = { "date", "time", "commit" };
    StringBuilder build = new StringBuilder();
    for (int i = 0; i < csvTitle.length; i++) {
      build.append(csvTitle[i]);
      if (i == csvTitle.length) {
        break;
      }
      build.append(",");
    }
    build.append("\n");

    try {
      preStmt = conn.prepareStatement(query);
      preStmt.setString(1, hw);
      ResultSet rs = preStmt.executeQuery();
      while (rs.next()) {
        int commit = rs.getInt("count(commit)");
        String fullTime = rs.getString("time");
        String date = fullTime.substring(0, 10);
        String time = fullTime.substring(11);

        build.append(date);
        build.append(",");
        build.append(time);
        build.append(",");
        build.append(commit);
        build.append("\n");
      }
      System.out.println(build.toString());
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        conn.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * delete build result of specific hw
   * 
   * @param conn
   *          db connection
   * @param hw
   *          hw
   */
  public void deleteResult(Connection conn, String hw) {
    PreparedStatement preStmt = null;
    String sql = "DELETE FROM Commit_Result WHERE hw=?";

    try {
      preStmt = conn.prepareStatement(sql);
      preStmt.setString(1, hw);

      preStmt.executeUpdate();
      preStmt.close();

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
