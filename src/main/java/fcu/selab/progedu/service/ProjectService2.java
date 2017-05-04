package fcu.selab.progedu.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.gitlab.api.models.GitlabProject;
import org.gitlab.api.models.GitlabUser;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import fcu.selab.progedu.config.JenkinsConfig;
import fcu.selab.progedu.conn.Conn;
import fcu.selab.progedu.data.Project;
import fcu.selab.progedu.db.ProjectDbManager;
import fcu.selab.progedu.exception.LoadConfigFailureException;
import fcu.selab.progedu.jenkins.JenkinsApi;
import fcu.selab.progedu.utils.ZipHandler;

@Path("project2/")
public class ProjectService2 {

  private Conn conn = Conn.getInstance();
  private GitlabUser root = conn.getRoot();
  private ZipHandler zipHandler;
  private JenkinsApi jenkins = JenkinsApi.getInstance();

  private JenkinsConfig jenkinsData = JenkinsConfig.getInstance();

  private String jenkinsRootUsername;
  private String jenkinsRootPassword;

  private StringBuilder javacSb;

  private ProjectDbManager dbManager = ProjectDbManager.getInstance();

  boolean isSave = true;

  /**
   * Constuctor
   */
  public ProjectService2() {
    try {
      zipHandler = new ZipHandler();
      jenkinsRootUsername = jenkinsData.getJenkinsRootUsername();
      jenkinsRootPassword = jenkinsData.getJenkinsRootPassword();
    } catch (LoadConfigFailureException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * 
   * @param name
   *          abc
   * @param readMe
   *          abc
   * @param fileType
   *          abc
   * @param uploadedInputStream
   *          abc
   * @param fileDetail
   *          abc
   * @return abc
   */
  @POST
  @Path("create")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public Response newProject(
      @FormDataParam("Hw_Name") String name,
      @FormDataParam("Hw_README") String readMe,
      @FormDataParam("fileRadio") String fileType,
      @FormDataParam("file") InputStream uploadedInputStream,
      @FormDataParam("file") FormDataContentDisposition fileDetail) {
    int rootProjectId;
    String rootProjectUrl = null;
    String folderName = null;
    String filePath = null;
    boolean hasTemplate = false;

    // 1. Create root project and get project id and url
    createRootProject(name);
    rootProjectId = getThisProjectId(name);
    rootProjectUrl = getThisProjectUrl(name);

    // 2. Clone the project to C:\\Users\\users\\AppData\\Temp\\uploads
    String cloneCommand = "git clone " + rootProjectUrl;
    execCmdInUploads(cloneCommand);

    // 3. Store Zip File to folder if file is not empty
    if (!fileDetail.getFileName().isEmpty()) {
      hasTemplate = true;
      // get the folder name
      folderName = fileDetail.getFileName();
      // store to C://User/AppData/Temp/uploads/
      filePath = storeFileToTemp(fileDetail.getFileName(), uploadedInputStream);
    } else {
      if (fileType != null && !"".equals(fileType)) {
        // fileType is not null
        if (fileType.equals("Javac")) {
          // fileType == Javac
          filePath = this.getClass().getResource("JavacQuickStart.zip").getFile();
          folderName = "MvnQuickStart.zip";
        } else {
          // fileType == Maven
          filePath = this.getClass().getResource("MvnQuickStart.zip").getFile();
          folderName = "MvnQuickStart.zip";
        }
      } else {
        // fileType is null
        filePath = this.getClass().getResource("MvnQuickStart.zip").getFile();
        folderName = "MvnQuickStart.zip";
      }
    }

    // 4. Unzip the file to the root project
    unzipFile(filePath, rootProjectId, folderName, name);

    // 5. if README is not null
    if (!readMe.equals("<br>") || !"".equals(readMe) || !readMe.isEmpty()) {
      // Add readme to folder
      createReadmeFile(readMe, name);
    }

    // 6. Cmd gitlab add
    String addCommand = "git add .";
    execCmd(addCommand, name);

    // 7. Cmd gitlab commit
    String commitCommand = "git commit -m \"Teacher commit \"";
    execCmd(commitCommand, name);

    // 8. Cmd gitlab push
    String pushCommand = "git push";
    execCmd(pushCommand, name);

    // 9. Add project to database
    addProject(name, readMe, fileType, hasTemplate);

    // 10. Create student project, and import project
    conn.createPrivateProject(name, rootProjectUrl);

    // 11. Create each Jenkins Jobs
    createJenkinsJob(name, fileType);

    // send notification email to student
    // sendEmail();

    Response response = Response.ok().build();
    // if (!isSave) {
    // response =
    // Response.serverError().status(Status.INTERNAL_SERVER_ERROR).build();
    // }
    return response;
  }

  private void createRootProject(String name) {
    conn.createRootProject(name);
  }

  private int getThisProjectId(String name) {
    Integer id = null;
    List<GitlabProject> rootProjects = conn.getProject(root);
    for (GitlabProject project : rootProjects) {
      String proName = project.getName();
      if (proName.equals(name)) {
        id = project.getId();
      }
    }
    return id;
  }

  private String getThisProjectUrl(String name) {
    String url = null;
    List<GitlabProject> rootProjects = conn.getProject(root);
    for (GitlabProject project : rootProjects) {
      String proName = project.getName();
      if (proName.equals(name)) {
        url = project.getWebUrl();
        url = url.replace("0912fe2b3e43", "root:iecsfcu123456@140.134.26.71:20080");
        url = url + ".git";
      }
    }
    return url;
  }

  private void execCmd(String command, String projectName) {
    Process process;
    String tempDir = System.getProperty("java.io.tmpdir");
    String uploadDir = tempDir + "uploads\\" + projectName;

    try {
      process = Runtime.getRuntime()
          .exec("cmd.exe /c " + command, // path to executable
              null, // env vars, null means pass parent env
              new File(uploadDir));
      BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
      String line;
      while (true) {
        line = br.readLine();
        if (line == null) {
          break;
        }
        System.out.println(line);
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private void execCmdInUploads(String command) {
    Process process;
    String tempDir = System.getProperty("java.io.tmpdir");
    String uploadDir = tempDir + "uploads\\";

    try {
      process = Runtime.getRuntime()
          .exec("cmd.exe /c " + command, // path to executable
              null, // env vars, null means pass parent env
              new File(uploadDir));
      BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
      String line;
      while (true) {
        line = br.readLine();
        if (line == null) {
          break;
        }
        System.out.println(line);
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private String storeFileToTemp(String fileName, InputStream uploadedInputStream) {
    String tempDir = System.getProperty("java.io.tmpdir");
    String uploadDir = tempDir + "uploads\\";
    try {
      createFolderIfNotExists(uploadDir);
    } catch (SecurityException se) {
      System.out.println(se.toString());
    }
    String uploadedFileLocation = uploadDir + fileName;
    try {
      saveToFile(uploadedInputStream, uploadedFileLocation);
    } catch (IOException e) {
      System.out.println(e.toString());
    }
    return uploadedFileLocation;
  }

  /**
   * Utility method to save InputStream data to target location/file
   * 
   * @param inStream
   *          - InputStream to be saved
   * @param target
   *          - full path to destination file
   */
  private void saveToFile(InputStream inStream, String target) throws IOException {
    OutputStream out = null;
    int read = 0;
    byte[] bytes = new byte[1024];
    out = new FileOutputStream(new File(target));
    while ((read = inStream.read(bytes)) != -1) {
      out.write(bytes, 0, read);
    }
    out.flush();
    out.close();
  }

  /**
   * Creates a folder to desired location if it not already exists
   * 
   * @param dirName
   *          - full path to the folder
   * @throws SecurityException
   *           - in case you don't have permission to create the folder
   */
  private void createFolderIfNotExists(String dirName) throws SecurityException {
    File theDir = new File(dirName);
    if (!theDir.exists()) {
      theDir.mkdir();
    }
  }

  private void unzipFile(String filePath, int projectId, String folderName, String projectName) {
    try {
      // unzip file
      zipHandler.unzip(filePath, projectId, folderName, projectName);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Send the notification email to student
   *
   */
  public void sendEmail() {
    String email;
    email = "";
    final String username = "rtc@mail.fcu.edu.tw"; // teacher's email
    final String password = "xrjiuuiityofurzi";// teacher's mail password

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
      String content = "";
      // Message message = new MimeMessage(session);
      MimeMessage message = new MimeMessage(session);
      message.setFrom(new InternetAddress("rtc@mail.fcu.edu.tw")); // send from
                                                                   // teacher's
                                                                   // email
      message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
      message.setSubject("You got a new assignment", "utf-8");
      message.setContent(content, "text/html;charset=utf-8");

      Transport.send(message);

      System.out.println("Mail sent succesfully!");

    } catch (MessagingException e) {
      throw new RuntimeException(e);
    }
  }

  private void createJenkinsJob(String name, String fileType) {
    String jenkinsCrumb = jenkins.getCrumb(jenkinsRootUsername, jenkinsRootPassword);
    StringBuilder sb = zipHandler.getStringBuilder();
    jenkins.createRootJob(name, jenkinsCrumb, fileType, sb);
    try {
      jenkins.createJenkinsJob(name, jenkinsCrumb, fileType, sb);
      jenkins.buildJob(name, jenkinsCrumb);
    } catch (LoadConfigFailureException | IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  private void createReadmeFile(String readMe, String projectName) {
    String tempDir = System.getProperty("java.io.tmpdir");
    String uploadDir = tempDir + "uploads\\";
    String projectDir = uploadDir + projectName;

    Writer writer = null;
    System.out.println("readMe : " + readMe);

    try {
      writer = new BufferedWriter(new OutputStreamWriter(
          new FileOutputStream(projectDir + "\\README.md"), "utf-8"));
      writer.write(readMe);
    } catch (IOException ex) {
      // report
    } finally {
      try {
        writer.close();
      } catch (Exception ex) {
        /* ignore */
      }
    }
  }

  /**
   * Add a project to database
   * 
   * @param name
   *          Project name
   * @param readMe
   *          Project readme
   * @param fileType
   *          File type
   * @param hasTemplate
   *          Has template
   */
  public void addProject(String name, String readMe, String fileType, boolean hasTemplate) {
    Project project = new Project();

    project.setName(name);
    project.setDescription(readMe);
    project.setType(fileType);
    project.setHasTemplate(hasTemplate);

    dbManager.addProject(project);
  }

}
