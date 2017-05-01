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
   * add group member into db
   * 
   * @param groupName group name
   * @param userName member name
   * @param isLeader whether current member is leader or not
   */
  public void addGroup(String groupName, String userName, boolean isLeader) {

    Connection conn = database.getConnection();
    PreparedStatement preStmt = null;
    Statement stmt = null;
    String sql = "INSERT INTO Team(name, sId, isLeader) " + "VALUES(?, ?, ?)";
    String query = "SELECT * FROM Team";

    try {
      int id = -1;
      id = udb.getUserId(userName);
      
      preStmt = conn.prepareStatement(sql);
      preStmt.setString(1, groupName);
      preStmt.setInt(2, id);
      preStmt.setBoolean(3, isLeader);
      
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
