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
   * @param s
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
  public void addCommitRecordState(String hw, int s, int csf, int cpf, int ctf, int nb) {
    Connection conn = database.getConnection();
    PreparedStatement preStmt = null;
    Statement stmt = null;
    String sql = "INSERT INTO " + "Commit_Record_State(hw, S, CSF, CPF, CTF, NB)  "
        + "VALUES(?, ?, ?, ?, ?, ?)";
    String query = "SELECT * FROM CommitRecordState";

    try {
      preStmt = conn.prepareStatement(sql);

      preStmt.setString(1, hw);
      preStmt.setInt(2, s);
      preStmt.setInt(3, csf);
      preStmt.setInt(4, cpf);
      preStmt.setInt(5, ctf);
      preStmt.setInt(6, nb);

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
