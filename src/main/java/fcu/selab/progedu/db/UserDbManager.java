package fcu.selab.progedu.db;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.gitlab.api.models.GitlabUser;

import fcu.selab.progedu.data.User;

public class UserDbManager {

  private static UserDbManager DB_MANAGER = new UserDbManager();

  public static UserDbManager getInstance() {
    return DB_MANAGER;
  }

  private IDatabase database = new MySqlDatabase();

  private UserDbManager() {

  }

  /**
   * Add gitlab user to database
   * @param user    The gitlab user
   */
  public void addUser(GitlabUser user) {
    Connection conn = database.getConnection();
    PreparedStatement preStmt = null;
    Statement stmt = null;
    String sql = "INSERT INTO "
                 + "User(gitLabId, stuId, name, password, email, privateToken)  "
                 + "VALUES(?, ?, ?, ?, ?, ?)";
    String query = "SELECT * FROM User";

    try {
      String password = passwordMD5(user.getUsername());
      password += user.getUsername();
      preStmt = conn.prepareStatement(sql);
      preStmt.setInt(1, user.getId());
      preStmt.setString(2, user.getUsername());
      preStmt.setString(3, user.getName());
      preStmt.setString(4, password);
      preStmt.setString(5, user.getEmail());
      preStmt.setString(6, user.getPrivateToken());
      preStmt.executeUpdate();
      preStmt.close();

      stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(query);
      System.out.println("List All Students");
      while (rs.next()) {
        System.out.println("GitLabId: " + rs.getString("gitLabId") + ", StuId: "
            + rs.getString("stuId") + ", Name: " + rs.getString("name") + ", Email: "
            + rs.getString("email") + ", Private_Token: " + rs.getString("privateToken")
            + ", privateToken: " + rs.getString("privateToken"));
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
  }
  
  /**
   * encrypt the user password  
   * @param password   The user's password
   * @return MD5 string
   * @throws NoSuchAlgorithmException on security api call error
   */
  public String passwordMD5(String password) {
    String hashtext = "";
    try {
      String msg = password;
      MessageDigest md = MessageDigest.getInstance("MD5");
      byte[] messageDigest = md.digest(msg.getBytes());
      BigInteger number = new BigInteger(1, messageDigest);
      hashtext = number.toString(16);
        
      while (hashtext.length() < 32) {
        hashtext = "0" + hashtext;
      }
      System.out.println(hashtext);
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    
    return hashtext;
  }

  /**
   * Get user from database
   * @param userName      The gitlab user name
   * @return user
   */
  public User getUser(String userName) {
    User user = new User();
    Connection conn = database.getConnection();
    String query = "SELECT * FROM User WHERE stuId = ?";
    PreparedStatement preStmt = null;

    try {
      preStmt = conn.prepareStatement(query);
      preStmt.setString(1, userName);
      ResultSet rs = preStmt.executeQuery();
      while (rs.next()) {
        int gitLabId = rs.getInt("gitLabId");
        String stuId = userName;
        String name = rs.getString("name");
        String password = rs.getString("password");
        String email = rs.getString("email");
        String privateToken = rs.getString("privateToken");

        user.setGitLabId(gitLabId);
        user.setUserName(stuId);
        user.setName(name);
        user.setPassword(password);
        user.setEmail(email);
        user.setPrivateToken(privateToken);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return user;
  }

  /**
   * List all the database user
   * @return list of user
   */
  public List<User> listAllUsers() {
    List<User> lsUsers = new ArrayList<User>();

    Connection conn = database.getConnection();
    String sql = "SELECT * FROM User";
    Statement stmt = null;

    try {
      stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(sql);
      while (rs.next()) {
        int gitLabId = rs.getInt("gitLabId");
        String stuId = rs.getString("userName");
        String name = rs.getString("name");
        String password = rs.getString("password");
        String email = rs.getString("email");
        String privateToken = rs.getString("privateToken");

        User user = new User();
        user.setGitLabId(gitLabId);
        user.setUserName(stuId);
        user.setName(name);
        user.setPassword(password);
        user.setEmail(email);
        user.setPrivateToken(privateToken);
        lsUsers.add(user);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return lsUsers;
  }
}
