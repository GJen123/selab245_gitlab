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
  private static ProjectDbManager projectDb = ProjectDbManager.getInstance();

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

    String sql = "INSERT INTO " + "Commit_Record_State(hw, success, checkStyleError, compileFailure"
        + ", testFailure, notBuild, commitCounts)  " + "VALUES(?, ?, ?, ?, ?, ?, ?)";

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
  public void updateCommitRecordState(String hw, int success, int csf, int cpf, int ctf, int nb,
      int ccs) {

    Connection conn = database.getConnection();
    PreparedStatement preStmt = null;

    String sql = "UPDATE " + "Commit_Record_State  SET success = ? ,checkStyleError = ? "
        + ", compileFailure = ? , testFailure = ? , notBuild = ? , commitCounts = ?  "
        + "where hw = ? ";

    try {
      preStmt = conn.prepareStatement(sql);

      preStmt.setInt(1, success);
      preStmt.setInt(2, csf);
      preStmt.setInt(3, cpf);
      preStmt.setInt(4, ctf);
      preStmt.setInt(5, nb);
      preStmt.setInt(6, ccs);
      preStmt.setString(7, hw);

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
   * @return state's counts
   */
  public List<Integer> getCommitRecordStateCounts(String state) {
    Connection conn = database.getConnection();
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
   * check if hw exists in Commit_Record_State DB table
   * @param hw
   *          hw name
   * 
   * @return check result (boolean)
   */
  public boolean checkCommitRecordStatehw(String hw) {

    Connection conn = database.getConnection();
    String query = "SELECT hw FROM Commit_Record_State where hw=?";
    PreparedStatement preStmt = null;
    ResultSet rs = null;

    boolean check = false;

    try {
      preStmt = conn.prepareStatement(query);
      preStmt.setString(1, hw);
      rs = preStmt.executeQuery();

      while (rs.next()) {
        check = true;
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
    return check;
  }

  /**
   * get commits sum group by hw
   * @return commits sum
   */

  public List<Integer> getCommitSum() {
    Connection conn = database.getConnection();
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
