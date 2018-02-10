package fcu.selab.progedu.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

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
   * @param blue
   *          build success
   * @param orange
   *          check style error
   * @param red
   *          build fault
   * @param green
   *          junit fault
   * @param gray
   *          not build
   */
  public void addCommitRecordState(int hw, int blue, int orange, int red, int green, int gray) {
    Connection conn = database.getConnection();
    PreparedStatement preStmt = null;
    Statement stmt = null;
    String sql = "INSERT INTO " + "Commit_Record_State(hw, blue, orange, red, green, gray)  "
        + "VALUES(?, ?, ?, ?, ?, ?)";
    String query = "SELECT * FROM CommitRecordState";

    try {
      preStmt = conn.prepareStatement(sql);

      preStmt.setInt(1, hw);
      preStmt.setInt(2, blue);
      preStmt.setInt(3, orange);
      preStmt.setInt(4, red);
      preStmt.setInt(5, green);
      preStmt.setInt(6, gray);

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

}
