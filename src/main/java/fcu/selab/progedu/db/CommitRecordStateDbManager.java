package fcu.selab.progedu.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CommitRecordStateDbManager {

  private static CommitRecordStateDbManager DB_MANAGER = new CommitRecordStateDbManager();

  public static CommitRecordStateDbManager getInstance() {
    return DB_MANAGER;
  }

  private IDatabase database = new MySqlDatabase();

  private CommitRecordStateDbManager() {

  }

  /**
   * add each hw CommitRecordState counts
   * 
   * @param hw
   *          hw's number
   * @param success
   *          build success
   * @param csf
   *          check style error
   * @param cpf
   *          build fault
   * @param ctf
   *          junit fault
   * @param nb
   *          not build
   */
  public void addCommitRecordState(String hw, int success, int csf, int cpf, int ctf, int nb,
      int ccs) {
    Connection conn = database.getConnection();
    PreparedStatement preStmt = null;
    Statement stmt = null;
    String sql = "INSERT INTO "
        + "Commit_Record_State(hw, success, checkStyleError, compileFailure"
        + ", testFailure, notBuild, commitCounts)  "
        + "VALUES(?, ?, ?, ?, ?, ?, ?)";
    String query = "SELECT * FROM CommitRecordState";

    try {
      preStmt = conn.prepareStatement(sql);

      preStmt.setString(1, hw);
      preStmt.setInt(2, success);
      preStmt.setInt(3, csf);
      preStmt.setInt(4, cpf);
      preStmt.setInt(5, ctf);
      preStmt.setInt(6, nb);
      preStmt.setInt(7, ccs);

      preStmt.executeUpdate();
      preStmt.close();

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
   * get all state counts
   * 
   * @param conn
   *          db connection
   * @return state's counts
   */
  public List<Integer> getCommitRecordStateCounts(Connection conn, String state) {
    String query = "SELECT * FROM Commit_Record_State";
    PreparedStatement preStmt = null;
    ResultSet rs = null;
    List<Integer> array = new ArrayList<Integer>();

    try {
      preStmt = conn.prepareStatement(query);
      rs = preStmt.executeQuery();
      while (rs.next()) {
        array.add(rs.getInt(state));
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

}
