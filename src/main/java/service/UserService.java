package service;

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

import conn.Conn;
import data.CourseData;
import data.User;

@Path("user/")
public class UserService {

  Conn userConn = Conn.getInstance();

  CourseData course = new CourseData();

  @POST
  @Path("upload")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public Response upload(@FormDataParam("file") InputStream uploadedInputStream,
      @FormDataParam("file") FormDataContentDisposition fileDetail) {
    boolean isSave = true;

    String tempDir = System.getProperty("java.io.tmpdir");

    String uploadDir = tempDir + "uploads\\";

    File fUploadDir = new File(uploadDir);
    if (!fUploadDir.exists()) {
      fUploadDir.mkdirs();
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
    // String output = "File successfully uploaded to : " + uploadedFileLocation;
    // System.out.println(StringUtils.substringAfterLast(fileDetail.getFileName(), ":"));
    // java.net.URI location = new java.net.URI("../teacherHW.jsp");
    // return Response.temporaryRedirect(location).build();
    return response;
  }

  public void register(List<String> data) {
    List<User> lsStudent = new ArrayList<User>();
    for (String lsData : data) {
      String[] row = lsData.split(",");
      String password = row[0];
      String userName = row[0];
      String fullName = row[1];
      String ID = row[0];
      String email = "";

      if (row[0].equalsIgnoreCase("studentid"))
        continue;

      if (row.length >= 3)
        email = row[2];
      else
        email = row[0] + course.getSchoolEmail();

      User student = new User();
      student.setID(ID);
      student.setUserName(userName);
      student.setPassword(password);
      student.setEmail(email);
      student.setName(fullName);
      lsStudent.add(student);

      if (userConn.createUser(email, password, userName, fullName))
        System.out.println("register " + row[1] + " success!");
    }
    printStudent(lsStudent);
  }

  public void printStudent(List<User> student) {
    String ID = "", userName = "", password = "", email = "", name = "";
    for (User user : student) {
      ID = user.getID();
      userName = user.getUserName();
      password = user.getPassword();
      email = user.getEmail();
      name = user.getName();

      System.out.println("ID: " + ID + ", userName: " + userName + ", password: " + password
          + ", email: " + email + ", name: " + name);
    }
  }

  public List<GitlabUser> getUsers() {
    return userConn.getUsers();
  }
}
