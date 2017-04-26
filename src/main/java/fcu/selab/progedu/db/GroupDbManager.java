package fcu.selab.progedu.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import fcu.selab.progedu.data.Group;

public class GroupDbManager {

  private static GroupDbManager DB_MANAGER = new GroupDbManager();

  public static GroupDbManager getInstance() {
    return DB_MANAGER;
  }

  private IDatabase database = new MySqlDatabase();

  private GroupDbManager() {

  }

  private UserDbManager udb = UserDbManager.getInstance();

  /**
   * Insert group into database
   * 
   * @param gname group's name
   * @param userName student id
   * @param isLeader whether student is team leader or not
   */
  public void addGroup(String gname, String userName, boolean isLeader) {

    Connection conn = database.getConnection();
    PreparedStatement preStmt = null;
    Statement stmt = null;
    String sql = "INSERT INTO Team(name, sId, isLeader)  " + "VALUES(?, ?, ?)";
    String query = "SELECT * FROM Team";

    try {
      int sid = -1;
      sid = udb.getUser(userName).getId();
      preStmt = conn.prepareStatement(sql);
      preStmt.setString(1, gname);
      preStmt.setInt(2, sid);
      preStmt.setBoolean(3, isLeader);
      preStmt.executeQuery();
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
