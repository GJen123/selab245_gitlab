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
    String sql = "INSERT INTO Assignment(name, description, hasTemplate, type)  VALUES(?, ?, ?, ?)";
    String query = "SELECT * FROM Assignment";
    List<User> users = udb.listAllUsers();

    try {
      preStmt = conn.prepareStatement(sql);
      preStmt.setString(1, project.getName());
      preStmt.setString(2, project.getDescription());
      preStmt.setBoolean(3, project.isHasTemplate());
      preStmt.setString(4, project.getType());
      preStmt.executeUpdate();
      preStmt.close();
      
      for (User user : users) {
        sendEmail(user.getEmail());
      }

      stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(query);
      System.out.println("List All Projects");
      while (rs.next()) {
        System.out.println("Name: " + rs.getString("name") + ", Description: "
            + rs.getString("description") + ", HasTemplate: " + rs.getBoolean("hasTemplate")
            + ", Type: " + rs.getString("type"));
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
   * Send notification email to student
   * @param email students' email
   */
  public void sendEmail(String email) {
    
    final String username = "fcuselab245@gmail.com";
//    final String password = "csclbyqwjhgogypt";// your password
    final String password = "52005505";

    Properties props = new Properties();
    props.put("mail.smtp.host", "smtp.gmail.com");
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.port", "587");
    Session session = Session.getInstance(props, new javax.mail.Authenticator() {
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username, password);
      }
    });

    try {
      String content = "您有新作業!";
      MimeMessage message = new MimeMessage(session);
      message.setFrom(new InternetAddress(username));
      message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
      message.setSubject("新作業通知", "utf-8");
      message.setContent(content, "text/html;charset=utf-8");

      Transport.send(message);

      System.out.println("Mail sent succesfully!");

    } catch (MessagingException e) {
      throw new RuntimeException(e);
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
