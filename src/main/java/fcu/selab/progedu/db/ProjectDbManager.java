package fcu.selab.progedu.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import fcu.selab.progedu.data.Project;

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
   * 
   * @param project
   *          Project
   */
  public void addProject(Project project) {
    Connection conn = database.getConnection();
    PreparedStatement preStmt = null;
    String sql = "INSERT INTO Assignment(name, createTime, deadline, description, hasTemplate"
        + ", type, zipChecksum, zipUrl)  VALUES(?, ?, ?, ?, ?, ?, ?, ?)";

    try {
      preStmt = conn.prepareStatement(sql);
      preStmt.setString(1, project.getName());
      preStmt.setString(2, project.getCreateTime());
      preStmt.setString(3, project.getDeadline());
      preStmt.setString(4, project.getDescription());
      preStmt.setBoolean(5, project.isHasTemplate());
      preStmt.setString(6, project.getType());
      preStmt.setString(7, project.getTestZipChecksum());
      preStmt.setString(8, project.getTestZipUrl());
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
   * get project info by project name
   * 
   * @param name
   *          project name
   * @return project
   */
  public Project getProjectByName(String name) {
    Project project = new Project();
    Connection conn = database.getConnection();
    String sql = "SELECT * FROM Assignment WHERE name = ?";
    PreparedStatement stmt = null;

    try {
      stmt = conn.prepareStatement(sql);
      stmt.setString(1, name);
      ResultSet rs = stmt.executeQuery();
      while (rs.next()) {
        String createTime = rs.getString("createTime");
        String deadline = rs.getString("deadline").replace("T", " ");
        String description = rs.getString("description");
        boolean hasTemplate = rs.getBoolean("hasTemplate");
        String type = rs.getString("type");
        String checksum = rs.getString("zipChecksum");
        String zipUrl = rs.getString("zipUrl");

        project.setName(name);
        project.setCreateTime(createTime);
        project.setDescription(description);
        project.setHasTemplate(hasTemplate);
        project.setType(type);
        project.setDeadline(deadline);
        project.setTestZipChecksum(checksum);
        project.setTestZipUrl(zipUrl);

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
    return project;
  }

  /**
   * List all the projects
   * 
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
        String checksum = rs.getString("zipChecksum");
        String zipUrl = rs.getString("zipUrl");
        String createTime = rs.getString("createTime");
        String deadline = rs.getString("deadline");

        Project project = new Project();
        project.setName(name);
        project.setDescription(description);
        project.setHasTemplate(hasTemplate);
        project.setType(type);
        project.setTestZipChecksum(checksum);
        project.setTestZipUrl(zipUrl);
        project.setCreateTime(createTime);
        project.setDeadline(deadline);

        lsProjects.add(project);
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
    return lsProjects;
  }

  /**
   * list all project names;
   * 
   * @return all names
   */
  public List<String> listAllProjectNames() {
    List<String> lsNames = new ArrayList<String>();

    Connection conn = database.getConnection();
    String sql = "SELECT * FROM Assignment";
    Statement stmt = null;

    try {
      stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(sql);
      while (rs.next()) {
        String name = rs.getString("name");
        lsNames.add(name);
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
    return lsNames;
  }

  /**
   * Delete project from database
   * 
   * @param name
   *          project name
   */
  public void deleteProject(String name) {
    Connection conn = database.getConnection();
    PreparedStatement preStmt = null;
    String sql = "DELETE FROM Assignment WHERE name='" + name + "'";
    try {
      preStmt = conn.prepareStatement(sql);
      preStmt.executeUpdate();
      preStmt.close();
    } catch (SQLException e) {
      // TODO Auto-generated catch block
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
   * Edit project from database
   * 
   * @param deadline
   *          new deadline
   * @param readMe
   *          new readMe
   * @param name
   *          project name
   */
  public void editProject(String deadline, String readMe, String name) {
    Connection conn = database.getConnection();
    PreparedStatement preStmt = null;
    String sql = "UPDATE Assignment SET deadline=?, description=? WHERE name=?";
    try {
      preStmt = conn.prepareStatement(sql);
      preStmt.setString(1, deadline);
      preStmt.setString(2, readMe);
      preStmt.setString(3, name);
      System.out.println(preStmt.toString());
      preStmt.executeUpdate();
      preStmt.close();
    } catch (SQLException e) {
      // TODO Auto-generated catch block
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
