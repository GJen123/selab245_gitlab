package fcu.selab.progedu.utils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import fcu.selab.progedu.config.GitlabConfig;
import fcu.selab.progedu.conn.HttpConnect;
import fcu.selab.progedu.exception.LoadConfigFailureException;

public class ZipHandler {
  HttpConnect httpConn = new HttpConnect();
  private static final String tempDir = System.getProperty("java.io.tmpdir");

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
   * @param folderName
   *          The folder name
   * @throws IOException
   *           on fileinputstream call error
   */
  public void unzip(String zipFilePath, Integer projectId, String folderName, String projectName)
      throws IOException {
    folderName = folderName.substring(0, folderName.length() - 4);
    System.out.println("folderName : " + folderName);
    System.out.println("zipFilePath : " + zipFilePath);

    String destDirectory = tempDir + "uploads\\" + projectName;
    File destDir = new File(destDirectory);
    if (!destDir.exists()) {
      destDir.mkdir();
    }
    ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
    ZipEntry entry = zipIn.getNextEntry();
    // iterates over entries in the zip file
    while (entry != null) {
      String entryNewName = entry.getName().substring(folderName.length() + 1);
      System.out.println("entryNewName : " + entryNewName);

      String filePath = destDirectory + File.separator + entryNewName;

      if (!entry.isDirectory()) {
        // if the entry is a file, extracts it
        extractFile(zipIn, filePath);
        String entryName = entryNewName;

        final String fileContent = readFile(filePath);

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

        // // ---httpPost to Gitlab---
        // String url = hostUrl + "/api/v3/projects/" + projectId +
        // "/repository/files?private_token="
        // + token;
        // httpConn.httpPostFile(fileName, url, fileContent);
        // // ------------------------

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

  private String readFile(String fileName) throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(fileName));
    try {
      StringBuilder sb = new StringBuilder();
      String line = br.readLine();

      while (line != null) {
        sb.append(line);
        sb.append("\n");
        line = br.readLine();
      }
      return sb.toString();
    } finally {
      br.close();
    }
  }

  public void setStringBuilder(StringBuilder sb) {
    this.sb = sb;
  }

  public StringBuilder getStringBuilder() {
    return sb;
  }
}