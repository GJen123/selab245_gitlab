package fcu.selab.progedu.service;

import java.io.InputStream;
import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import fcu.selab.progedu.config.GitlabConfig;
import fcu.selab.progedu.config.JenkinsConfig;
import fcu.selab.progedu.data.Project;
import fcu.selab.progedu.db.ProjectDbManager;
import fcu.selab.progedu.jenkins.JenkinsApi;
import fcu.selab.progedu.utils.ZipHandler;
import org.gitlab.api.models.GitlabProject;
import org.gitlab.api.models.GitlabUser;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import fcu.selab.progedu.config.CourseConfig;
import fcu.selab.progedu.conn.Conn;
import fcu.selab.progedu.data.User;
import fcu.selab.progedu.db.UserDbManager;
import fcu.selab.progedu.exception.LoadConfigFailureException;

@Path("user/")
public class UserService {

  Conn userConn = Conn.getInstance();

  CourseConfig course = CourseConfig.getInstance();
  
  private UserDbManager dbManager = UserDbManager.getInstance();
  private ProjectDbManager projectDbManager = ProjectDbManager.getInstance();
  private GitlabConfig gitlabData = GitlabConfig.getInstance();
  private JenkinsApi jenkins = JenkinsApi.getInstance();
  private ZipHandler zipHandler;
  private JenkinsConfig jenkinsData = JenkinsConfig.getInstance();

  /**
   * Upload a csv file for student batch registration
   * 
   * @param uploadedInputStream
   *          file of student list
   * @param fileDetail
   *          file information
   * @return Response
   */
  @POST
  @Path("upload")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public Response upload(@FormDataParam("file") InputStream uploadedInputStream,
      @FormDataParam("file") FormDataContentDisposition fileDetail) {
    boolean isSave = true;

    try {
      String tempDir = System.getProperty("java.io.tmpdir");

      String uploadDir = tempDir + "/uploads/";

      File fileUploadDir = new File(uploadDir);
      if (!fileUploadDir.exists()) {
        fileUploadDir.mkdirs();
      }
      String fileName = fileDetail.getFileName();
      String uploadedFileLocation = uploadDir + fileName;
      System.out.println("uploadDir : " + uploadDir);
      System.out.println("fileName : " + fileName);
      System.out.println("uploadedFileLocation : " + uploadedFileLocation);

      List<String> studentList = new ArrayList<String>();

      FileOutputStream out = new FileOutputStream(new File(uploadedFileLocation));
      int read = 0;
      byte[] bytes = new byte[1024];
      out = new FileOutputStream(new File(uploadedFileLocation));
      while ((read = uploadedInputStream.read(bytes)) != -1) {
        out.write(bytes, 0, read);
      }

      // parse file
      File file = new File(uploadedFileLocation);
      InputStreamReader fr = new InputStreamReader(new FileInputStream(file),"BIG5");
      BufferedReader br;
      br = new BufferedReader(fr);

      String line = "";
      String convert = "";

      while ((line = br.readLine()) != null) {
        String[] row = line.split(",");
        convert = row[0];
        for (int i = 1; i < row.length; i++) {
          convert = convert + "," + row[i];
        }

        studentList.add(convert);
      }
      register(studentList);

      br.close();

      out.flush();
      out.close();
    } catch (Exception e) {
      isSave = false;
      e.printStackTrace();
    }
    Response response = Response.ok().build();
    if (!isSave) {
      response = Response.serverError().status(Status.INTERNAL_SERVER_ERROR).build();
    }
    return response;
  }

  /**
   * Translate uploaded file content to string and parse to register
   * 
   * @param data
   *          file content to string
   */
  public void register(List<String> data) {
    List<User> lsStudent = new ArrayList<User>();
    for (String lsData : data) {
      String[] row = lsData.split(",");
      String password;
      password = row[0];

      String userName;
      userName = row[0];

      String fullName;
      fullName = row[1];

      String email = "";

      if (row[0].equalsIgnoreCase("studentid")) {
        continue;
      }

      if (row.length >= 3) {
        email = row[2];
      } else {
        try {
          email = row[0] + course.getSchoolEmail();
        } catch (LoadConfigFailureException e) {
          // TODO Auto-generated catch bloc
          e.printStackTrace();
        }
      }

      User student = new User();
      student.setUserName(userName);
      student.setPassword(password);
      student.setEmail(email);
      student.setName(fullName);
      lsStudent.add(student);
      boolean check = userConn.createUser(email, password, userName, fullName);

      if (check) {
        System.out.println("register " + row[1] + " success!");
      }
    }
    printStudent(lsStudent);
  }

