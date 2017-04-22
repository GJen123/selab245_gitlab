package service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
//import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.gitlab.api.models.GitlabGroup;
import org.gitlab.api.models.GitlabUser;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import conn.Conn;
import data.Group;

@Path("group/")
public class GroupService {

  Conn userConn = Conn.getInstance();
  UserService userService = new UserService();

  @POST
  @Path("upload")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public Response upload(@FormDataParam("file") InputStream uploadedInputStream,
      @FormDataParam("file") FormDataContentDisposition fileDetail) {
    boolean isSave = true;

    String tempDir = System.getProperty("java.io.tmpdir");

    String uploadDir = tempDir + "uploads/";

    File fUploadDir = new File(uploadDir);
    if (!fUploadDir.exists()) {
      fUploadDir.mkdirs();
    }
    String fileName = fileDetail.getFileName();
    System.out.println("fileName :" + fileName);
    String uploadedFileLocation = uploadDir + fileName;
    List<String> groupList = new ArrayList<String>();
    System.out.println("File successfully uploaded to : " + uploadedFileLocation);

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

        groupList.add(convert);
      }
      newGroup(groupList);

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
    // System.out.println("fileName :" + fileName);
    // java.net.URI location = new java.net.URI("../teacherManageGroup.jsp");
    // return Response.temporaryRedirect(location).build();
    return response;
  }

  public void newGroup(List<String> data) {
    String groupName = "", masterName = "";
    List<String> cons = new ArrayList<String>();
    List<Group> groups = new ArrayList<Group>();
    Group group = new Group();

    int number = 0;
    int flag = 0;

    for (String lsData : data) {
      number++;
      String[] row = lsData.split(",");

      if (row[0].equals("Team"))
        continue;
      if (!row[0].isEmpty()) { // Team
        if (flag == 1) {
          group.setGroupName(groupName);
          group.setMaster(masterName);
          group.setContributor(cons);

          groups.add(group);

          flag = 0;
        }
        group = new Group();
        cons = new ArrayList<String>();
        groupName = row[0];
        if (!row[1].isEmpty())
          masterName = row[3]; // teamLeader
        else
          cons.add(row[3]);
      } else {
        flag = 1;
        if (!row[1].isEmpty())
          masterName = row[3]; // teamLeader
        else
          cons.add(row[3]);
      }
      if (number == data.size()) {
        System.out.println("data final");
        group.setGroupName(groupName);
        group.setMaster(masterName);
        group.setContributor(cons);
        groups.add(group);
      }
    }
    for (Group g : groups) {
      System.out.println("Group name: " + g.getGroupName());
      System.out.println("Group master: " + g.getMaster());
      for (String c : g.getContributor()) {
        System.out.println("Group member: " + c);
      }
    }
  }

  public GitlabGroup newGroup(String name) {
    GitlabGroup group = userConn.createGroup(name);
    System.out.println(group.getName() + ", " + group.getId());
    return group;
  }

  public int newGroupId(GitlabGroup group) {
    return group.getId();
  }

  public void createGroup(Group group) {
    int groupId = -1, masterId = -1, developerId = -1;
    ;

    // for (Group group : groups) {
    groupId = newGroupId(newGroup(group.getGroupName()));
    masterId = findUser(group.getMaster());
    userConn.addMember(groupId, masterId, 40);

    for (String developName : group.getContributor()) {
      developerId = findUser(developName);
      userConn.addMember(groupId, developerId, 30);
    }
    // }
  }

  public int findUser(String userName) {
    List<GitlabUser> users = userConn.getUsers();
    for (GitlabUser user : users) {
      if (user.getUsername().equals(userName))
        return user.getId();
    }
    return -1;
  }

  @GET
  @Path("export")
  @Produces(MediaType.APPLICATION_OCTET_STREAM)
  public Response exportStudentList() throws Exception {

    String tempDir = System.getProperty("java.io.tmpdir");

    String downloadDir = tempDir + "downloads/";

    File fUploadDir = new File(downloadDir);
    if (!fUploadDir.exists()) {
      fUploadDir.mkdirs();
    }

    String filepath = downloadDir + "StdentList.csv";

    File file = new File(filepath);
    FileWriter writer = new FileWriter(filepath);
    StringBuilder build = new StringBuilder();

    String[] csvTitle = { "Team", "TeamLeader", "Student_Id", "name" };

    List<GitlabUser> lsUsers = userService.getUsers();
    Collections.reverse(lsUsers);

    // insert title into file
    for (int i = 0; i < csvTitle.length; i++) {
      build.append(csvTitle[i]);
      if (i == csvTitle.length)
        break;
      build.append(",");
    }
    build.append("\n");

    // insert user's id and name into file
    for (GitlabUser user : lsUsers) {
      if (user.getId() == 1)
        continue;
      build.append(""); // Team
      build.append(",");
      build.append(""); // TeamLeader
      build.append(",");
      build.append(user.getUsername()); // userName
      build.append(",");
      build.append(user.getName()); // name
      build.append("\n");
    }

    // write the file
    System.out.println("content:\n" + build.toString());
    writer.write(build.toString());
    writer.close();

    ResponseBuilder response = Response.ok((Object) file);
    response.header("Content-Disposition", "attachment;filename=StudentList.csv");
    return response.build();

  }
}
