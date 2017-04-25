package fcu.selab.progedu.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.gitlab.api.models.GitlabUser;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import fcu.selab.progedu.config.CourseConfig;
import fcu.selab.progedu.conn.Conn;
import fcu.selab.progedu.data.User;
import fcu.selab.progedu.exception.LoadConfigFailureException;

@Path("user/")
public class UserService {

  Conn userConn = Conn.getInstance();

  CourseConfig course = CourseConfig.getInstance();

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

    String tempDir = System.getProperty("java.io.tmpdir");

    String uploadDir = tempDir + "uploads\\";

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

    try {
      FileOutputStream out = new FileOutputStream(new File(uploadedFileLocation));
      int read = 0;
      byte[] bytes = new byte[1024];
      out = new FileOutputStream(new File(uploadedFileLocation));
      while ((read = uploadedInputStream.read(bytes)) != -1) {
        out.write(bytes, 0, read);
      }

      // parse file
      FileReader fr = new FileReader(uploadedFileLocation);
      BufferedReader br = new BufferedReader(fr);

      String line = "";
      String convert = "";

      while ((line = br.readLine()) != null) {
        String[] row = line.split(",");
        convert = row[0];
        for (int i = 1; i < row.length; i++) {
          convert = convert + "," + row[i];
        }
        System.out.println(convert + "\n");

        studentList.add(convert);
      }
      register(studentList);

      fr.close();
      br.close();

      out.flush();
      out.close();
    } catch (IOException e) {
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

      String id;
      id = row[0];

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
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }

      User student = new User();
      student.setId(id);
      student.setUserName(userName);
      student.setPassword(password);
      student.setEmail(email);
      student.setName(fullName);
      lsStudent.add(student);

      try {
        if (userConn.createUser(email, password, userName, fullName)) {
          System.out.println("register " + row[1] + " success!");
        }
      } catch (LoadConfigFailureException | IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    printStudent(lsStudent);
  }

  /**
   * Print user information
   * 
   * @param student
   *          user
   */
  public void printStudent(List<User> student) {
    String id = "";
    String userName = "";
    String password = "";
    String email = "";
    String name = "";
    for (User user : student) {
      id = user.getId();
      userName = user.getUserName();
      password = user.getPassword();
      email = user.getEmail();
      name = user.getName();

      System.out.println("ID: " + id + ", userName: " + userName + ", password: " + password
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
    try {
      users = userConn.getUsers();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return users;
  }
}
