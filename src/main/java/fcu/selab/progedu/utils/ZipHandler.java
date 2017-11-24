package fcu.selab.progedu.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import fcu.selab.progedu.config.GitlabConfig;
import fcu.selab.progedu.conn.HttpConnect;
import fcu.selab.progedu.exception.LoadConfigFailureException;

public class ZipHandler {
  HttpConnect httpConn = new HttpConnect();
  private static final String tempDir = System.getProperty("java.io.tmpdir");
  private static final String uploadDir = tempDir + "/uploads/";
  private static final String testDir = tempDir + "/tests/";

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
        String testFilePath = testDirectory + File.separator + entry.getName();
        copyTestFileToFolder(testFilePath, zipIn);

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

  private void copyTestFileToFolder(String testFilePath, ZipInputStream zipIn) {
    if (testFilePath.contains("src/test")) {
      File testNewFile = new File(testFilePath);
      new File(testNewFile.getParent()).mkdirs();
      try {
        extractFile(zipIn, testFilePath);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

}