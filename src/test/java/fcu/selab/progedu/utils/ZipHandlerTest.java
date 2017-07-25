package fcu.selab.progedu.utils;

import java.io.IOException;

import org.junit.Test;

import fcu.selab.progedu.exception.LoadConfigFailureException;

public class ZipHandlerTest {

  @Test
  public void testUnzipFile() {
    ZipHandler unzip = null;
    try {
      unzip = new ZipHandler();
    } catch (LoadConfigFailureException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    String zipFilePath = this.getClass().getResource("MvnQuickStart.zip").getFile();
    // String filePath =
    // "C:\\Users\\GJen\\AppData\\Local\\Temp\\uploads\\JavacQuickStart.zip";
    String filePath = "C:\\Users\\GJen\\AppData\\Local\\Temp\\uploads\\selab245_gitlab.zip";
    try {
      unzip.unzip(filePath, "selab245_gitlab.zip", "selab245_gitlab");
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

}
