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
    try {
      unzip.unzip(zipFilePath, 0, "MvnQuickStart.zip", "abc");
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

}
