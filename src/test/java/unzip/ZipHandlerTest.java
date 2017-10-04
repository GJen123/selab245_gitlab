package unzip;

import java.io.IOException;

import org.junit.Test;

import fcu.selab.progedu.exception.LoadConfigFailureException;
import fcu.selab.progedu.service.ProjectService2;
import fcu.selab.progedu.utils.ZipHandler;

public class ZipHandlerTest {

  public void unzipTest() {
    try {
      ZipHandler zip = new ZipHandler();
      System.out.println("aaa :" + ProjectService2.class.getResource("MvnQuickStart.zip").getFile());
      String filePath = ProjectService2.class.getResource("MvnQuickStart.zip").getFile();
      String folderName = "MvnQuickStart";
      String projectName = "OOP-test44";
      zip.unzip(filePath, folderName, projectName);
    } catch (LoadConfigFailureException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
