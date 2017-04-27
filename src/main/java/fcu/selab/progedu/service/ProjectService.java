package fcu.selab.progedu.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.gitlab.api.models.GitlabProject;
import org.gitlab.api.models.GitlabUser;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import fcu.selab.progedu.config.GitlabConfig;
import fcu.selab.progedu.config.JenkinsConfig;
import fcu.selab.progedu.conn.Conn;
import fcu.selab.progedu.conn.HttpConnect;
import fcu.selab.progedu.data.Project;
import fcu.selab.progedu.db.ProjectDbManager;
import fcu.selab.progedu.db.UserDbManager;
import fcu.selab.progedu.exception.LoadConfigFailureException;
import fcu.selab.progedu.jenkins.JenkinsApi;
import fcu.selab.progedu.utils.ZipHandler;

@Path("project/")
public class ProjectService {

  private GitlabConfig gitData = GitlabConfig.getInstance();
  private JenkinsConfig jenkinsData = JenkinsConfig.getInstance();
  private Conn userConn = Conn.getInstance();
  private JenkinsApi jenkins;
  private ZipHandler unzip;
  private GitlabUser root;
  private HttpConnect httpConn = new HttpConnect();
  private ProjectDbManager dbManager = ProjectDbManager.getInstance();
  private UserDbManager userDb = UserDbManager.getInstance();
  private List<GitlabUser> users;
  private String jenkinsHostUrl;
  private String jenkinsRootUsername;
  private String jenkinsRootPassword;

  private static final String tempDir = System.getProperty("java.io.tmpdir");

  /**
   * Constructor
   */
  public ProjectService() {
    try {
      jenkins = JenkinsApi.getInstance();
      unzip = new ZipHandler();
      root = userConn.getRoot();
      users = userConn.getUsers();
      jenkinsHostUrl = jenkinsData.getJenkinsHostUrl();
      jenkinsRootUsername = jenkinsData.getJenkinsRootUsername();
      jenkinsRootPassword = jenkinsData.getJenkinsRootPassword();
    } catch (LoadConfigFailureException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * New a gitlab project and jenkins job
   * 
   * @param name
   *          Project name
   * @param readMe
   *          Project readme
   * @param fileType
   *          File type
   * @param uploadedInputStream
   *          File upload input stream
   * @param fileDetail
   *          File detail
   * @return response
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

    boolean hasTemplate = false;
    boolean isSave = true;

    try {
      // create root project
      userConn.createRootProject(name);
      Integer projectId = getNewProId(name);
      final String projectUrl = getNewProUrl(name);

      // Clone the new root gitlab project
      String cloneCommand = "git clone " + projectUrl;
      execCmd(cloneCommand);

      String filePath = null;
      String folderName = null;
      // if file detail is not empty
      if (!fileDetail.getFileName().isEmpty()) {
        hasTemplate = true;

        // get the folder name
        folderName = fileDetail.getFileName();
        // store to C://User/AppData/Temp/uploads/
        filePath = storeFileToTemp(fileDetail.getFileName(), uploadedInputStream);
      } else { // if file detail is empty
        filePath = this.getClass().getResource("MvnQuickStart.zip").getFile();
        folderName = "MvnQuickStart.zip";
      }
      unzipFile(filePath, projectId, folderName, name);
      // config_javac.xml command line
      StringBuilder sb = new StringBuilder();
      sb = unzip.getStringBuilder();

      if (!readMe.equals("<br>")) {
        String readmeUrl = gitData.getGitlabHostUrl() + "/api/v3/projects/" + projectId
            + "/repository/files?private_token=" + gitData.getGitlabApiToken();
        httpConn.httpPostReadme(readmeUrl, readMe);
      }

      // create student project
      userConn.createPrivateProject(name, projectUrl);

      // ---jenkins create job---
      String jenkinsCrumb = jenkins.getCrumb(jenkinsRootUsername, jenkinsRootPassword);
      jenkins.createRootJob(name, jenkinsCrumb, fileType, sb);
      jenkins.createJenkinsJob(name, jenkinsCrumb, fileType, sb);
      jenkins.buildJob(name, jenkinsCrumb);
      // -----------------------

      addProject(name, readMe, fileType, hasTemplate);
    } catch (Exception e) {
      isSave = false;
      e.printStackTrace();
    }

    // java.net.URI location = new java.net.URI("../teacherHW.jsp");
    // return Response.temporaryRedirect(location).build();
    Response response = Response.ok().build();
    if (!isSave) {
      response = Response.serverError().status(Status.INTERNAL_SERVER_ERROR).build();
    }
    return response;
  }

  /**
   * Unzip the file
   * 
   * @param filePath
   *          File path
   * @param projectId
   *          Project id
   * @param folderName
   *          Folder name
   */
  public void unzipFile(String filePath, int projectId, String folderName, String projectName) {
    try {
      // unzip file
      unzip.unzip(filePath, projectId, folderName, projectName);
    } catch (IOException e) {
      e.printStackTrace();
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

  private Integer getNewProId(String name) {
    Integer id = null;
    List<GitlabProject> rootProjects = userConn.getProject(root);
    for (GitlabProject project : rootProjects) {
      String proName = project.getName();
      if (proName.equals(name)) {
        id = project.getId();
      }
    }
    return id;
  }

  private String getNewProUrl(String name) {
    String url = null;
    List<GitlabProject> rootProjects = userConn.getProject(root);
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

  private void execCmd(String command) {
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
}
