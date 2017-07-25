package fcu.selab.progedu.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import fcu.selab.progedu.data.Project;
import fcu.selab.progedu.data.User;

public class ProjectDbManager {

  private static ProjectDbManager DB_MANAGER = new ProjectDbManager();

  public static ProjectDbManager getInstance() {
    return DB_MANAGER;
  }

  private IDatabase database = new MySqlDatabase();

  private ProjectDbManager() {

  }
  
  UserDbManager udb = UserDbManager.getInstance();

  /**
   * Add project to database
   * @param project         Project
   */
  public void addProject(Project project) {
    Connection conn = database.getConnection();
    PreparedStatement preStmt = null;
    Statement stmt = null;
    String sql = "INSERT INTO Assignment(name, deadline, description, hasTemplate, type)"
        + "  VALUES(?, ?, ?, ?, ?)";
    String query = "SELECT * FROM Assignment";

    try {
      preStmt = conn.prepareStatement(sql);
      preStmt.setString(1, project.getName());
      preStmt.setString(2, project.getDeadline());
      preStmt.setString(3, project.getDescription());
      preStmt.setBoolean(4, project.isHasTemplate());
      preStmt.setString(5, project.getType());
      preStmt.executeUpdate();
      preStmt.close();
//
//      stmt = conn.createStatement();
//      ResultSet rs = stmt.executeQuery(query);
//      System.out.println("List All Projects");
//      while (rs.next()) {
//        System.out.println("Name: " + rs.getString("name") 
//            + ", Deadline: " + rs.getString("deadline") 
//            + ", Description: "  + rs.getString("description") 
//            + ", HasTemplate: " + rs.getBoolean("hasTemplate")
//            + ", Type: " + rs.getString("type"));
//      }
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
   * List all the projects
   * @return List of projects
   */
  public List<Project> listAllProjects() {
    List<Project> lsProjects = new ArrayList<Project>();

    Connection conn = database.getConnection();
    String sql = "SELECT * FROM Assignment";
    Statement stmt = null;

    try {
      stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(sql);
      while (rs.next()) {
        String name = rs.getString("name");
        String description = rs.getString("description");
        boolean hasTemplate = rs.getBoolean("hasTemplate");
        String type = rs.getString("type");

        Project project = new Project();
        project.setName(name);
        project.setDescription(description);
        project.setHasTemplate(hasTemplate);
        project.setType(type);

        lsProjects.add(project);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return lsProjects;
  }
}