  /**
   * 
   * @param name name
   * @param id id
   * @param email email 
   * @return response
   */
  @POST
  @Path("new")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public Response createAStudentAccount(@FormDataParam("studentName") String name,
                                        @FormDataParam("studentId") String id,
                                        @FormDataParam("studentEmail") String email) {
    boolean isSave = false;
    try {
      isSave = userConn.createUser(email, id, id, name);
      User user = dbManager.getUser(id);
      boolean isSuccess = importPreviousProject(user);
      isSave = isSave && isSuccess;
    } catch (Exception e) {
      e.printStackTrace();
    }
    Response response = Response.ok().build();
    if (!isSave) {
      response = Response.serverError().status(Status.INTERNAL_SERVER_ERROR).build();
    }
    return response;
  }

  /**
   * create previous project for new student.
   * @param user student
   * @return check
   */
  public boolean importPreviousProject(User user) {
    boolean check = false;
    List<Project> projects = projectDbManager.listAllProjects();
    String url = "";
    String gitlabUrl = "";
    String userName = user.getUserName();
    try {
      gitlabUrl = gitlabData.getGitlabRootUrl();
      for (Project project : projects) {
        String projectName = project.getName();
        Project project1 = projectDbManager.getProjectByName(projectName);
        url = gitlabUrl + "/root/" + projectName;
        userConn.createPrivateProject(user.getGitLabId(), project.getName(), url);
        boolean isSuccess = createPreviuosJob(userName, projectName, project1.getType());
        check = check && isSuccess;
      }
      check = true;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return  check;
  }

  /**
   * create previous job for new student.
   * @param username student name
   * @param name job name
   * @param fileType job type
   * @return check
   */
  public boolean createPreviuosJob(String username, String name, String fileType) {
    boolean check = false;
    String jenkinsRootUsername = null;
    String jenkinsRootPassword = null;
    try {
      jenkinsRootUsername = jenkinsData.getJenkinsRootUsername();
      jenkinsRootPassword = jenkinsData.getJenkinsRootPassword();
      String jenkinsCrumb = jenkins.getCrumb(jenkinsRootUsername, jenkinsRootPassword);
      StringBuilder sb = zipHandler.getStringBuilder();
      jenkins.createRootJob(name, jenkinsCrumb, fileType, sb);
      jenkins.createJenkinsJob(username, name, jenkinsCrumb, fileType, sb);
      jenkins.buildJob(username, name, jenkinsCrumb);
      check = true;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return  check;
  }

  /**
   * Print user information
   * 
   * @param student
   *          user
   */
  public void printStudent(List<User> student) {
    String userName = "";
    String password = "";
    String email = "";
    String name = "";
    for (User user : student) {
      userName = user.getUserName();
      password = user.getPassword();
      email = user.getEmail();
      name = user.getName();

      System.out.println("userName: " + userName + ", password: " + password
          + ", email: " + email + ", name: " + name);
    }
  }

  /**
   * Get all user on GitLab
   * 
   * @return all GitLab users
   */
  public List<GitlabUser> getUsers() {
    List<GitlabUser> users = new ArrayList<GitlabUser>();
    users = userConn.getUsers();
    return users;
  }

  /**
   * Change user password
   * 
   * @param oldPwd
   *          old password
   * @param newPwd
   *          new password
   * @param checkPwd
   *          check new password
   * @return true false
   */
  @POST
  @Path("changePwd")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public Response changePassword(@FormDataParam("oldPwd") String oldPwd,
      @FormDataParam("newPwd") String newPwd,
      @FormDataParam("checkPwd") String checkPwd,
      @FormDataParam("userId") Integer userId) {

    System.out.println("oldPwd : " + oldPwd);
    System.out.println("newPwd : " + newPwd);
    System.out.println("checkPwd : " + checkPwd);
    System.out.println("userId : " + userId);
    
    String userName = userConn.getUserById(userId).getUsername();
    System.out.println(userName);
    boolean check = dbManager.checkPassword(userName, newPwd);
    if (check) {
      System.out.println("false");
      Response response = Response.serverError().status(Status.INTERNAL_SERVER_ERROR).build();
      return response;
    } else {
      dbManager.modifiedUserPassword(userName, newPwd);
      userConn.updateUserPassword(userId, newPwd);
    }
    
    Response response = Response.ok().build();
    return response;

  }
}
