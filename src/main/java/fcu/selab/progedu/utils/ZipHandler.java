package fcu.selab.progedu.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import fcu.selab.progedu.config.GitlabConfig;
import fcu.selab.progedu.conn.HttpConnect;
import fcu.selab.progedu.exception.LoadConfigFailureException;

public class ZipHandler {
  HttpConnect httpConn = new HttpConnect();
  private static final String tempDir = System.getProperty("java.io.tmpdir");
  private static final String uploadDir = tempDir + "uploads\\";

  GitlabConfig gitData = GitlabConfig.getInstance();

  private String hostUrl;

  private String token;

  StringBuilder sb = new StringBuilder();

  public ZipHandler() throws LoadConfigFailureException {
    hostUrl = gitData.getGitlabHostUrl();
    token = gitData.getGitlabApiToken();
  }

  /**
   * Size of the buffer to read/write data
   */
  private static final int BUFFER_SIZE = 4096;

  /**
   * Extracts a zip file specified by the zipFilePath to a directory specified
   * by destDirectory (will be created if does not exists)
   * 
   * @param zipFilePath
   *          The zip file's path
   * @param projectId
   *          The gitlab project id
   * @param zipFolderName
   *          The zip folder name
   * @throws IOException
   *           on fileinputstream call error
   */
  public void unzip(String zipFilePath, Integer projectId, String zipFolderName, String projectName)
      throws IOException {
    String parentDir = null;
    int parDirLength = 0;
    zipFolderName = zipFolderName.substring(0, zipFolderName.length() - 4);

    String destDirectory = uploadDir + projectName;
    File destDir = new File(destDirectory);
    if (!destDir.exists()) {
      destDir.mkdir();
    }
    ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
    ZipEntry entry = zipIn.getNextEntry();
    // iterates over entries in the zip file
    while (entry != null) {
      String filePath = destDirectory + File.separator + entry.getName();

      if (filePath.substring(filePath.length() - 4).equals("src/")) {
        parentDir = getParentDir(filePath);
        parDirLength = parentDir.length() + 1;
      }
      String entryNewName = filePath.substring(parDirLength);

      if (!entry.isDirectory()) {
        // if the entry is a file, extracts it
        extractFile(zipIn, filePath);

        // Search the java file which jenkins java config needs.
        searchJavaFile(entryNewName);
      } else {
        // if the entry is a directory, make the directory
        File dir = new File(filePath);
        dir.mkdir();
      }
      zipIn.closeEntry();
      entry = zipIn.getNextEntry();
    }
    zipIn.close();
  }

  /**
   * Extracts a zip entry (file entry)
   * 
   * @param zipIn
   *          The zip inputstream
   * @param filePath
   *          The file path
   * @throws IOException
   *           on fileoutputstream call error
   */
  private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
    byte[] bytesIn = new byte[BUFFER_SIZE];
    int read = 0;
    while ((read = zipIn.read(bytesIn)) != -1) {
      bos.write(bytesIn, 0, read);
    }
    bos.close();
  }

  public void setStringBuilder(StringBuilder sb) {
    this.sb = sb;
  }

  public StringBuilder getStringBuilder() {
    return sb;
  }

  /**
   * 
   * @param filePath
   *          a
   * @return aa
   */
  public String getParentDir(String filePath) {
    String dir = null;
    File file = new File(filePath);
    dir = file.getParent();
    return dir;
  }

  private void searchJavaFile(String entryName) {
    // ".java" length = 5
    String last = entryName.substring(entryName.length() - 5, entryName.length());
    String fileName = null;
    for (int i = 0; i < entryName.length() - 3; i++) {
      if (entryName.substring(i, i + 3).equals("src")) {
        fileName = entryName.substring(i);
        System.out.println("fileName : " + fileName);
        if (last.equals(".java")) {
          sb.append("javac " + fileName + "\n");
          setStringBuilder(sb);
        }
      }
    }
  }

}