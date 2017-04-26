package fcu.selab.progedu.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import fcu.selab.progedu.data.Group;

public class GroupDbManager {

  private static GroupDbManager DB_MANAGER = new GroupDbManager();

  private static GroupDbManager getInstance() {
    return DB_MANAGER;
  }

  private IDatabase database = new MySqlDatabase();

  private GroupDbManager() {

  }

  private UserDbManager udb = UserDbManager.getInstance();

  /**
   * Insert group into database
   * 
   * @param group
   *          new group
   */
  public void addGroup(Group group) {

    Connection conn = database.getConnection();
    PreparedStatement preStmt = null;
    Statement stmt = null;
    String sql = "INSERT INTO Team(name, sId, isLeader)  " + "VALUES(?, ?, ?)";
    String query = "SELECT * FROM Team";

    try {
      preStmt.setString(1, group.getGroupName());
      int id;
      id = udb.getUser(group.getMaster()).getId();
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
