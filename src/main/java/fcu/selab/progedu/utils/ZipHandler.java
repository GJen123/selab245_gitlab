package fcu.selab.progedu.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import fcu.selab.progedu.config.CourseConfig;
import fcu.selab.progedu.config.GitlabConfig;
import fcu.selab.progedu.conn.HttpConnect;
import fcu.selab.progedu.exception.LoadConfigFailureException;

public class ZipHandler {
  HttpConnect httpConn = new HttpConnect();
  private static final String tempDir = System.getProperty("java.io.tmpdir");
  private static final String uploadDir = tempDir + "/uploads/";
  private static final String testDir = tempDir + "/tests/";

  private final List<File> fileList;
  private List<String> paths;

  GitlabConfig gitData = GitlabConfig.getInstance();
  CourseConfig courseData = CourseConfig.getInstance();

  private String serverIp;

  private String hostUrl;

  private String token;

  StringBuilder sb = new StringBuilder();

  List<String> filesListInDir = new ArrayList<String>();

  long checksum = 0;

  String urlForJenkinsDownloadTestFile = "";

  /**
   * Constructor.
   */
  public ZipHandler() throws LoadConfigFailureException {
    hostUrl = gitData.getGitlabHostUrl();
    token = gitData.getGitlabApiToken();
    fileList = new ArrayList<>();
    paths = new ArrayList<>(25);
    serverIp = courseData.getTomcatServerIp();
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
   * @param zipFolderName
   *          The zip folder name
   * @throws IOException
   *           on fileinputstream call error
   */
  public void unzip(String zipFilePath, String zipFolderName, String projectName)
      throws IOException {
    String parentDir = null;
    int parDirLength = 0;
    // -4 because .zip
    zipFolderName = zipFolderName.substring(0, zipFolderName.length() - 4);

    File fileUploadDir = new File(uploadDir);
    if (!fileUploadDir.exists()) {
      fileUploadDir.mkdir();
    }

    String destDirectory = uploadDir + projectName;
    File destDir = new File(destDirectory);
    if (!destDir.exists()) {
      destDir.mkdir();
    }

    String testDirectory = testDir + projectName;
    File testDir = new File(testDirectory);
    if (!testDir.exists()) {
      testDir.mkdir();
    }

    ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
    ZipEntry entry = zipIn.getNextEntry();
    // iterates over entries in the zip file
    while (entry != null) {
      String filePath = destDirectory + File.separator + entry.getName();
      File newFile = new File(filePath);

      // create all non exists folders
      // else you will hit FileNotFoundException for compressed folder
      new File(newFile.getParent()).mkdirs();

      if (filePath.substring(filePath.length() - 4).equals("src/")) {
        parentDir = getParentDir(filePath);
        parDirLength = parentDir.length() + 1;
      }
      String entryNewName = filePath.substring(parDirLength);

      if (!entry.isDirectory()) {
        // if the entry is a file, extracts it
        extractFile(zipIn, filePath);

        // if filePath equals pom.xml, modify the project name
        if (filePath.substring(filePath.length() - 7, filePath.length()).equals("pom.xml")) {
          modifyPomXml(filePath, projectName);
        }

        // Get the test folder
        // searchTestFile(entryNewName, testDirectory + "/" + entry.getName());

        // String testFilePath = testDirectory + File.separator +
        // entry.getName();
        // copyTestFileToFolder(testFilePath, projectName);

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

    copyTestFile(destDir, destDirectory, testDirectory);

    File testFile = new File(testDirectory);
    if (testFile.exists()) {
      zipTestFolder(testDirectory);

      setUrlForJenkinsDownloadTestFile(
          serverIp + "/ProgEdu/webapi/jenkins/getTestFile?filePath=" + testDir + ".zip");
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
    String last = "";
    if (entryName.endsWith(".java")) {
      last = entryName.substring(entryName.length() - 5, entryName.length());
    }
    String fileName = null;
    for (int i = 0; i < entryName.length() - 3; i++) {
      if (entryName.substring(i, i + 3).equals("src")) {
        fileName = entryName.substring(i);
        System.out.println("Search java file fileName : " + fileName);
        if (last.equals(".java")) {
          sb.append("javac " + fileName + "\n");
          setStringBuilder(sb);
        }
      }
    }
  }

  private void modifyPomXml(String filePath, String projectName) {
    try {
      System.out.println("modify filePath : " + filePath);
      System.out.println("projectName : " + projectName);
      DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
      Document doc = docBuilder.parse(filePath);

      // NodeList project = doc.getElementsByTagName("project");
      // for(int i=0; i<project.getLength(); i++){
      // Element
      // }

      Node ndId = doc.getElementsByTagName("artifactId").item(0);
      ndId.setTextContent(projectName);
      System.out.println("doc : " + ndId.getTextContent());

      Node ndName = doc.getElementsByTagName("name").item(0);
      ndName.setTextContent(projectName);
      System.out.println("doc : " + doc.getElementsByTagName("name").item(0));

      // write the content into xml file
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      DOMSource source = new DOMSource(doc);
      StreamResult result = new StreamResult(new File(filePath));
      transformer.transform(source, result);
    } catch (ParserConfigurationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (TransformerException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (SAXException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private void zipTestFolder(String testFilePath) {
    File testFile = new File(testFilePath);
    // File testZipFile = new File(testFilePath + ".zip");
    // zipIt(testFile, testZipFile);
    zipDirectory(testFile, testFilePath + ".zip");
  }

  private void zipDirectory(File dir, String zipDirName) {
    try {
      populateFilesList(dir);
      // now zip files one by one
      // create ZipOutputStream to write to the zip file
      FileOutputStream fos = new FileOutputStream(zipDirName);
      CheckedOutputStream checksum = new CheckedOutputStream(fos, new CRC32());

      ZipOutputStream zos = new ZipOutputStream(checksum);
      for (String filePath : filesListInDir) {
        System.out.println("Zipping " + filePath);
        // for ZipEntry we need to keep only relative file path, so we used
        // substring on absolute path
        ZipEntry ze = new ZipEntry(
            filePath.substring(dir.getAbsolutePath().length() + 1, filePath.length()));
        zos.putNextEntry(ze);
        // read the file and write to ZipOutputStream
        FileInputStream fis = new FileInputStream(filePath);
        byte[] buffer = new byte[1024];
        int len;
        while ((len = fis.read(buffer)) > 0) {
          zos.write(buffer, 0, len);
        }
        zos.closeEntry();
        fis.close();
      }
      zos.close();
      fos.close();
      System.out.println("Checksum : " + checksum.getChecksum().getValue());
      setChecksum(checksum.getChecksum().getValue());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void populateFilesList(File dir) throws IOException {
    File[] files = dir.listFiles();
    for (File file : files) {
      if (file.isFile()) {
        filesListInDir.add(file.getAbsolutePath());
      } else {
        populateFilesList(file);
      }

    }
  }

  public void setChecksum(long checksum) {
    this.checksum = checksum;
  }

  public long getChecksum() {
    return checksum;
  }

  public void setUrlForJenkinsDownloadTestFile(String urlForJenkinsDownloadTestFile) {
    this.urlForJenkinsDownloadTestFile = urlForJenkinsDownloadTestFile;
  }

  public String getUrlForJenkinsDownloadTestFile() {
    return urlForJenkinsDownloadTestFile;
  }

  private void copyTestFile(File folder, String strFolder, String testFilePath) {
    for (final File fileEntry : folder.listFiles()) {
      if (fileEntry.isDirectory()) {
        copyTestFile(fileEntry, strFolder, testFilePath);
      } else {
        if (fileEntry.getAbsolutePath().contains("src")) {
          String entry = fileEntry.getAbsolutePath();
          if (entry.contains("src/test")) {

            File dataFile = new File(strFolder + "/src/test");
            File targetFile = new File(testFilePath + "/src/test");
            try {
              FileUtils.copyDirectory(dataFile, targetFile);
              FileUtils.deleteDirectory(dataFile);
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        }

      }
    }
  }

}