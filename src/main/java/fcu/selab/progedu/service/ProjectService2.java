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

import org.gitlab.api.models.GitlabProject;
import org.gitlab.api.models.GitlabUser;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import fcu.selab.progedu.conn.Conn;
import fcu.selab.progedu.exception.LoadConfigFailureException;
import fcu.selab.progedu.utils.ZipHandler;

@Path("project2/")
public class ProjectService2 {

  private Conn conn = Conn.getInstance();
  private GitlabUser root = conn.getRoot();
  private ZipHandler zipHandler;

  boolean isSave = true;

  /**
   * Constuctor
   */
  public ProjectService2() {
    try {
      zipHandler = new ZipHandler();
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

    // 1. Create root project and get project id and url
    createRootProject(name);
    rootProjectId = getThisProjectId(name);
    rootProjectUrl = getThisProjectUrl(name);

    // 2. Clone the project to C:\\Users\\users\\AppData\\Temp\\uploads
    String cloneCommand = "git clone " + rootProjectUrl;
    execCmd(cloneCommand, name);

    // 3. Store Zip File to folder if file is not empty
    if (!fileDetail.getFileName().isEmpty()) {
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
    if (!readMe.equals("<br>")) {
      // Add readme to folder
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

}
