package fcu.selab.progedu.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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
  
  /**
   * get all groups
   * 
   * @return  all group on gitlab
   */
  
  public List<Group> listGroups() {
    List<Group> lsGroups = new ArrayList<Group>();
    List<String> members = new ArrayList<String>();
    Group group = new Group();
    String groupName = "";
    
    Connection conn = database.getConnection();
    String sql = "SELECT * FROM Team";
    Statement stmt = null;
    
    try {
      stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(sql);
      
      while (rs.next()) {
        if (groupName.equals(rs.getString("name"))) {
          boolean isLeader = rs.getBoolean("isLeader");
          
          if (isLeader) {
            int leaderId = rs.getInt("sId");
            group.setMaster(udb.getName(leaderId));
          } else {
            int memberId = rs.getInt("sId");
            members.add(udb.getName(memberId));
            group.setContributor(members);
          }
        } else {
          group = new Group();
          members = new ArrayList<String>();
          
          groupName = rs.getString("name");
          group.setGroupName(groupName);
          boolean isLeader = rs.getBoolean("isLeader");
          if (isLeader) {
            int leaderId = rs.getInt("sId");
            group.setMaster(udb.getName(leaderId));
          } else {
            int memberId = rs.getInt("sId");
            members.add(udb.getName(memberId));
            group.setContributor(members);
          }
          lsGroups.add(group);
        }
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
    return lsGroups;
  }
}
