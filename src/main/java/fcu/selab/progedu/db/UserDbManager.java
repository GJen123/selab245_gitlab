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
   * 
   * @param user
   *          The gitlab user
   */
  public void addUser(GitlabUser user) {
    Connection conn = database.getConnection();
    PreparedStatement preStmt = null;
    Statement stmt = null;
    String sql = "INSERT INTO " + "User(gitLabId, userName, name, password, email, privateToken)  "
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

      // stmt = conn.createStatement();
      // ResultSet rs = stmt.executeQuery(query);
      // System.out.println("List All Students");
      // while (rs.next()) {
      // System.out.println("GitLabId: " + rs.getString("gitLabId") + ", StuId:
      // "
      // + rs.getString("userName") + ", Name: " + rs.getString("name") + ",
      // Email: "
      // + rs.getString("email") + ", Private_Token: " +
      // rs.getString("privateToken")
      // + ", privateToken: " + rs.getString("privateToken"));
      // }
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
   * 
   * @param password
   *          The user's password
   * @return MD5 string
   * @throws NoSuchAlgorithmException
   *           on security api call error
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
   * get user password
   * 
   * @param userName
   *          user stu id
   * @return password
   */
  public String getPassword(String userName) {
    String password = "";
    Connection conn = database.getConnection();
    String query = "SELECT * FROM User WHERE userName = ?";
    PreparedStatement preStmt = null;

    try {
      preStmt = conn.prepareStatement(query);
      preStmt.setString(1, userName);
      ResultSet rs = preStmt.executeQuery();
      while (rs.next()) {
        password = rs.getString("password");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return password;
  }

  /**
   * update user db password
   * 
   * @param userName
   *          user stu id
   * @param password
   *          user new password
   */
  public void modifiedUserPassword(String userName, String password) {
    Connection conn = database.getConnection();
    String query = "UPDATE User SET password=? WHERE userName = ?";
    PreparedStatement preStmt = null;

    try {
      String newPass = passwordMD5(password) + userName;
      preStmt = conn.prepareStatement(query);
      preStmt.setString(1, newPass);
      preStmt.setString(2, userName);
      preStmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * check old password
   * 
   * @param userName
   *          user stu id
   * @param password
   *          user old password
   * @return T or F
   */
  public boolean checkPassword(String userName, String password) {
    String currPassword = getPassword(userName);
    String newPassword = passwordMD5(password) + userName;
    boolean check = false;
    if (currPassword.equals(newPassword)) {
      check = true;
    }
    return check;
  }

  /**
   * Get user from database
   * 
   * @param userName
   *          The gitlab user name
   * @return user
   */
  public User getUser(String userName) {
    User user = new User();
    Connection conn = database.getConnection();
    String query = "SELECT * FROM User WHERE userName = ?";
    PreparedStatement preStmt = null;

    try {
      preStmt = conn.prepareStatement(query);
      preStmt.setString(1, userName);
      ResultSet rs = preStmt.executeQuery();
      while (rs.next()) {
        int gitLabId = rs.getInt("gitLabId");
        int id = rs.getInt("id");
        String stuId = userName;
        String name = rs.getString("name");
        String password = rs.getString("password");
        String email = rs.getString("email");
        String privateToken = rs.getString("privateToken");

        user.setGitLabId(gitLabId);
        user.setId(id);
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
   * Get user from database
   *
   * @param id
   *          The gitlab user id
   * @return user
   */
  public User getUser(int id) {
    User user = new User();
    Connection conn = database.getConnection();
    String query = "SELECT * FROM User WHERE id = ?";
    PreparedStatement preStmt = null;

    try {
      preStmt = conn.prepareStatement(query);
      preStmt.setInt(1, id);
      ResultSet rs = preStmt.executeQuery();
      while (rs.next()) {
        int gitLabId = rs.getInt("gitLabId");
        String stuId = rs.getString("userName");
        String name = rs.getString("name");
        String password = rs.getString("password");
        String email = rs.getString("email");
        String privateToken = rs.getString("privateToken");

        user.setGitLabId(gitLabId);
        user.setId(id);
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
   * Get user from database
   *
   * @param id
   *          The gitlab user id
   * @return user
   */
  public User getUser(Connection conn, int id) {
    User user = new User();
    String query = "SELECT * FROM User WHERE id = ?";
    PreparedStatement preStmt = null;

    try {
      preStmt = conn.prepareStatement(query);
      preStmt.setInt(1, id);
      ResultSet rs = preStmt.executeQuery();
      while (rs.next()) {
        int gitLabId = rs.getInt("gitLabId");
        String stuId = rs.getString("userName");
        String name = rs.getString("name");
        String password = rs.getString("password");
        String email = rs.getString("email");
        String privateToken = rs.getString("privateToken");

        user.setGitLabId(gitLabId);
        user.setId(id);
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
   * Get user from database
   *
   * @param userId
   *          The db user id
   * @return user
   */
  public String getName(int userId) {
    Connection conn = database.getConnection();
    String query = "SELECT * FROM User WHERE id = ?";
    PreparedStatement preStmt = null;
    String name = "";

    try {
      preStmt = conn.prepareStatement(query);
      preStmt.setInt(1, userId);
      ResultSet rs = preStmt.executeQuery();
      while (rs.next()) {
        name = rs.getString("name");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return name;
  }

  /**
   * Get user from database
   *
   * @param userId
   *          The db user id
   * @return user
   */
  public String getUserName(Connection conn, int userId) {
    String query = "SELECT * FROM User WHERE id = ?";
    PreparedStatement preStmt = null;
    String name = "";

    try {
      preStmt = conn.prepareStatement(query);
      preStmt.setInt(1, userId);
      ResultSet rs = preStmt.executeQuery();
      while (rs.next()) {
        name = rs.getString("userName");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return name;
  }

  /**
   * user name to find userId in db
   * 
   * @param name
   *          user's name
   * @return id
   */
  public int getUserId(String name) {
    Connection conn = database.getConnection();
    String query = "SELECT * FROM User WHERE name = ?";
    PreparedStatement preStmt = null;
    int id = -1;

    try {
      preStmt = conn.prepareStatement(query);
      preStmt.setString(1, name);
      ResultSet rs = preStmt.executeQuery();
      while (rs.next()) {
        id = rs.getInt("id");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return id;
  }

  /**
   * List all the database user
   * 
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
        int id = rs.getInt("id");
        int gitLabId = rs.getInt("gitLabId");
        String stuId = rs.getString("userName");
        String name = rs.getString("name");
        String password = rs.getString("password");
        String email = rs.getString("email");
        String privateToken = rs.getString("privateToken");

        User user = new User();
        user.setId(id);
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
